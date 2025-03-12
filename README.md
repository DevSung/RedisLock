# Redis Lock 예제 프로젝트

이 프로젝트는 **Redisson Lock**을 활용하여 다중 스레드 환경에서 데이터 일관성을 보장하는 샘플 프로젝트 입니다.  

---

## 프로젝트 구조

### 주요 클래스

#### **1. DistributedLock**
- 파일: `DistributedLock.java`
- 설명:  
  메서드에 적용되는 분산 락을 위한 커스텀 애노테이션.  
  락의 이름, 대기 시간, 유지 시간 등을 정의할 수 있습니다.
- 주요 구성:
  - **`key()`**: 락의 이름 또는 접두사가 되는 고유 키.
  - **`paramIndex()`**: 락 키를 생성할 파라미터 인덱스.
  - **`waitTime()`**: 락 대기 시간 (기본값: 5초).
  - **`leaseTime()`**: 락 유지 시간 (기본값: 3초).
  - **`timeUnit()`**: 시간 단위. 기본값은 초(SECONDS).

#### **2. DistributedLockAspect**
- 파일: `DistributedLockAspect.java`
- 설명:  
  AOP를 활용해 `@DistributedLock` 애노테이션을 처리하며, 락을 제어합니다.
- 주요 구성:
  - **`@Around("@annotation(distributedLock)")`**: `DistributedLock` 애노테이션이 적용된 메서드에 실행 시점 Aspect 추가.
  - Redisson의 `RLock`을 이용하여 락을 획득하거나 실패 시 예외를 발생.
  - 작업을 트랜잭션과 동기화하여 락을 안전하게 해제.

#### **3. LockTransactionSynchronizer**
- 파일: `LockTransactionSynchronizer.java`
- 설명:  
  락 해제를 트랜잭션과 동기화하여 트랜잭션 완료 후 락을 해제합니다.  
  트랜잭션이 활성화되지 않은 경우, 독립적인 락 해제 로직이 실행됩니다.
- 주요 구성:
  - **`executeWithTransactionSync()`**: 트랜잭션 상태에 따라 락의 해제 동작을 관리.
  - **`registerUnlockSynchronization()`**: 트랜잭션 완료 후 락 해제를 등록.
  - **`unlockIfHeldByCurrentThread()`**: 트랜잭션이 없거나 작업 완료 시 락 해제.

---
