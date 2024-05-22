package com.Douane.backendPFE.DTOs.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandeVerifieDto {
    private String statue;
    private Long id;
    private boolean archive;
}
