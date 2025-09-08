package com.orderms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orderms.dto.PlaceOrderRequest;
import com.orderms.entity.Order;
import com.orderms.exception.OutOfStockException;
import com.orderms.exception.UserNotFoundException;
import com.orderms.service.OrderService;

/**
 * Controller class to handle orders requests
 * 
 * This class handles the mappings for REST requests for Orders.
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;
	
	@PostMapping("/checkout")
	public ResponseEntity<Order> placeOrder(
			@RequestBody PlaceOrderRequest request,
			@RequestHeader("Authorization") String authHeader
			) throws OutOfStockException, UserNotFoundException{
		
		String jwtToken = authHeader.substring(7);
		Order order = orderService.placeOrder(request, jwtToken);
		
		return new ResponseEntity<Order>(order, HttpStatus.CREATED);
	}
	
	@GetMapping("/{orderId}")
	public ResponseEntity<Order> getOrder(@PathVariable Long orderId, @RequestHeader("Authorization") String authHeader) throws Exception{
		
		String jwtToken = authHeader.substring(7);
		Order order = orderService.getOrder(orderId, jwtToken);
		
		return new ResponseEntity<Order>(order, HttpStatus.OK);
	}
	
	@GetMapping("/users/{userId}")
	public ResponseEntity<List<Order>> getOrderByUser(@PathVariable Long userId, @RequestHeader("Authorization") String authHeader) throws Exception{
		
		String jwtToken = authHeader.substring(7);
		List<Order> orderList = orderService.getOrderByUser(userId, jwtToken);
		
		return new ResponseEntity<>(orderList, HttpStatus.OK);
	}
	
	@PostMapping("/{orderId}/cancel")
	public ResponseEntity<?> cancelOrder(@PathVariable Long orderId, @RequestHeader("Authorization") String authHeader) throws Exception{
		
		String jwtToken = authHeader.substring(7);
		orderService.cancelOrder(orderId, jwtToken);
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	
}
