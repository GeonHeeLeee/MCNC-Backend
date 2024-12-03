package mcnc.survwey.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    M("MALE"),
    F("FEMALE");

    private final String value;
}

