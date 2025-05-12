package com.esgglobal.order_history_loader.entity;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @Column(name = "order_id", nullable = false, updatable = false)
    private String orderId;

    @Column(name = "customer", nullable = false)
    private String customer;

    @Column(name = "status", nullable = false)
    private String status;
}
