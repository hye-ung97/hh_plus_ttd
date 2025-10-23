# hhplus week 1 TDD - 포인트 관리 시스템

## 🔄 TDD 사이클 적용 방식

[![](https://mermaid.ink/img/pako:eNqlU8uO2yAU_RVENq3kpOC3mWqkSWbZ2UwXlVp3gc3FtkKMhbGSaZRvmVVH6t_1E0ocuxPNsmXFgfO4wOWISy0AMyyV3pc1NxZ9esxb5Mbdu285_v38_BN9MY0FxD8W5sOt5I1q2gpZ6G2Ov7-_cNcT9xd64FtAtoaRfCahjvf9K3MzMV_QI0heWm0ue1MmWi5v0XoyHcFm0o3gbiaWyrneg0QGBJKNUmwhZRInkddbo7fAFiIOSEAnuNw3wtbM7w5eqZU2bOGLIAxiT-rWLvfQVLVlhVbi5o19ZQDaKSCKQJbh3wBCijQL_zegUANM_klYZFK--mdpCME_-F8luAt1F3S9sL4c6XppMxYx69zrbj_bJwWIeNTz0VxO4RdQvikn6A432MOVaQRm1gzg4R2YHT9DfDz75dj1wg5yzNxUuBcflGubvD05Wcfbr1rvZqXRQ1XPYOgEt3Df8Mpwx5Bc9WcKtALMRg-txYySKB5NMDvig8NJvEoyP4nilGaUevgJMz_0V1GUkjSOKMkykpw8_GPMJKs4oDFNkjSMCKG-7wQgGtePD5cPMf6L0x-x7v3s?type=png)](https://mermaid.live/edit#pako:eNqlU8uO2yAU_RVENq3kpOC3mWqkSWbZ2UwXlVp3gc3FtkKMhbGSaZRvmVVH6t_1E0ocuxPNsmXFgfO4wOWISy0AMyyV3pg1NxZ9esxb5Mbdu285_v38_BN9MY0FxD8W5sOt5I1q2gpZ6G2Ov7-_cNcT9xd64FtAtoaRfCahjvf9K3MzMV_QI0heWm0ue1MmWi5v0XoyHcFm0o3gbiaWyrneg0QGBJKNUmwhZRInkddbo7fAFiIOSEAnuNw3wtbM7w5eqZU2bOGLIAxiT-rWLvfQVLVlhVbi5o19ZQDaKSCKQJbh3wBCijQL_zegUANM_klYZFK--mdpCME_-F8luAt1F3S9sL4c6XppMxYx69zrbj_bJwWIeNTz0VxO4RdQvikn6A432MOVaQRm1gzg4R2YHT9DfDz75dj1wg5yzNxUuBcflGubvD05Wcfbr1rvZqXRQ1XPYOgEt3Df8Mpwx5Bc9WcKtALMRg-txYySKB5NMDvig8NJvEoyP4nilGaUevgJMz_0V1GUkjSOKMkykpw8_GPMJKs4oDFNkjSMCKG-7wQgGtePD5cPMf6L0x-x7v3s)

### Red-Green-Refactor 사이클

1. **🔴 Red (실패하는 테스트 작성)**
   - 테스트를 먼저 작성하여 컴파일 에러를 발생시킨다
   - 테스트가 실패하는 것을 확인한다

2. **🟢 Green (테스트 통과시키기)**
   - 해당 테스트를 통과하기 위한 최소한의 코드를 작성한다
   - 테스트가 통과하는 것을 확인한다

3. **🔵 Refactor (리팩토링)**
   - 중복되는 코드 제거 및 리팩토링을 진행한다
   - 코드 품질을 개선한다

4. **🔄 반복**
   - 위 사이클을 반복하며 기능을 확장해 나간다

## ✏️ 테스트 구현 내용

### 단위 테스트 (PointServiceTest)
- **포인트 조회**: 사용자 포인트 조회 기능 테스트
- **포인트 충전**: 포인트 충전 및 내역 저장 테스트
- **포인트 사용**: 포인트 사용 및 내역 저장 테스트
- **포인트 내역 조회**: 거래 내역 조회 기능 테스트

### 동시성 테스트 (PointServiceConcurrencyTest)

- **동시 충전 테스트**: 10개 스레드가 동시에 포인트 충전
- **동시 사용 테스트**: 10개 스레드가 동시에 포인트 사용
- **혼합 작업 테스트**: 충전과 사용을 동시에 실행
- **다중 사용자 병렬성 테스트**: 서로 다른 사용자는 동시 처리됨을 검증
- **대량 동시 요청 테스트**: 100개 스레드로 데이터 정합성 검증
- **잔액 부족 동시성 테스트**: 잔액 부족 시 일부만 성공함을 검증

## 🤖 Claude Code 활용

### TDD 검증 커맨드
- 테스트 코드가 TDD 사이클에 맞는 테스트인지 확인하는 커맨드 생성
- 각 테스트마다 커맨드를 이용하여 TDD 사이클에 맞는지 검증
- 서비스 함수를 보고 각 함수에 맞는 단위 테스트 케이스 작성

## 📜 비즈니스 정책

### 포인트 충전 정책
- **최소 충전 금액**: 100원 이상
- **최대 보유 한도**: 100,000원
- **충전 금액 제한**: 0원 및 음수 금액 불가

### 포인트 사용 정책
- **사용 단위**: 100원 단위로만 사용 가능
- **잔액 확인**: 사용 금액이 보유 포인트를 초과할 수 없음
- **사용 금액 제한**: 0원 및 음수 금액 사용 불가

## 🔒 동시성 제어

### 동시성 제어 방법

#### 1. ReentrantLock
synchronized의 기능을 확장하면서 공정성, 타임아웃, 조건 변수(Condition) 같은 세밀한 제어를 제공하는 락이다. 같은 스레드가 이미 획득한 락을 다시 획득할 수 있는 재진입 가능(Reentrant) 특성을 가지고 있다

##### 특징

1. 명시적 락 관리
    - lock(), unlock()을 명시적으로 호출하며 락의 획득과 해제를 직접 제어할 수 있다
2. 재진입 가능
    - 같은 스레드가 여러 번 락을 획득 가능하며 획득한 횟수만큼 해제해야 한다
3. 공정성 옵션
    - 대기 시간이 긴 스레드에게 우선권을 부여할 수 있다
4. 조건 변수
    - 여러 개의 wait/notify 조건을 만들 수 있다

**사용 방식**
```java
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private int count = 0;
    private final ReentrantLock lock = new ReentrantLock();
    
    public void increment() {
        lock.lock();  // 락 획득
        try {
            count++;
        } finally {
            lock.unlock();  // 반드시 finally에서 해제
        }
    }
}
```

**타임아웃 설정**

```java
import java.util.concurrent.TimeUnit;

public void incrementWithTimeout() {
    try {
        // 3초 동안 락 획득 시도
        if (lock.tryLock(3, TimeUnit.SECONDS)) {
            try {
                count++;
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println("락 획득 실패");
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}
```

**공정한 락**
```java
// 대기 시간이 긴 스레드에게 우선권
private final ReentrantLock fairLock = new ReentrantLock(true);

public void fairIncrement() {
    fairLock.lock();
    try {
        count++;
    } finally {
        fairLock.unlock();
    }
}
```

**condition - wait/notify**

```java
import java.util.concurrent.locks.Condition;

public class BoundedBuffer {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    private final Object[] items = new Object[100];
    private int count, putIndex, takeIndex;
    
    public void put(Object item) throws InterruptedException {
        lock.lock();
        try {
            while (count == items.length) {
                notFull.await();  // 버퍼가 가득 차면 대기
            }
            items[putIndex] = item;
            if (++putIndex == items.length) putIndex = 0;
            count++;
            notEmpty.signal();  // 소비자에게 알림
        } finally {
            lock.unlock();
        }
    }
    
    public Object take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();  // 버퍼가 비어있으면 대기
            }
            Object item = items[takeIndex];
            if (++takeIndex == items.length) takeIndex = 0;
            count--;
            notFull.signal();  // 생산자에게 알림
            return item;
        } finally {
            lock.unlock();
        }
    }
}
```

##### 장점

- tryLock, 공정성, Condition 등 다양한 옵션을 제공하여 세밀한 제어가 가능하다
- 타임아웃을 지원하여 데드락 방지가 가능하다
- 여러 조건을 독립적으로 관리 가능하다
- 재진입이 가능하여 같은 스레드가 여러 번 Lock 획득이 가능하다

##### 단점

- synchronized보다 사용이 복잡하고, 실수하기 쉽다
- finally 블록에서 반드시 unlock()을 호출해야 하며, 빠뜨리면 심각한 문제가 발생한다

---

#### 2. Synchronized

한 시점에 하나의 스레드만 해당 코드 블록을 실행하도록 보장하는 방식

##### 특징
1. 상호배제
   - 한 번에 하나의 스레드만 Synchronized 블록/메서드에 접근 가능
   - 다른 스레드들은 락이 해제될 때까지 대기
2. 가시성 보장
    - 한 스레드의 변경 사항이 다른 스레드에게 즉시 보임
    - 메모리 일관성 보장
3. 재진입 가능
    - 같은 스레드는 이미 획득한 락을 다시 획득 가능

**동기화 메서드**
```java
public class Counter {
    private int count = 0;
    
    // 인스턴스 메서드 동기화 (this 객체에 락)
    public synchronized void increment() {
        count++;
    }
    
    // static 메서드 동기화 (Class 객체에 락)
    public static synchronized void staticMethod() {
        // ...
    }
}
```

**동기화 블록**
```java
public class Counter {
    private int count = 0;
    private Object lock = new Object();
    
    public void increment() {
        synchronized(this) {  // this 객체에 락
            count++;
        }
    }
    
    public void anotherMethod() {
        synchronized(lock) {  // 별도 객체에 락
            // 임계 영역
        }
    }
}
```

##### 장점
- 사용하기 쉽고 직관적이며, 명시적인 lock/unlock 관리가 필요 없음
- 예외가 발생해도 JVM이 자동으로 락을 해제
- happens-before 관계를 보장하여 스레드 간 변경 사항이 올바르게 전파된다
- 단순한 동기화 요구사항에는 충분하고 효율적이다

##### 단점
- 락을 세밀하게 제어하기 어렵다
- 락 대기 중인 스레드는 blocking 상태가 되어 성능 저하가 발생할 수 있다
- 여러 자원에 순서 없이 락을 걸면 교착 상태가 발생할 수 있다
- JVM 내부 락이라 멀티 인스턴스/멀티 서버 환경에서는 적용이 불가능하다

---

#### 3. Optimistic Lock
실제로 락을 걸지 않고, 데이터 변경 시점에 충돌을 검사하는 동시성 제어 방식

##### 특징
1. 락을 사용하지 않음
    - 실제 lock을 걸지 않아 블로킹이 없음
    - 읽기 작업이 자유로움
2. 버전 관리 방식
    - version / timestamp / hash
3. CAS (Compare-And-Swap) 기반
    - 원자적 연산으로 구현됨

**JPA에서 사용**
```java
import javax.persistence.*;

@Entity
public class Product {
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    private int stock;
    
    @Version  // JPA가 자동으로 버전 관리
    private Long version;
    
    // getters and setters
}

// 사용 예시
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    @Transactional
    public void updateStock(Long productId, int quantity) {
        try {
            Product product = productRepository.findById(productId)
                .orElseThrow();
            
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
            
        } catch (OptimisticLockException e) {
            // 충돌 발생 시 처리
            System.out.println("다른 트랜잭션이 데이터를 수정했습니다.");
            // 재시도 로직
        }
    }
}
```

**Timestamp 사용**
```java
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

public class Document {
    private final AtomicReference<Content> contentRef;

    private static class Content {
        final String text;
        final LocalDateTime lastModified;

        Content(String text) {
            this.text = text;
            this.lastModified = LocalDateTime.now();
        }
    }

    public Document(String initialContent) {
        this.contentRef = new AtomicReference<>(new Content(initialContent));
    }

    // 낙관적 락으로 업데이트 (CAS 사용)
    public boolean update(String newContent, LocalDateTime expectedTimestamp) {
        Content current = contentRef.get();

        // 타임스탬프 비교
        if (!current.lastModified.equals(expectedTimestamp)) {
            return false;  // 충돌
        }

        // CAS(Compare-And-Swap)로 원자적 업데이트
        Content newContentObj = new Content(newContent);
        return contentRef.compareAndSet(current, newContentObj);
    }

    public String getContent() {
        return contentRef.get().text;
    }

    public LocalDateTime getLastModified() {
        return contentRef.get().lastModified;
    }
}
```

##### 장점
- 락을 걸지 않아 여러 스레드가 동시에 읽기 작업을 수행할 수 있어 높은 동시성이 있다
- 락을 사용하지 않아 데드락이 발생하지 않는다
- 읽기 작업에 대한 제약이 없어 읽기가 많은 환경에서 유리하다
- 락 경합이 없어 멀티코어 환경에서 확장성이 좋다
- 락 대기 시간이 없어 평균 응답 시간이 빠르다

##### 단점
- 충돌이 발생하면 재시도 로직을 구현해야 하므로 코드가 복잡해진다
- 버전 관리, 재시도 로직, 백오프 전략 등을 직접 구현해야 한다
- DB 환경에서 충돌 시 트랜잭션 롤백 비용이 발생한다
- 충돌이 계속 발생하는 스레드는 기아 상태에 빠질 수 있다


---

#### 4. Pessimistic Lock

데이터를 읽는 시점에 다른 트랜잭션이 동시에 수정할 가능성이 있다고 비관적으로 보고, 데이터에 즉시 락을 걸어 다른 트랜잭션의 접근을 차단하는 방식

##### 특징

1. 강력한 데이터 정합성
    - 동시 수정을 원천 차단하며 충돌을 미리 방지
2. 블로킹 방식
    - 락을 획득할 때까지 대기
    - 다른 트랜잭션이 블로킹됨
3. 선제적 락킹
    - 데이터에 접근하기 전에 먼저 락을 획득
    - 다른 트랜잭션은 락이 해제될 때까지 대기

**어노테이션으로 락 설정**
```java
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.*;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Wallet w where w.id = :id")
    Optional<Wallet> findByIdForUpdate(@Param("id") Long id);
}
```

**서비스 코드에서**
```java
@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;

    @Transactional
    public void increaseBalance(Long walletId, int amount) {
        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow();
        wallet.setBalance(wallet.getBalance() + amount);
    }
}
```

##### 장점

- 동시 수정을 원천 차단하여 데이터 일관성을 완벽하게 보장한다
- 충돌이 발생하기 전에 미리 차단하므로 재시도 로직이 필요 없다
- 락을 획득한 순서대로 처리되어 동작이 명확하고 예측 가능하다
- 충돌이 자주 발생하는 환경에서는 재시도 오버헤드가 없어 더 효율적이다

##### 단점

- 락 대기 시간으로 인해 처리 속도가 느려진다
- 한 번에 하나의 트랜잭션만 처리되어 동시성이 낮다
- 락 경합으로 인해 멀티코어 환경에서 확장성이 떨어진다
- 락 대기로 인해 응답 시간이 길어진다

---


#### 5. 분산 락 (Redis)

여러 서버(혹은 인스턴스)가 동시에 같은 리소스를 접근할 때, 전역적으로 단 하나의 주체만 임계 영역에 들어갈 수 있도록 보장하는 락

##### 구현 방법
1. Redis를 이용한 분산락
2. AOP를 이용
3. MySQL을 이용 (Named Lock)
4. Zookeeper를 이용

##### 장점
- 다중 서버 환경을 지원하여 여러 서버 간의 동시성을 제어할 수 있다
- 서버를 추가해도 동시성 제어가 정상 작동한다
- 락 상태를 한 곳에서 관리하여 일관성 보장이 쉽다
- 락 획득 대기 시간과 유효 시간을 세밀하게 제어 가능하다
- TTL 설정으로 데드락 방지

##### 단점
- 네트워크 지연이나 장애에 영향을 받는다
- Redis 등 별도의 추가 인프라가 필요하다
- 단일 서버 락보다 구현과 관리가 복잡하다
- 네트워크 통신으로 인한 지연이 발생한다

---


#### 6. Semaphore

동시에 접근할 수 있는 스레드의 수를 제한하는 방식

``` java
public class ConnectionPool {
    private final Semaphore semaphore = new Semaphore(3); // 동시에 3개만 허용

    public void useResource(String name) {
        try {
            semaphore.acquire(); // permit 없으면 대기
            System.out.println(name + " start using resource");
            Thread.sleep(1000);  // 리소스 사용
            System.out.println(name + " done");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaphore.release(); // permit 반환
        }
    }
}

public class App {
    public static void main(String[] args) {
        ConnectionPool pool = new ConnectionPool();

        for (int i = 1; i <= 10; i++) {
            int id = i;
            new Thread(() -> pool.useResource("Thread-" + id)).start();
        }
    }
}
```
➡️ 이 코드를 실행하면 10개의 스레드 중 동시에 3개까지만 리소스를 사용 (나머지는 대기 → 하나 끝나면 다음이 acquire.)

##### 특징
- 동시 접근 가능한 스레드 수를 제한
- Counting semaphore (permit 개수 관리)
- 리소스 풀 크기 제어에 적합

##### 스프링에서 잘 사용하지 않는 이유

- Thread 관리를 직접 하지 않고 → @Async, ExecutorService, WebFlux, Reactor 등 고수준 API로 대체됨
- 실제 병렬 제어는 스레드 풀 크기 제한이나 레이트 리미터(Bucket4j, Resilience4j)로 관리함
- 단일 인스턴스에서만 유효하므로, 멀티 인스턴스/분산 환경에서는 Redis 기반 제어가 필요

---
### 이 프로젝트에서 적용한 동시성 제어 방법과 이유

☑️ 이 프로젝트에서 적용한 동시성 제어 방법은 **ReentrantLock**

#### 1. 사용자별 격리
- 각 사용자마다 독립적인 Lock을 사용하여 서로 다른 사용자의 요청은 병렬로 처리 가능
- 전역 Lock에 비해 처리량(Throughput) 향상

#### 2. 공정성 보장 (Fairness)
- `new ReentrantLock(true)`로 공정성 옵션 활성화
- 먼저 대기한 스레드가 먼저 Lock을 획득하여 기아 상태(Starvation) 방지

#### 3. 단순성과 안정성
- synchronized보다 유연하면서도 구현이 복잡하지 않음
- 예외 발생 시에도 `finally` 블록에서 확실하게 unlock 보장

#### 4. 단일 JVM 환경에 적합
- 현재 프로젝트는 단일 서버 환경을 가정하므로 JVM 레벨 Lock으로 충분
- Redis 같은 외부 의존성 불필요
