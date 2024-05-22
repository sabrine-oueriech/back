package com.Douane.backendPFE.controllers;


import com.Douane.backendPFE.DTOs.request.DemandeVerifieDto;
import com.Douane.backendPFE.DTOs.response.MessageResponse;
import com.Douane.backendPFE.exceptions.EntityNotFoundException;
import com.Douane.backendPFE.models.bureau.Bureau;
import com.Douane.backendPFE.models.demandeAutorisation.Demande;
import com.Douane.backendPFE.models.demandeAutorisation.enums.Statue;
import com.Douane.backendPFE.models.user.UserModel;
import com.Douane.backendPFE.services.autorisationS.DemandeService;
import com.Douane.backendPFE.services.bureauS.BureauService;
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
@RequestMapping("/api/chefbr")
@PreAuthorize("hasRole('ROLE_CHEF_BUREAU') or hasRole('ROLE_ADMIN')")
public class ChefBRController {
    @Autowired
    private UserService userService;
    @Autowired
    private DemandeService demandeService;
    @Autowired
    private BureauService bureauService;
    @GetMapping("/dems")
    public ResponseEntity<?> allDemandeByUserValidate(Principal principal){
        try {
            UserModel user=userService.findByEmail(principal.getName());
            return new ResponseEntity<>(demandeService.getAllByValidateUser(user.getId()), HttpStatus.OK);
        }catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            Map<String, Object> response = new HashMap<>();
            response.put("error", errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }
    }

    @GetMapping("/demsverifier")
    public ResponseEntity<?> allDemandeverifier(Principal principal){
        UserModel uservalidat=userService.findByEmail(principal.getName());
        try {



            return new ResponseEntity<>(demandeService.getAllByStatueAndBureau(true,uservalidat.getBureau()) , HttpStatus.OK);
        }catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            Map<String, Object> response = new HashMap<>();
            response.put("error", errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }
    }
    @PostMapping("/validdemande")
    public ResponseEntity<?> validedemande(@RequestBody DemandeVerifieDto demandeVerifieDto , Principal principal){

        try {
            UserModel user=userService.findByEmail(principal.getName());
            return new ResponseEntity<>(demandeService.validationDemande(demandeVerifieDto.getStatue(),demandeVerifieDto.getId(),demandeVerifieDto.isArchive(),user) , HttpStatus.OK);
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


