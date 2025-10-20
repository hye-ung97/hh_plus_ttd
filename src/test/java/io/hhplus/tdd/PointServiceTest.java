package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
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

    @Test
    @DisplayName("포인트 조회 성공")
    void getPoint_success() {
        // given
        long userId = 1L;
        long point = 100L;
        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());
        when(userPointTable.selectById(userId)).thenReturn(userPoint);

        // when
        UserPoint result = pointService.getPointById(userId);

        // then
        assertThat(result).isEqualTo(userPoint);
        verify(userPointTable, times(1)).selectById(userId);
    }

    @Test
    @DisplayName("포인트 충전 성공")
    void charge_success() {
        // given
        long userId = 1L;
        long initialPoint = 100L;
        long chargeAmount = 100L;
        long expectedPoint = initialPoint + chargeAmount;
        
        UserPoint currentPoint = new UserPoint(userId, initialPoint, System.currentTimeMillis());
        UserPoint updatedPoint = new UserPoint(userId, expectedPoint, System.currentTimeMillis());
        
        when(userPointTable.selectById(userId)).thenReturn(currentPoint);
        when(userPointTable.insertOrUpdate(userId, expectedPoint)).thenReturn(updatedPoint);

        // when
        UserPoint result = pointService.chargePoint(userId, chargeAmount);

        // then
        assertThat(result.point()).isEqualTo(expectedPoint);
        verify(userPointTable, times(1)).selectById(userId);
        verify(userPointTable, times(1)).insertOrUpdate(userId, expectedPoint);
        verify(pointHistoryTable, times(1)).insert(userId, chargeAmount, TransactionType.CHARGE, updatedPoint.updateMillis());
    }
}
