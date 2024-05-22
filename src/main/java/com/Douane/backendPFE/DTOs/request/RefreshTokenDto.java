package com.Douane.backendPFE.DTOs.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDto {
    @NotBlank(message = "refresh token is required")
    private String refreshToken ;

}
