package com.example.redislock.infrastructure.config;


import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@RequiredArgsConstructor
public class LockTransactionSynchronizer {

    private final RLock lock;

    public Object executeWithTransactionSync(TransactionalOperation operation) throws Throwable {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            registerUnlockSynchronization();
            return operation.execute();
        } else {
            return executeWithoutTransaction(operation);
        }
    }

    private void registerUnlockSynchronization() {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                unlockIfHeldByCurrentThread();
            }
        });
    }

    private Object executeWithoutTransaction(TransactionalOperation operation) throws Throwable {
        try {
            return operation.execute();
        } finally {
            unlockIfHeldByCurrentThread();
        }
    }

    private void unlockIfHeldByCurrentThread() {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    @FunctionalInterface
    public interface TransactionalOperation {
        Object execute() throws Throwable;
    }

}
