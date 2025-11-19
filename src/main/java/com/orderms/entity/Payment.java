package com.orderms.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    private Long id;
    private Long orderId;
    private Integer userId;
    private String cardNumber;
    private Date expiryDate;
    private Integer cvv;
    private String upiId;
    private String name;
    private String address;
    private BigDecimal amount;
    private String status;
    private Instant timestamp;

}
