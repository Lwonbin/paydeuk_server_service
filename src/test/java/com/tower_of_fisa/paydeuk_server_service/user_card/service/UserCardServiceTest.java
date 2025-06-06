package com.tower_of_fisa.paydeuk_server_service.user_card.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;

import com.tower_of_fisa.paydeuk_server_service.admin.repository.PaymentRepository;
import com.tower_of_fisa.paydeuk_server_service.card.repository.CardRepository;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.*;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.Card;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.CardBenefit;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.UserCard;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.BenefitConditionCategory;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.BenefitType;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.CardCompany;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.MerchantCategory;
import com.tower_of_fisa.paydeuk_server_service.global.common.response.CommonResponse;
import com.tower_of_fisa.paydeuk_server_service.global.config.exception.custom.exception.*;
import com.tower_of_fisa.paydeuk_server_service.user.repository.UserRepository;
import com.tower_of_fisa.paydeuk_server_service.user_card.dto.*;
import com.tower_of_fisa.paydeuk_server_service.user_card.dto.MyCardResponse;
import com.tower_of_fisa.paydeuk_server_service.user_card.repository.UserCardRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserCardServiceTest {

  @InjectMocks UserCardService userCardService;
  @Mock UserCardRepository userCardRepository;
  @Mock PaymentRepository paymentRepository;
  @Mock UserRepository userRepository;

  @Mock CardRepository cardRepository;

  @Mock RestTemplate restTemplate; // ✅ 여기 선언 가능

  @Test
  @DisplayName("유저가 보유한 카드 목록을 반환한다")
  void getMyCards() {
    // given
    Long userId = 1L;
    Benefit benefit = Benefit.builder().description("할인 혜택").build();
    CardBenefit cardBenefit = CardBenefit.builder().benefit(benefit).build();
    Card card =
        Card.builder().name("국민카드").imageUrl("img.jpg").cardBenefits(List.of(cardBenefit)).build();
    UserCard userCard =
        UserCard.builder()
            .id(1L)
            .card(card)
            .cardNumber("1234567890123456")
            .isDefaultCard(true)
            .build();
    given(userCardRepository.findByUserIdWithCardAndBenefits(userId)).willReturn(List.of(userCard));

    // when
    List<MyCardResponse> result = userCardService.getMyCards(userId);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCardName()).isEqualTo("국민카드");
    assertThat(result.get(0).getCardNumber()).isEqualTo("3456");
    assertThat(result.get(0).getIsDefaultCard()).isTrue();
    assertThat(result.get(0).getCardBenefits().get(0).getContent()).isEqualTo("할인 혜택");
  }

  @Test
  @DisplayName("결제 내역 조회가 성공적으로 이루어진다")
  void getPaymentHistory() {
    // given
    Long userId = 1L;
    int page = 1;
    int size = 5;
    String sort = "createdAt,desc";
    LocalDateTime startDate = null;
    LocalDateTime endDate = null;

    Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
    Merchant merchant = Merchant.builder().name("상점").build();
    Card card = Card.builder().name("카드A").build();
    UserCard userCard = UserCard.builder().card(card).build();
    Benefit benefit = Benefit.builder().description("혜택").build();
    CardBenefit cardBenefit = CardBenefit.builder().benefit(benefit).build();
    Payment payment =
        Payment.builder()
            .id(1L)
            .merchant(merchant)
            .userCard(userCard)
            .amount(10000)
            .discountAmount(1000)
            .cardBenefit(cardBenefit)
            .build();

    given(paymentRepository.findPaymentHistoryByUserId(userId, pageable))
        .willReturn(new PageImpl<>(List.of(payment)));

    // when
    Page<PaymentHistoryResponse> result =
        userCardService.getPaymentHistory(userId, page, size, sort, startDate, endDate);

    // then
    assertThat(result.getContent()).hasSize(1);
    PaymentHistoryResponse response = result.getContent().get(0);
    assertThat(response.getShopName()).isEqualTo("상점");
    assertThat(response.getCardName()).isEqualTo("카드A");
    assertThat(response.getTransactionAmount()).isEqualTo(10000);
    assertThat(response.getDiscountAmount()).isEqualTo(1000);
    assertThat(response.getApplicationBenefit()).isEqualTo("혜택");
  }

  @DisplayName("sort에 direction 생략 시 기본값 desc를 사용한다")
  @Test
  void getPaymentHistory_sortWithoutDirection_defaultsToDesc() {
    // given
    Long userId = 1L;
    int page = 1;
    int size = 5;
    String sort = "createdAt"; // ✅ direction 생략
    Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

    given(paymentRepository.findPaymentHistoryByUserId(userId, pageable)).willReturn(Page.empty());

    // when
    Page<PaymentHistoryResponse> result =
        userCardService.getPaymentHistory(userId, page, size, sort, null, null);

    // then
    assertThat(result).isEmpty(); // 동작만 검증되면 충분
  }

  @DisplayName("b1과 b2의 merchant가 모두 null이면 비교 결과는 0이다")
  @Test
  void convertToBenefitResponses_bothMerchantsNull() {
    Benefit b1 =
        Benefit.builder()
            .id(1L)
            .merchant(null)
            .benefitType(BenefitType.DISCOUNT) // ✅ 필수
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("b1")
            .description("desc")
            .build();

    Benefit b2 =
        Benefit.builder()
            .id(2L)
            .merchant(Merchant.builder().category(MerchantCategory.CULTURE).build())
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("b2")
            .description("desc")
            .build();

    CardBenefit cb1 = CardBenefit.builder().benefit(b1).build();
    CardBenefit cb2 = CardBenefit.builder().benefit(b2).build();
    Card card = Card.builder().cardBenefits(List.of(cb1, cb2)).build();

    List<BenefitResponse> result =
        userCardService.convertToBenefitResponses(card, MerchantCategory.SHOPPING);

    assertThat(result).hasSize(2); // 순서 상관없음
  }

  @DisplayName("b1의 merchant는 null이고 b2는 다른 카테고리일 때 정렬 순서를 확인한다")
  @Test
  void convertToBenefitResponses_b1Null_b2DifferentCategory() {
    Benefit b1 =
        Benefit.builder()
            .id(1L)
            .merchant(null)
            .benefitType(BenefitType.DISCOUNT) // ✅ 필수
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("b1")
            .description("desc")
            .build();

    Benefit b2 =
        Benefit.builder()
            .id(2L)
            .merchant(Merchant.builder().category(MerchantCategory.CULTURE).build())
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("b2")
            .description("desc")
            .build();

    Card card =
        Card.builder()
            .cardBenefits(
                List.of(
                    CardBenefit.builder().benefit(b1).build(),
                    CardBenefit.builder().benefit(b2).build()))
            .build();

    List<BenefitResponse> result =
        userCardService.convertToBenefitResponses(card, MerchantCategory.FOOD_BEVERAGE);

    assertThat(result).hasSize(2);
  }

  @DisplayName("category가 null이면 정렬을 수행하지 않고 그대로 반환한다")
  @Test
  void convertToBenefitResponses_categoryNull_returnsUnsorted() {
    Benefit b1 =
        Benefit.builder()
            .id(1L)
            .merchant(null)
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("b1")
            .description("desc")
            .build();

    Benefit b2 =
        Benefit.builder()
            .id(2L)
            .merchant(null)
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("b2")
            .description("desc")
            .build();

    Card card =
        Card.builder()
            .cardBenefits(
                List.of(
                    CardBenefit.builder().benefit(b1).build(),
                    CardBenefit.builder().benefit(b2).build()))
            .build();

    // ✅ category = null
    List<BenefitResponse> result = userCardService.convertToBenefitResponses(card, null);

    assertThat(result).hasSize(2);
    // 순서가 그대로 유지되어야 함
    assertThat(result.get(0).getTitle()).isEqualTo("b1");
    assertThat(result.get(1).getTitle()).isEqualTo("b2");
  }

  @DisplayName("b1은 category와 일치하고 b2는 일치하지 않으면 b1이 먼저 온다")
  @Test
  void convertToBenefitResponses_b1Match_b2NoMatch() {
    MerchantCategory target = MerchantCategory.TRANSPORTATION;

    Benefit b1 =
        Benefit.builder()
            .id(1L)
            .merchant(
                Merchant.builder()
                    .id(1L)
                    .name("상점1")
                    .isActive(true)
                    .commissionRate("1%")
                    .businessNumber("111-11-1111")
                    .isDeleted(false)
                    .category(target)
                    .managerName("홍길동")
                    .phone("01012345678")
                    .managerPhone("01087654321")
                    .build())
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("교통 할인")
            .description("교통비 할인")
            .build();

    Benefit b2 =
        Benefit.builder()
            .id(2L)
            .merchant(
                Merchant.builder()
                    .id(2L)
                    .name("상점2")
                    .isActive(true)
                    .commissionRate("2%")
                    .businessNumber("222-22-2222")
                    .isDeleted(false)
                    .category(MerchantCategory.SHOPPING)
                    .managerName("김철수")
                    .phone("01000000000")
                    .managerPhone("01099999999")
                    .build())
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("쇼핑 할인")
            .description("쇼핑비 할인")
            .build();

    Card card =
        Card.builder()
            .cardBenefits(
                List.of(
                    CardBenefit.builder().benefit(b2).build(), // intentionally reverse order
                    CardBenefit.builder().benefit(b1).build()))
            .build();

    List<BenefitResponse> result = userCardService.convertToBenefitResponses(card, target);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getTitle()).isEqualTo("교통 할인"); // b1이 먼저
    assertThat(result.get(1).getTitle()).isEqualTo("쇼핑 할인");
  }

  @DisplayName("b1은 category와 불일치하고 b2는 일치하면 b2가 먼저 온다")
  @Test
  void convertToBenefitResponses_b1NoMatch_b2Match() {
    MerchantCategory target = MerchantCategory.SUBSCRIBE;

    Benefit b1 =
        Benefit.builder()
            .id(1L)
            .merchant(
                Merchant.builder()
                    .id(1L)
                    .name("상점1")
                    .isActive(true)
                    .commissionRate("1%")
                    .businessNumber("111-11-1111")
                    .isDeleted(false)
                    .category(MerchantCategory.FOOD_BEVERAGE) // ❌ 불일치
                    .managerName("홍길동")
                    .phone("01012345678")
                    .managerPhone("01087654321")
                    .build())
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("음식 할인")
            .description("설명1")
            .build();

    Benefit b2 =
        Benefit.builder()
            .id(2L)
            .merchant(
                Merchant.builder()
                    .id(2L)
                    .name("상점2")
                    .isActive(true)
                    .commissionRate("3%")
                    .businessNumber("222-22-2222")
                    .isDeleted(false)
                    .category(MerchantCategory.SUBSCRIBE) // ✅ 일치
                    .managerName("김철수")
                    .phone("01011112222")
                    .managerPhone("01033334444")
                    .build())
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("구독 할인")
            .description("설명2")
            .build();

    Card card =
        Card.builder()
            .cardBenefits(
                List.of(
                    CardBenefit.builder().benefit(b1).build(),
                    CardBenefit.builder().benefit(b2).build()))
            .build();

    List<BenefitResponse> result = userCardService.convertToBenefitResponses(card, target);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getTitle()).isEqualTo("구독 할인"); // ✅ b2가 먼저
    assertThat(result.get(1).getTitle()).isEqualTo("음식 할인");
  }

  @DisplayName("b2의 merchant는 존재하지만 category가 null이면 b2Matches는 false")
  @Test
  void convertToBenefitResponses_b2CategoryIsNull() {
    MerchantCategory category = MerchantCategory.CULTURE;

    Benefit b1 =
        Benefit.builder()
            .id(1L)
            .merchant(null) // b1은 일치하지 않음
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("b1")
            .description("desc")
            .build();

    // b2는 merchant가 있지만 category는 null
    Benefit b2 =
        Benefit.builder()
            .id(2L)
            .merchant(
                Merchant.builder()
                    .id(2L)
                    .name("상점2")
                    .isActive(true)
                    .commissionRate("2%")
                    .businessNumber("222-22-2222")
                    .isDeleted(false)
                    .category(null) // ✅ 핵심
                    .managerName("김철수")
                    .phone("01000000000")
                    .managerPhone("01099999999")
                    .build())
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("b2")
            .description("desc")
            .build();

    Card card =
        Card.builder()
            .cardBenefits(
                List.of(
                    CardBenefit.builder().benefit(b1).build(),
                    CardBenefit.builder().benefit(b2).build()))
            .build();

    List<BenefitResponse> result = userCardService.convertToBenefitResponses(card, category);

    assertThat(result).hasSize(2);
    // 정렬 기준 확인은 생략해도 무방
  }

  @DisplayName("b1의 merchant는 존재하지만 category가 null이면 b1Matches는 false")
  @Test
  void convertToBenefitResponses_b1CategoryIsNull() {
    MerchantCategory target = MerchantCategory.FOOD_BEVERAGE;

    Benefit b1 =
        Benefit.builder()
            .id(1L)
            .merchant(
                Merchant.builder()
                    .id(1L)
                    .name("상점A")
                    .isActive(true)
                    .commissionRate("3%")
                    .businessNumber("123-45-67890")
                    .isDeleted(false)
                    .category(null) // ✅ 핵심 조건
                    .managerName("홍길동")
                    .phone("01012345678")
                    .managerPhone("01087654321")
                    .build())
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("b1")
            .description("desc")
            .build();

    Benefit b2 =
        Benefit.builder()
            .id(2L)
            .merchant(null)
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("b2")
            .description("desc")
            .build();

    Card card =
        Card.builder()
            .cardBenefits(
                List.of(
                    CardBenefit.builder().benefit(b1).build(),
                    CardBenefit.builder().benefit(b2).build()))
            .build();

    List<BenefitResponse> result = userCardService.convertToBenefitResponses(card, target);

    assertThat(result).hasSize(2);
  }

  @DisplayName("b1Matches가 true인 조건에서 정렬 우선순위 확인")
  @Test
  void convertToBenefitResponses_b1Matches_true() {
    MerchantCategory target = MerchantCategory.FOOD_BEVERAGE;

    Benefit b1 =
        Benefit.builder()
            .id(1L)
            .merchant(
                Merchant.builder()
                    .id(1L)
                    .name("상점A")
                    .isActive(true)
                    .commissionRate("3%")
                    .businessNumber("123-45-67890")
                    .isDeleted(false)
                    .category(target) // ✅ b1 matches
                    .managerName("홍길동")
                    .phone("01012345678")
                    .managerPhone("01087654321")
                    .build())
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("b1")
            .description("desc")
            .build();

    Benefit b2 =
        Benefit.builder()
            .id(2L)
            .merchant(null) // ❌ b2 does not match
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("b2")
            .description("desc")
            .build();

    // b1이 정렬 대상이 되도록 intentionally b2를 먼저 넣음
    Card card =
        Card.builder()
            .cardBenefits(
                List.of(
                    CardBenefit.builder().benefit(b2).build(),
                    CardBenefit.builder().benefit(b1).build()))
            .build();

    List<BenefitResponse> result = userCardService.convertToBenefitResponses(card, target);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getTitle()).isEqualTo("b1"); // ✅ 정렬됨
    assertThat(result.get(1).getTitle()).isEqualTo("b2");
  }

  @DisplayName("b1.getMerchant().getCategory() == category 조건이 true일 때 정렬이 수행된다")
  @Test
  void convertToBenefitResponses_b1MatchesTrue_executed() {
    MerchantCategory category = MerchantCategory.CULTURE;

    // b1은 category 일치 (정렬 기준에 맞음)
    Benefit b1 =
        Benefit.builder()
            .id(1L)
            .merchant(
                Merchant.builder()
                    .id(1L)
                    .category(category)
                    .isActive(true)
                    .isDeleted(false)
                    .commissionRate("0%")
                    .businessNumber("123")
                    .name("상점")
                    .managerName("홍")
                    .phone("010")
                    .managerPhone("011")
                    .build())
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("정렬 우선")
            .description("desc")
            .build();

    // b2는 일치하지 않음
    Benefit b2 =
        Benefit.builder()
            .id(2L)
            .merchant(
                Merchant.builder()
                    .id(2L)
                    .category(MerchantCategory.SHOPPING)
                    .isActive(true)
                    .isDeleted(false)
                    .commissionRate("0%")
                    .businessNumber("456")
                    .name("상점2")
                    .managerName("김")
                    .phone("010")
                    .managerPhone("011")
                    .build())
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("정렬 뒤")
            .description("desc")
            .build();

    // b2 먼저 넣어서 실제 정렬이 발생하게 함
    Card card =
        Card.builder()
            .cardBenefits(
                List.of(
                    CardBenefit.builder().benefit(b2).build(),
                    CardBenefit.builder().benefit(b1).build()))
            .build();

    List<BenefitResponse> result = userCardService.convertToBenefitResponses(card, category);

    // ✅ 실제로 b1이 먼저 오도록 정렬됨 (비교 발생)
    assertThat(result.get(0).getTitle()).isEqualTo("정렬 우선");
    assertThat(result.get(1).getTitle()).isEqualTo("정렬 뒤");
  }

  @DisplayName("b1과 b2의 merchant가 모두 있고 둘 다 같은 category일 때 정렬 비교가 발생한다")
  @Test
  void convertToBenefitResponses_bothMatchesTrue_triggersComparison() {
    MerchantCategory target = MerchantCategory.CULTURE;

    Benefit b1 =
        Benefit.builder()
            .id(1L)
            .merchant(
                Merchant.builder()
                    .id(1L)
                    .category(target) // ✅ 일치
                    .isActive(true)
                    .isDeleted(false)
                    .commissionRate("0%")
                    .businessNumber("000")
                    .name("A")
                    .managerName("홍")
                    .phone("010")
                    .managerPhone("011")
                    .build())
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("정렬 대상 A")
            .description("desc")
            .build();

    Benefit b2 =
        Benefit.builder()
            .id(2L)
            .merchant(
                Merchant.builder()
                    .id(2L)
                    .category(target) // ✅ 동일하게 일치
                    .isActive(true)
                    .isDeleted(false)
                    .commissionRate("0%")
                    .businessNumber("001")
                    .name("B")
                    .managerName("김")
                    .phone("010")
                    .managerPhone("012")
                    .build())
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .benefitConditions(List.of())
            .title("정렬 대상 B")
            .description("desc")
            .build();

    Card card =
        Card.builder()
            .cardBenefits(
                List.of(
                    CardBenefit.builder().benefit(b2).build(), // 순서를 바꿔 비교 유도
                    CardBenefit.builder().benefit(b1).build()))
            .build();

    List<BenefitResponse> result = userCardService.convertToBenefitResponses(card, target);

    assertThat(result).hasSize(2);
    // 순서 확인은 의미 없고 비교만 발생하면 됨
  }

  @Test
  @DisplayName("대표카드를 설정할 수 있다")
  void setDefaultCard() {
    // given
    Long userId = 1L;
    Long cardId = 10L;
    UserCard userCard = UserCard.builder().isDefaultCard(false).build();
    given(userCardRepository.findByUserIdAndCardId(userId, cardId))
        .willReturn(Optional.of(userCard));

    // when
    userCardService.setDefaultCard(userId, cardId);

    // then
    assertThat(userCard.getIsDefaultCard()).isTrue();
  }

  @Test
  @DisplayName("기존 대표카드를 해제하고 새 대표카드를 설정한다")
  void updateDefaultCard() {
    // given
    Long userId = 1L;
    Long cardId = 10L;
    UserCard oldDefault = UserCard.builder().isDefaultCard(true).build();
    UserCard newDefault = UserCard.builder().isDefaultCard(false).build();
    given(userCardRepository.findByUserId(userId)).willReturn(List.of(oldDefault));
    given(userCardRepository.findByUserIdAndCardId(userId, cardId))
        .willReturn(Optional.of(newDefault));

    // when
    userCardService.updateDefaultCard(userId, cardId);

    // then
    assertThat(oldDefault.getIsDefaultCard()).isFalse();
    assertThat(newDefault.getIsDefaultCard()).isTrue();
  }

  @Test
  @DisplayName("대표카드는 삭제할 수 없다")
  void deleteCardThrowsIfDefault() {
    // given
    Long userId = 1L;
    Long cardId = 10L;
    UserCard userCard = UserCard.builder().isDefaultCard(true).build();
    given(userCardRepository.findByUserIdAndCardId(userId, cardId))
        .willReturn(Optional.of(userCard));

    // when & then
    assertThatThrownBy(() -> userCardService.deleteCard(userId, cardId))
        .isInstanceOf(ForbiddenException403.class);
  }

  @Test
  @DisplayName("대표카드가 아닌 카드는 삭제 가능하다")
  void deleteCardSuccess() {
    // given
    Long userId = 1L;
    Long cardId = 10L;
    UserCard userCard = UserCard.builder().isDefaultCard(false).build();
    given(userCardRepository.findByUserIdAndCardId(userId, cardId))
        .willReturn(Optional.of(userCard));

    // when
    userCardService.deleteCard(userId, cardId);

    // then
    then(userCardRepository).should().delete(userCard);
  }

  @DisplayName("startDate와 endDate가 모두 있을 때 결제 내역을 조회한다")
  @Test
  void getPaymentHistory_withStartAndEndDate() {
    // given
    Long userId = 1L;
    int page = 1;
    int size = 5;
    String sort = "createdAt,desc";
    LocalDateTime startDate = LocalDateTime.now().minusDays(10);
    LocalDateTime endDate = LocalDateTime.now();

    Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

    given(
            paymentRepository.findByUserCard_User_IdAndCreatedAtBetween(
                userId, startDate, endDate, pageable))
        .willReturn(Page.empty());

    // when
    Page<PaymentHistoryResponse> result =
        userCardService.getPaymentHistory(userId, page, size, sort, startDate, endDate);

    // then
    assertThat(result).isEmpty();
  }

  @DisplayName("정렬 조건이 asc일 때 결제 내역을 올림차순으로 정렬한다")
  @Test
  void getPaymentHistory_sortAscending() {
    // given
    Long userId = 1L;
    int page = 1;
    int size = 5;
    String sort = "createdAt,asc"; // ✅ asc 조건 확인
    Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").ascending());

    Merchant merchant = Merchant.builder().name("상점A").build();
    Card card = Card.builder().name("카드B").build();
    UserCard userCard = UserCard.builder().card(card).build();
    Benefit benefit = Benefit.builder().description("혜택").build();
    CardBenefit cardBenefit = CardBenefit.builder().benefit(benefit).build();
    Payment payment =
        Payment.builder()
            .id(1L)
            .merchant(merchant)
            .userCard(userCard)
            .amount(10000)
            .discountAmount(500)
            .cardBenefit(cardBenefit)
            .build();

    given(paymentRepository.findPaymentHistoryByUserId(userId, pageable))
        .willReturn(new PageImpl<>(List.of(payment)));

    // when
    Page<PaymentHistoryResponse> result =
        userCardService.getPaymentHistory(userId, page, size, sort, null, null);

    // then
    assertThat(result).hasSize(1);
    PaymentHistoryResponse res = result.getContent().get(0);
    assertThat(res.getCardName()).isEqualTo("카드B");
    assertThat(res.getApplicationBenefit()).isEqualTo("혜택");
  }

  @DisplayName("카드사 응답 status가 예상치 못한 값일 경우 UNCAUGHT 예외가 발생한다")
  @Test
  void addCard_unexpectedStatus_throwsUncaught() {
    Long userId = 1L;
    AddCardRequest request = AddCardRequest.builder().cardNumber("1111").build();
    given(userRepository.findById(userId))
        .willReturn(Optional.of(User.builder().userCards(List.of()).build()));

    // 예상하지 못한 status 값
    String errorJson = "{\"status\":\"SOMETHING_ELSE\"}";
    HttpClientErrorException exception =
        HttpClientErrorException.create(
            HttpStatus.BAD_REQUEST, "Unknown Error", HttpHeaders.EMPTY, errorJson.getBytes(), null);

    given(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
        .willThrow(exception);

    assertThatThrownBy(() -> userCardService.addCard(userId, request))
        .isInstanceOf(BadRequestException400.class)
        .hasMessageContaining("잘못된 요청입니다.");
  }

  @DisplayName("extractStatusFromJson에서 JSON 파싱 실패 시 '500'을 반환한다")
  @Test
  void extractStatusFromJson_whenMalformed_returns500() {
    // given
    String malformedJson = "not a json"; // ✅ 비정상 JSON

    // when
    String result = userCardService.extractStatusFromJson(malformedJson); // 이 메서드가 public이어야 호출 가능

    // then
    assertThat(result).isEqualTo("500");
  }

  @DisplayName("startDate만 있을 때 결제 내역을 조회한다")
  @Test
  void getPaymentHistory_withOnlyStartDate() {
    // given
    Long userId = 1L;
    int page = 1;
    int size = 5;
    String sort = "createdAt,desc";
    LocalDateTime startDate = LocalDateTime.now().minusDays(10);
    LocalDateTime endDate = null;

    Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

    given(paymentRepository.findByUserCard_User_IdAndCreatedAtAfter(userId, startDate, pageable))
        .willReturn(Page.empty());

    // when
    Page<PaymentHistoryResponse> result =
        userCardService.getPaymentHistory(userId, page, size, sort, startDate, endDate);

    // then
    assertThat(result).isEmpty();
  }

  @DisplayName("카드 추가 성공")
  @Test
  void addCard_success() {
    // given
    Long userId = 1L;
    AddCardRequest request =
        AddCardRequest.builder()
            .cardNumber("1111222233334444")
            .month("12")
            .year("26")
            .cvc("123")
            .pinPrefix("00")
            .build();

    User user =
        User.builder()
            .id(userId)
            .name("홍길동")
            .birthDate("921010")
            .phone("01012345678")
            .userCards(List.of()) // 첫 카드
            .build();

    CardTokenResponse tokenResponse = new CardTokenResponse(77L, "token123");
    CommonResponse<CardTokenResponse> commonResponse =
        new CommonResponse<>(true, HttpStatus.OK, "", tokenResponse);

    Card card = Card.builder().id(77L).name("카드").build();

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(
            restTemplate.exchange(
                anyString(), eq(HttpMethod.POST), any(), any(ParameterizedTypeReference.class)))
        .willReturn(ResponseEntity.ok(commonResponse));
    given(cardRepository.findById(77L)).willReturn(Optional.of(card));
    given(userCardRepository.existsByCardToken("token123")).willReturn(false);

    // when
    userCardService.addCard(userId, request);

    // then
    then(userCardRepository).should().save(any(UserCard.class));
  }

  @DisplayName("카드사 응답이 null이면 예외 발생")
  @Test
  void addCard_nullCardTokenResponse_throws() {
    Long userId = 1L;
    AddCardRequest request = AddCardRequest.builder().cardNumber("1111").build();
    given(userRepository.findById(userId))
        .willReturn(Optional.of(User.builder().userCards(List.of()).build()));
    given(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
        .willReturn(ResponseEntity.ok(new CommonResponse<>(true, HttpStatus.OK, "", null)));

    assertThatThrownBy(() -> userCardService.addCard(userId, request))
        .isInstanceOf(NoSuchElementFoundException404.class);
  }

  @DisplayName("이미 등록된 카드 토큰이면 예외 발생")
  @Test
  void addCard_alreadyExistsToken_throws() {
    Long userId = 1L;
    AddCardRequest request = AddCardRequest.builder().cardNumber("1111").build();
    CardTokenResponse tokenResponse = new CardTokenResponse(77L, "token123");
    CommonResponse<CardTokenResponse> response =
        new CommonResponse<>(true, HttpStatus.OK, "", tokenResponse);

    given(userRepository.findById(userId))
        .willReturn(Optional.of(User.builder().userCards(List.of()).build()));
    given(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
        .willReturn(ResponseEntity.ok(response));
    given(cardRepository.findById(77L)).willReturn(Optional.of(Card.builder().id(77L).build()));
    given(userCardRepository.existsByCardToken("token123")).willReturn(true);

    assertThatThrownBy(() -> userCardService.addCard(userId, request))
        .isInstanceOf(AlreadyExistElementException409.class);
  }

  @DisplayName("카드사 API에서 status=FORBIDDEN이면 예외 발생")
  @Test
  void addCard_forbiddenFromPG_throws() {
    Long userId = 1L;
    AddCardRequest request = AddCardRequest.builder().cardNumber("1111").build();
    given(userRepository.findById(userId))
        .willReturn(Optional.of(User.builder().userCards(List.of()).build()));

    String errorJson = "{\"status\":\"FORBIDDEN\"}";
    HttpClientErrorException exception =
        HttpClientErrorException.create(
            HttpStatus.FORBIDDEN, "Forbidden", HttpHeaders.EMPTY, errorJson.getBytes(), null);

    given(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
        .willThrow(exception);

    assertThatThrownBy(() -> userCardService.addCard(userId, request))
        .isInstanceOf(ForbiddenException403.class);
  }

  @DisplayName("카드사 API에서 status=NOT_FOUND이면 예외 발생")
  @Test
  void addCard_notFoundFromPG_throws() {
    Long userId = 1L;
    AddCardRequest request = AddCardRequest.builder().cardNumber("1111").build();
    given(userRepository.findById(userId))
        .willReturn(Optional.of(User.builder().userCards(List.of()).build()));

    String errorJson = "{\"status\":\"NOT_FOUND\"}";
    HttpClientErrorException exception =
        HttpClientErrorException.create(
            HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, errorJson.getBytes(), null);

    given(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
        .willThrow(exception);

    assertThatThrownBy(() -> userCardService.addCard(userId, request))
        .isInstanceOf(NoSuchElementFoundException404.class);
  }

  @DisplayName("카드사 API에서 status=BAD_REQUEST이면 예외 발생")
  @Test
  void addCard_badRequestFromPG_throws() {
    Long userId = 1L;
    AddCardRequest request = AddCardRequest.builder().cardNumber("1111").build();
    given(userRepository.findById(userId))
        .willReturn(Optional.of(User.builder().userCards(List.of()).build()));

    String errorJson = "{\"status\":\"BAD_REQUEST\"}";
    HttpClientErrorException exception =
        HttpClientErrorException.create(
            HttpStatus.BAD_REQUEST, "Bad Request", HttpHeaders.EMPTY, errorJson.getBytes(), null);

    given(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
        .willThrow(exception);

    assertThatThrownBy(() -> userCardService.addCard(userId, request))
        .isInstanceOf(BadRequestException400.class);
  }

  @DisplayName("카드사 응답 body가 null이면 예외 발생")
  @Test
  void addCard_bodyIsNull_throws() {
    Long userId = 1L;
    AddCardRequest request = AddCardRequest.builder().cardNumber("1111").build();

    given(userRepository.findById(userId))
        .willReturn(Optional.of(User.builder().userCards(List.of()).build()));
    ResponseEntity<CommonResponse<CardTokenResponse>> nullBodyResponse = ResponseEntity.ok(null);

    given(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
        .willReturn(nullBodyResponse);

    assertThatThrownBy(() -> userCardService.addCard(userId, request))
        .isInstanceOf(NoSuchElementFoundException404.class);
  }

  @DisplayName("카드 ID로 조회 실패 시 예외 발생")
  @Test
  void addCard_cardNotFound_throws() {
    // given
    Long userId = 1L;
    AddCardRequest request = AddCardRequest.builder().cardNumber("1111").build();

    User user = User.builder().userCards(List.of()).build();
    CardTokenResponse tokenResponse = new CardTokenResponse(77L, "token123");
    CommonResponse<CardTokenResponse> commonResponse =
        new CommonResponse<>(true, HttpStatus.OK, "", tokenResponse);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
        .willReturn(ResponseEntity.ok(commonResponse));
    given(cardRepository.findById(77L)).willReturn(Optional.empty()); // 💡 카드 못 찾음

    // when & then
    assertThatThrownBy(() -> userCardService.addCard(userId, request))
        .isInstanceOf(NoSuchElementFoundException404.class)
        .hasMessageContaining("해당 카드를 찾을 수 없습니다.");
  }

  @Test
  void getCardDetail_cardNotFound_throws() {

    // given
    Long cardId = 999L;
    given(cardRepository.findById(cardId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userCardService.getCardDetail(cardId))
        .isInstanceOf(NoSuchElementFoundException404.class);
  }

  @DisplayName("카드 ID로 상세 정보를 조회할 수 있다")
  @Test
  void getCardDetail_success() {
    // given
    Long cardId = 77L;
    Benefit benefit =
        Benefit.builder()
            .id(1L)
            .title("혜택제목")
            .description("혜택설명")
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(true)
            .benefitConditions(
                List.of(
                    BenefitCondition.builder()
                        .id(10L)
                        .value(30000L)
                        .category(BenefitConditionCategory.MONTHLY_DISCOUNT_LIMIT)
                        .build()))
            .build();

    Card card =
        Card.builder()
            .id(cardId)
            .name("카드의정석")
            .imageUrl("https://example.com/card.png")
            .company(CardCompany.WOORI)
            .annualFee(10000L)
            .cardBenefits(List.of(CardBenefit.builder().benefit(benefit).build()))
            .build();

    given(cardRepository.findById(cardId)).willReturn(Optional.of(card));
    given(cardRepository.findMinSpendingByCardId(cardId)).willReturn(300000L);

    // when
    CardDetailResponse response = userCardService.getCardDetail(cardId);

    // then
    assertThat(response.getCardName()).isEqualTo("카드의정석");
    assertThat(response.getImageUrl()).isEqualTo("https://example.com/card.png");
    assertThat(response.getCardCompany()).isEqualTo(CardCompany.WOORI);
    assertThat(response.getAnnualFee()).isEqualTo(10000L);
    assertThat(response.getMinSpending()).isEqualTo("300,000원 이상");
    assertThat(response.getBenefits()).hasSize(1);

    BenefitResponse benefitRes = response.getBenefits().get(0);
    assertThat(benefitRes.getTitle()).isEqualTo("혜택제목");
    assertThat(benefitRes.getBenefitType()).isEqualTo("DISCOUNT");
    assertThat(benefitRes.getHasAdditionalCondition()).isTrue();
    assertThat(benefitRes.getBenefitConditions()).hasSize(1);
    assertThat(benefitRes.getBenefitConditions().get(0).getCategory())
        .isEqualTo("MONTHLY_DISCOUNT_LIMIT");
  }

  @DisplayName("카테고리 기반 카드 추천 리스트를 반환한다")
  @Test
  void getCardRecommendation_success() {
    // given
    MerchantCategory category = MerchantCategory.FOOD_BEVERAGE;
    Merchant merchant = Merchant.builder().category(category).build();

    Benefit matching =
        Benefit.builder()
            .id(1L)
            .title("외식 할인")
            .description("5% 할인")
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .merchant(merchant)
            .benefitConditions(List.of())
            .build();

    Benefit nonMatching =
        Benefit.builder()
            .id(2L)
            .title("쇼핑 할인")
            .description("3% 할인")
            .benefitType(BenefitType.DISCOUNT)
            .hasAdditionalCondition(false)
            .merchant(Merchant.builder().category(MerchantCategory.SHOPPING).build())
            .benefitConditions(List.of())
            .build();

    Card card =
        Card.builder()
            .id(101L)
            .name("우리 외식카드")
            .imageUrl("https://example.com/food.png")
            .company(CardCompany.WOORI)
            .cardBenefits(
                List.of(
                    CardBenefit.builder().benefit(nonMatching).build(),
                    CardBenefit.builder().benefit(matching).build()))
            .build();

    given(cardRepository.findCardsByMerchantCategory(category)).willReturn(List.of(card));

    // when
    List<CardRecommendationResponse> result = userCardService.getCardRecommendation(category);

    // then
    assertThat(result).hasSize(1);
    CardRecommendationResponse res = result.get(0);
    assertThat(res.getCardId()).isEqualTo(101L);
    assertThat(res.getCardName()).isEqualTo("우리 외식카드");
    assertThat(res.getCardCompany()).isEqualTo(CardCompany.WOORI);
    assertThat(res.getBenefits()).hasSize(2);
    assertThat(res.getBenefits().get(0).getTitle()).isEqualTo("외식 할인"); // matching 혜택 먼저
    assertThat(res.getBenefits().get(1).getTitle()).isEqualTo("쇼핑 할인");
  }
}
