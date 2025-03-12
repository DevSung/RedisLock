package com.example.redislock.infrastructure.config;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * 락의 이름 (접두사)
     */
    String key();

    /**
     * 락 키를 생성할 파라미터 인덱스 (기본값: 0)
     */
    int paramIndex() default 0;

    /**
     * 락의 시간 단위
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 락 대기 시간 (default - 5초)
     */
    long waitTime() default 5L;

    /**
     * 락 유지 시간 (default - 3초)
     */
    long leaseTime() default 3L;

}
