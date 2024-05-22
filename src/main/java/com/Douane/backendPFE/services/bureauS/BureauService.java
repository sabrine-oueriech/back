package com.Douane.backendPFE.services.bureauS;

import com.Douane.backendPFE.exceptions.EntityNotFoundException;
import com.Douane.backendPFE.models.bureau.Bureau;
import com.Douane.backendPFE.repositories.bureauR.BureauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BureauService {
    @Autowired
    private BureauRepository bureauRepository;

    public Bureau save(Bureau bureau){

        return bureauRepository.save(bureau);
    }

    public Bureau getById(Long id){
        Bureau bureau=bureauRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Bureau not found with id : "+id));
        return bureau;
    }
    public List<Bureau> gatAllBureau(){
        return bureauRepository.findAll();
    }
    public void deleteById(Long id){
        try {
            bureauRepository.deleteById(id);
        }catch (Exception e){
            throw  new EntityNotFoundException("Bureau not found with id : "+id);
        }
    }


    public List<Bureau> getAll() {
        return bureauRepository.findAll();
    }
}
