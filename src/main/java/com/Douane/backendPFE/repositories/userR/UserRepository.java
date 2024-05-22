package com.Douane.backendPFE.repositories.userR;

import com.Douane.backendPFE.models.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserModel,Long> {



    @Query("select (count(u) > 0) from UserModel u where u.email = ?1")
    Boolean existsByEmail(String email);

    @Query("select u from UserModel u where u.enabled = true")
    List<UserModel> findAllByEnabledTrue();

    @Query("select u from UserModel u where u.email = ?1")
    Optional<UserModel> findByEmail(String email);
}
