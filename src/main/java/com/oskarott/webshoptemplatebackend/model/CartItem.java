package com.oskarott.webshoptemplatebackend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "article_id"}))
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Cart getCart() { return cart; }
    public Article getArticle() { return article; }
    public int getQuantity() { return quantity; }
    public LocalDateTime getAddedAt() { return addedAt; }

    public void setCart(Cart cart) { this.cart = cart; }
    public void setArticle(Article article) { this.article = article; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
