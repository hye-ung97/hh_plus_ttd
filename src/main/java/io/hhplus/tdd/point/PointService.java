package io.hhplus.tdd.point;

import java.util.List;

import org.springframework.stereotype.Service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.exception.InsufficientPointException;
import io.hhplus.tdd.point.exception.InvalidChargeAmountException;
import io.hhplus.tdd.point.exception.InvalidPointUnitException;
import io.hhplus.tdd.point.exception.PointLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {
    private static final long MIN_CHARGE_AMOUNT = 100L;
    private static final long POINT_UNIT = 100L;
    private static final long MAX_POINT = 100_000L;

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPoint getPointById(long userId) {
        return userPointTable.selectById(userId);
    }

    public UserPoint chargePoint(long userId, long amount) {
        UserPoint currentPoint = userPointTable.selectById(userId);
        long newAmount = currentPoint.point() + amount;
        if (amount < MIN_CHARGE_AMOUNT) {
            log.error("포인트 충전 최소 금액 미만: userId={}, chargeAmount={}", userId, amount);
            throw new InvalidChargeAmountException(amount);
        }
        if (newAmount > MAX_POINT) {
            log.error("포인트 충전 한도 초과: userId={}, currentPoint={}, chargeAmount={}", userId, currentPoint.point(), amount);
            throw new PointLimitExceededException(currentPoint.point(), amount, MAX_POINT);
        }

        UserPoint updatedPoint = userPointTable.insertOrUpdate(userId, newAmount);
        pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, updatedPoint.updateMillis());
        return updatedPoint;
    }

    public UserPoint usePoint(long userId, long amount) {
        UserPoint currentPoint = userPointTable.selectById(userId);
        if (currentPoint.point() < amount) {
            log.error("포인트 부족: userId={}, currentPoint={}, useAmount={}", userId, currentPoint.point(), amount);
            throw new InsufficientPointException(currentPoint.point(), amount);
        }
        if (amount % POINT_UNIT != 0) {
            log.error("포인트 이용 금액 단위 오류: userId={}, useAmount={}", userId, amount);
            throw new InvalidPointUnitException(amount);
        }
        long newAmount = currentPoint.point() - amount;
        UserPoint updatedPoint = userPointTable.insertOrUpdate(userId, newAmount);
        pointHistoryTable.insert(userId, amount, TransactionType.USE, updatedPoint.updateMillis());
        return updatedPoint;
    }

    public List<PointHistory> getPointHistory(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }
}
