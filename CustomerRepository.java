package com.example.ecommrece.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommrece.dto.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	boolean existsByMobile(long mobile);
	boolean existsByEmail(String string);
	Customer findByEmail(String email);

	
}
