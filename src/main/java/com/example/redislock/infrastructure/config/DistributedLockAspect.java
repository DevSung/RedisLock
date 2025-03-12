package com.example.redislock.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        Object[] args = joinPoint.getArgs();
        validateArgs(args);

        Long id = extractId(args, distributedLock);
        String lockKey = distributedLock.key() + id;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!tryAcquireLock(lock, distributedLock)) {
                throw new RuntimeException("락을 획득할 수 없습니다: " + lockKey);
            }

            LockTransactionSynchronizer synchronizer = new LockTransactionSynchronizer(lock);
            return synchronizer.executeWithTransactionSync(joinPoint::proceed);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 획득 중 인터럽트 발생", e);
        }
    }

    private void validateArgs(Object[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("DistributedLock 애노테이션이 적용된 메서드는 적어도 하나의 인자를 가져야 합니다.");
        }
    }

    private Long extractId(Object[] args, DistributedLock distributedLock) {
        try {
            return (Long) args[distributedLock.paramIndex()];
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("DistributedLock 애노테이션이 적용된 메서드의 첫 번째 인자는 Long 타입이어야 합니다.");
        }
    }

    private boolean tryAcquireLock(RLock lock, DistributedLock distributedLock) throws InterruptedException {
        return lock.tryLock(
                distributedLock.waitTime(),
                distributedLock.leaseTime(),
                distributedLock.timeUnit()
        );
    }
}



