package com.javatechie.crud.example.repository;

import com.javatechie.crud.example.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    Product findByName(String name);

    @Query("select p from Product p where p.name in (:productNames)")
    List<Product> findByNameIn(List<String> productNames);

    List<Product> findByIdIn(List<Integer> id);

}

