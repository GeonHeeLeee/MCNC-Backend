package mcnc.survwey.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.dto.GenderCountDTO;
import mcnc.survwey.domain.user.repository.UserRepository;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND_BY_EMAIL));
    }

    public User findByUserId(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND_BY_ID));
    }

    public List<GenderCountDTO> getGenderCountListBySurveyId(Long surveyId) {
        return userRepository.findGenderCountBySurveyId(surveyId);
    }

    public Map<String, Integer> getAgeGroupCountBySurveyId(Long surveyId) {
        List<LocalDate> birthList = userRepository.findBirthBySurveyId(surveyId);

        Map<String, Integer> ageMap = initializeAgeMap();

        for (LocalDate birthDate : birthList) {
            int age = calculateAge(birthDate);
            String ageGroup = getAgeGroup(age);
            ageMap.computeIfPresent(ageGroup, (key, value) -> value + 1); // 해당 연령대 수 증가
        }
        return ageMap;
    }


    private Map<String, Integer> initializeAgeMap() {
        Map<String, Integer> ageMap = new HashMap<>();
        ageMap.put("10대 미만", 0);
        for (int age = 10; age <= 80; age += 10) {
            ageMap.put(age + "대", 0);
        }
        ageMap.put("80세 이상", 0);
        return ageMap;
    }


    private int calculateAge(LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        if (birthDate != null) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;  // 생년월일이 없으면 0세로 처리
        }
    }

    private String getAgeGroup(int age) {
        if (age < 10) {
            return "10대 미만";
        } else if (age >= 10 && age < 20) {
            return "10대";
        } else if (age >= 20 && age < 30) {
            return "20대";
        } else if (age >= 30 && age < 40) {
            return "30대";
        } else if (age >= 40 && age < 50) {
            return "40대";
        } else if (age >= 50 && age < 60) {
            return "50대";
        } else if (age >= 60 && age < 70) {
            return "60대";
        } else if (age >= 70 && age < 80) {
            return "70대";
        } else {
            return "80세 이상";
        }
    }
}
