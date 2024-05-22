package com.Douane.backendPFE.DTOs.request;

import com.Douane.backendPFE.models.bureau.ETache;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BureauDto {

    private String name;
    private Set<String> taches;
}
