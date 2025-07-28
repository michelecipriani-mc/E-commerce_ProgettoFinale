package com.smartbay.progettofinale.Repositories;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import com.smartbay.progettofinale.Models.Category;
@Repository
public interface CategoryRepository extends ListCrudRepository<Category, Long>{
    
}