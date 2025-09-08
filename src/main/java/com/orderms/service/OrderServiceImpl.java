package com.orderms.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.orderms.dto.OrderItemRequest;
import com.orderms.dto.PlaceOrderRequest;
import com.orderms.dto.ProductDTO;
import com.orderms.dto.UserDTO;
import com.orderms.entity.Order;
import com.orderms.entity.OrderItem;
import com.orderms.entity.OrderStatus;
import com.orderms.exception.ForbiddenTaskException;
import com.orderms.exception.OrderCancelException;
import com.orderms.exception.OrderNotFoundException;
import com.orderms.exception.OutOfStockException;
import com.orderms.exception.UserNotFoundException;
import com.orderms.repository.OrderRepository;
import com.orderms.utility.KafkaUtitily;

/**
 * Implementation Class for Order Service
 * 
 * This class implements the Order Service Interface
 */
@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private KafkaUtitily kafkaUtitily;

    @Override
    public Order placeOrder(PlaceOrderRequest request, String jwtToken) throws OutOfStockException, UserNotFoundException {
     
    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Authorization", "Bearer "+jwtToken);
    	HttpEntity<Void> entity = new HttpEntity<Void>(headers);
    	
    	// Check if user exists
    	ResponseEntity<UserDTO> userResponse = restTemplate.exchange(
    			"http://localhost:8081/users/id/" + request.getUserId(), 
    			HttpMethod.GET, 
    			entity,
    			UserDTO.class);
    	
    	if (userResponse.getStatusCode() != HttpStatus.OK) {
            throw new UserNotFoundException("User not found");
        }
    	
    	List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;
        
        for(OrderItemRequest itemReq : request.getItems()) {
        	
        	ResponseEntity<ProductDTO> productResponse = restTemplate.exchange(
        			"http://localhost:8082/products/nocache/"+itemReq.getProductId(),
        			HttpMethod.GET,
        			entity,
        			ProductDTO.class
        			);
        	
        	ProductDTO product = productResponse.getBody();
        	if (product.getProductQuantity() < itemReq.getQuantity()) {
        		throw new OutOfStockException("Product "+product.getProductName()+" has insufficient stock");
        	}
        	
        	OrderItem orderItem = new OrderItem();
        	orderItem.setProductId(itemReq.getProductId());
        	orderItem.setQuantity(itemReq.getQuantity());
        	orderItem.setPrice(product.getProductPrice());
        	
        	orderItems.add(orderItem);
        	
        	totalAmount += product.getProductPrice() * itemReq.getQuantity();
        }
        
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setItems(orderItems);
        order.setShippingAddress(request.getShippingAddress());
        orderItems.forEach(oi -> oi.setOrder(order));
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PLACED);
        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Send a Kafka Event for the Order Placed
        orderItems.forEach(orderItem -> {
        	kafkaUtitily.orderPlaced(orderItem.getProductId(), orderItem.getQuantity());
        });

        return orderRepository.save(order);
    	
    }

    @Override
	public Order getOrder(Long orderId, String jwtToken) throws Exception{

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+jwtToken);
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		
		ResponseEntity<Boolean> userResponse = restTemplate.exchange(
				"http://localhost:8081/users/auth/validate",
				HttpMethod.POST,
				entity,
				Boolean.class
				);
		
		if (userResponse.getStatusCode() != HttpStatus.OK) {
            throw new UserNotFoundException("User not found");
        }
		
		Boolean isAuth = userResponse.getBody();
		if (!isAuth) {
			throw new ForbiddenTaskException("User Validation Failed. Please try again");
		}
		
		Optional<Order> optional = orderRepository.findById(orderId);
		Order order = optional.orElseThrow(() -> new OrderNotFoundException("The order with order id: "+orderId+" is not present."));
		
		return order;
	}

	@Override
	public List<Order> getOrderByUser(Long userId, String jwtToken) throws Exception{

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+jwtToken);
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		
		ResponseEntity<Boolean> userResponse = restTemplate.exchange(
				"http://localhost:8081/users/auth/validate",
				HttpMethod.POST,
				entity,
				Boolean.class
				);
		
		if (userResponse.getStatusCode() != HttpStatus.OK) {
            throw new UserNotFoundException("User not found");
        }
		
		Boolean isAuth = userResponse.getBody();
		if (!isAuth) {
			throw new ForbiddenTaskException("User Validation Failed. Please try again");
		}
		
		List<Order> orders = orderRepository.findByUserId(userId);
		
		return orders;
	}

	@Override
	public void cancelOrder(Long orderId, String jwtToken) throws Exception{

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+jwtToken);
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		
		ResponseEntity<Boolean> userResponse = restTemplate.exchange(
				"http://localhost:8081/users/auth/validate",
				HttpMethod.POST,
				entity,
				Boolean.class
				);
		
		if (userResponse.getStatusCode() != HttpStatus.OK) {
            throw new UserNotFoundException("User not found");
        }
		
		Boolean isAuth = userResponse.getBody();
		if (!isAuth) {
			throw new ForbiddenTaskException("User Validation Failed. Please try again");
		}
		
		Optional<Order> optional = orderRepository.findById(orderId);
		Order order = optional.orElseThrow(() -> new OrderNotFoundException("The order with order id: "+orderId+" is not present."));
		
		if (order.getStatus() == OrderStatus.SHIPPED) {
			throw new OrderCancelException("The order is already shipped. Please cancel at the time of delivery");
		}else if (order.getStatus() == OrderStatus.CANCELLED) {
			throw new OrderCancelException("The order is already cancelled. Please check and try again");
		}
		
		order.setStatus(OrderStatus.CANCELLED);
		
	}

}
