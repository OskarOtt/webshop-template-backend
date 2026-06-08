package com.oskarott.webshoptemplatebackend.resource;

import com.oskarott.webshoptemplatebackend.dto.ArticleRequest;
import com.oskarott.webshoptemplatebackend.dto.ArticleResponse;
import com.oskarott.webshoptemplatebackend.model.ArticleStatus;
import com.oskarott.webshoptemplatebackend.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Articles", description = "Article management endpoints")
@RestController
@RequestMapping("/articles")
public class ArticleResource {

    private final ArticleService articleService;

    public ArticleResource(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Operation(summary = "Get articles by status (defaults to ACTIVE). ADMIN required for non-ACTIVE statuses.")
    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getAll(
            @RequestParam(required = false) ArticleStatus status) {
        return ResponseEntity.ok(articleService.getAll(status));
    }

    @Operation(summary = "Get article by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getById(id));
    }

    @Operation(summary = "Get articles by category ID (defaults to ACTIVE)")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ArticleResponse>> getByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) ArticleStatus status) {
        return ResponseEntity.ok(articleService.getByCategory(categoryId, status));
    }

    @Operation(summary = "Create a new article (ADMIN only)", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "201", description = "Article created",
                    content = @Content(schema = @Schema(implementation = ArticleResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArticleResponse> create(@RequestBody ArticleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(articleService.create(request));
    }

    @Operation(summary = "Update an article (ADMIN only)", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Article updated",
                    content = @Content(schema = @Schema(implementation = ArticleResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Article not found", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArticleResponse> update(@PathVariable Long id, @RequestBody ArticleRequest request) {
        return ResponseEntity.ok(articleService.update(id, request));
    }

    @Operation(summary = "Change article status to ACTIVE or DISABLED (ADMIN only)", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Status updated",
                    content = @Content(schema = @Schema(implementation = ArticleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Article not found", content = @Content)
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArticleResponse> changeStatus(
            @PathVariable Long id,
            @RequestParam ArticleStatus status) {
        return ResponseEntity.ok(articleService.changeStatus(id, status));
    }

    @Operation(summary = "Soft-delete an article (ADMIN only) — sets status to DELETED",
            security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "204", description = "Article soft-deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Article not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
