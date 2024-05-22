package com.Douane.backendPFE.DTOs.response;

import com.Douane.backendPFE.models.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    private String firstName;
    private String lastName;
    private Boolean enabled;
    private String email;
    private List<String> roles;


}
