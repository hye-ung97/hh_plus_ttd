package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PointService 단위 테스트 - TDD")
public class PointServiceTest {

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private PointService pointService;

    private static final long USER_ID = 1L;

    private UserPoint createUserPoint(long userId, long point) {
        return new UserPoint(userId, point, System.currentTimeMillis());
    }

    @Test
    @DisplayName("포인트 조회 성공")
    void getPoint_success() {
        // given
        long point = 100L;
        UserPoint userPoint = createUserPoint(USER_ID, point);
        when(userPointTable.selectById(USER_ID)).thenReturn(userPoint);

        // when
        UserPoint result = pointService.getPointById(USER_ID);

        // then
        assertThat(result).isEqualTo(userPoint);
        verify(userPointTable, times(1)).selectById(USER_ID);
    }

    @Test
    @DisplayName("포인트 충전 성공")
    void charge_success() {
        // given
        long initialPoint = 100L;
        long chargeAmount = 100L;
        long expectedPoint = initialPoint + chargeAmount;
        
        UserPoint currentPoint = createUserPoint(USER_ID, initialPoint);
        UserPoint updatedPoint = createUserPoint(USER_ID, expectedPoint);
        
        when(userPointTable.selectById(USER_ID)).thenReturn(currentPoint);
        when(userPointTable.insertOrUpdate(USER_ID, expectedPoint)).thenReturn(updatedPoint);

        // when
        UserPoint result = pointService.chargePoint(USER_ID, chargeAmount);

        // then
        assertThat(result.point()).isEqualTo(expectedPoint);
        verify(userPointTable, times(1)).selectById(USER_ID);
        verify(userPointTable, times(1)).insertOrUpdate(USER_ID, expectedPoint);
        verify(pointHistoryTable, times(1)).insert(USER_ID, chargeAmount, TransactionType.CHARGE, updatedPoint.updateMillis());
    }

    @Test
    @DisplayName("포인트 사용 성공")
    void usePoint_success() {
        // given
        long initialPoint = 1000L;
        long useAmount = 100L;
        long expectedPoint = initialPoint - useAmount;
        
        UserPoint currentPoint = createUserPoint(USER_ID, initialPoint);
        UserPoint updatedPoint = createUserPoint(USER_ID, expectedPoint);

        when(userPointTable.selectById(USER_ID)).thenReturn(currentPoint);
        when(userPointTable.insertOrUpdate(USER_ID, expectedPoint)).thenReturn(updatedPoint);
        when(pointHistoryTable.insert(USER_ID, useAmount, TransactionType.USE, updatedPoint.updateMillis())).thenReturn(new PointHistory(1, USER_ID, useAmount, TransactionType.USE, updatedPoint.updateMillis()));

        //when
        UserPoint result = pointService.usePoint(USER_ID, useAmount);

        //then
        assertThat(result.point()).isEqualTo(expectedPoint);
        assertThat(result.id()).isEqualTo(USER_ID);
        verify(userPointTable, times(1)).selectById(USER_ID);
        verify(userPointTable, times(1)).insertOrUpdate(USER_ID, expectedPoint);
        verify(pointHistoryTable, times(1)).insert(USER_ID, useAmount, TransactionType.USE, updatedPoint.updateMillis());
    }
}
