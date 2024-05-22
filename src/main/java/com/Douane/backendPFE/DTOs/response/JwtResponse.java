package com.Douane.backendPFE.DTOs.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
public class JwtResponse {
    private String token,refreshToken;
    private List<String> roles;
    private String firstname;
    private String lastname;
    private String email;
}
