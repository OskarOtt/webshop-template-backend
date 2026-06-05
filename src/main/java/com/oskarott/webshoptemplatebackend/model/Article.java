package com.oskarott.webshoptemplatebackend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ElementCollection
    @CollectionTable(name = "article_images", joinColumns = @JoinColumn(name = "article_id"))
    @Column(name = "image_url")
    @OrderColumn(name = "position")
    private List<String> images = new ArrayList<>();

    @Column(unique = true)
    private String sku;

    private String size;

    @Column(precision = 10, scale = 3)
    private BigDecimal weight;

    private String color;

    @ElementCollection
    @CollectionTable(name = "article_tags", joinColumns = @JoinColumn(name = "article_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public Category getCategory() { return category; }
    public Brand getBrand() { return brand; }
    public List<String> getImages() { return images; }
    public String getSku() { return sku; }
    public String getSize() { return size; }
    public BigDecimal getWeight() { return weight; }
    public String getColor() { return color; }
    public List<String> getTags() { return tags; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public void setCategory(Category category) { this.category = category; }
    public void setBrand(Brand brand) { this.brand = brand; }
    public void setImages(List<String> images) { this.images = images; }
    public void setSku(String sku) { this.sku = sku; }
    public void setSize(String size) { this.size = size; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public void setColor(String color) { this.color = color; }
    public void setTags(List<String> tags) { this.tags = tags; }
}

