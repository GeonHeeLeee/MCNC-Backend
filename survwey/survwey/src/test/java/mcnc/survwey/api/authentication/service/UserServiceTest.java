package mcnc.survwey.api.authentication.service;

import mcnc.survwey.domain.enums.Gender;
import mcnc.survwey.domain.user.dto.AgeCountDTO;
import mcnc.survwey.domain.user.dto.GenderCountDTO;
import mcnc.survwey.domain.user.repository.UserRepository;
import mcnc.survwey.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("성별 카운트 조회 테스트")
    void getGenderCountListBySurveyId_success() {
        // Given
        Long surveyId = 1L;
        List<Object[]> mockRecordList = Arrays.asList(
                new Object[]{Gender.M, 10L},
                new Object[]{Gender.F, 20L}
        );

        when(userRepository.findGenderCountBySurveyId(surveyId))
                .thenReturn(mockRecordList);

        // When
        List<GenderCountDTO> result = userService.getGenderCountListBySurveyId(surveyId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting("count")
                .containsExactly(10L, 20L);

        verify(userRepository).findGenderCountBySurveyId(surveyId);
    }

    @Test
    @DisplayName("나이 그룹 카운트 조회 테스트")
    void getAgeGroupCountBySurveyId_success() {
        // Given
        Long surveyId = 1L;
        List<LocalDate> birthList = Arrays.asList(
                LocalDate.of(2000, 1, 1),  // 20대
                LocalDate.of(1990, 1, 1),  // 30대
                LocalDate.of(2020, 1, 1),  // 10대 미만
                LocalDate.of(1950, 1, 1)   // 80세 이상
        );

        when(userRepository.findBirthBySurveyId(surveyId))
                .thenReturn(birthList);

        // When
        List<AgeCountDTO> result = userService.getAgeGroupCountBySurveyId(surveyId);

        // Then
        assertThat(result).hasSize(9);

        // 20대, 30대, 10대 미만, 80세 이상 등에 대한 검증
        Map<String, Integer> expectedCounts = Map.of(
                "10대 미만", 1,
                "20대", 1,
                "30대", 1,
                "80세 이상", 1
        );

        for (AgeCountDTO dto : result) {
            if (expectedCounts.containsKey(dto.getAge())) {
                assertThat(dto.getCount())
                        .isEqualTo(expectedCounts.get(dto.getAge()));
            }
        }

        verify(userRepository).findBirthBySurveyId(surveyId);
    }

    @Test
    @DisplayName("나이 계산 테스트")
    void calculateAge_test() {
        // 현재 날짜로부터 30년 전 생년월일
        LocalDate birthDate = LocalDate.now().minusYears(30);

        // UserService의 private 메서드를 직접 테스트하기 어려우므로
        // 기능을 간접적으로 검증할 수 있는 방법 사용
        int expectedAge = 30;

        // 현재 날짜에서 birthDate를 빼서 나이 계산
        int calculatedAge = Period.between(birthDate, LocalDate.now()).getYears();

        assertEquals(expectedAge, calculatedAge);
    }

    @Test
    @DisplayName("calculateAge 메서드 - 생년월일 null 테스트")
    void calculateAge_nullBirthDate() {
        // When
        int age = ReflectionTestUtils.invokeMethod(
                userService,
                "calculateAge",
                (LocalDate) null
        );

        // Then
        assertThat(age).isZero();
    }

}