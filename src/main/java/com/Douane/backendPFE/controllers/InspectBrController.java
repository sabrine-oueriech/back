package com.Douane.backendPFE.controllers;

import com.Douane.backendPFE.DTOs.request.DemandeVerifieDto;
import com.Douane.backendPFE.DTOs.response.MessageResponse;
import com.Douane.backendPFE.exceptions.EntityNotFoundException;
import com.Douane.backendPFE.models.demandeAutorisation.Demande;
import com.Douane.backendPFE.models.demandeAutorisation.enums.Statue;
import com.Douane.backendPFE.models.user.UserModel;
import com.Douane.backendPFE.repositories.autoritationR.DemandeRepository;
import com.Douane.backendPFE.services.autorisationS.DemandeService;
import com.Douane.backendPFE.services.userS.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
@CrossOrigin()
@RestController
@RequestMapping("/api/inspectateur")
@PreAuthorize("hasRole('ROLE_INSPECTATEURBR')")
public class InspectBrController {

    @Autowired
    private UserService userService;
    @Autowired
    private DemandeService demandeService;
    @Autowired
    private DemandeRepository demandeRepository;
    @GetMapping("/demandes")
    public ResponseEntity<?> allDemandeByUserVerifier(Principal principal){
        UserModel user=userService.findByEmail(principal.getName());
        try {

            return new ResponseEntity<>(demandeService.getAllByVerifyUser(user.getId()), HttpStatus.OK);
        }catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            Map<String, Object> response = new HashMap<>();
            response.put("error", errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }
    }

    @PostMapping("/demverifie")
    public ResponseEntity<?> verifieDemande(@RequestBody DemandeVerifieDto demandeVerifieDto ,Principal principal ) {
        try {
            UserModel user=userService.findByEmail(principal.getName());

            return new ResponseEntity<>(demandeService.verificationDemande(demandeVerifieDto.getStatue(), demandeVerifieDto.getId() , user), HttpStatus.OK);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            Map<String, Object> response = new HashMap<>();
            response.put("error", errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }
    }
    // liste de demande affiche a user verifie pour chwoisur la quell demande en verifier
    @GetMapping("/demandesencours")
    public ResponseEntity<?> allDemandeEncours(Principal principal){
        UserModel uservalidat=userService.findByEmail(principal.getName());
        try {

            return new ResponseEntity<>(demandeService.getAllByStatueDemandeAndBureau(Statue.ENCOURS,uservalidat.getBureau()) , HttpStatus.OK);
        }catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            Map<String, Object> response = new HashMap<>();
            response.put("error", errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }
    }
    @GetMapping("/demande/{id}")
    public ResponseEntity<?> getDemandeById(@PathVariable Long id) {
        try {
            Demande demande = demandeService.getByID(id);
            return new ResponseEntity<>(demande, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: Demande not found with id: " + id));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to retrieve demande: " + e.getMessage()));
        }
    }


}
