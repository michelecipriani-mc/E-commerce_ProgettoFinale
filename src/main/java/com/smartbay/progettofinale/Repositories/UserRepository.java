package com.smartbay.progettofinale.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartbay.progettofinale.Models.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    
    User findByEmail(String email);
    Optional<User> findByUsername(String username);
    
}
