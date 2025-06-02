package com.tower_of_fisa.paydeuk_server_service.user_card.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;

import com.tower_of_fisa.paydeuk_server_service.admin.repository.PaymentRepository;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.*;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.Card;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.CardBenefit;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.UserCard;
import com.tower_of_fisa.paydeuk_server_service.global.config.exception.custom.exception.*;
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
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserCardServiceTest {

  @InjectMocks UserCardService userCardService;
  @Mock UserCardRepository userCardRepository;
  @Mock PaymentRepository paymentRepository;

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
}
