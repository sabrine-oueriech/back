package com.Douane.backendPFE.repositories.vehiculleR;

import com.Douane.backendPFE.models.vehiculle.Vehiculle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehiculleRepository extends JpaRepository<Vehiculle,Long> {

    @Query("select v from Vehiculle v where v.numchassis = ?1")
    Optional<Vehiculle> findVehiculleByNumchassis(String num);
}
