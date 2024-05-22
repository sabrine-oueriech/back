package com.Douane.backendPFE.controllers;

import com.Douane.backendPFE.DTOs.request.DemandeDtoRequest;
import com.Douane.backendPFE.DTOs.request.UpdateUserDto;
import com.Douane.backendPFE.DTOs.response.MessageResponse;
import com.Douane.backendPFE.DTOs.response.UserDto;
import com.Douane.backendPFE.Views.View;
import com.Douane.backendPFE.exceptions.BadRequestException;
import com.Douane.backendPFE.models.autorisation.Autorisation;
import com.Douane.backendPFE.models.bureau.Bureau;
import com.Douane.backendPFE.models.demandeAutorisation.Demande;
import com.Douane.backendPFE.models.demandeAutorisation.DocumentMetadata;
import com.Douane.backendPFE.models.demandeAutorisation.ModeDapurement;
import com.Douane.backendPFE.models.demandeAutorisation.Proprietaire;
import com.Douane.backendPFE.models.demandeAutorisation.enums.TypeDemande;
import com.Douane.backendPFE.models.user.Role;
import com.Douane.backendPFE.models.user.UserModel;
import com.Douane.backendPFE.models.vehiculle.Vehiculle;
import com.Douane.backendPFE.repositories.userR.UserRepository;
import com.Douane.backendPFE.services.autorisationS.DemandeService;
import com.Douane.backendPFE.services.bureauS.BureauService;
import com.Douane.backendPFE.services.flickr.FlickrService;
import com.Douane.backendPFE.services.userS.UserService;
import com.Douane.backendPFE.services.vehiculleS.VehiculleService;
import com.fasterxml.jackson.annotation.JsonView;
import com.flickr4java.flickr.FlickrException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@CrossOrigin()
@RequestMapping("/api/user")
@PreAuthorize("hasRole('ROLE_CITOYEN') or hasRole('ROLE_ADMIN') or hasRole('ROLE_INSPECTATEURBR') or hasRole('ROLE_CHEF_BUREAU')")
public class UserController {
    @Autowired
    private DemandeService demandeService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehiculleService vehiculleService;
    @Autowired
    private BureauService bureauService;
    @Autowired
    private UserService userService;
    @Autowired
    private FlickrService flickrService;

