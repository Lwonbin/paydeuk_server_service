# PAYDEUK_SERVER_SERVICE


## 프로젝트 상세 내용


### 개발 목표

![image](https://github.com/Lwonbin/DIDACTO_graprac/assets/128762057/00c79df5-b5d1-4d63-8051-822e53524537)


### API 개발, 인터페이스 작성 및 문서자동화(Swagger)


![image](https://github.com/Lwonbin/DIDACTO_graprac/assets/128762057/11ef7b1a-01f3-4e99-876f-2d490b1c116b)



### 의사소통(Slack, Notion)

![image](https://github.com/Lwonbin/DIDACTO_graprac/assets/128762057/13170eff-4b77-4f23-bc8a-25cf5600434d)




### 1차 개발범위

1차 개발범위에 해당하는 내용입니다.

1. MEMBER(회원)
   1. 회원 가입
   2. 회원 수정, 탈퇴
   3. 인증(Authentication)
   4. 인가(Authorization)
  
2. LECTURE(강의)
   1. 강의 생성, 수정, 삭제
   2. 강의 검색 (키워드 검색)
   3. 강의에 속한 학생들 조회
   4. 자신이 속해있는 강의들 조회
   5. 강의 탈퇴 (학생)
   6. 학생 강퇴 (교수)
  
3. ENROLLMENT(참여)
   1. 강의 참여 요청/취소
   2. 참여 요청 수락/거절
  




### 2차 개발범위

1. 모니터링 : 일정 간격으로 실습 컴퓨터들의 상태와 스크린을 Push 및 Polling하여 웹에서 학생들의 접속 현황과 스크린 상태를 모니터링   할 수 있는 모니터링 기능
2. 결제시스템 : SW의 지속성을 고려해 수익모델이 필요함. 따라서 유료결제 시스템을 이용해 교수자의 등급으로 강의 설계를 제한하도록 하는 결제시스템 기능
3. 스트리밍 : 학생과 교수자의 1:N 단위의 화면 스트리밍기능



## 개인 기여
1. MEMBER(회원) API 작성 -1차
2. 결제시스템 구축 - 2차





### 결제 시스템 기본동작 설명
https://decorous-calendula-c7c.notion.site/e16b4b3b9aee49b2b700a78841c080e9?pvs=4





### 결제시스템 고도화 진행
웹훅을 이용한 결제시스템 안정성 높이기

https://decorous-calendula-c7c.notion.site/62ac3394b2e449fbb671d2f83f187cd0?pvs=4



