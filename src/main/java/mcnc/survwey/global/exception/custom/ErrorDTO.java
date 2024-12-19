package mcnc.survwey.global.exception.custom;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class ErrorDTO {
    private String errorMessage;

    /**
     * CustomException을 이용하여 응답 생성 메서드
     * - 상태 코드, 에러 코드를 이용하여 클라이언트에게 응답 반환
     * @param ex
     * @return
     */
    public static ResponseEntity<ErrorDTO> toResponseEntity(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ErrorDTO.builder()
                        .errorMessage(errorCode.getErrorMessage())
                        .build());
    }
}
