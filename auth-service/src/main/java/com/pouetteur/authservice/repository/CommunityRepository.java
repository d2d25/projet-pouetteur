package com.pouetteur.authservice.repository;

import com.pouetteur.authservice.model.Community;
import com.pouetteur.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, String> {
    Optional<Community> findByUsername(String username);
}
