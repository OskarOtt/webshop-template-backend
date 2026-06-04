package com.oskarott.webshoptemplatebackend.dto;

import java.util.List;

public record OrderRequest(
        List<OrderItemRequest> items,
        String shippingAddress
) {}
