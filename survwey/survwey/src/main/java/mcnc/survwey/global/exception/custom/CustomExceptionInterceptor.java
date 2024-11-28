package mcnc.survwey.global.exception.custom;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomExceptionInterceptor {
    /**
     * 커스텀 예외 발생 시 처리 메서드
     * - 커스텀 예외 발생 시 Log 찍기
     * - 해당 상태 코드, 메세지로 클라이언트에게 응답
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public final ResponseEntity<ErrorDTO> handleCustomException(CustomException ex, HttpServletRequest request) {
        log.error("CustomException occurred: [Status: {}, Code: {}, Message: {}]",
                ex.getHttpStatus(), ex.getErrorCode(), ex.getMessage());
        return ErrorDTO.toResponseEntity(ex);
    }
}
