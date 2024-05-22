package com.Douane.backendPFE.services.autorisationS;

import com.Douane.backendPFE.DTOs.request.DemandeDtoRequest;
import com.Douane.backendPFE.exceptions.BadRequestException;
import com.Douane.backendPFE.exceptions.EntityNotFoundException;
import com.Douane.backendPFE.models.autorisation.Autorisation;
import com.Douane.backendPFE.models.bureau.Bureau;
import com.Douane.backendPFE.models.demandeAutorisation.Demande;
import com.Douane.backendPFE.models.demandeAutorisation.Proprietaire;
import com.Douane.backendPFE.models.demandeAutorisation.enums.*;
import com.Douane.backendPFE.models.user.UserModel;
import com.Douane.backendPFE.models.vehiculle.Vehiculle;
import com.Douane.backendPFE.repositories.autoritationR.DemandeRepository;
import com.Douane.backendPFE.services.userS.UserService;
import com.Douane.backendPFE.services.vehiculleS.VehiculleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.List;

@Service
public class DemandeService {

    @Autowired
    private DemandeRepository demandeRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private VehiculleService vehiculleService;
    public Demande save(Demande demande){
        return demandeRepository.save(demande);
    }
    public Demande getByID(Long id){
        Demande demande=demandeRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Demande not found with id : "+id));
        return demande;
    }

    public List<Demande> allDemande(){
        return demandeRepository.findAll();
    }


    public List<Demande> getAllByCreateUser(Long id) {
        UserModel createUser=userService.findById(id);
        return demandeRepository.getAllByCreateUser(createUser);
    }

    public List<Demande> getAllDemande(Boolean archived){
        return demandeRepository.getAllByArchiveAuto(archived);
    }

    public List<Demande> getAllByVerifyUser(Long id) {
        UserModel verifyUser=userService.findById(id);
        return demandeRepository.getAllByVerifyUser(verifyUser);
    }

    public List<Demande> getAllByValidateUser(Long id) {
        UserModel validateUser=userService.findById(id);
        return demandeRepository.getAllByValidateUser(validateUser);
    }
    public List<Demande> getAllByStatueAndBureau(Boolean statue, Bureau bureau){
        return demandeRepository.getAllByIdfMarchandisesAndBureauAutorite(statue,bureau);
    }
    public List<Demande> getAllByStatueDemandeAndBureau(Statue statue, Bureau bureau){
        return demandeRepository.getAllByStatueDemAndBureauAutorite(statue,bureau);
    }

    public Demande validationDemande(String statue,Long id,boolean archiveAuto,UserModel user){
        Demande demande=this.getByID(id);
        demande.setValidateUser(user);
        if (demande.getIdfMarchandises()) {
            demande.setStatueDem(addStatue(statue));
            demande.setArchiveAuto(archiveAuto);
            //les chpm docume
            return this.save(demande);

        }else {
           throw new BadRequestException(" This demande is not  verified");
        }
    }
    public Demande verificationDemande(String statue,Long id,UserModel user){
        Demande demande=this.getByID(id);
        demande.setVerifyUser(user);
        if (demande.getStatueDem().equals(Statue.ENCOURS)) {
            if (demande.getType() != TypeDemande.PREMIERE_DEMANDE) {
                Vehiculle vehiculle = vehiculleService.getVehiculleByNumChassis(demande.getNumchassis());
                if (vehiculle.getNumImmatriculation().equals(demande.getNumImmatriculation()) || vehiculle.getMarque().equals(demande.getMarque())) {
                    demande.setStatueDem(addStatue(statue));
                    demande.setIdfMarchandises(true);
                    return this.save(demande);
                } else {
                    throw new IllegalArgumentException("Invalid vehicle information");
                }
            } else {
                demande.setStatueDem(addStatue(statue));
                demande.setIdfMarchandises(true);
                return this.save(demande);
            }
        }else {
            throw new BadRequestException(" This demande is already verified ");
        }
    }

    private Statue addStatue(String statue) {
        switch (statue){
            case "verifier":
                return Statue.VERIFIE;
            case "valid":
                return Statue.VALIDE;
            case "encours":
                return Statue.ENCOURS;
            default:
                throw new BadRequestException("Invalid Statue specified.");

        }
    }

    public List<Demande> getAllByStatueDem(Statue statueDem) {

        return demandeRepository.getAllByStatueDem(statueDem);
    }