    @PostMapping("/dem")
    @JsonView(View.base.class)
    public ResponseEntity<?> createDemande(
            @RequestBody DemandeDtoRequest demandeDtoRequest,
            Principal principal) {

        Demande demande;
        Vehiculle vehiculle;
        Bureau bureau;
        try {
            demande = demandeService.createDemande(demandeDtoRequest);
            vehiculle = vehiculleService.getVehiculleByNumChassis(demandeDtoRequest.getNumchassis());
            if (demandeDtoRequest.getAutorisation().equals("1") || demandeDtoRequest.getAutorisation().equals("2") || demandeDtoRequest.getAutorisation().equals("3") || demandeDtoRequest.getAutorisation().equals("4")) {
                bureau = bureauService.getById(1L);
                demande.setBureauAutorite(bureau);
            }

            UserModel userCreator = userService.findByEmail(principal.getName());
            demande.setDesiDemande(userCreator.getLastname() + "_" + userCreator.getFirstname());

            demande.setCreateUser(userCreator);

            demande.setVehiculle(vehiculle);
            if (demandeDtoRequest.getAutorisation().equals("3") || demandeDtoRequest.getAutorisation().equals("4")) {
                demande.setMotifArret(demandeService.addMotifArret(demandeDtoRequest.getMotifArret()));
            }
            if (demandeDtoRequest.getAutorisation().equals("2") || demandeDtoRequest.getAutorisation().equals("3") || demandeDtoRequest.getAutorisation().equals("4")) {
                demande.setDeclartionPrecedente(demandeDtoRequest.getDeclartionPrecedente());
                ModeDapurement modeDapurement = demande.getModeDapurement();
                if (modeDapurement == null) {
                    modeDapurement = new ModeDapurement();
                    demande.setModeDapurement(modeDapurement);
                }
                if (demandeDtoRequest.getQuantiteQCS() != null && demandeDtoRequest.getPoidsNet() != null && demandeDtoRequest.getValeurDeviseEtrangere() != null && demandeDtoRequest.getValeurDT() != null) {
                    modeDapurement.setQuantiteQCS(demandeDtoRequest.getQuantiteQCS());
                    modeDapurement.setPoidsNet(demandeDtoRequest.getPoidsNet());
                    modeDapurement.setValeurDeviseEtrangere(demandeDtoRequest.getValeurDeviseEtrangere());
                    modeDapurement.setValeurDT(demandeDtoRequest.getValeurDT());
                }
                demande.setInterdictionsEtRestrictions(demandeService.getInterdiction(demandeDtoRequest.getInterdictionsEtRestrictions()));
            }
            if (demandeDtoRequest.getAutorisation().equals("4")) {
                demande.setDescription(demandeDtoRequest.getDescription());

                Proprietaire proprietaire = demande.getProprietaire();
                if (proprietaire == null) {
                    proprietaire = new Proprietaire();
                    demande.setProprietaire(proprietaire);
                }
                if (demandeDtoRequest.getNom() != null && demandeDtoRequest.getPrenom() != null && demandeDtoRequest.getCinCarteSejour() != null) {
                    proprietaire.setNom(demandeDtoRequest.getNom());
                    proprietaire.setPrenom(demandeDtoRequest.getPrenom());
                    proprietaire.setCinCarteSejour(demandeDtoRequest.getCinCarteSejour());

                }
            }
            if (demandeDtoRequest.getAutorisation().equals("3")) {
                demande.setGuaranteeReferenceNumber(demandeDtoRequest.getGuaranteeReferenceNumber());
                demande.setMontantGarantie(demandeDtoRequest.getMontantGarantie());
            }
            if (demandeDtoRequest.getAutorisation().equals("2")) {
                String echianceDate = demandeDtoRequest.getEchianceDate();
                LocalDateTime echianceDateTime = LocalDateTime.parse(echianceDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                demande.setEchianceDate(echianceDateTime);
            }


            // userService.saveUser(userCreator);
            demandeService.save(demande);
            return new ResponseEntity<>(demande, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            Map<String, Object> response = new HashMap<>();
            response.put("error", errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }
    }

    @PostMapping("/demupdate/{id}")
    @JsonView(View.base.class)
    public ResponseEntity<?> updateDemande(@PathVariable Long id,
                                           @RequestBody DemandeDtoRequest demandeDtoRequest,
                                           Principal principal) {
        Demande demande = demandeService.getByID(id);
        Vehiculle vehiculle;
        try {
            demande = demandeService.createDemande(demandeDtoRequest);
            vehiculle = vehiculleService.getVehiculleByNumChassis(demandeDtoRequest.getNumchassis());


            demande.setVehiculle(vehiculle);
            if (demandeDtoRequest.getAutorisation().equals("3") || demandeDtoRequest.getAutorisation().equals("4")) {
                demande.setMotifArret(demandeService.addMotifArret(demandeDtoRequest.getMotifArret()));
            }
            if (demandeDtoRequest.getAutorisation().equals("2") || demandeDtoRequest.getAutorisation().equals("3") || demandeDtoRequest.getAutorisation().equals("4")) {
                demande.setDeclartionPrecedente(demandeDtoRequest.getDeclartionPrecedente());
                ModeDapurement modeDapurement = demande.getModeDapurement();
                if (modeDapurement == null) {
                    modeDapurement = new ModeDapurement();
                    demande.setModeDapurement(modeDapurement);
                }
                if (demandeDtoRequest.getQuantiteQCS() != null && demandeDtoRequest.getPoidsNet() != null && demandeDtoRequest.getValeurDeviseEtrangere() != null && demandeDtoRequest.getValeurDT() != null) {
                    modeDapurement.setQuantiteQCS(demandeDtoRequest.getQuantiteQCS());
                    modeDapurement.setPoidsNet(demandeDtoRequest.getPoidsNet());
                    modeDapurement.setValeurDeviseEtrangere(demandeDtoRequest.getValeurDeviseEtrangere());
                    modeDapurement.setValeurDT(demandeDtoRequest.getValeurDT());
                }
                demande.setInterdictionsEtRestrictions(demandeService.getInterdiction(demandeDtoRequest.getInterdictionsEtRestrictions()));
            }
            if (demandeDtoRequest.getAutorisation().equals("4")) {
                demande.setDescription(demandeDtoRequest.getDescription());

                Proprietaire proprietaire = demande.getProprietaire();
                if (proprietaire == null) {
                    proprietaire = new Proprietaire();
                    demande.setProprietaire(proprietaire);
                }
                if (demandeDtoRequest.getNom() != null && demandeDtoRequest.getPrenom() != null && demandeDtoRequest.getCinCarteSejour() != null) {
                    proprietaire.setNom(demandeDtoRequest.getNom());
                    proprietaire.setPrenom(demandeDtoRequest.getPrenom());
                    proprietaire.setCinCarteSejour(demandeDtoRequest.getCinCarteSejour());

                }
            }
            if (demandeDtoRequest.getAutorisation().equals("3")) {
                demande.setGuaranteeReferenceNumber(demandeDtoRequest.getGuaranteeReferenceNumber());
                demande.setMontantGarantie(demandeDtoRequest.getMontantGarantie());
            }
            if (demandeDtoRequest.getAutorisation().equals("2")) {
                String echianceDate = demandeDtoRequest.getEchianceDate();
                LocalDateTime echianceDateTime = LocalDateTime.parse(echianceDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                demande.setEchianceDate(echianceDateTime);
            }


            demandeService.save(demande);
            return new ResponseEntity<>(demande, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            Map<String, Object> response = new HashMap<>();
            response.put("error", errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }
    }

    @GetMapping("/dems")
    @JsonView(View.base.class)
    public ResponseEntity<?> allDemandeByUserCreated(Principal principal) {
        try {
            UserModel user = userService.findByEmail(principal.getName());
            return new ResponseEntity<>(demandeService.getAllByCreateUser(user.getId()), HttpStatus.OK);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            Map<String, Object> response = new HashMap<>();
            response.put("error", errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        try {
            UserModel userProfile = userService.findByEmail(principal.getName());

            return new ResponseEntity<>(userProfile, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/uploadphoto")
    public ResponseEntity<String> uploadPhoto(
            MultipartFile profilePhoto,
            //@RequestParam("title") String title,
            Principal principal
    ) {
        try {
            UserModel userConnect = userService.findByEmail(principal.getName());
            if (profilePhoto != null) {
                InputStream profilePhotoInputStream = profilePhoto.getInputStream();
                String profilePhotoUrl = flickrService.savePhoto(profilePhotoInputStream, "profile");
                userConnect.setPhotoUrl(profilePhotoUrl);
                userService.saveUser(userConnect);
                return ResponseEntity.ok().body("Uploaded and updated user's profile photo URL: " + profilePhotoUrl);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"Status\": \" Photo Not Existed \"}");

            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading and updating photos.");
        }
    }

    @PostMapping("/{id}/addphotos")
    public ResponseEntity<String> addPhotos(
            @PathVariable Long id,
            @RequestParam("cinUrl") MultipartFile cinUrl,
            @RequestParam("passportUrl") MultipartFile passportUrl,
            @RequestParam("carteGriseUrl") MultipartFile carteGriseUrl,
            @RequestParam("assuranceUrl") MultipartFile assuranceUrl,
            @RequestParam("justPossessionUrl") MultipartFile justPossessionUrl,
            @RequestParam("autrJustUrl") MultipartFile autrJustUrl,
            @RequestParam("docTaxeCirculationUrl") MultipartFile docTaxeCirculationUrl,
            @RequestParam("docPlaqueImmatriculation") MultipartFile docPlaqueImmatriculation,
            @RequestParam("docContratAssurance") MultipartFile docContratAssurance,
            @RequestParam("guaranteeDocumentUrl") MultipartFile guaranteeDocumentUrl,
            Principal principal
    ) {
        try {
            Demande dem = demandeService.getByID(id);
            UserModel userConnect = userService.findByEmail(principal.getName());
            if (dem != null && Objects.equals(userConnect, dem.getCreateUser())) {
                DocumentMetadata documentMetadata = dem.getDocumentMetadata();
                if (documentMetadata == null) {
                    documentMetadata = new DocumentMetadata();
                    dem.setDocumentMetadata(documentMetadata);
                }

                if (cinUrl != null && !cinUrl.isEmpty()) {
                    InputStream profilePhotoInputStream = cinUrl.getInputStream();
                    String cinPhoto = flickrService.savePhoto(profilePhotoInputStream, "profile");
                    documentMetadata.setCinUrl(cinPhoto);
                }

                if (passportUrl != null && !passportUrl.isEmpty()) {
                    InputStream passportInputStream = passportUrl.getInputStream();
                    String passportPhoto = flickrService.savePhoto(passportInputStream, "passport");
                    documentMetadata.setPassportUrl(passportPhoto);
                }

                if (carteGriseUrl != null && !carteGriseUrl.isEmpty()) {
                    InputStream carteGriseInputStream = carteGriseUrl.getInputStream();
                    String carteGrisePhoto = flickrService.savePhoto(carteGriseInputStream, "carteGrise");
                    documentMetadata.setCarteGriseUrl(carteGrisePhoto);
                }

                if (assuranceUrl != null && !assuranceUrl.isEmpty()) {
                    InputStream assuranceInputStream = assuranceUrl.getInputStream();
                    String assurancePhoto = flickrService.savePhoto(assuranceInputStream, "assurance");
                    documentMetadata.setAssuranceUrl(assurancePhoto);
                }

                if (justPossessionUrl != null && !justPossessionUrl.isEmpty()) {
                    InputStream justPossessionInputStream = justPossessionUrl.getInputStream();
                    String justPossessionPhoto = flickrService.savePhoto(justPossessionInputStream, "justPossession");
                    documentMetadata.setJustPossessionUrl(justPossessionPhoto);
                }

                if (autrJustUrl != null && !autrJustUrl.isEmpty()) {
                    InputStream autrJustInputStream = autrJustUrl.getInputStream();
                    String autrJustPhoto = flickrService.savePhoto(autrJustInputStream, "autrJust");
                    documentMetadata.setAutrJustUrl(autrJustPhoto);
                }
                if (dem.getType().equals(TypeDemande.DEMANDE_RENOUVELLEMENT_AUTORISATION)) {
                    if (docTaxeCirculationUrl != null && !docTaxeCirculationUrl.isEmpty()) {
                        InputStream docTaxeCirculationInputStream = docTaxeCirculationUrl.getInputStream();
                        String docTaxeCirculationPhoto = flickrService.savePhoto(docTaxeCirculationInputStream, "docTaxeCirculation");
                        documentMetadata.setDocTaxeCirculationUrl(docTaxeCirculationPhoto);
                    }

                    if (docPlaqueImmatriculation != null && !docPlaqueImmatriculation.isEmpty()) {
                        InputStream docPlaqueImmatriculationInputStream = docPlaqueImmatriculation.getInputStream();
                        String docPlaqueImmatriculationPhoto = flickrService.savePhoto(docPlaqueImmatriculationInputStream, "docPlaqueImmatriculation");
                        documentMetadata.setDocPlaqueImmatriculation(docPlaqueImmatriculationPhoto);
                    }

                    if (docContratAssurance != null && !docContratAssurance.isEmpty()) {
                        InputStream docContratAssuranceInputStream = docContratAssurance.getInputStream();
                        String docContratAssurancePhoto = flickrService.savePhoto(docContratAssuranceInputStream, "docContratAssurance");
                        documentMetadata.setDocContratAssurance(docContratAssurancePhoto);
                    }
                }

                if (guaranteeDocumentUrl != null && !guaranteeDocumentUrl.isEmpty()) {
                    InputStream guaranteeDocumentInputStream = guaranteeDocumentUrl.getInputStream();
                    String guaranteeDocumentPhoto = flickrService.savePhoto(guaranteeDocumentInputStream, "guaranteeDocument");
                    dem.setGuaranteeDocumentUrl(guaranteeDocumentPhoto);
                }
                demandeService.save(dem);
                return ResponseEntity.ok().body("Uploaded and updated demande photos successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"Status\": \"Unauthorized access\"}");
            }
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading file.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading and updating photos.");
        }
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<?> updateProfile( @RequestBody UpdateUserDto updateUserDto, Principal principal) {
        try {
            UserModel user = userService.findByEmail(principal.getName());
            UserModel updatedUser = userService.updateUser(user.getId(), updateUserDto);
            return ResponseEntity.ok(updatedUser);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("User not found."));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed to update user: " + e.getMessage()));
        }
    }

//    @PostMapping("/updateProfile")
//    public ResponseEntity<?> updateProfile(@RequestBody UpdateUserDto updateUserDto, Principal principal) {
//        try {
//            UserModel user = userService.findByEmail(principal.getName());
//            UserModel updatedUser = userService.updateUser(user.getId(), updateUserDto);
//            return ResponseEntity.ok(updatedUser);
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("User not found."));
//        } catch (BadRequestException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed to update user: " + e.getMessage()));
//        }
//    }

    @GetMapping("/users/specific-roles")
    public ResponseEntity<List<UserDto>> getUsersBySpecificRoles() {
        try {
            List<UserDto> users = userService.getUsersBySpecificRoles();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/dem/{id}")
    public ResponseEntity<?> deleteDemandeById(@PathVariable Long id) {
        try {
            demandeService.deleteDemande(id);
            return ResponseEntity.ok().body(new MessageResponse("Demande successfully deleted"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Demande not found with id: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed to delete the demande: " + e.getMessage()));
        }
    }

}


