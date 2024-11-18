package mcnc.survwey.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.enums.Gender;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.dto.AgeCountDTO;
import mcnc.survwey.domain.user.dto.GenderCountDTO;
import mcnc.survwey.domain.user.repository.UserRepository;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

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
        List<Object[]> recordList = userRepository.findGenderCountBySurveyId(surveyId);
        Map<Gender, GenderCountDTO> genderCountDTOMap = new LinkedHashMap<>();
        genderCountDTOMap.put(Gender.M, new GenderCountDTO(Gender.M.getValue(), 0));
        genderCountDTOMap.put(Gender.F, new GenderCountDTO(Gender.F.getValue(), 0));
        for (Object[] record : recordList) {
            Gender gender = (Gender) record[0];
            Long count = (Long) record[1];
            genderCountDTOMap.get(gender).setCount(count);
        }
        return genderCountDTOMap.values().stream().toList();
    }

    public List<AgeCountDTO> getAgeGroupCountBySurveyId(Long surveyId) {
        List<LocalDate> birthList = userRepository.findBirthBySurveyId(surveyId);
        Map<Integer, Integer> ageMap = groupAgesByDecade(birthList);
        return mapAgeGroupsToDTO(ageMap);
    }

    private Map<Integer, Integer> groupAgesByDecade(List<LocalDate> birthList) {
        Map<Integer, Integer> ageMap = new LinkedHashMap<>();
        for (LocalDate birthDate : birthList) {
            int decade = calculateAge(birthDate) / 10;
            if (decade < 8) {
                ageMap.put(decade, ageMap.getOrDefault(decade, 0) + 1);
            } else {
                ageMap.put(8, ageMap.getOrDefault(8, 0) + 1);
            }
        }
        return ageMap;
    }


    private List<AgeCountDTO> mapAgeGroupsToDTO(Map<Integer, Integer> ageMap) {
        List<AgeCountDTO> ageCountDTOList = new ArrayList<>();
        ageCountDTOList.add(new AgeCountDTO("10대 미만", ageMap.getOrDefault(0, 0)));
        for (int decade = 1; decade <= 7; decade++) {
            String ageGroup = decade * 10 + "대";
            ageCountDTOList.add(new AgeCountDTO(ageGroup, ageMap.getOrDefault(decade, 0)));
        }
        ageCountDTOList.add(new AgeCountDTO("80세 이상", ageMap.getOrDefault(8, 0)));
        return ageCountDTOList;
    }


    private int calculateAge(LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        if (birthDate != null) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;  // 생년월일이 없으면 0세로 처리
        }
    }
}
