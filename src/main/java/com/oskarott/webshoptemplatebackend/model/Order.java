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

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "firstName",    column = @Column(name = "shipping_first_name")),
            @AttributeOverride(name = "lastName",     column = @Column(name = "shipping_last_name")),
            @AttributeOverride(name = "company",      column = @Column(name = "shipping_company")),
            @AttributeOverride(name = "street",       column = @Column(name = "shipping_street")),
            @AttributeOverride(name = "addressLine2", column = @Column(name = "shipping_address_line2")),
            @AttributeOverride(name = "area",         column = @Column(name = "shipping_area")),
            @AttributeOverride(name = "postalCode",   column = @Column(name = "shipping_postal_code")),
            @AttributeOverride(name = "country",      column = @Column(name = "shipping_country")),
            @AttributeOverride(name = "phone",        column = @Column(name = "shipping_phone"))
    })
    private Address shippingAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "firstName",    column = @Column(name = "billing_first_name")),
            @AttributeOverride(name = "lastName",     column = @Column(name = "billing_last_name")),
            @AttributeOverride(name = "company",      column = @Column(name = "billing_company")),
            @AttributeOverride(name = "street",       column = @Column(name = "billing_street")),
            @AttributeOverride(name = "addressLine2", column = @Column(name = "billing_address_line2")),
            @AttributeOverride(name = "area",         column = @Column(name = "billing_area")),
            @AttributeOverride(name = "postalCode",   column = @Column(name = "billing_postal_code")),
            @AttributeOverride(name = "country",      column = @Column(name = "billing_country")),
            @AttributeOverride(name = "phone",        column = @Column(name = "billing_phone"))
    })
    private Address billingAddress;

    @Column(name = "total_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "shipping_cost", precision = 19, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(name = "shipping_method", length = 100)
    private String shippingMethod;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "tracking_number", length = 255)
    private String trackingNumber;

    @Column(name = "currency", length = 10)
    private String currency;

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
    public Address getShippingAddress() { return shippingAddress; }
    public Address getBillingAddress() { return billingAddress; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public BigDecimal getShippingCost() { return shippingCost; }
    public String getShippingMethod() { return shippingMethod; }
    public String getNotes() { return notes; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getCurrency() { return currency; }
    public List<OrderItem> getItems() { return items; }

    public void setUser(UserEntity user) { this.user = user; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }
    public void setBillingAddress(Address billingAddress) { this.billingAddress = billingAddress; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public void setShippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}

