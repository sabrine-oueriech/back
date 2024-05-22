package com.Douane.backendPFE.DTOs.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

import static com.Douane.backendPFE.config.Constraint.*;
@Data
public class SignupRequest {
    @NotNull
    @Length(min = FIRSTNAME_MIN, max = FIRSTNAME_MAX)
   // @Pattern(regexp = FIRSTNAME_PATTERN, message = FIRSTNAME_PATTERN_MESSAGE)
    private  String firstname;
    @NotNull
    @Length(min = LASTNAME_MIN, max = LASTNAME_MAX)
   // @Pattern(regexp = LASTNAME_PATTERN, message = LASTNAME_PATTERN_MESSAGE)
    private  String lastname;
    @NotNull
    @Email
    private String email;
    @NotNull
    @Length(min = PASSWORD_MIN, max = PASSWORD_MAX)
    //@Pattern(regexp = PASSWORD_PATTERN, message = PASSWORD_PATTERN_MESSAGE)

    private String password;

    private Long idBureau;

    private String roles;
}
