package com.oskarott.webshoptemplatebackend.service;

import com.oskarott.webshoptemplatebackend.dto.BrandRequest;
import com.oskarott.webshoptemplatebackend.dto.BrandResponse;
import com.oskarott.webshoptemplatebackend.model.Brand;
import com.oskarott.webshoptemplatebackend.repository.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<BrandResponse> getAll() {
        return brandRepository.findAll().stream()
                .map(BrandResponse::from)
                .toList();
    }

    public BrandResponse getById(Long id) {
        return BrandResponse.from(findOrThrow(id));
    }

    public BrandResponse create(BrandRequest request) {
        Brand brand = new Brand();
        applyRequest(brand, request);
        return BrandResponse.from(brandRepository.save(brand));
    }

    public BrandResponse update(Long id, BrandRequest request) {
        Brand brand = findOrThrow(id);
        applyRequest(brand, request);
        return BrandResponse.from(brandRepository.save(brand));
    }

    public void delete(Long id) {
        findOrThrow(id);
        brandRepository.deleteById(id);
    }

    public Brand findOrThrow(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + id));
    }

    private void applyRequest(Brand brand, BrandRequest request) {
        brand.setName(request.name());
        brand.setLogoUrl(request.logoUrl());
        brand.setDescription(request.description());
    }
}
