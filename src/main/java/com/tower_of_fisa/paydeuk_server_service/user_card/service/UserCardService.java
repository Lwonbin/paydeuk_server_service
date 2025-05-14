package com.tower_of_fisa.paydeuk_server_service.user_card.service;

import com.tower_of_fisa.paydeuk_server_service.admin.repository.PaymentRepository;
import com.tower_of_fisa.paydeuk_server_service.card.repository.CardRepository;
import com.tower_of_fisa.paydeuk_server_service.common.ErrorDefineCode;
import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.NoSuchElementFoundException404;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.Card;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.CardBenefit;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.Payment;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.UserCard;
import com.tower_of_fisa.paydeuk_server_service.user.repository.UserRepository;
import com.tower_of_fisa.paydeuk_server_service.user_card.dto.*;
import com.tower_of_fisa.paydeuk_server_service.user_card.repository.UserCardRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCardService {
  private final UserCardRepository userCardRepository;
  private final PaymentRepository paymentRepository;
  private final UserRepository userRepository;
  private final CardRepository cardRepository;

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
  public List<PaymentHistoryResponse> getPaymentHistory(Long userId) {
    List<Payment> payments = paymentRepository.findPaymentHistoryByUserId(userId);

    return payments.stream()
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
                    .build())
        .toList();
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
  public AddCardResponse addCard(Long userId, AddCardRequest addCardRequest) {
    // 1. 유저 조회
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));

    // addCardRequest를 카드사에게 주고 카드사에게 이 카드의 정보(cardId or cardName, ex-현대카드와 cardToken을 받음)
    Card card =
        cardRepository
            .findById(1L)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.CARD_NOT_FOUND));

    // 2.카드 토큰 생성 (실제로는 addCardRequest를 카드사에게 주고 카드사 API를 통해 토큰화)
    String cardToken = UUID.randomUUID().toString();
    // 토큰이랑 userId로 user-card table 조회해서 이미 있으면 duplicate 예외처리
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

    return AddCardResponse.builder().cardId(userCard.getId()).cardImage(card.getImageUrl()).build();
  }
}
