package mcnc.survwey.domain.question.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;

@Slf4j
@Getter
@AllArgsConstructor
public enum QuestionType {
    SUBJECTIVE("주관식"),
    OBJ_SINGLE("단일 객관식"),
    OBJ_MULTI("다중 객관식");

    private final String value;

    /**
     * 요청 시 Enum에 정의된 질문 value가 아니면 Error 발생 메서드
     * - 일치하지 않을 시 400번 에러로 클라이언트에게 응답
     * @param type
     */
    public static void checkQuestionType(String type) {
        for (QuestionType questionType : values()) {
            if (questionType.name().equals(type)) { // Enum 이름을 비교
                return;
            }
        }
        log.error("유효하지 않은 질문 유형: {}", type);
        throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_QUESTION_TYPE);
    }
}