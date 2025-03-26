package com.example.ecommrece.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommrece.dto.Cart;
import com.example.ecommrece.dto.Customer;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findById(int id);

	Cart findByCustomer(Customer customer);
}

