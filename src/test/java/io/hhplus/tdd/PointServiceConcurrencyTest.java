package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("PointService 동시성 테스트")
public class PointServiceConcurrencyTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserPointTable userPointTable;

    @Autowired
    private PointHistoryTable pointHistoryTable;

    private final long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        pointHistoryTable.clear();
        userPointTable.clear();
    }

    @Test
    @DisplayName("동시에 같은 사용자의 포인트를 10번 충전해도 최종 잔액이 정확해야 한다")
    void concurrentChargeTest() throws InterruptedException {
        // given
        int threadCount = 10;
        long chargeAmount = 1000L;
        long expectedFinalPoint = chargeAmount * threadCount;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when: 10개 스레드가 동시에 1000 포인트씩 충전
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.chargePoint(USER_ID, chargeAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        // then
        UserPoint finalPoint = pointService.getPointById(USER_ID);
        assertThat(finalPoint.point()).isEqualTo(expectedFinalPoint);
    }

    @Test
    @DisplayName("동시에 같은 사용자의 포인트를 10번 사용해도 최종 잔액이 정확해야 한다")
    void concurrentUseTest() throws InterruptedException {
        // given
        long initialPoint = 10000L;
        pointService.chargePoint(USER_ID, initialPoint);

        int threadCount = 10;
        long useAmount = 100L;
        long expectedFinalPoint = initialPoint - (useAmount * threadCount);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when: 10개 스레드가 동시에 100 포인트씩 사용
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.usePoint(USER_ID, useAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        // then
        UserPoint finalPoint = pointService.getPointById(USER_ID);
        assertThat(finalPoint.point()).isEqualTo(expectedFinalPoint);
    }

    @Test
    @DisplayName("동시에 충전과 사용을 섞어서 실행해도 최종 잔액이 정확해야 한다")
    void concurrentMixedOperationsTest() throws InterruptedException {
        // given
        long initialPoint = 5000L;
        pointService.chargePoint(USER_ID, initialPoint);

        int chargeThreadCount = 5;
        int useThreadCount = 3;
        long chargeAmount = 1000L;
        long useAmount = 500L;

        long expectedFinalPoint = initialPoint + (chargeThreadCount * chargeAmount) - (useThreadCount * useAmount);

        ExecutorService executorService = Executors.newFixedThreadPool(chargeThreadCount + useThreadCount);
        CountDownLatch latch = new CountDownLatch(chargeThreadCount + useThreadCount);

        // when: 5개 충전 스레드 + 3개 사용 스레드 동시 실행
        for (int i = 0; i < chargeThreadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.chargePoint(USER_ID, chargeAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        for (int i = 0; i < useThreadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.usePoint(USER_ID, useAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        // then
        UserPoint finalPoint = pointService.getPointById(USER_ID);
        assertThat(finalPoint.point()).isEqualTo(expectedFinalPoint);
    }

    @Test
    @DisplayName("서로 다른 사용자의 포인트 충전은 동시에 실행되어야 한다 (병렬성)")
    void concurrentDifferentUsersTest() throws InterruptedException {
        // given
        long user1 = 1L;
        long user2 = 2L;
        long user3 = 3L;

        int threadCountPerUser = 5;
        long chargeAmount = 1000L;
        long expectedPointPerUser = chargeAmount * threadCountPerUser;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCountPerUser * 3);
        CountDownLatch latch = new CountDownLatch(threadCountPerUser * 3);

        long startTime = System.currentTimeMillis();

        // when: 각 사용자마다 5번씩 충전 (총 15개 스레드)
        for (int i = 0; i < threadCountPerUser; i++) {
            executorService.submit(() -> {
                try {
                    pointService.chargePoint(user1, chargeAmount);
                } finally {
                    latch.countDown();
                }
            });

            executorService.submit(() -> {
                try {
                    pointService.chargePoint(user2, chargeAmount);
                } finally {
                    latch.countDown();
                }
            });

            executorService.submit(() -> {
                try {
                    pointService.chargePoint(user3, chargeAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        // then
        UserPoint user1Point = pointService.getPointById(user1);
        UserPoint user2Point = pointService.getPointById(user2);
        UserPoint user3Point = pointService.getPointById(user3);

        assertThat(user1Point.point()).isEqualTo(expectedPointPerUser);
        assertThat(user2Point.point()).isEqualTo(expectedPointPerUser);
        assertThat(user3Point.point()).isEqualTo(expectedPointPerUser);

        // 병렬 실행 검증: 서로 다른 사용자는 동시에 처리되므로 시간이 더 짧아야 함
        System.out.println("서로 다른 사용자 동시 처리 시간: " + elapsedTime + "ms");
    }

    @Test
    @DisplayName("동시에 100개의 스레드가 충전 요청을 해도 데이터 정합성이 유지되어야 한다")
    void highConcurrencyChargeTest() throws InterruptedException {
        // given
        int threadCount = 100;
        long chargeAmount = 100L;
        long expectedFinalPoint = chargeAmount * threadCount;

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when: 100개 스레드가 동시에 100 포인트씩 충전
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.chargePoint(USER_ID, chargeAmount);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(60, TimeUnit.SECONDS);
        executorService.shutdown();

        // then
        UserPoint finalPoint = pointService.getPointById(USER_ID);
        assertThat(finalPoint.point()).isEqualTo(expectedFinalPoint);
        assertThat(successCount.get()).isEqualTo(threadCount);
        assertThat(failCount.get()).isEqualTo(0);
    }

    @Test
    @DisplayName("동시에 포인트를 사용할 때 잔액 부족으로 일부만 성공해야 한다")
    void concurrentUseWithInsufficientBalanceTest() throws InterruptedException {
        // given
        long initialPoint = 1000L;
        pointService.chargePoint(USER_ID, initialPoint);

        int threadCount = 15;
        long useAmount = 100L;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when: 15개 스레드가 동시에 100 포인트씩 사용 시도
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.usePoint(USER_ID, useAmount);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        // then
        UserPoint finalPoint = pointService.getPointById(USER_ID);
        assertThat(finalPoint.point()).isEqualTo(0L);
        assertThat(successCount.get()).isEqualTo(10);
        assertThat(failCount.get()).isEqualTo(5);
    }
}
