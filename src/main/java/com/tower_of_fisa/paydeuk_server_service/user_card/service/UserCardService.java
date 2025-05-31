package com.tower_of_fisa.paydeuk_server_service.user_card.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tower_of_fisa.paydeuk_server_service.admin.repository.PaymentRepository;
import com.tower_of_fisa.paydeuk_server_service.card.repository.CardRepository;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.Card;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.CardBenefit;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.UserCard;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.MerchantCategory;
import com.tower_of_fisa.paydeuk_server_service.global.common.ErrorDefineCode;
import com.tower_of_fisa.paydeuk_server_service.global.common.response.CommonResponse;
import com.tower_of_fisa.paydeuk_server_service.global.config.exception.custom.exception.AlreadyExistElementException409;
import com.tower_of_fisa.paydeuk_server_service.global.config.exception.custom.exception.BadRequestException400;
import com.tower_of_fisa.paydeuk_server_service.global.config.exception.custom.exception.ForbiddenException403;
import com.tower_of_fisa.paydeuk_server_service.global.config.exception.custom.exception.NoSuchElementFoundException404;
import com.tower_of_fisa.paydeuk_server_service.user.repository.UserRepository;
import com.tower_of_fisa.paydeuk_server_service.user_card.dto.*;
import com.tower_of_fisa.paydeuk_server_service.user_card.repository.UserCardRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCardService {
  private final UserCardRepository userCardRepository;
  private final PaymentRepository paymentRepository;
  private final UserRepository userRepository;
  private final CardRepository cardRepository;
  private final RestTemplate restTemplate;

  /**
   * [user-card 탐색] user-card table를 탐색 하여 탐색 결과를 반환한다.
   *
   * @param userId Long - 유저 ID
   * @return MyCardResponse - 유저가 가지고 있는 카드정보와 카드 혜택들 조회
   */
  public List<MyCardResponse> getMyCards(Long userId) {
    List<UserCard> userCards = userCardRepository.findByUserIdWithCardAndBenefits(userId);

    return userCards.stream()
        .map(
            userCard ->
                MyCardResponse.builder()
                    .id(userCard.getId())
                    .cardName(userCard.getCard().getName())
                    .cardNumber(
                        userCard.getCardNumber().substring(userCard.getCardNumber().length() - 4))
                    .imageUrl(userCard.getCard().getImageUrl())
                    .isDefaultCard(userCard.getIsDefaultCard())
                    .cardBenefits(
                        userCard.getCard().getCardBenefits().stream()
                            .map(this::convertToCardBenefitResponse)
                            .toList())
                    .build())
        .toList();
  }

  /**
   * [결제 내역 조회] 유저의 결제 내역을 조회한다.
   *
   * @param userId Long - 유저 ID
   * @return List<PaymentHistoryResponse> - 결제 내역 리스트
   */
  public Page<PaymentHistoryResponse> getPaymentHistory(Long userId, int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);

    return paymentRepository
        .findPaymentHistoryByUserId(userId, pageable)
        .map(
            payment ->
                PaymentHistoryResponse.builder()
                    .id(payment.getId())
                    .shopName(payment.getMerchant().getName())
                    .cardName(payment.getUserCard().getCard().getName())
                    .transactionAmount(payment.getAmount())
                    .discountAmount(payment.getDiscountAmount())
                    .applicationBenefit(payment.getCardBenefit().getBenefit().getDescription())
                    .createdAt(payment.getCreatedAt())
                    .build());
  }

  /**
   * [대표카드 설정] 유저의 대표카드를 설정한다.
   *
   * @param userId Long - 유저 ID
   * @param cardId Long - 설정할 카드 ID
   */
  @Transactional
  public void setDefaultCard(Long userId, Long cardId) {
    // 유효성 검사
    UserCard newDefaultCard = validateUserCard(userId, cardId);
    // 새로운 대표카드 설정
    newDefaultCard.setIsDefaultCard(true);
  }

  /**
   * [대표카드 변경] 유저의 대표카드를 변경한다.
   *
   * @param userId Long - 유저 ID
   * @param cardId Long - 변경할 카드 ID
   */
  @Transactional
  public void updateDefaultCard(Long userId, Long cardId) {
    // 기존 대표카드 해제
    userCardRepository.findByUserId(userId).stream()
        .filter(UserCard::getIsDefaultCard)
        .forEach(userCard -> userCard.setIsDefaultCard(false));
    // 유효성 검사
    UserCard newDefaultCard = validateUserCard(userId, cardId);
    // 새로운 대표카드 설정
    newDefaultCard.setIsDefaultCard(true);
  }

  /**
   * [카드 혜택 변환] 카드 혜택을 CardBenefitResponse로 변환한다.
   *
   * @param cardBenefit CardBenefit - 카드 혜택
   * @return CardBenefitResponse - 카드 혜택 응답 DTO
   */
  private CardBenefitResponse convertToCardBenefitResponse(CardBenefit cardBenefit) {
    return CardBenefitResponse.builder().content(cardBenefit.getBenefit().getDescription()).build();
  }

  /**
   * [카드 유효성 검사] - 카드 ID와 유저 ID로 UserCard를 조회하여 유효성을 검사한다.
   *
   * @param cardId Long - 카드 ID
   * @param userId Long - 유저 ID
   * @return UserCard - UserCard
   */
  private UserCard validateUserCard(Long userId, Long cardId) {
    return userCardRepository
        .findByUserIdAndCardId(userId, cardId)
        .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.CARD_NOT_FOUND));
  }

  /**
   * [카드 추가] 유저의 카드를 추가한다.
   *
   * @param userId Long - 유저 ID
   * @param addCardRequest AddCardRequest - 카드 추가 요청
   */
  @Transactional
  public void addCard(Long userId, AddCardRequest addCardRequest) {

    // 1. 유저 조회
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));

    // 2.카드 토큰 생성 (실제로는 addCardRequest를 카드사에게 주고 카드사 API를 통해 토큰화)
    String url = "http://localhost:8081/api/card/token/issue";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String userName = user.getName();
    String userBirthDate = user.getBirthDate();
    String userPhone = user.getPhone();

    // 카드 토큰 발급 요청 생성
    CardTokenRequest cardTokenRequest =
        CardTokenRequest.builder()
            .userName(userName)
            .userBirthDate(userBirthDate)
            .userPhone(userPhone)
            .cardNumber(addCardRequest.getCardNumber())
            .month(addCardRequest.getMonth())
            .year(addCardRequest.getYear())
            .cvc(addCardRequest.getCvc())
            .pinPrefix(addCardRequest.getPinPrefix())
            .build();

    HttpEntity<CardTokenRequest> entity = new HttpEntity<>(cardTokenRequest, headers);

    try {
      ResponseEntity<CommonResponse<CardTokenResponse>> response =
          restTemplate.exchange(
              url, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {});
      CommonResponse<CardTokenResponse> body = response.getBody();

      if (body == null || body.getResponse() == null) {
        throw new NoSuchElementFoundException404(ErrorDefineCode.UNCAUGHT);
      }

      // 3. 카드 정보 조회
      Card card =
          cardRepository
              .findById(body.getResponse().getCardId())
              .orElseThrow(
                  () -> new NoSuchElementFoundException404(ErrorDefineCode.CARD_NOT_FOUND));

      String cardToken = body.getResponse().getCardToken();

      if (userCardRepository.existsByCardToken(cardToken)) {
        throw new AlreadyExistElementException409(ErrorDefineCode.CARD_ALREADY_ISSUED);
      }

      // 4. UserCard 생성
      UserCard userCard =
          UserCard.builder()
              .cardToken(cardToken)
              .cardNumber(addCardRequest.getCardNumber())
              .isDefaultCard(user.getUserCards().isEmpty()) // 첫 카드면 대표카드로 설정
              .user(user)
              .card(card)
              .build();

      // 5. UserCard 저장
      userCardRepository.save(userCard);
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      // 8081 서버의 에러 메시지 추출
      String errorBody = e.getResponseBodyAsString();
      String status = extractStatusFromJson(errorBody);

      if ("NOT_FOUND".equals(status)) {
        throw new NoSuchElementFoundException404(ErrorDefineCode.CARD_NOT_FOUND);
      } else if ("FORBIDDEN".equals(status)) {
        throw new ForbiddenException403(ErrorDefineCode.CARD_OWNER_MISMATCH);
      } else if ("BAD_REQUEST".equals(status)) {
        throw new BadRequestException400(ErrorDefineCode.INVALID_CARD);
      } else {
        throw new BadRequestException400(ErrorDefineCode.UNCAUGHT);
      }
    }
  }

  @Transactional
  public void deleteCard(Long userId, Long cardId) {
    UserCard userCard = validateUserCard(userId, cardId);

    // 카드가 대표카드인 경우 예외 처리
    if (Boolean.TRUE.equals(userCard.getIsDefaultCard())) {
      throw new ForbiddenException403(ErrorDefineCode.DEFAULT_CARD_NOT_REMOVABLE);
    }

    userCardRepository.delete(userCard);
  }

  private String extractStatusFromJson(String json) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode node = mapper.readTree(json);
      return node.get("status").asText();
    } catch (Exception e) {
      return "500";
    }
  }

  public List<CardRecommendationResponse> getCardRecommendation(MerchantCategory category) {
    // 1. 해당 카테고리의 가맹점들이 제공하는 혜택을 가진 카드들을 조회
    List<Card> recommendedCards = cardRepository.findCardsByMerchantCategory(category);

    // 2. 각 카드에 대한 추천 응답 생성
    return recommendedCards.stream()
        .map(
            card ->
                CardRecommendationResponse.builder()
                    .cardId(card.getId())
                    .cardName(card.getName())
                    .imageUrl(card.getImageUrl())
                    .cardCompany(card.getCompany())
                    .benefits(
                        card.getCardBenefits().stream()
                            .map(CardBenefit::getBenefit)
                            .filter(
                                benefit ->
                                    benefit.getMerchant() != null
                                        && benefit.getMerchant().getCategory() == category)
                            .map(
                                benefit ->
                                    BenefitResponse.builder()
                                        .id(benefit.getId())
                                        .title(benefit.getTitle())
                                        .description(benefit.getDescription())
                                        .benefitType(benefit.getBenefitType().name())
                                        .hasAdditionalCondition(benefit.getHasAdditionalCondition())
                                        .benefitConditions(
                                            benefit.getBenefitConditions().stream()
                                                .map(
                                                    condition ->
                                                        BenefitConditionResponse.builder()
                                                            .id(condition.getId())
                                                            .value(condition.getValue())
                                                            .category(
                                                                condition.getCategory().name())
                                                            .build())
                                                .toList())
                                        .build())
                            .toList())
                    .build())
        .toList();
  }

  public CardDetailResponse getCardDetail(Long cardId) {
    Card card =
        cardRepository
            .findById(cardId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.CARD_NOT_FOUND));

    return CardDetailResponse.builder()
        .cardName(card.getName())
        .imageUrl(card.getImageUrl())
        .cardCompany(card.getCompany())
        .annualFee(card.getAnnualFee())
        .minSpending(String.format("%,d원 이상", cardRepository.findMinSpendingByCardId(cardId)))
        .benefits(
            card.getCardBenefits().stream()
                .map(
                    cardBenefit ->
                        BenefitResponse.builder()
                            .id(cardBenefit.getBenefit().getId())
                            .title(cardBenefit.getBenefit().getTitle())
                            .description(cardBenefit.getBenefit().getDescription())
                            .benefitType(cardBenefit.getBenefit().getBenefitType().name())
                            .hasAdditionalCondition(
                                cardBenefit.getBenefit().getHasAdditionalCondition())
                            .benefitConditions(
                                cardBenefit.getBenefit().getBenefitConditions().stream()
                                    .map(
                                        condition ->
                                            BenefitConditionResponse.builder()
                                                .id(condition.getId())
                                                .value(condition.getValue())
                                                .category(condition.getCategory().name())
                                                .build())
                                    .toList())
                            .build())
                .toList())
        .build();
  }
}
