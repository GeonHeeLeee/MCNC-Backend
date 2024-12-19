package mcnc.survwey.global.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final HttpStatus httpStatus; //HTTP 상태 코드
    private final ErrorCode errorCode; //커스텀 에러 코드

    public CustomException(HttpStatus httpStatus, ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.httpStatus = null;
        this.errorCode = errorCode;
    }
}
