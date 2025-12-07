package com.orderms.service;

import java.text.ParseException;
import java.util.List;

import com.nimbusds.jwt.JWTClaimsSet;
import com.orderms.dto.PlaceOrderRequest;
import com.orderms.entity.Order;
import com.orderms.entity.PaymentStatus;
import com.orderms.exception.OrderNotFoundException;
import com.orderms.exception.OutOfStockException;

/**
 * Interface for Order Service
 * 
 * This interface declares all the methods for Order Service
 */
public interface OrderService{

	public Order placeOrder(PlaceOrderRequest request, JWTClaimsSet claims) throws OutOfStockException, ParseException;
	public Order getOrder(Long orderId) throws Exception;	
	public List<Order> getOrderByUser(Long userId) throws Exception;
	public void cancelOrder(Long orderId) throws Exception;
	public void updatePaymentStatus(Long orderId, PaymentStatus status) throws OrderNotFoundException;

}
