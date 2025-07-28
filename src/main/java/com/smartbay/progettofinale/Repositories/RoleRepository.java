package com.smartbay.progettofinale.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartbay.progettofinale.Models.Role;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

    Role findByName(String name);
    
}
