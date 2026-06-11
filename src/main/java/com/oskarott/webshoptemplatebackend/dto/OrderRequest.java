package com.oskarott.webshoptemplatebackend.dto;

import java.util.List;

public record OrderRequest(
        List<OrderItemRequest> items,
        AddressDto shippingAddress,
        AddressDto billingAddress,
        String shippingMethod,
        String notes,
        String currency
) {}
