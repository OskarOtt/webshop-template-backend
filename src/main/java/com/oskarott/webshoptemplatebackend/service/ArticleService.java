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
    private final CategoryService categoryService;
    private final BrandService brandService;

    public ArticleService(ArticleRepository articleRepository,
                          CategoryService categoryService,
                          BrandService brandService) {
        this.articleRepository = articleRepository;
        this.categoryService = categoryService;
        this.brandService = brandService;
    }

    public List<ArticleResponse> getAll() {
        return articleRepository.findAll().stream()
                .map(ArticleResponse::from)
                .toList();
    }

    public ArticleResponse getById(Long id) {
        return ArticleResponse.from(findOrThrow(id));
    }

    public List<ArticleResponse> getByCategory(Long categoryId) {
        return articleRepository.findByCategoryId(categoryId).stream()
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
        article.setImages(request.images() != null ? request.images() : List.of());
        article.setSku(request.sku());
        article.setSize(request.size());
        article.setWeight(request.weight());
        article.setColor(request.color());
        article.setTags(request.tags() != null ? request.tags() : List.of());

        article.setCategory(request.categoryId() != null
                ? categoryService.findOrThrow(request.categoryId()) : null);
        article.setBrand(request.brandId() != null
                ? brandService.findOrThrow(request.brandId()) : null);
    }
}

