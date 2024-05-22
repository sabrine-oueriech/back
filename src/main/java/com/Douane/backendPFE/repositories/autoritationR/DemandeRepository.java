package com.Douane.backendPFE.repositories.autoritationR;

import com.Douane.backendPFE.models.autorisation.Autorisation;
import com.Douane.backendPFE.models.bureau.Bureau;
import com.Douane.backendPFE.models.demandeAutorisation.Demande;
import com.Douane.backendPFE.models.demandeAutorisation.enums.EtatReserve;
import com.Douane.backendPFE.models.demandeAutorisation.enums.Statue;
import com.Douane.backendPFE.models.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeRepository extends JpaRepository<Demande,Long> {
    @Query("select d from Demande d where d.createUser = ?1")
    List<Demande> getAllByCreateUser(UserModel createUser);

    @Query("select d from Demande d where d.verifyUser = ?1")
    List<Demande> getAllByVerifyUser(UserModel verifyUser);

    @Query("select d from Demande d where d.validateUser = ?1")
    List<Demande> getAllByValidateUser(UserModel validateUser);

    @Query("select d from Demande d where d.statueDem = ?1")
    List<Demande> getAllByStatueDem(Statue statueDem);

    @Query("select d from Demande d where d.autorisation = ?1")
    List<Demande> getAllByAutorisation(Autorisation autorisation);

    @Query("select d from Demande d where d.etatReserve = ?1")
    List<Demande> getAllByEtatReserve(EtatReserve etatReserve);
    @Query("select d from Demande d where d.archiveAuto = ?1")
    List<Demande> getAllByArchiveAuto(boolean archive);

    @Query("select d from Demande d where d.idfMarchandises = ?1 and d.bureauAutorite = ?2")
    List<Demande> getAllByIdfMarchandisesAndBureauAutorite(boolean verifier, Bureau bureauAutorite);
    @Query("select d from Demande d where d.statueDem = ?1 and d.bureauAutorite = ?2")
    List<Demande> getAllByStatueDemAndBureauAutorite(Statue statue, Bureau bureau);
}
