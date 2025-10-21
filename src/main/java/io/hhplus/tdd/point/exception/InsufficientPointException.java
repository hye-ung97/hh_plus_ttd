package io.hhplus.tdd.point.exception;

public class InsufficientPointException extends PointException {
    private static final String ERROR_CODE = "POINT_001";
    private static final String MESSAGE = "포인트가 부족합니다.";

    public InsufficientPointException() {
        super(ERROR_CODE, MESSAGE);
    }

    public InsufficientPointException(long currentPoint, long requiredPoint) {
        super(ERROR_CODE, String.format("포인트가 부족합니다. (보유: %d원, 필요: %d원)", currentPoint, requiredPoint));
    }
}
