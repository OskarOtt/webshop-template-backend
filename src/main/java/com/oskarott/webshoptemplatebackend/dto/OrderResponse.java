package com.oskarott.webshoptemplatebackend.dto;

import com.oskarott.webshoptemplatebackend.model.Order;
import com.oskarott.webshoptemplatebackend.model.OrderStatus;
import com.oskarott.webshoptemplatebackend.model.PaymentInfo;
import com.oskarott.webshoptemplatebackend.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        String userEmail,
        OrderStatus status,
        PaymentInfoResponse payment,
        LocalDateTime orderDate,
        LocalDateTime updatedAt,
        String shippingAddress,
        BigDecimal totalPrice,
        List<OrderItemResponse> items
) {
    public record PaymentInfoResponse(
            PaymentStatus paymentStatus,
            String stripeSessionId,
            String stripePaymentIntentId,
            LocalDateTime paidAt
    ) {
        public static PaymentInfoResponse from(PaymentInfo info) {
            return new PaymentInfoResponse(
                    info.getPaymentStatus(),
                    info.getStripeSessionId(),
                    info.getStripePaymentIntentId(),
                    info.getPaidAt()
            );
        }
    }

    public static OrderResponse from(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getEmail(),
                order.getStatus(),
                PaymentInfoResponse.from(order.getPayment()),
                order.getOrderDate(),
                order.getUpdatedAt(),
                order.getShippingAddress(),
                order.getTotalPrice(),
                itemResponses
        );
    }
}
