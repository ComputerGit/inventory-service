package com.at.t.ecommerce.inventory.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_reservations") // <--- The Link to Flyway's Table
@Data               // <--- Generates Getters, Setters, toString, equals, hashCode
@NoArgsConstructor    // <--- REQUIRED by Hibernate (Default Constructor)
@AllArgsConstructor   // <--- Useful for testing
public class StockReservationJpaEntity {

    @Id
    private UUID id;

    @Column(name = "stock_id")
    private String stockId;

    @Column(name = "order_id")
    private String orderId;

    private Integer qty;

    private String status;

    @Column(name = "created_at")
    private Instant createdAt;

}