package com.oskarott.webshoptemplatebackend.service;

import com.oskarott.webshoptemplatebackend.dto.ArticleRequest;
import com.oskarott.webshoptemplatebackend.dto.ArticleResponse;
import com.oskarott.webshoptemplatebackend.model.Article;
import com.oskarott.webshoptemplatebackend.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public List<ArticleResponse> getAll() {
        return articleRepository.findAll().stream()
                .map(ArticleResponse::from)
                .toList();
    }

    public ArticleResponse getById(Long id) {
        return ArticleResponse.from(findOrThrow(id));
    }

    public List<ArticleResponse> getByCategory(String category) {
        return articleRepository.findByCategory(category).stream()
                .map(ArticleResponse::from)
                .toList();
    }

    public ArticleResponse create(ArticleRequest request) {
        Article article = new Article();
        applyRequest(article, request);
        return ArticleResponse.from(articleRepository.save(article));
    }

    public ArticleResponse update(Long id, ArticleRequest request) {
        Article article = findOrThrow(id);
        applyRequest(article, request);
        return ArticleResponse.from(articleRepository.save(article));
    }

    public void delete(Long id) {
        findOrThrow(id);
        articleRepository.deleteById(id);
    }

    private Article findOrThrow(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found: " + id));
    }

    private void applyRequest(Article article, ArticleRequest request) {
        article.setName(request.name());
        article.setDescription(request.description());
        article.setPrice(request.price());
        article.setStockQuantity(request.stockQuantity());
        article.setCategory(request.category());
        article.setImageUrl(request.imageUrl());
    }
}
