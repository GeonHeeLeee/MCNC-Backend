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
}

