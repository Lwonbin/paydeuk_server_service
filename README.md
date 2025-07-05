# PAYDEUK_SERVER_SERVICE


## 프로젝트 상세 내용


### 개발 목표

![image](https://github.com/Lwonbin/DIDACTO_graprac/assets/128762057/00c79df5-b5d1-4d63-8051-822e53524537)


### API 개발, 인터페이스 작성 및 문서자동화(Notion, Swagger)


![image](https://github.com/user-attachments/assets/d3a258cc-2e78-48ef-9e17-95c7d632f5cd)




### 의사소통(Slack, Notion)

![image](https://github.com/user-attachments/assets/ceb5a220-3f54-423a-ac37-68e8fdad81c4)




### 🛠 개발 범위

**MSA 구조로 구성된 페이득 서비스의 주요 기능입니다.**
카드 추천 및 결제 로직은 `CARD` 서버에 구현되어 있습니다.

#### MEMBER (회원)

* 회원 가입 및 수정, 탈퇴
* 본인인증 및 아이디/비밀번호 찾기
* 간편결제 비밀번호 등록 / 검증 / 변경
* 사용자 정보 조회 및 수정

#### CARD (카드)

* 카드 등록 및 대표카드 설정/변경
* 사용자 카드 목록 조회
* 결제 내역 조회 (전체/단건)

#### ADMIN (관리자)

* 가맹점 등록 / 조회 / 수정 / 삭제
* 가맹점 결제 내역 및 통계/트렌드 조회
* 사용자 목록 및 통계 조회

#### MERCHANT (가맹점)

* 본인 가맹점의 결제 내역 조회

---



### 👤 개인 기여

1. **관리자 대시보드 프론트/백엔드 구현**

   * 가맹점, 거래건수·거래금액 조회 API 개발 및 연동
   * 거래 추이를 시각화한 대시보드 개발

2. **간편결제 비밀번호 검증 및 변경 기능 구현**

3. **사용자 회원가입 및 프로필 관리 기능 개발**

4. **KG이니시스 본인인증 연동**

   * 토스, 카카오 등 다양한 인증 수단 지원

5. **인증/인가 및 카드사 API에 대한 로깅 처리**

   * Logback 기반 로깅 적용

6. **ELK 스택 기반 모니터링 체계 구축**

   * 실시간 API 처리 결과 추적 및 분석

7. **GitHub Actions 기반 CI/CD 파이프라인 구축**

8. **전체 프론트엔드·백엔드 코드 리뷰 및 검수**  

  
  
### MSA에서 DB 정합성 유지하기
[https://decorous-calendula-c7c.notion.site/e16b4b3b9aee49b2b700a78841c080e9?pvs=4](https://lwb9036.tistory.com/25)





### Cypress와 Puppeteer로 E2E 테스트하기
[https://decorous-calendula-c7c.notion.site/62ac3394b2e449fbb671d2f83f187cd0?pvs=4](https://lwb9036.tistory.com/26)



