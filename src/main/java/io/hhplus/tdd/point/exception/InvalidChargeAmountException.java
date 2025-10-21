package io.hhplus.tdd.point.exception;

public class InvalidChargeAmountException extends PointException {
    private static final String ERROR_CODE = "POINT_002";
    private static final String MESSAGE = "포인트는 최소 100원 이상 충전할 수 있습니다.";

    public InvalidChargeAmountException() {
        super(ERROR_CODE, MESSAGE);
    }

    public InvalidChargeAmountException(long amount) {
        super(ERROR_CODE, String.format("포인트는 최소 100원 이상 충전할 수 있습니다. (입력: %d원)", amount));
    }
}
