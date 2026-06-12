package com.oskarott.webshoptemplatebackend.service;

import com.oskarott.webshoptemplatebackend.dto.OrderItemRequest;
import com.oskarott.webshoptemplatebackend.dto.OrderRequest;
import com.oskarott.webshoptemplatebackend.dto.OrderResponse;
import com.oskarott.webshoptemplatebackend.model.*;
import com.oskarott.webshoptemplatebackend.repository.ArticleRepository;
import com.oskarott.webshoptemplatebackend.repository.OrderRepository;
import com.oskarott.webshoptemplatebackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        ArticleRepository articleRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponse placeOrder(Long userId, OrderRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.shippingAddress() != null ? request.shippingAddress().toEntity() : null);
        order.setBillingAddress(request.billingAddress() != null ? request.billingAddress().toEntity() : null);
        order.setShippingMethod(request.shippingMethod());
        order.setNotes(request.notes());
        order.setCurrency(request.currency());

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.items()) {
            Article article = articleRepository.findById(itemReq.articleId())
                    .orElseThrow(() -> new IllegalArgumentException("Article not found: " + itemReq.articleId()));

            if (article.getStatus() != ArticleStatus.ACTIVE) {
                throw new IllegalStateException(
                        "Article '%s' is not available for purchase (status: %s)"
                                .formatted(article.getName(), article.getStatus())
                );
            }

            if (article.getStockQuantity() < itemReq.quantity()) {
                throw new IllegalStateException(
                        "Insufficient stock for article '%s'. Available: %d, requested: %d"
                                .formatted(article.getName(), article.getStockQuantity(), itemReq.quantity())
                );
            }

            article.setStockQuantity(article.getStockQuantity() - itemReq.quantity());
            articleRepository.save(article);

            BigDecimal unitPrice = article.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.quantity()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setArticle(article);
            item.setQuantity(itemReq.quantity());
            item.setUnitPrice(unitPrice);
            item.setSubtotal(subtotal);

            items.add(item);
            total = total.add(subtotal);
        }

        order.setItems(items);
        order.setTotalPrice(total);
        order.setPaymentStatus(PaymentStatus.AWAITING_PAYMENT);

        return OrderResponse.from(orderRepository.save(order));
    }

    public List<OrderResponse> getOrdersForUser(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::from)
                .toList();
    }

    public OrderResponse getOrderById(Long orderId, Long userId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (!isAdmin && !order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.setStatus(status);
        return OrderResponse.from(orderRepository.save(order));
    }
}
