package com.example.redislock.infrastructure.repository.impl;


import com.example.redislock.domain.product.model.Product;
import com.example.redislock.domain.product.repository.ProductRepository;
import com.example.redislock.infrastructure.persistence.entity.ProductEntity;
import com.example.redislock.infrastructure.persistence.mapper.ProductMapper;
import com.example.redislock.infrastructure.repository.JpaProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductMapper productMapper;
    private final JpaProductRepository jpaProductRepository;

    @Override
    public Optional<Product> findById(Long productId) {
        return jpaProductRepository.findById(productId)
                .map(productMapper::toProduct);
    }

    @Override
    public void save(Product product) {
        ProductEntity productEntity = productMapper.toProductEntity(product);
        jpaProductRepository.save(productEntity);
    }

}
