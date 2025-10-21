package io.hhplus.tdd.point.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "포인트가 부족합니다."),
    INVALID_CHARGE_AMOUNT(HttpStatus.BAD_REQUEST, "포인트는 최소 100원 이상 충전할 수 있습니다."),
    POINT_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "포인트 충전 한도 초과입니다. 최대 100,000원까지 충전할 수 있습니다."),
    INVALID_POINT_UNIT(HttpStatus.BAD_REQUEST, "포인트는 100원 단위로만 사용할 수 있습니다.");

    private final HttpStatus code;
    private final String message;

    ErrorCode(HttpStatus code, String message) {
        this.code = code;
        this.message = message;
    }
}
