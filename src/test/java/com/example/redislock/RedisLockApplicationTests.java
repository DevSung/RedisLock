package com.example.redislock;

import com.example.redislock.domain.product.model.Product;
import com.example.redislock.domain.product.repository.ProductRepository;
import com.example.redislock.domain.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class ProductServiceIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceIntegrationTest.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void saveTestProduct() {
        Product product = Product.createProduct("테스트 상품", 1000);
        productRepository.save(product);

        log.info("상품 저장 완료 - 상품명: {}, 재고: {}", product.getName(), product.getStock());
    }

    @Test
    void 동시에_500명이_주문할때_재고확인() throws InterruptedException {
        Long productId = 1L;
        int userCount = 500; // 사용자 수
        ExecutorService executorService = Executors.newFixedThreadPool(32); // 32개의 스레드를 사용
        CountDownLatch latch = new CountDownLatch(userCount);

        // 스레드 풀 초기화 시 생성된 스레드 출력
        for (int i = 0; i < 32; i++) {
            executorService.submit(() -> {
                String threadName = Thread.currentThread().getName();
                log.info("스레드 초기화 - 스레드 이름: {}", threadName);
            });
        }

        for (int i = 0; i < userCount; i++) {
            executorService.submit(() -> {
                try {
                    Product product = productService.decreaseStock(productId, 1);
                    log.info("스레드: {}, 남은재고: {}", Thread.currentThread().getName(), product.getStock());
                } catch (Exception e) {
                    log.error("재고 감소 중 오류 발생: {}", e.getMessage(), e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Product productAfterTest = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("테스트 후 상품을 찾을 수 없습니다"));

        log.info("테스트 완료 - 상품 ID: {}, 최종 재고: {}",
                productAfterTest.getId(), productAfterTest.getStock());
    }

}
