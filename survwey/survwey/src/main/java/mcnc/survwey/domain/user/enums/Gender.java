package mcnc.survwey.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;

import static mcnc.survwey.global.exception.custom.ErrorCode.*;
import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
@Slf4j
public enum Gender {
    M("MALE"),
    F("FEMALE");

    private final String value;

    /**
     * 유효한 성별 유형 체크 없는 유형일 경우 BAD_REQUEST
     * @param type
     */
    public static void checkUserGender(String type){
        for (Gender genderType : values()) {
            if (genderType.name().equals(type)) {
                //타입이 맞을 경우
                return;
            } else {
                log.info("Gender Type error: {}", type);
                throw new CustomException(BAD_REQUEST, INVALID_GENDER_TYPE);
            }
        }
    }
}

