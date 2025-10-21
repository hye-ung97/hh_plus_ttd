package io.hhplus.tdd.point;

import org.springframework.stereotype.Service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPoint getPointById(long userId) {
        return userPointTable.selectById(userId);
    }

    public UserPoint chargePoint(long userId, long amount) {
        UserPoint currentPoint = userPointTable.selectById(userId);
        long newAmount = currentPoint.point() + amount;
        UserPoint updatedPoint = userPointTable.insertOrUpdate(userId, newAmount);
        pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, updatedPoint.updateMillis());
        return updatedPoint;
    }

    public UserPoint usePoint(long userId, long amount) {
        UserPoint currentPoint = userPointTable.selectById(userId);
        long newAmount = currentPoint.point() - amount;
        UserPoint updatedPoint = userPointTable.insertOrUpdate(userId, newAmount);
        pointHistoryTable.insert(userId, amount, TransactionType.USE, updatedPoint.updateMillis());
        return updatedPoint;
    }
}
