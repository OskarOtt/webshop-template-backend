package com.oskarott.webshoptemplatebackend.service;

import com.oskarott.webshoptemplatebackend.dto.CheckoutResponse;
import com.oskarott.webshoptemplatebackend.model.Article;
import com.oskarott.webshoptemplatebackend.model.Order;
import com.oskarott.webshoptemplatebackend.model.PaymentStatus;
import com.oskarott.webshoptemplatebackend.repository.ArticleRepository;
import com.oskarott.webshoptemplatebackend.repository.OrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final ArticleRepository articleRepository;
    private final EmailService emailService;

    @Value("${stripe.api-key}")
    private String apiKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    public PaymentService(OrderRepository orderRepository,
                          ArticleRepository articleRepository,
                          EmailService emailService) {
        this.orderRepository = orderRepository;
        this.articleRepository = articleRepository;
        this.emailService = emailService;
    }

    public CheckoutResponse createCheckoutSession(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        if (order.getPayment().getPaymentStatus() != PaymentStatus.AWAITING_PAYMENT) {
            throw new IllegalStateException("Order is not awaiting payment");
        }

        Stripe.apiKey = apiKey;

        List<SessionCreateParams.LineItem> lineItems = order.getItems().stream()
                .map(item -> SessionCreateParams.LineItem.builder()
                        .setQuantity((long) item.getQuantity())
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("nok")
                                .setUnitAmount(toOre(item.getUnitPrice()))
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(item.getArticle().getName())
                                        .build())
                                .build())
                        .build())
                .toList();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .putMetadata("orderId", String.valueOf(order.getId()))
                .addAllLineItem(lineItems)
                .build();

        try {
            Session session = Session.create(params);
            order.getPayment().setStripeSessionId(session.getId());
            orderRepository.save(order);
            return new CheckoutResponse(session.getUrl());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Stripe Checkout Session: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void handleWebhook(String payload, String sigHeader) throws EventDataObjectDeserializationException {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new IllegalArgumentException("Invalid Stripe webhook signature", e);
        }

        switch (event.getType()) {
            case "checkout.session.completed" -> {
                Session session = (Session) event.getDataObjectDeserializer()
                        .deserializeUnsafe();
                Order order = orderRepository.findByPaymentStripeSessionId(session.getId())
                        .orElseThrow(() -> new RuntimeException("No order found for session: " + session.getId()));
                order.setPaymentStatus(PaymentStatus.PAID);
                order.getPayment().setStripePaymentIntentId(session.getPaymentIntent());
                order.getPayment().setPaidAt(LocalDateTime.now());
                orderRepository.save(order);
                emailService.sendOrderConfirmation(order);
            }
            case "checkout.session.expired" -> {
                Session session = (Session) event.getDataObjectDeserializer()
                        .deserializeUnsafe();
                orderRepository.findByPaymentStripeSessionId(session.getId()).ifPresent(order -> {
                    order.setPaymentStatus(PaymentStatus.FAILED);
                    orderRepository.save(order);
                    restoreStock(order);
                });
            }
            default -> { /* ignore other event types */ }
        }
    }

    private void restoreStock(Order order) {
        order.getItems().forEach(item -> {
            Article article = item.getArticle();
            article.setStockQuantity(article.getStockQuantity() + item.getQuantity());
            articleRepository.save(article);
        });
    }

    private long toOre(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).longValue();
    }
}
