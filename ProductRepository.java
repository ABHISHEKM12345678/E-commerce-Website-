package com.example.ecommrece.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommrece.dto.Product;
import com.example.ecommrece.dto.Seller;


public interface ProductRepository extends JpaRepository<Product, Integer> {

	List<Product> findBySeller(Seller seller);
}
