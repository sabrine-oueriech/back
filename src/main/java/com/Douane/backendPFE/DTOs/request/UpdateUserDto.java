package com.Douane.backendPFE.DTOs.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {
    private String email;
    private String firstname;
    private String lastname;
    private String password;
}
