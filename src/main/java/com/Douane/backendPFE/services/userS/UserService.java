package com.Douane.backendPFE.services.userS;

import com.Douane.backendPFE.DTOs.RoleDto;
import com.Douane.backendPFE.DTOs.request.SignupRequest;
import com.Douane.backendPFE.DTOs.request.UpdateUserDto;
import com.Douane.backendPFE.DTOs.response.MessageResponse;
import com.Douane.backendPFE.DTOs.response.UserDto;
import com.Douane.backendPFE.exceptions.BadRequestException;
import com.Douane.backendPFE.exceptions.EntityNotFoundException;
import com.Douane.backendPFE.models.user.ERole;
import com.Douane.backendPFE.models.user.Role;
import com.Douane.backendPFE.models.user.UserModel;
import com.Douane.backendPFE.repositories.userR.RoleRepository;
import com.Douane.backendPFE.repositories.userR.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
   @Autowired
   private PasswordEncoder passwordEncoder;
    public UserModel saveUser(UserModel user){
        return userRepository.save(user);
    }
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
    public Boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }
    public UserModel findById(Long id){
        UserModel user=userRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("User not found with id : "+id));
        return user;
    }
    public UserModel findByEmail(String email){
        UserModel user=userRepository.findByEmail(email)
                .orElseThrow(()-> new EntityNotFoundException("User not found with email : "+email));
        return user;
    }

    public List<UserDto> findAllUser(){
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_CITOYEN")))
                .map(userModel -> UserDto.builder()
                        .id(userModel.getId())
                        .firstName(userModel.getFirstname())
                        .lastName(userModel.getLastname())
                        .email(userModel.getEmail())
                        .roles(userModel.getRoles().stream().map(role -> role.getName().toString()).collect(Collectors.toList()))
                        .enabled(userModel.getEnabled())
                        .build())
                .collect(Collectors.toList());
    }


    public List<RoleDto> findAllRole(){
        return roleRepository.findAll()
                .stream()
                .map(
                       role -> RoleDto.builder()
                               .id(role.getId())
                               .name(role.getName().toString())
                                .build()
                )
                .collect(Collectors.toList());
    }


    public UserDto findUserDtoById(Long id ){
        UserModel user=userRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("User not found with id : "+id));
       UserDto userdto= UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstname())
                .lastName(user.getLastname())
                .email(user.getEmail())
                .enabled(user.getEnabled())
                .roles(user.getRoles().stream().map(role -> role.getName().toString()).collect(Collectors.toList()))
                .build();
        return userdto ;
    }


    public UserModel updateUser(Long userId, UpdateUserDto updateUserDto) throws BadRequestException {
        UserModel user = this.findById(userId);
        if (updateUserDto.getEmail() != null && !user.getEmail().equals(updateUserDto.getEmail())) {
            if (userRepository.existsByEmail(updateUserDto.getEmail())) {
                throw new BadRequestException("Email is already in use!");
            }
            user.setEmail(updateUserDto.getEmail());
        }

        if (updateUserDto.getFirstname() != null) {
            user.setFirstname(updateUserDto.getFirstname());
        }

        if (updateUserDto.getLastname() != null) {
            user.setLastname(updateUserDto.getLastname());
        }

        if (updateUserDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
        }

        return this.saveUser(user);
    }
    public List<UserDto> getUsersBySpecificRoles() {
        List<UserModel> filteredUsers = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().toString().equals("ROLE_CHEF_BUREAU") || role.getName().toString().equals("ROLE_INSPECTATEURBR")))
                .collect(Collectors.toList());
                return filteredUsers.stream()

                .map(
                        userModel -> UserDto.builder()
                                .id(userModel.getId())
                                .firstName(userModel.getFirstname())
                                .lastName(userModel.getLastname())
                                .email(userModel.getEmail())
                                .roles(userModel.getRoles().stream().map(role -> role.getName().toString()).collect(Collectors.toList()))
                                .enabled(userModel.getEnabled())
                                .build()

                )
                .collect(Collectors.toList());


    }




}
