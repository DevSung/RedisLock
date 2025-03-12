package com.example.redislock.infrastructure.persistence.mapper;

import com.example.redislock.domain.product.model.Product;
import com.example.redislock.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductEntity productEntity);

    ProductEntity toProductEntity(Product product);
}
