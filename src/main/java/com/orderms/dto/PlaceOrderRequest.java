package com.orderms.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Order in Request Body
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequest {
    private Long userId;
    private String shippingAddress;
    private List<OrderItemRequest> items;
}
