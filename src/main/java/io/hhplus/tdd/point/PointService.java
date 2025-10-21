package io.hhplus.tdd.point;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    private final Logger log = LoggerFactory.getLogger(PointService.class);
    private final long MAX_POINT = 100_000L;

    public UserPoint getPointById(long userId) {
        return userPointTable.selectById(userId);
    }

    public UserPoint chargePoint(long userId, long amount) {
        UserPoint currentPoint = userPointTable.selectById(userId);
        long newAmount = currentPoint.point() + amount;
        if (amount < 100L) {
            log.error("포인트 충전 최소 금액 미만: userId={}, chargeAmount={}", userId, amount);
            throw new IllegalArgumentException("포인트는 최소 100원 이상 충전할 수 있습니다.");
        }
        if (newAmount > MAX_POINT) {
            log.error("포인트 충전 한도 초과: userId={}, currentPoint={}, chargeAmount={}", userId, currentPoint.point(), amount);
            throw new IllegalArgumentException("포인트 충전 한도 초과입니다. 최대 100,000원까지 충전할 수 있습니다.");
        }

        UserPoint updatedPoint = userPointTable.insertOrUpdate(userId, newAmount);
        pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, updatedPoint.updateMillis());
        return updatedPoint;
    }

    public UserPoint usePoint(long userId, long amount) {
        UserPoint currentPoint = userPointTable.selectById(userId);
        if (currentPoint.point() < amount) {
            log.error("포인트 부족: userId={}, currentPoint={}, useAmount={}", userId, currentPoint.point(), amount);
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        if (amount % 100L != 0) {
            log.error("포인트 이용 금액 단위 오류: userId={}, useAmount={}", userId, amount);
            throw new IllegalArgumentException("포인트는 100원 단위로만 사용할 수 있습니다.");
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
