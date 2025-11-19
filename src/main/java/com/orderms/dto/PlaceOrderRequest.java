package com.orderms.dto;

import java.util.List;

import com.orderms.entity.Payment;

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
    private String userId;
    private Payment paymentDetails;
    private String shippingAddress;
    private List<OrderItemRequest> items;
    private String paymentType;  // CREDIT_CARD, UPI, etc.
}
