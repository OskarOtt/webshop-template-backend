package com.oskarott.webshoptemplatebackend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Embeddable
public class PaymentInfo {

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "stripe_session_id")
    private String stripeSessionId;

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public String getStripeSessionId() { return stripeSessionId; }
    public String getStripePaymentIntentId() { return stripePaymentIntentId; }
    public LocalDateTime getPaidAt() { return paidAt; }

    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setStripeSessionId(String stripeSessionId) { this.stripeSessionId = stripeSessionId; }
    public void setStripePaymentIntentId(String stripePaymentIntentId) { this.stripePaymentIntentId = stripePaymentIntentId; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}
