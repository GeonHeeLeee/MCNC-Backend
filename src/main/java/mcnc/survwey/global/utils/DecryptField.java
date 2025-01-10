package mcnc.survwey.global.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 이메일, 설문 아이디 등 복호화 어노테이션
 * - 프론트엔드에서 암호화해서 데이터를 보내는데, 이를 복호화하기 위한 커스텀 어노테이션
 * @Author 이건희
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DecryptField {
}
