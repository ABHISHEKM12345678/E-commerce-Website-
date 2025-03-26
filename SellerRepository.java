package com.example.ecommrece.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.ecommrece.dto.Seller;

public interface SellerRepository extends JpaRepository<Seller, Integer> {
	boolean existsByMobile(long mobile);
	boolean existsByEmail(String string);
	Seller findByEmail(String email);
}
