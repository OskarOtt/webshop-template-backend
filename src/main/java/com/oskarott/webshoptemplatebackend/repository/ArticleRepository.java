package com.oskarott.webshoptemplatebackend.repository;

import com.oskarott.webshoptemplatebackend.model.Article;
import com.oskarott.webshoptemplatebackend.model.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByStatus(ArticleStatus status);
    List<Article> findByCategoryId(Long categoryId);
    List<Article> findByCategoryIdAndStatus(Long categoryId, ArticleStatus status);
}
