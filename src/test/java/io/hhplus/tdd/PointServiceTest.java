package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.exception.ErrorCode;
import io.hhplus.tdd.point.exception.PointException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
    @DisplayName("포인트 충전 실패 - 최소 금액 미만")
    void chargePoint_fail_minimumAmount() {
        // given
        long initialPoint = 100L;
        long chargeAmount = 50L; // 최소 금액 100원 미만

        UserPoint currentPoint = createUserPoint(USER_ID, initialPoint);
        when(userPointTable.selectById(USER_ID)).thenReturn(currentPoint);

        // when & then
        PointException exception = assertThrows(PointException.class, () -> pointService.chargePoint(USER_ID, chargeAmount));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_CHARGE_AMOUNT);

        verify(userPointTable, times(1)).selectById(USER_ID);
        verify(userPointTable, times(0)).insertOrUpdate(USER_ID, initialPoint + chargeAmount);
    }

    @Test
    @DisplayName("포인트 충전 실패 - 한도 초과")
    void chargePoint_fail_exceedLimit() {
        // given
        long initialPoint = 99_000L;
        long chargeAmount = 2_000L; // 총 101,000원으로 한도 초과

        UserPoint currentPoint = createUserPoint(USER_ID, initialPoint);
        when(userPointTable.selectById(USER_ID)).thenReturn(currentPoint);

        // when & then
        PointException exception = assertThrows(PointException.class, () -> pointService.chargePoint(USER_ID, chargeAmount));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POINT_LIMIT_EXCEEDED);

        verify(userPointTable, times(1)).selectById(USER_ID);
        verify(userPointTable, times(0)).insertOrUpdate(USER_ID, initialPoint + chargeAmount);
    }

    @Test
    @DisplayName("포인트 충전 성공 - 경계값 테스트 (최소 금액)")
    void chargePoint_success_boundary_minimum() {
        // given
        long initialPoint = 0L;
        long chargeAmount = 100L; // 최소 충전 금액

        UserPoint currentPoint = createUserPoint(USER_ID, initialPoint);
        UserPoint updatedPoint = createUserPoint(USER_ID, initialPoint + chargeAmount);

        when(userPointTable.selectById(USER_ID)).thenReturn(currentPoint);
        when(userPointTable.insertOrUpdate(USER_ID, initialPoint + chargeAmount)).thenReturn(updatedPoint);

        // when
        UserPoint result = pointService.chargePoint(USER_ID, chargeAmount);

        // then
        assertThat(result.point()).isEqualTo(initialPoint + chargeAmount);
        verify(userPointTable, times(1)).selectById(USER_ID);
        verify(userPointTable, times(1)).insertOrUpdate(USER_ID, initialPoint + chargeAmount);
        verify(pointHistoryTable, times(1)).insert(USER_ID, chargeAmount, TransactionType.CHARGE, updatedPoint.updateMillis());
    }

    @Test
    @DisplayName("포인트 충전 실패 - 0원 충전 실패")
    void chargePoint_fail_zeroAmount() {
        // given
        long initialPoint = 100L;
        long chargeAmount = 0L;

        UserPoint currentPoint = createUserPoint(USER_ID, initialPoint);
        when(userPointTable.selectById(USER_ID)).thenReturn(currentPoint);

        // when & then
        PointException exception = assertThrows(PointException.class, () -> pointService.chargePoint(USER_ID, chargeAmount));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_CHARGE_AMOUNT);

        verify(userPointTable, times(1)).selectById(USER_ID);
        verify(userPointTable, times(0)).insertOrUpdate(USER_ID, initialPoint + chargeAmount);
    }

    @Test
    @DisplayName("포인트 충전 실패 - 음수 금액 충전 실패)")
    void chargePoint_fail_negativeAmount() {
        // given
        long initialPoint = 100L;
        long chargeAmount = -100L;

        UserPoint currentPoint = createUserPoint(USER_ID, initialPoint);
        when(userPointTable.selectById(USER_ID)).thenReturn(currentPoint);

        // when & then
        PointException exception = assertThrows(PointException.class, () -> pointService.chargePoint(USER_ID, chargeAmount));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_CHARGE_AMOUNT);

        verify(userPointTable, times(1)).selectById(USER_ID);
        verify(userPointTable, times(0)).insertOrUpdate(USER_ID, initialPoint + chargeAmount);
    }

    @Test
    @DisplayName("포인트 충전 성공 - 경계값 테스트 (최대 한도)")
    void chargePoint_success_boundary_maximum() {
        // given
        long initialPoint = 99_900L;
        long chargeAmount = 100L; // 총 100,000원으로 최대 한도

        UserPoint currentPoint = createUserPoint(USER_ID, initialPoint);
        UserPoint updatedPoint = createUserPoint(USER_ID, initialPoint + chargeAmount);

        when(userPointTable.selectById(USER_ID)).thenReturn(currentPoint);
        when(userPointTable.insertOrUpdate(USER_ID, initialPoint + chargeAmount)).thenReturn(updatedPoint);

        // when
        UserPoint result = pointService.chargePoint(USER_ID, chargeAmount);

        // then
        assertThat(result.point()).isEqualTo(initialPoint + chargeAmount);
        verify(userPointTable, times(1)).selectById(USER_ID);
        verify(userPointTable, times(1)).insertOrUpdate(USER_ID, initialPoint + chargeAmount);
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

    @Test
    @DisplayName("포인트 사용 실패 - 포인트 부족")
    void usePoint_fail_insufficientPoint() {
        // given
        long initialPoint = 100L;
        long useAmount = 150L; // 사용하려는 금액이 보유 포인트보다 많음

        UserPoint currentPoint = createUserPoint(USER_ID, initialPoint);
        when(userPointTable.selectById(USER_ID)).thenReturn(currentPoint);

        // when & then
        PointException exception = assertThrows(PointException.class, () -> pointService.usePoint(USER_ID, useAmount));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INSUFFICIENT_POINT);

        verify(userPointTable, times(1)).selectById(USER_ID);
        verify(userPointTable, times(0)).insertOrUpdate(USER_ID, initialPoint - useAmount);
    }

    @Test
    @DisplayName("포인트 사용 실패 - 금액 단위 오류")
    void usePoint_fail_invalidAmount() {
        // given
        long initialPoint = 1000L;
        long useAmount = 150L; // 100원 단위가 아님

        UserPoint currentPoint = createUserPoint(USER_ID, initialPoint);
        when(userPointTable.selectById(USER_ID)).thenReturn(currentPoint);

        // when & then
        PointException exception = assertThrows(PointException.class, () -> pointService.usePoint(USER_ID, useAmount));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_POINT_UNIT);

        verify(userPointTable, times(1)).selectById(USER_ID);
        verify(userPointTable, times(0)).insertOrUpdate(USER_ID, initialPoint - useAmount);
    }

    @Test
    @DisplayName("포인트 사용 성공 - 경계값 테스트 (100원 단위)")
    void usePoint_success_boundary_hundredUnit() {
        // given
        long initialPoint = 1000L;
        long useAmount = 100L; // 100원 단위
        
        UserPoint currentPoint = createUserPoint(USER_ID, initialPoint);
        UserPoint updatedPoint = createUserPoint(USER_ID, initialPoint - useAmount);

        when(userPointTable.selectById(USER_ID)).thenReturn(currentPoint);
        when(userPointTable.insertOrUpdate(USER_ID, initialPoint - useAmount)).thenReturn(updatedPoint);
        when(pointHistoryTable.insert(USER_ID, useAmount, TransactionType.USE, updatedPoint.updateMillis())).thenReturn(new PointHistory(1, USER_ID, useAmount, TransactionType.USE, updatedPoint.updateMillis()));

        // when
        UserPoint result = pointService.usePoint(USER_ID, useAmount);

        // then
        assertThat(result.point()).isEqualTo(initialPoint - useAmount);
        verify(userPointTable, times(1)).selectById(USER_ID);
        verify(userPointTable, times(1)).insertOrUpdate(USER_ID, initialPoint - useAmount);
        verify(pointHistoryTable, times(1)).insert(USER_ID, useAmount, TransactionType.USE, updatedPoint.updateMillis());
    }

    @Test
    @DisplayName("포인트 내역 조회 성공")
    void getPointHistory_success(){
        // given
        List<PointHistory> expectedHistories = List.of(
            new PointHistory(1L, USER_ID, 100L, TransactionType.CHARGE, System.currentTimeMillis()),
            new PointHistory(2L, USER_ID, 50L, TransactionType.USE, System.currentTimeMillis())
        );
        
        when(pointHistoryTable.selectAllByUserId(USER_ID)).thenReturn(expectedHistories);

        // when
        List<PointHistory> result = pointService.getPointHistory(USER_ID);

        // then
        assertThat(result).isEqualTo(expectedHistories);
        verify(pointHistoryTable, times(1)).selectAllByUserId(USER_ID);
    }
}
