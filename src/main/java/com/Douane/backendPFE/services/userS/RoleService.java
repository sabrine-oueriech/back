package com.Douane.backendPFE.services.userS;

import com.Douane.backendPFE.exceptions.EntityNotFoundException;
import com.Douane.backendPFE.models.user.ERole;
import com.Douane.backendPFE.models.user.Role;
import com.Douane.backendPFE.repositories.userR.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role findRoleByName(ERole name){
        Role role=roleRepository.findRoleByName(name)
                .orElseThrow(()->new EntityNotFoundException("role not found with name :"+name));
        return role;
    }
}
