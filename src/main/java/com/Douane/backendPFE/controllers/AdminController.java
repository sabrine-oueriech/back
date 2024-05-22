package com.Douane.backendPFE.controllers;

import com.Douane.backendPFE.DTOs.request.BureauDto;
import com.Douane.backendPFE.DTOs.request.SignupRequest;
import com.Douane.backendPFE.DTOs.response.MessageResponse;
import com.Douane.backendPFE.DTOs.response.UserDto;
import com.Douane.backendPFE.exceptions.BadRequestException;
import com.Douane.backendPFE.models.bureau.Bureau;
import com.Douane.backendPFE.models.bureau.ETache;
import com.Douane.backendPFE.models.demandeAutorisation.Demande;
import com.Douane.backendPFE.models.user.ERole;
import com.Douane.backendPFE.models.user.Role;
import com.Douane.backendPFE.models.user.UserModel;
import com.Douane.backendPFE.services.autorisationS.DemandeService;
import com.Douane.backendPFE.services.bureauS.BureauService;
import com.Douane.backendPFE.services.userS.RoleService;
import com.Douane.backendPFE.services.userS.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
@CrossOrigin()
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private RoleService roleService;
    @Autowired
    private BureauService bureauService;
    @Autowired
    private DemandeService demandeService;

    @PostMapping("/createEmp")
    public ResponseEntity<?> createEmployer(@Valid @RequestBody SignupRequest signUpRequest) {


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
        Bureau bureau = bureauService.getById(signUpRequest.getIdBureau());
        String strRoles = signUpRequest.getRoles();

        Set<Role> roles = new HashSet<>();
        // Role userRole = roleService.findRoleByName(ERole.ROLE_CITOYEN);

        try {


            switch (strRoles) {
                case "chef_Br":
                    Role adminRole = roleService.findRoleByName(ERole.ROLE_CHEF_BUREAU);
                    roles.add(adminRole);
                    // roles.add(userRole);
                    break;
                case "inspectateur_Br":
                    Role hostRole = roleService.findRoleByName(ERole.ROLE_INSPECTATEURBR);
                    roles.add(hostRole);
                    // roles.add(userRole);
                    break;
                default:
                    throw new BadRequestException("Invalid role specified.");
            }
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Role not found!"));
        }
        user.setRoles(roles);
        user.setBureau(bureau);
        //  bureau.getUsers().add(user);
        userService.saveUser(user);
        //   bureauService.save(bureau);
        return ResponseEntity.ok(new MessageResponse("Employer registered successfully!"));

    }

    //creation de bureau
    @PostMapping("/bureau")
    public ResponseEntity<?> cerationBureau(@RequestBody BureauDto bureauDto) {
        Bureau bureau;
        Set<String> taches = bureauDto.getTaches();
        Set<ETache> Etaches = new HashSet<>();

        try {
            taches.forEach(tache -> {
                switch (tache) {
                    case "etude":
                        Etaches.add(ETache.ETUDE);
                        break;
                    case "Accord":
                        Etaches.add(ETache.ACCORD);
                        break;
                    default:
                        throw new BadRequestException("Invalid Tache specified.");

                }
            });
            bureau = Bureau.builder()
                    .name(bureauDto.getName())
                    .taches(Etaches)
                    .build();
            return new ResponseEntity<>(bureauService.save(bureau), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new BadRequestException("Invalid request");
        }
    }

    @PutMapping("/bureau/{id}")
    public ResponseEntity<?> updateBureau(@PathVariable Long id, @RequestBody BureauDto bureauDto) {
        Bureau bureau = bureauService.getById(id);
        try {
            if (bureauDto.getName() != null && !bureauDto.getName().isEmpty()) {
                bureau.setName(bureauDto.getName());
            }
            if (bureauDto.getTaches() != null) {
                Set<String> taches = bureauDto.getTaches();
                Set<ETache> Etaches = new HashSet<>();

                taches.forEach(tache -> {
                    switch (tache) {
                        case "etude":
                            Etaches.add(ETache.ETUDE);
                            break;
                        case "Accord":
                            Etaches.add(ETache.ACCORD);
                            break;
                        default:
                            throw new BadRequestException("Invalid Tache specified.");

                    }
                });
                bureau.setTaches(Etaches);

            }
            return new ResponseEntity<>(bureauService.save(bureau), HttpStatus.OK);
        } catch (Exception ex) {
            throw new BadRequestException("Invalid request");
        }
    }

    @GetMapping("/bureaus")
    public ResponseEntity<?> getAllBureau() {

        return new ResponseEntity<>(bureauService.getAll(), HttpStatus.OK);

    }

    @GetMapping("/demnonarch")
    public ResponseEntity<?> getAllDemandsNonArchived() {
        try {
            List<Demande> demands = demandeService.getAllDemande(false);
            return new ResponseEntity<>(demands, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to retrieve demands: " + e.getMessage()));
        }
    }
    //All demande archived

    @GetMapping("/demands")
    public ResponseEntity<?> getAllDemands() {
        try {
            List<Demande> demands = demandeService.getAllDemande(true);
            return new ResponseEntity<>(demands, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to retrieve demands: " + e.getMessage()));
        }
    }


    @DeleteMapping("/delete/{bureauId}")
    public ResponseEntity<?> deleteBureau(@PathVariable Long bureauId) {
        try {
            bureauService.deleteById(bureauId);
            return ResponseEntity.ok(new MessageResponse("Bureau deleted successfully!"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: Bureau not found with id: " + bureauId));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to delete bureau: " + e.getMessage()));
        }
    }

}