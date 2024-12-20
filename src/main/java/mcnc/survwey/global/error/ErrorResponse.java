package mcnc.survwey.global.error;


import lombok.Getter;
import mcnc.survwey.global.exception.custom.ErrorCode;

@Getter
public class ErrorResponse {
    private final String errorMessage;

    public ErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorResponse(ErrorCode errorCode) {
        this.errorMessage = errorCode.getErrorMessage();
    }
}
