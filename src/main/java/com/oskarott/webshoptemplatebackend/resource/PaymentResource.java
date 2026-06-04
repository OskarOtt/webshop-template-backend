package com.oskarott.webshoptemplatebackend.resource;

import com.oskarott.webshoptemplatebackend.dto.CheckoutResponse;
import com.oskarott.webshoptemplatebackend.model.UserEntity;
import com.oskarott.webshoptemplatebackend.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Tag(name = "Payments", description = "Stripe payment endpoints")
@RestController
@RequestMapping("/payments")
public class PaymentResource {

    private final PaymentService paymentService;

    public PaymentResource(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Create a Stripe Checkout Session for an order", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Checkout session created",
                    content = @Content(schema = @Schema(implementation = CheckoutResponse.class))),
            @ApiResponse(responseCode = "400", description = "Order not in AWAITING_PAYMENT state", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @PostMapping("/checkout/{orderId}")
    public ResponseEntity<CheckoutResponse> createCheckout(@PathVariable Long orderId,
                                                           @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(paymentService.createCheckoutSession(orderId, user.getId()));
    }

    @Operation(summary = "Stripe webhook receiver (called by Stripe, not by clients)", responses = {
            @ApiResponse(responseCode = "200", description = "Event processed"),
            @ApiResponse(responseCode = "400", description = "Invalid signature or payload", content = @Content)
    })
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(HttpServletRequest request) throws IOException {
        String payload = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String sigHeader = request.getHeader("Stripe-Signature");
        paymentService.handleWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }
}
