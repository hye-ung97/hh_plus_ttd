package io.hhplus.tdd.point.exception;

public class PointLimitExceededException extends PointException {
    private static final String ERROR_CODE = "POINT_003";
    private static final String MESSAGE = "포인트 충전 한도 초과입니다. 최대 100,000원까지 충전할 수 있습니다.";

    public PointLimitExceededException() {
        super(ERROR_CODE, MESSAGE);
    }

    public PointLimitExceededException(long currentPoint, long chargeAmount, long maxPoint) {
        super(ERROR_CODE, String.format("포인트 충전 한도 초과입니다. (현재: %d원, 충전: %d원, 한도: %d원)",
            currentPoint, chargeAmount, maxPoint));
    }
}
