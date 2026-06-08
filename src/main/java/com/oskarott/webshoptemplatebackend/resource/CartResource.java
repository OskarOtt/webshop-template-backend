package com.oskarott.webshoptemplatebackend.resource;

import com.oskarott.webshoptemplatebackend.dto.AddToCartRequest;
import com.oskarott.webshoptemplatebackend.dto.CartResponse;
import com.oskarott.webshoptemplatebackend.dto.UpdateCartItemRequest;
import com.oskarott.webshoptemplatebackend.model.UserEntity;
import com.oskarott.webshoptemplatebackend.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart", description = "Shopping cart endpoints (requires authentication)")
@RestController
@RequestMapping("/cart")
@SecurityRequirement(name = "bearerAuth")
public class CartResource {

    private final CartService cartService;

    public CartResource(CartService cartService) {
        this.cartService = cartService;
    }

    @Operation(summary = "Get current user's cart", responses = {
            @ApiResponse(responseCode = "200", description = "Cart retrieved",
                    content = @Content(schema = @Schema(implementation = CartResponse.class)))
    })
    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(cartService.getCart(user));
    }

    @Operation(summary = "Add an item to the cart (increments quantity if already present)", responses = {
            @ApiResponse(responseCode = "200", description = "Item added",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or article unavailable", content = @Content)
    })
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @AuthenticationPrincipal UserEntity user,
            @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addItem(user, request));
    }

    @Operation(summary = "Update quantity of a cart item (set to 0 to remove)", responses = {
            @ApiResponse(responseCode = "200", description = "Quantity updated",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "404", description = "Item not in cart", content = @Content)
    })
    @PutMapping("/items/{articleId}")
    public ResponseEntity<CartResponse> updateItem(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long articleId,
            @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItem(user, articleId, request));
    }

    @Operation(summary = "Remove a specific item from the cart", responses = {
            @ApiResponse(responseCode = "200", description = "Item removed",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "404", description = "Item not in cart", content = @Content)
    })
    @DeleteMapping("/items/{articleId}")
    public ResponseEntity<CartResponse> removeItem(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long articleId) {
        return ResponseEntity.ok(cartService.removeItem(user, articleId));
    }

    @Operation(summary = "Clear all items from the cart", responses = {
            @ApiResponse(responseCode = "200", description = "Cart cleared",
                    content = @Content(schema = @Schema(implementation = CartResponse.class)))
    })
    @DeleteMapping
    public ResponseEntity<CartResponse> clearCart(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(cartService.clearCart(user));
    }
}
