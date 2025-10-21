package io.hhplus.tdd.point.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PointException extends RuntimeException {
    private final ErrorCode errorCode;

    public PointException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public PointException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getCode();
    }
}
