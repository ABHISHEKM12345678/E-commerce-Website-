package com.example.ecommrece.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ecommrece.dto.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // You can add custom query methods here if needed
}
