package com.Douane.backendPFE.services.vehiculleS;

import com.Douane.backendPFE.exceptions.BadRequestException;
import com.Douane.backendPFE.exceptions.EntityNotFoundException;
import com.Douane.backendPFE.models.vehiculle.Vehiculle;
import com.Douane.backendPFE.repositories.vehiculleR.VehiculleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VehiculleService {
    @Autowired
    private VehiculleRepository vehiculleRepository;

    public Vehiculle save(Vehiculle vehiculle){
        return vehiculleRepository.save(vehiculle);
    }
    public Vehiculle getById(Long id){
        Vehiculle vehiculle=vehiculleRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Vehiculle not found with id : "+id));
        return vehiculle;
    }
    public  Vehiculle getVehiculleByNumChassis(String numCh){
        Vehiculle vehiculle=vehiculleRepository.findVehiculleByNumchassis(numCh)
                .orElseThrow(()->new EntityNotFoundException("Vehiculle not found with Num chassis : "+ numCh));
        return vehiculle;
    }



}
