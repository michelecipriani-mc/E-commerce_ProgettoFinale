package com.smartbay.progettofinale.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartbay.progettofinale.Models.Ordine;
import com.smartbay.progettofinale.Models.User;

@Repository
//repository dell'ordine 
public interface OrdineRepository extends JpaRepository<Ordine, Long> {
  //metodi
  List<Ordine> findByUser(User user);
  List<Ordine> findByUserOrderByDataOrdineDesc(User user);
}