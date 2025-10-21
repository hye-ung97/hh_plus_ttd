package io.hhplus.tdd.point.exception;

public class InvalidPointUnitException extends PointException {
    private static final String ERROR_CODE = "POINT_004";
    private static final String MESSAGE = "포인트는 100원 단위로만 사용할 수 있습니다.";

    public InvalidPointUnitException() {
        super(ERROR_CODE, MESSAGE);
    }

    public InvalidPointUnitException(long amount) {
        super(ERROR_CODE, String.format("포인트는 100원 단위로만 사용할 수 있습니다. (입력: %d원)", amount));
    }
}
