package com.example.redislock.domain.product.service;

import com.example.redislock.domain.product.model.Product;
import com.example.redislock.domain.product.repository.ProductRepository;
import com.example.redislock.infrastructure.config.DistributedLock;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    @DistributedLock(key = "PRODUCT_STOCK_LOCK_")
    public Product decreaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        product.decreaseStock(quantity);
        productRepository.save(product);
        return product;
    }

}
