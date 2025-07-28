package com.smartbay.progettofinale.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.smartbay.progettofinale.model.Ordine;
import com.smartbay.progettofinale.model.User;

public interface OrdineRepository extends JpaRepository<Ordine, Long> {
  List<Ordine> findByUser(User user);
}