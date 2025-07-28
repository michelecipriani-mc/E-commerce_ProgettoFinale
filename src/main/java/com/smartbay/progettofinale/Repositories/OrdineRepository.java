package com.smartbay.progettofinale.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartbay.progettofinale.Models.Ordine;
import com.smartbay.progettofinale.Models.User;


public interface OrdineRepository extends JpaRepository<Ordine, Long> {
  List<Ordine> findByUser(User user);
}