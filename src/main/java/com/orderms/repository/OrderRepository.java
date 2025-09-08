package com.orderms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderms.entity.Order;

/**
 * Interface for Order Repository
 * 
 * This interface facilitates communication with the database
 */
public interface OrderRepository extends JpaRepository<Order, Long>{

	List<Order> findByUserId(Long userId);

}