    public Demande createDemande(DemandeDtoRequest demandeDtoRequest){

        LocalDateTime createdAt=LocalDateTime.now();
        Vehiculle vehiculle;
        if(!(getType(demandeDtoRequest.getTypeDemande()) == TypeDemande.PREMIERE_DEMANDE)){
             vehiculle = vehiculleService.getVehiculleByNumChassis(demandeDtoRequest.getNumchassis());
                vehiculle.setUpdateAt(LocalDateTime.now());
                vehiculleService.save(vehiculle);
        }else {

         vehiculle = Vehiculle.builder()
                    .numchassis(demandeDtoRequest.getNumchassis()) // verification si il existe
                    .numImmatriculation(demandeDtoRequest.getNumImmatriculation())
                    .marque(demandeDtoRequest.getMarque())
                    .complementMarque(demandeDtoRequest.getComplementMarque())
                    .createdAt(LocalDateTime.now())
                    .updateAt(LocalDateTime.now())
                    .build();
            vehiculleService.save(vehiculle);
        }
        String expirationAtString = demandeDtoRequest.getExpirationAt();
        LocalDateTime expirationAtDateTime = LocalDateTime.parse(expirationAtString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));


        Demande demande= Demande.builder()
                .type(getType(demandeDtoRequest.getTypeDemande()))
                .NumRef(generateReference(addAutorisation(demandeDtoRequest.getAutorisation())))
                .numchassis(demandeDtoRequest.getNumchassis())
                .numImmatriculation(demandeDtoRequest.getNumImmatriculation())
                .marque(demandeDtoRequest.getMarque())
                .codeDemande(demandeDtoRequest.getCodeDemande())
                .createdAt( createdAt)
                .expirationAt( validateDate(expirationAtDateTime,createdAt))
                .brRattachement(demandeDtoRequest.getBrRattachement())
                .brFrontalier(demandeDtoRequest.getBrFrontalier())
                .idfMarchandises(false)// verificaion
                .etatReserve(validationEtat(getType(demandeDtoRequest.getTypeDemande()), vehiculle.getUpdateAt()))
                .info(demandeDtoRequest.getInfo())
                .paysOrigine(demandeDtoRequest.getPaysOrigine())
                .nationalite(demandeDtoRequest.getNationalite())
                .motif(addMotif(demandeDtoRequest.getMotif()))
                .numDiptyque(generateRandomString(12))
                .autorisation(addAutorisation(demandeDtoRequest.getAutorisation()))
                .statueDem(Statue.ENCOURS)
                .archiveAuto(false)

