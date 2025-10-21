package io.hhplus.tdd.point.exception;

public abstract class PointException extends RuntimeException {
    private final String errorCode;

    protected PointException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
