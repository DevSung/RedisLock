package com.example.redislock.domain.product.repository;

import com.example.redislock.domain.product.model.Product;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long productId);

    void save(Product product);
}
