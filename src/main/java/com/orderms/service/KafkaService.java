package com.orderms.service;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderms.entity.PaymentStatus;
import com.orderms.exception.OrderNotFoundException;

@Service
public class KafkaService {

	static final Logger LOGGER = LoggerFactory.getLogger(KafkaService.class);
	
	@Autowired
	private OrderService orderService;
		
	@KafkaListener(id="payment_status_update", topics = "payment_status_update")
	public void paymentStatusUpdate(String jsonPayload) {
		ObjectMapper mapper = new ObjectMapper();
        Map<String, String> itemMap = null;
		try {
			itemMap = mapper.readValue(jsonPayload, new TypeReference<Map<String, String>>() {});
			
			Long orderId = Long.parseLong(itemMap.get("orderId"));
	        PaymentStatus status = PaymentStatus.valueOf(itemMap.get("status"));
			
			LOGGER.info("Received payment status {} for order ID {}.", status, orderId);
			orderService.updatePaymentStatus(orderId, status);
			LOGGER.info("Updated payment status {} for order ID {}.", status, orderId);
		} catch (JsonProcessingException | NumberFormatException e) {
	        LOGGER.error("Failed to process Kafka message: {}", jsonPayload, e);
	        // Optionally send to a dead-letter topic or alert system
	    } catch (OrderNotFoundException e) {
			// TODO Auto-generated catch block
	    	LOGGER.error("Order with specified order number not found.");
		}
		
	}
}
