package com.oskarott.webshoptemplatebackend.config;

import com.oskarott.webshoptemplatebackend.model.Article;
import com.oskarott.webshoptemplatebackend.model.Brand;
import com.oskarott.webshoptemplatebackend.model.Category;
import com.oskarott.webshoptemplatebackend.model.Role;
import com.oskarott.webshoptemplatebackend.model.UserEntity;
import com.oskarott.webshoptemplatebackend.repository.ArticleRepository;
import com.oskarott.webshoptemplatebackend.repository.BrandRepository;
import com.oskarott.webshoptemplatebackend.repository.CategoryRepository;
import com.oskarott.webshoptemplatebackend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("local")
public class DevDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ArticleRepository articleRepository;

    public DevDataInitializer(UserRepository userRepository,
                               PasswordEncoder passwordEncoder,
                               CategoryRepository categoryRepository,
                               BrandRepository brandRepository,
                               ArticleRepository articleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedCategories();
        seedBrands();
        seedArticles();
    }

    private void seedAdmin() {
        if (userRepository.existsByEmail("admin@local.dev")) {
            return;
        }
        UserEntity admin = new UserEntity();
        admin.setEmail("admin@local.dev");
        admin.setFirstName("Admin");
        admin.setLastName("Local");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }

    private void seedCategories() {
        if (categoryRepository.existsByName("Clothing")) {
            return;
        }
        Category clothing = saveCategory("Clothing", null);
        Category electronics = saveCategory("Electronics", null);
        Category sports = saveCategory("Sports", null);

        saveCategory("T-Shirts", clothing);
        saveCategory("Phones", electronics);
        saveCategory("Running", sports);
    }

    private Category saveCategory(String name, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setParent(parent);
        return categoryRepository.save(category);
    }

    private void seedBrands() {
        if (brandRepository.existsByName("Nike")) {
            return;
        }
        saveBrand("Nike",
                "https://upload.wikimedia.org/wikipedia/commons/a/a6/Logo_NIKE.svg",
                "Nike is a global leader in athletic footwear, apparel and equipment.");
        saveBrand("Apple",
                "https://upload.wikimedia.org/wikipedia/commons/f/fa/Apple_logo_black.svg",
                "Apple designs and manufactures consumer electronics, software and services.");
        saveBrand("Samsung",
                "https://upload.wikimedia.org/wikipedia/commons/2/24/Samsung_Logo.svg",
                "Samsung is a multinational conglomerate known for electronics and semiconductors.");
    }

    private void saveBrand(String name, String logoUrl, String description) {
        Brand brand = new Brand();
        brand.setName(name);
        brand.setLogoUrl(logoUrl);
        brand.setDescription(description);
        brandRepository.save(brand);
    }

    private void seedArticles() {
        if (articleRepository.count() > 0) {
            return;
        }

        Category tShirts = categoryRepository.findByName("T-Shirts").orElse(null);
        Category phones = categoryRepository.findByName("Phones").orElse(null);
        Category running = categoryRepository.findByName("Running").orElse(null);

        Brand nike = brandRepository.findByName("Nike").orElse(null);
        Brand apple = brandRepository.findByName("Apple").orElse(null);
        Brand samsung = brandRepository.findByName("Samsung").orElse(null);

        saveArticle("Nike Dri-FIT T-Shirt", "Lightweight moisture-wicking training tee.", new BigDecimal("29.99"), 120,
                tShirts, nike,
                List.of(
                        "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400",
                        "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=400",
                        "https://images.unsplash.com/photo-1562157873-818bc0726f68?w=400"
                ),
                "NK-TSHIRT-BLK-M", "M", new BigDecimal("0.200"), "Black", List.of("t-shirt", "training", "dri-fit"));

        saveArticle("Nike Air Max 270", "Casual lifestyle sneaker with large Air unit.", new BigDecimal("149.99"), 45,
                running, nike,
                List.of(
                        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400",
                        "https://images.unsplash.com/photo-1605348532760-6753d2c43329?w=400",
                        "https://images.unsplash.com/photo-1600185365483-26d7a4cc7519?w=400"
                ),
                "NK-AM270-WHT-42", "42", new BigDecimal("0.750"), "White", List.of("shoes", "sneaker", "air-max"));

        saveArticle("Nike Running Shorts", "Lightweight shorts built for speed.", new BigDecimal("34.99"), 80,
                running, nike,
                List.of(
                        "https://images.unsplash.com/photo-1506629082955-511b1aa562c8?w=400",
                        "https://images.unsplash.com/photo-1556906781-9a412961a28c?w=400"
                ),
                "NK-SHORTS-BLU-L", "L", new BigDecimal("0.150"), "Blue", List.of("shorts", "running", "sport"));

        saveArticle("Apple iPhone 15", "6.1-inch Super Retina XDR display, A16 Bionic chip.", new BigDecimal("999.00"), 30,
                phones, apple,
                List.of(
                        "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400",
                        "https://images.unsplash.com/photo-1591337676887-a217a6970a8a?w=400",
                        "https://images.unsplash.com/photo-1574755393849-623942496936?w=400"
                ),
                "APL-IP15-BLK-128", null, new BigDecimal("0.171"), "Black", List.of("iphone", "smartphone", "apple"));

        saveArticle("Apple iPhone 15 Pro", "Titanium design with A17 Pro chip and ProRes video.", new BigDecimal("1199.00"), 20,
                phones, apple,
                List.of(
                        "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400",
                        "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400"
                ),
                "APL-IP15PRO-TIT-256", null, new BigDecimal("0.187"), "Natural Titanium", List.of("iphone", "pro", "smartphone"));

        saveArticle("Samsung Galaxy S24", "6.2-inch Dynamic AMOLED, Snapdragon 8 Gen 3.", new BigDecimal("849.00"), 35,
                phones, samsung,
                List.of(
                        "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400",
                        "https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=400",
                        "https://images.unsplash.com/photo-1567581935884-3349723552ca?w=400"
                ),
                "SAM-S24-VLT-128", null, new BigDecimal("0.167"), "Violet", List.of("samsung", "galaxy", "android"));

        saveArticle("Samsung Galaxy Watch 6", "Sapphire crystal display with advanced health tracking.", new BigDecimal("299.00"), 50,
                null, samsung,
                List.of(
                        "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400",
                        "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=400"
                ),
                "SAM-GW6-SLV-44", "44mm", new BigDecimal("0.059"), "Silver", List.of("smartwatch", "samsung", "wearable"));

        saveArticle("Nike Pro Compression Tee", "Second-skin fit with Dri-FIT ADV technology.", new BigDecimal("44.99"), 60,
                tShirts, nike,
                List.of(
                        "https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=400",
                        "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=400",
                        "https://images.unsplash.com/photo-1562157873-818bc0726f68?w=400"
                ),
                "NK-PROTEE-GRY-L", "L", new BigDecimal("0.180"), "Grey", List.of("t-shirt", "compression", "pro"));
    }

    private void saveArticle(String name, String description, BigDecimal price, int stock,
                              Category category, Brand brand, List<String> images,
                              String sku, String size, BigDecimal weight, String color, List<String> tags) {
        Article article = new Article();
        article.setName(name);
        article.setDescription(description);
        article.setPrice(price);
        article.setStockQuantity(stock);
        article.setCategory(category);
        article.setBrand(brand);
        article.setImages(images);
        article.setSku(sku);
        article.setSize(size);
        article.setWeight(weight);
        article.setColor(color);
        article.setTags(tags);
        articleRepository.save(article);
    }
}

