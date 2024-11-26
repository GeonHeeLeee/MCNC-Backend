package mcnc.survwey.global.exception.custom;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND_BY_EMAIL("해당 이메일의 사용자가 존재하지 않습니다."),
    USER_NOT_FOUND_BY_ID("해당 아이디의 사용자가 존재하지 않습니다."),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다."),
    USER_ID_ALREADY_EXISTS("해당 아이디의 사용자가 이미 존재합니다."),
    USER_EMAIL_ALREADY_EXISTS("해당 이메일의 사용자가 이미 존재합니다."),
    INVALID_QUESTION_TYPE("유효하지 않은 질문 유형입니다."),
    SURVEY_NOT_FOUND_BY_ID("해당 아이디의 설문이 존재하지 않습니다."),
    QUESTION_NOT_FOUND_BY_ID("해당 아이디의 질문이 존재하지 않습니다."),
    SELECTION_NOT_FOUND_BY_ID("해당 아이디의 보기가 존재하지 않습니다."),
    RESPOND_ALREADY_EXISTS("해당 설문에 답한 사용자가 이미 존재합니다."),
    EXPIRED_SURVEY("해당 설문은 종료된 설문입니다."),
    SURVEY_NOT_FOUND("해당 설문이 존재하지 않습니다."),
    SURVEY_CREATOR_NOT_MATCH("본인이 생성한 설문이 아닙니다."),
    QUESTION_NOT_MATCH_TO_SURVEY("해당 요청의 질문은 해당 설문의 질문이 아니거나 응답하지 않은 질문이 있습니다."),
    HAS_NOT_RESPOND_TO_SURVEY("해당 설문에 참여하지 않았습니다."),
    HAS_ALREADY_RESPOND_TO_SURVEY("해당 설문에 이미 응답하셨습니다.");
    private final String errorMessage;

    ErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
