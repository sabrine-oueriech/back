package com.Douane.backendPFE.repositories.bureauR;


import com.Douane.backendPFE.models.bureau.Bureau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BureauRepository extends JpaRepository<Bureau,Long> {
}
