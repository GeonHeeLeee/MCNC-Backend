# 모빌씨앤씨 인턴쉽 프로젝트 - Survwey

**설명**  
Survwey는 설문조사 플랫폼으로, 사용자 기반 모바일/웹 설문 생성/관리/참여 어플리케이션입니다.  

---

## 목차
1. [프로젝트 개요](#프로젝트-개요)
2. [기술 스택](#기술-스택)
3. [메인 화면](#메인-화면)


## 프로젝트 개요

- **프로젝트 목표**: 반응형 UI로, 모바일/웹 모두 대응 가능한 사용자 친화적인 설문조사 플랫폼 구축
- **주요 기능**:
  - 사용자 인증 및 권한 관리
  - 설문 생성 및 관리
  - 설문 참여 및 참여한 설문 확인
  - 설문 결과 통계 확인 및 엑셀 다운로드
  - 설문 종료 알림 및 초대 메일 전송
---


## 기술 스택

- **백엔드**: Java 17, Spring Boot, MySQL, QueryDSL, Spring Data JPA, Redis 
- **프론트엔드**: Vue.js, Vuetify
- **배포**: Jenkins, Nginx
- **기타**: Swagger, Spring Security

![image](https://github.com/user-attachments/assets/3f89c84f-85cf-43ad-9921-d7faff8d4581)

- - - 

## 메인 화면

### 로그인 화면
![image](https://github.com/user-attachments/assets/ee4aa78c-6f37-4118-ab7e-6c561acd9e4d)

### 홈 화면
![image](https://github.com/user-attachments/assets/92c4ca86-8d31-431e-a022-2355421d5108)

### 설문 생성
![image](https://github.com/user-attachments/assets/47f9ce4b-cd1a-44ec-9956-e4411bd07e32)

### 설문 목록/검색
![image](https://github.com/user-attachments/assets/c2f40efc-31a8-4ec1-9db7-c0dea25fb1b7)


### 참여한 설문
![image](https://github.com/user-attachments/assets/ae223a77-da51-49c5-a65f-454156397608)

### 설문 결과 확인
![image](https://github.com/user-attachments/assets/415d465b-6a8e-4d24-9ee9-c90721a0bbf1)


|refactor|코드 리팩토링|
|style|코드 의미에 영향을 주지 않는 변경사항 (코드 포맷팅, 오타 수정, 변수명 변경, 에셋 추가)|
|chore|빌드 부분 혹은 패키지 매니저 수정 사항 / 파일 이름 변경 및 위치 변경 / 파일 삭제|
|docs|문서 추가 및 수정|
|rename|패키지 혹은 폴더명, 클래스명 수정 (단독으로 시행하였을 시)|
|remove|패키지 혹은 폴더, 클래스를 삭제하였을 때 (단독으로 시행하였을 시)|
