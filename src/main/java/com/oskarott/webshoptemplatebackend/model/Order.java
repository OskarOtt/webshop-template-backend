package com.oskarott.webshoptemplatebackend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Embedded
    private PaymentInfo payment = new PaymentInfo();

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "total_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /** Sets payment status and auto-syncs the order status accordingly. */
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        payment.setPaymentStatus(paymentStatus);
        this.status = switch (paymentStatus) {
            case AWAITING_PAYMENT -> OrderStatus.PENDING;
            case PAID -> OrderStatus.CONFIRMED;
            case FAILED, REFUNDED -> OrderStatus.CANCELLED;
        };
    }

    public Long getId() { return id; }
    public UserEntity getUser() { return user; }
    public OrderStatus getStatus() { return status; }
    public PaymentInfo getPayment() { return payment; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getShippingAddress() { return shippingAddress; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public List<OrderItem> getItems() { return items; }

    public void setUser(UserEntity user) { this.user = user; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}

