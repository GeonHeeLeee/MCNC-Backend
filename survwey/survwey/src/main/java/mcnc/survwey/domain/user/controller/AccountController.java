package mcnc.survwey.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.user.dto.AuthDTO;
import mcnc.survwey.domain.user.dto.ChangePasswordDTO;
import mcnc.survwey.domain.user.dto.ModifyDTO;
import mcnc.survwey.domain.user.service.AccountService;
import mcnc.survwey.global.config.SessionContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
@Tag(name = "사용자 계정 관련", description = "회원가입/프로필 수정 API")
public class AccountController {

    private final AccountService accountService;

    /**
     * 회원 가입 로직
     * -중복 ID, Email 존재 시 에러 메시지
     *
     * @param authDTO
     * @return
     */
    @PostMapping("/join")
    @Operation(summary = "회원가입", description = "userId, email, password, birth, gender, name 응답으로 주면 됨 (미응답 X)<br>  "
            + " userId (5~20 글자, 영문과 숫자 조합), email(유효한 이메일 주소 형식), password(최소 8자, 숫자, 특수문자 및 대소문자 조합), birth(yyyy-mm-dd), gender(M, F), name(닉네임 기준인거지?)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = """
                    잘못된 요청:
                    - userId 미입력: "아이디는 필수 입니다."
                    - userId 글자 수 불일치: "사용자 아이디는 5글자 이상 20글자 이하입니다."
                    - userId 패턴 불일치: "사용자 아이디는 영문과 숫자의 조합이어야 합니다."
                    - email 미입력: "이메일은 필수 입니다."
                    - email 형식 불일치: "유효한 이메일 주소를 입력해주세요."
                    - password 미입력: "비밀번호는 필수입니다."
                    - password 패턴 불일치: "비밀번호는 최소 8자, 숫자, 특수문자 및 대소문자를 포함해야합니다."
                    - birth 미입력: "생년월일은 필수입니다."
                    - gender 미입력: "성별은 필수입니다."
                    - name 미입력: "이름은 필수입니다."
                    """)
    })
    public ResponseEntity<Object> register(@Valid @RequestBody AuthDTO authDTO) {
        accountService.registerUser(authDTO);

        return ResponseEntity.ok(authDTO.getUserId());
    }

    /**
     * ID, Email 중복 검사
     * @param request
     * @return
     */
    @PostMapping("/join/check")
    @Operation(summary = "ID, Email 중복 검사", description = "userId, email Map 응답으로 주면 됨 (미응답 X)<br>  "
            + " userId (5~20 글자, 영문과 숫자 조합), email(유효한 이메일 주소 형식)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "중복 검사 성공"),
    })
    public ResponseEntity<Map<String, Boolean>> checkIdAndEmail(@Valid @RequestBody Map<String, String> request) {
        Map<String, Boolean> map = accountService.duplicatedUserNameAndEmail(request.get("userId"), request.get("email"));
        return ResponseEntity.ok(map);
    }

    /**
     * 프로필 수정 로직
     * 사용자 이름, 생일, 성별 변경
     *
     * @param modifyDTO
     * @return
     */
    @PostMapping("/modify/profile")
    @Operation(summary = "프로필 수정", description = "userId, email, name, birth, gender(M or F)<br>"
            + "name, email 빈 값으로 들어오면 기존 사용자 정보 재사용 <br>"
            + "userId, birth, gender 변경사항 아님")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공"),
            @ApiResponse(responseCode = "401", description = "세션이 유효하지 않음")
    })
    public ResponseEntity<ModifyDTO> modify(@Valid @RequestBody ModifyDTO modifyDTO) {
        String userId = SessionContext.getCurrentUser();
        ModifyDTO modifyProfile = accountService.modifyUserProfile(modifyDTO, userId);
        return ResponseEntity.ok(modifyProfile);
    }

    /**
     * 사용자 프로필 조회
     * 사용자 ID 세션으로 가져온 후 조회
     * @return
     */
    @GetMapping("/profile")
    @Operation(summary = "사용자 프로필 조회", description = "응답 필요 없음")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "userId가 맞지 않을 때 - errorMessage: 해당 아이디의 사용자가 존재하지 않습니다."),
            @ApiResponse(responseCode = "401", description = "세션이 유효하지 않음")
    })
    public ResponseEntity<ModifyDTO> profileDetails(){
        String userId = SessionContext.getCurrentUser();
        ModifyDTO modifyDTO = accountService.getProfile(userId);
        return ResponseEntity.ok(modifyDTO);
    }

    /**
     * 사용자 비밀번호 변경 로직
     * 해당 사용자의 ID를 조회 후 변경
     *
     * @param changePasswordDTO
     * @return
     */
    @PostMapping("/modify/password")
    @Operation(summary = "사용자 password 변경", description = "userID, password 응답으로 보내주면됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "userId가 맞지 않을 때 - errorMessage: 해당 아이디의 사용자가 존재하지 않습니다.")
    })
    public ResponseEntity<Object> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        accountService.changePassword(changePasswordDTO);
        return ResponseEntity.ok(null);
    }
}
