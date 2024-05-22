package com.Douane.backendPFE.controllers;
import com.Douane.backendPFE.DTOs.request.LoginRequest;
import com.Douane.backendPFE.DTOs.request.RefreshTokenDto;
import com.Douane.backendPFE.DTOs.request.SignupRequest;
import com.Douane.backendPFE.DTOs.response.JwtRefreshResponse;
import com.Douane.backendPFE.DTOs.response.JwtResponse;
import com.Douane.backendPFE.DTOs.response.MessageResponse;
import com.Douane.backendPFE.models.user.ERole;
import com.Douane.backendPFE.models.user.Role;
import com.Douane.backendPFE.models.user.UserModel;
import com.Douane.backendPFE.repositories.userR.RoleRepository;
import com.Douane.backendPFE.security.jwt.JwtUtils;
import com.Douane.backendPFE.security.services.UserDetailsImpl;
import com.Douane.backendPFE.security.services.UserDetailsServiceImpl;
import com.Douane.backendPFE.services.userS.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@CrossOrigin()
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    UserService userService;
    @Autowired
    RoleRepository roleService;
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Value("${jwtSecret}")
    private String jwtSecret;

    @Value("${jwtExpirationMs}")
    private int jwtExpirationMs;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshJwt = jwtUtils.generateRefreshJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        UserModel user=userService.findById(userDetails.getId());
        user.setEnabled(true);

       userService.saveUser(user);
        return ResponseEntity.ok( JwtResponse.builder()
                .token(jwt)
                .refreshToken(refreshJwt)
                .roles(roles)
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .build());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {


        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        UserModel user = new UserModel(
                signUpRequest.getFirstname(),
                signUpRequest.getLastname(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        String strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleService.findRoleByName(ERole.ROLE_CITOYEN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }
        user.setRoles(roles);
        userService.saveUser(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    @PostMapping("/refreshtoken") // Todo:RefreshToken
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenDto refreshToken){
        if(jwtUtils.validateJwtToken(refreshToken.getRefreshToken())==false)
            return new ResponseEntity<>(new MessageResponse("refreshToken not valid"), HttpStatus.BAD_REQUEST);


        UserDetailsImpl userPrincipal = (UserDetailsImpl) userDetailsService.loadUserByUsername(jwtUtils.getUserNameFromJwtToken(refreshToken.getRefreshToken()));
        String token = Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .claim("roles",userPrincipal.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();


        return new ResponseEntity<>(new JwtRefreshResponse(token),HttpStatus.OK);
    }
}