                .build();
        vehiculleService.save(vehiculle);
        return demande;
    }


    public InterdictionsEtRestrictions getInterdiction(String inter){
        switch (inter){
            case "ica":
                return InterdictionsEtRestrictions.INCESSIBILITE_CINQ_ANS;
            case "incI":
                return InterdictionsEtRestrictions.INCESSIBILITE_ILLIMITE;
            case "pdt":
                return InterdictionsEtRestrictions.PAIEMENT_DROITS_TAXESst;
            case "vli":
                return InterdictionsEtRestrictions.VEHICULE_LOUAGE_INCESSIBLE;

            default:
                throw new BadRequestException("Invalid Interdictions AND Restrictions specified.");

        }
    }

    public MotifArret addMotifArret(String motifArrit) {
        switch (motifArrit){
            case "ddp":
                return MotifArret.DECES_DU_PROPRIETAIRE;
            case "eddi":
                return MotifArret.EXPIRATION_DU_DELAI_INCESSIBILITE;
            case "srsd":
                return MotifArret.SITUATION_REGULARISEE_SUITE_DESTRUCTION;
            case "dj":
                return MotifArret.DECISION_JUDICIAIRE;
            case "srsp":
                return MotifArret.SITUATION_REGULARISEE_SUITE_PAIEMENT;
            default:
                throw new BadRequestException("Invalid Motif Arret specified.");

        }
    }

    public List<Demande> getAllByAutorisation(Autorisation autorisation) {
        return demandeRepository.getAllByAutorisation(autorisation);
    }

    public List<Demande> getAllByEtatReserve(EtatReserve etatReserve) {
        return demandeRepository.getAllByEtatReserve(etatReserve);
    }

    //add motif
    public Motif addMotif(String motif){
        switch (motif){
            case "asc":
                return Motif.ASCENDANT;
            case "des":
                return Motif.DESCENDANT;
            case "fr":
                return Motif.FRERE_SOEUR;
            case "emp":
                return Motif.EMPLOYE;
            case "etE" :
                return Motif.ETUDIANT_ETRANGER;
            case "libre" :
                return Motif.ETRANGER_EMPLOYE_LIBRE;
            case "empc":
                return  Motif.EMPLOYE_CONVENTION_COOPERATION_BILATERALE;
            default:
                throw new BadRequestException("Invalid Motif specified.");

        }
    }
    //validation Etat Reserve
    public Set<EtatReserve> validationEtat(TypeDemande type, LocalDateTime dateUpdate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            Set<EtatReserve> etatsReserves = new HashSet<>();
        LocalDate currentDate = LocalDate.now();
        LocalDate updateDate = dateUpdate.toLocalDate();
        LocalDate dateValid = updateDate.plus(3, ChronoUnit.MONTHS);
        switch (type){
                case PREMIERE_DEMANDE:
                    if (currentDate.equals(updateDate)){
                        etatsReserves.add(EtatReserve.DIPTYQUE_VERT);

                    }else {
                        throw new IllegalArgumentException("Date d'expiration invalide !");
                    }
                   break;
                case DEMANDE_RECTIFICATION_AUTORISATION:
                    if (true){ //pour verifie les autre demande comme ca on change le contuni de if par true ce condution on a besonin ce lon les regle dautoriton avec le douane on ne peut pas crea un demende DEMANDE_RECTIFICATION_AUTORISATION pour la priemer foi de creation de demande la primier est de type primier demande
                        etatsReserves.add(EtatReserve.DIPTYQUE_JAUNE);

                    }else {
                        throw new IllegalArgumentException("Date d'expiration invalide !");
                    }
                    break;
                case DEMANDE_RENOUVELLEMENT_AUTORISATION:
                    if (currentDate.isAfter(dateValid)){
                        etatsReserves.add(EtatReserve.DIPTYQUE_ROUGE);
                        etatsReserves.add(EtatReserve.PAIEMENT_TAXES_CIRCULATION);
                        etatsReserves.add(EtatReserve.PAIEMENT_TAXES_CIRCULATION);
                        etatsReserves.add(EtatReserve.CONTRAT_ASSURANCE_VALIDE);

                    }else {
                        throw new IllegalArgumentException("Date d'expiration invalide !");
                    }
                    break;


            }
            return etatsReserves;
    }
    //determine Type de Demande
    public TypeDemande getType(String type){
        switch (type) {
            case "premierD":
                return TypeDemande.PREMIERE_DEMANDE;
            case "dArret":
                return TypeDemande.DEMANDE_ARRET_DECISION;
            case "dRectification":
                return TypeDemande.DEMANDE_RECTIFICATION_AUTORISATION;
            case "dRenouvellement":
                return TypeDemande.DEMANDE_RENOUVELLEMENT_AUTORISATION;
            default:
                throw new BadRequestException("Invalid Type Demande specified.");
        }
    }

    // validation de Date
    private LocalDateTime validateDate(LocalDateTime expirationAt, LocalDateTime createdAt) {
        if (expirationAt == null) {
            throw new IllegalArgumentException("Date d'expiration invalide !");
        }

        if (expirationAt.isBefore(createdAt.plusMonths(3))) {
            throw new IllegalArgumentException("Il doit y avoir au moins 3 mois entre la date de création et la date d'expiration !");
        }

        if (createdAt.getMonthValue() >= 4 && createdAt.getMonthValue() <= 12) {
            LocalDateTime minimumExpirationDate = createdAt.plusMonths(3);

            LocalDateTime maximumExpirationDate = createdAt.plusMonths(9);

            if (expirationAt.isBefore(minimumExpirationDate) || expirationAt.isAfter(maximumExpirationDate)) {
                throw new IllegalArgumentException("Date d'expiration invalide !");
            }
        }

        return expirationAt;
    }
    // get Autorisation
    public Autorisation addAutorisation(String auto){
        switch (auto) {
            case "1":
                return Autorisation.VH0100;
            case "2":
                return Autorisation.VH0200;
            case "3":
                return Autorisation.VH0300;
            case "4":
                return Autorisation.VH0400;
            default:
                throw new BadRequestException("Invalid Autorisation specified.");
        }
    }
    //Methode generate reference auto_randomStringAndNum
    public String generateReference(Autorisation autorisation) {
        String autorisationString = autorisation.toString();
        String randomPart = generateRandomString(8);
        return autorisationString + "_" + randomPart;
    }
    //Methode generate randomStringAndNumDeLength  8
    private String generateRandomString(int num) {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "").substring(0, num);
    }
    public void deleteDemande(Long id) throws EntityNotFoundException {
        if (!demandeRepository.existsById(id)) {
            throw new EntityNotFoundException("Demande not found with id : " + id);
        }
        demandeRepository.deleteById(id);
    }
}
