# PAYDEUK_SERVER_SERVICE

## 프로젝트 개요

혜택 기반 카드 추천 및 간편 결제 서비스  
MSA 구조로 설계되었으며, 서비스·카드사·결제모듈 서버로 구성되어 있습니다.

## 시스템 아키텍처
![image](https://github.com/user-attachments/assets/76459aad-525b-47db-b0f6-9f7eccc52bd3)

---

## ERD

### V1  
![image](https://github.com/user-attachments/assets/337b273c-e675-45a5-a078-f0c20682c282)

### V2  
![image](https://github.com/user-attachments/assets/b74442ed-e3a8-4b63-980e-ff085bd4cafd)

---

## API 문서 자동화 및 인터페이스 작성

Swagger와 Notion을 활용해 API 명세를 관리했습니다.

![image](https://github.com/user-attachments/assets/d3a258cc-2e78-48ef-9e17-95c7d632f5cd)

---

## 협업 방식

Notion과 Slack을 기반으로 의사소통하고 작업을 정리했습니다.

![image](https://github.com/user-attachments/assets/ceb5a220-3f54-423a-ac37-68e8fdad81c4)

---

## 개발 범위

MSA 구조 기반으로 주요 기능은 아래와 같이 구성되어 있으며, 카드 추천 및 결제 로직은 `CARD` 서버에 구현되어 있습니다.

### MEMBER (회원)

- 회원 가입, 수정, 탈퇴
- 본인인증, 아이디·비밀번호 찾기
- 간편결제 비밀번호 등록, 검증, 변경
- 사용자 정보 조회 및 수정

### CARD (카드)

- 카드 등록, 대표카드 설정/변경
- 사용자 카드 목록 조회
- 결제 내역 조회 (전체, 단건)

### ADMIN (관리자)

- 가맹점 등록, 조회, 수정, 삭제
- 가맹점 결제 내역, 통계, 트렌드 조회
- 사용자 목록 및 통계 조회

### MERCHANT (가맹점)

- 본인 가맹점의 결제 내역 조회

---

## 개인 기여

1. 관리자 대시보드 프론트/백엔드 구현  
   - 가맹점, 거래건수·거래금액 조회 API 개발  
   - 거래 추이를 시각화한 대시보드 구축

2. 간편결제 비밀번호 검증 및 변경 기능 개발

3. 사용자 회원가입 및 프로필 관리 기능 구현

4. KG이니시스 본인인증 연동  
   - 토스, 카카오 등 다양한 인증 수단 지원

5. 인증/인가 및 카드사 API 로깅 처리  
   - Logback 기반 로깅으로 요청 흐름 추적

6. ELK 스택 기반 모니터링 체계 구축  
   - API 요청 및 인증 흐름 실시간 분석

7. GitHub Actions 기반 CI/CD 파이프라인 구축

8. 전체 프론트엔드 및 백엔드 코드 리뷰 및 검수

---

## 관련 작성 글

**MSA에서 DB 정합성 유지하기**  
https://lwb9036.tistory.com/25
