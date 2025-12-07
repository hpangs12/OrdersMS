package com.orderms.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jwt.JWTClaimsSet;
import com.orderms.dto.OrderItemRequest;
import com.orderms.dto.PlaceOrderRequest;
import com.orderms.dto.ProductDTO;
import com.orderms.entity.Order;
import com.orderms.entity.OrderItem;
import com.orderms.entity.OrderStatus;
import com.orderms.entity.Payment;
import com.orderms.entity.PaymentStatus;
import com.orderms.exception.OrderCancelException;
import com.orderms.exception.OrderNotFoundException;
import com.orderms.exception.OutOfStockException;
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
    public Order placeOrder(PlaceOrderRequest request, JWTClaimsSet claims) throws OutOfStockException, ParseException {
     
    	// Get User Id from the Claims
		Integer userId = claims.getIntegerClaim("userId");
		
    	// Get JWT token
    	String jwtToken = ((JwtAuthenticationToken) SecurityContextHolder.getContext()
    	    .getAuthentication())
    	    .getToken()
    	    .getTokenValue();
    	
    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Authorization", "Bearer "+jwtToken);
    	HttpEntity<Void> entity = new HttpEntity<Void>(headers);
    	
    	List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;
        
        for(OrderItemRequest itemReq : request.getItems()) {
        	
        	ResponseEntity<ProductDTO> productResponse = restTemplate.exchange(
        			"http://productms/products/nocache/"+itemReq.getProductId(),
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
        order.setUserId(userId);
        order.setItems(orderItems);
        order.setShippingAddress(request.getShippingAddress());
        final Order tempOrder = order;
        orderItems.forEach(oi -> oi.setOrder(tempOrder));
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentType(request.getPaymentType());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Send a Kafka Event for the Order Placed
        orderItems.forEach(orderItem -> {
        	kafkaUtitily.orderPlaced(orderItem.getProductId(), orderItem.getQuantity());
        });
        
        order = orderRepository.save(order);
        
        Payment payment = request.getPaymentDetails();
        payment.setOrderId(order.getOrderId());
        payment.setUserId(userId);
        
    	HttpEntity<Payment> paymentEntity = new HttpEntity<Payment>(payment, headers);
        // Process Payment
    	@SuppressWarnings("unused")
		ResponseEntity<Payment> payResponse = restTemplate.exchange(
    			"http://paymentms/payments/process", 
    			HttpMethod.POST, 
    			paymentEntity,
    			Payment.class);
        
        return order;
    	
    }

    @Override
	public Order getOrder(Long orderId) throws Exception{
		
		Optional<Order> optional = orderRepository.findById(orderId);
		Order order = optional.orElseThrow(() -> new OrderNotFoundException("The order with order id: "+orderId+" is not present."));
		
		return order;
	}

	@Override
	public List<Order> getOrderByUser(Long userId) throws Exception{
		
		List<Order> orders = orderRepository.findByUserId(userId);
		
		return orders;
	}

	@Override
	public void cancelOrder(Long orderId) throws Exception{
		
		Optional<Order> optional = orderRepository.findById(orderId);
		Order order = optional.orElseThrow(() -> new OrderNotFoundException("The order with order id: "+orderId+" is not present."));
		
		if (order.getStatus() == OrderStatus.SHIPPED) {
			throw new OrderCancelException("The order is already shipped. Please cancel at the time of delivery");
		}else if (order.getStatus() == OrderStatus.CANCELLED) {
			throw new OrderCancelException("The order is already cancelled. Please check and try again");
		}
		
		order.setStatus(OrderStatus.CANCELLED);
		
	}

	@Override
	public void updatePaymentStatus(Long orderId, PaymentStatus status) throws OrderNotFoundException {

		Optional<Order> optional = orderRepository.findById(orderId);
		Order order = optional.orElseThrow(() -> new OrderNotFoundException("The order with order id: "+orderId+" is not present."));
		
		if(status == PaymentStatus.COMPLETED) {
			order.setStatus(OrderStatus.PAID);
		}
		
		order.setPaymentStatus(status);
		
		orderRepository.save(order);
	}

}
