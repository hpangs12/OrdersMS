package com.orderms.service;

import java.util.List;

import com.orderms.dto.PlaceOrderRequest;
import com.orderms.entity.Order;
import com.orderms.exception.OutOfStockException;
import com.orderms.exception.UserNotFoundException;

/**
 * Interface for Order Service
 * 
 * This interface declares all the methods for Order Service
 */
public interface OrderService{

	public Order placeOrder(PlaceOrderRequest request, String jwtToken) throws OutOfStockException, UserNotFoundException;
	public Order getOrder(Long orderId, String jwtToken) throws Exception;	
	public List<Order> getOrderByUser(Long userId, String jwtToken) throws Exception;
	public void cancelOrder(Long orderId, String jwtToken) throws Exception;

}
