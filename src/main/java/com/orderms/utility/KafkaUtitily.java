package com.orderms.utility;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Class for Kafka Utility
 * 
 * This class provides utility implementations for Kafka Operations.
 */
@Service
public class KafkaUtitily {

	@Autowired
	private KafkaTemplate<String, Map<String, String>> template;
	
	@Bean
	public NewTopic orderPlacedEvent() {
		return TopicBuilder.name("order_place_event").partitions(1).replicas(1).build();
	}
	
	public void orderPlaced(Long productId, Integer quantity) {
		Map<String, String> itemsMap = new HashMap<>();
		itemsMap.put("id", productId.toString());
		itemsMap.put("quantity", quantity.toString()
				);
		
		template.send("order_place_event", itemsMap);
	}
}
