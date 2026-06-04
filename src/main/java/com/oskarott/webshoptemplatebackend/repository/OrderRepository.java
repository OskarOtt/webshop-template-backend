package com.oskarott.webshoptemplatebackend.repository;

import com.oskarott.webshoptemplatebackend.model.Order;
import com.oskarott.webshoptemplatebackend.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(OrderStatus status);
    Optional<Order> findByPaymentStripeSessionId(String stripeSessionId);
}
