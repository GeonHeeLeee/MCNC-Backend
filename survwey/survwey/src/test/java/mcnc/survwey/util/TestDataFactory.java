package mcnc.survwey.util;

import mcnc.survwey.domain.enums.Gender;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.objAnswer.ObjAnswer;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.question.repository.QuestionRepository;
import mcnc.survwey.domain.selection.Selection;
import mcnc.survwey.domain.selection.SelectionId;
import mcnc.survwey.domain.selection.repository.SelectionRepository;
import mcnc.survwey.domain.subjAnswer.SubjAnswer;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.repository.SurveyRepository;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class TestDataFactory {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SelectionRepository selectionRepository;

    public User createUser(String userId, String email, String password, Gender gender) {
        return User.builder()
                .userId(userId)
                .name(userId)
                .email(email)
                .password(passwordEncoder.encode(password))
                .birth(LocalDate.now())
                .registerDate(LocalDateTime.now())
                .gender(gender)
                .build();
    }

    public Survey createSurvey(String title, String description, User user) {
        return Survey.builder()
                .expireDate(LocalDateTime.now().plusDays(30))
                .title(title)
                .description(description)
                .user(user)
                .build();
    }


    public Question createQuestion(String body, QuestionType type, Survey survey) {
        return Question.builder()
                .body(body)
                .survey(survey)
                .type(type)
                .build();
    }

    public Selection createSelection(String body, boolean isEtc, Question question, int sequence) {
        return Selection.builder()
                .id(new SelectionId(question.getQuesId(), sequence))
                .body(body)
                .isEtc(isEtc)
                .question(question)
                .build();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Survey saveSurvey(Survey survey) {
        return surveyRepository.save(survey);
    }

    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    public Selection saveSelection(Selection selection) {
        return selectionRepository.save(selection);
    }

    public Survey setUpUserAndSurveyData() {
        User testUser1 = createUser("testUser1", "testUser1@test.com", "test1", Gender.M);
        User testUser2 = createUser("testUser2", "testUser2@test.com", "test2", Gender.F);
        Survey survey1 = createSurvey("survey1", "survey1 description", testUser1);
        Survey survey2 = createSurvey("survey2", "survey2 description", testUser1);

        saveUser(testUser1);
        saveUser(testUser2);
        saveSurvey(survey1);
        saveSurvey(survey2);
        return survey1;
    }

    public Survey setUpSurveyWithDetailData() {
        User user = createUser("testUser1", "testUser1@test.com", "test1", Gender.M);
        saveUser(user);
        Survey survey = createSurvey("survey1", "survey1 description", user);
        saveSurvey(survey);

        Question question1 = createQuestion("질문1", QuestionType.SUBJECTIVE, survey);
        Question question2 = createQuestion("질문2", QuestionType.OBJ_SINGLE, survey);

        survey.addQuestion(question1);
        survey.addQuestion(question2);

        saveQuestion(question1);
        saveQuestion(question2);

        Selection selection1 = createSelection("선택1", false, question2, 0);
        Selection selection2 = createSelection("선택2", true, question2, 1);

        question2.addSelection(selection1);
        question2.addSelection(selection2);

        saveSelection(selection1);
        saveSelection(selection2);
        return survey;
    }

//    public Survey setUpSurveyResponseData() {
//        User user = createUser("testUser1", "testUser1@test.com", "test1", Gender.M);
//        saveUser(user);
//        Survey survey = createSurvey("survey1", "survey1 description", user);
//        saveSurvey(survey);
//
//        Question question1 = createQuestion("질문1", QuestionType.SUBJECTIVE, survey);
//        Question question2 = createQuestion("질문2", QuestionType.OBJ_SINGLE, survey);
//
//        survey.addQuestion(question1);
//        survey.addQuestion(question2);
//
//        saveQuestion(question1);
//        saveQuestion(question2);
//
//        Selection selection1 = createSelection("선택1", false, question2, 0);
//        Selection selection2 = createSelection("선택2", true, question2, 1);
//
//        question2.addSelection(selection1);
//        question2.addSelection(selection2);
//
//        saveSelection(selection1);
//        saveSelection(selection2);
//
//        ObjAnswer objAnswer1 = ObjAnswer.create(user, null, selection1);
//        ObjAnswer objAnswer2 = ObjAnswer.create(user, "기타", selection2);
//        SubjAnswer subjAnswer = SubjAnswer.create(user, "주관식 응답", question1);
//
//        obj
//        return survey;
//    }

}
