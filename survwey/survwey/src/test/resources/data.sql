MERGE INTO "user" (user_id, email, password, gender, birth, register_date, name)
VALUES ('testUser1', 'user1@example.com', 'password!', 'M', '1990-01-01', CURRENT_TIMESTAMP, 'User One');

MERGE INTO "user" (user_id, email, password, gender, birth, register_date, name)
VALUES ('testUser2', 'user2@example.com', 'password!', 'F', '2000-01-01', CURRENT_TIMESTAMP, 'User Two');

MERGE INTO survey (survey_id, create_date, expire_date, title, description, user_id)
VALUES (1, '2024-12-03', '2024-01-20', 'survey1', 'description1', 'testUser1');

MERGE INTO survey (survey_id, create_date, expire_date, title, description, user_id)
VALUES (2, '2024-12-07', '2024-01-25', 'survey2', 'description2', 'testUser1');



MERGE INTO question (ques_id, create_date, body, survey_id, type)
VALUES (1, '2024-12-03', '질문 1', 1, 'SUBJECTIVE');

MERGE INTO question (ques_id, create_date, body, survey_id, type)
VALUES (2, '2024-12-03', '질문 2', 1, 'OBJ_MULTI');

MERGE INTO question (ques_id, create_date, body, survey_id, type)
VALUES (3, '2024-12-03', '질문 3', 1, 'OBJ_SINGLE');



MERGE INTO selection (sequence, ques_id, body, create_date, is_etc)
VALUES (0, 2, '보기 1', '2024-12-03', false);

MERGE INTO selection (sequence, ques_id, body, create_date, is_etc)
VALUES (1, 2, '보기 2', '2024-12-03', false);

MERGE INTO selection (sequence, ques_id, body, create_date, is_etc)
VALUES (0, 3, '보기 1', '2024-12-03', false);

MERGE INTO selection (sequence, ques_id, body, create_date, is_etc)
VALUES (1, 3, '보기 2', '2024-12-03', true);



MERGE INTO respond (respond_id, respond_date, user_id, survey_id)
VALUES (1, CURRENT_TIMESTAMP, 'testUser1', 1);

MERGE INTO respond (respond_id, respond_date, user_id, survey_id)
VALUES (2, CURRENT_TIMESTAMP, 'testUser2', 1);



MERGE INTO subj_answer (subj_id, written_date, response, ques_id, user_id)
VALUES (1, CURRENT_TIMESTAMP, '유저1 응답1', 1, 'testUser1');

MERGE INTO subj_answer (subj_id, written_date, response, ques_id, user_id)
VALUES (2, CURRENT_TIMESTAMP, '유저2 응답1', 1, 'testUser2');



MERGE INTO obj_answer (obj_id, written_date, user_id, etc_answer, sequence, ques_id)
VALUES (1, CURRENT_TIMESTAMP, 'testUser1', null, 0, 2);

MERGE INTO obj_answer (obj_id, written_date, user_id, etc_answer, sequence, ques_id)
VALUES (2, CURRENT_TIMESTAMP, 'testUser1', null, 1, 2);

MERGE INTO obj_answer (obj_id, written_date, user_id, etc_answer, sequence, ques_id)
VALUES (3, CURRENT_TIMESTAMP, 'testUser1', '기타 응답', 1, 3);

MERGE INTO obj_answer (obj_id, written_date, user_id, etc_answer, sequence, ques_id)
VALUES (4, CURRENT_TIMESTAMP, 'testUser2', null, 0, 2);

MERGE INTO obj_answer (obj_id, written_date, user_id, etc_answer, sequence, ques_id)
VALUES (5, CURRENT_TIMESTAMP, 'testUser2', null, 0, 2);

