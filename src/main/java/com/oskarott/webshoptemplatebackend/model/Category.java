package com.oskarott.webshoptemplatebackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    public Long getId() { return id; }
    public String getName() { return name; }
    public Category getParent() { return parent; }

    public void setName(String name) { this.name = name; }
    public void setParent(Category parent) { this.parent = parent; }
}
