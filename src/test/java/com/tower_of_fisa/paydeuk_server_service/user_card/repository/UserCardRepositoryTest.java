package com.tower_of_fisa.paydeuk_server_service.user_card.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.*;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.BenefitType;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.CardCompany;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.CardType;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.UserRole;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.UserStatus;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
class UserCardRepositoryTest {

  @Autowired UserCardRepository userCardRepository;

  @Autowired EntityManager em;

  @Test
  @DisplayName("User ID로 카드 및 혜택까지 fetch 조인하여 조회 성공")
  void findByUserIdWithCardAndBenefits_success() {

    //given
    User user = createUser();
    Benefit benefit = createBenefit();
    Card card = createCard();

    em.persist(user);
    em.persist(card);
    em.persist(benefit);
    em.flush();

    CardBenefit cardBenefit = CardBenefit.builder().id(1L).card(card).benefit(benefit).build();

    card.getCardBenefits().add(cardBenefit);
    benefit.getCardBenefits().add(cardBenefit);

    em.persist(cardBenefit);

    UserCard userCard =
        UserCard.builder()
            .cardToken("token123")
            .cardNumber("1234-5678-9876-5432")
            .isDefaultCard(true)
            .user(user)
            .card(card)
            .build();

    em.persist(userCard);
    em.flush();
    em.clear();

    //when
    List<UserCard> results = userCardRepository.findByUserIdWithCardAndBenefits(user.getId());


    //then
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getCard().getCardBenefits()).isNotEmpty();
    assertThat(results.get(0).getCard().getCardBenefits().get(0).getBenefit().getTitle())
        .isEqualTo("커피 할인");
  }

  @Test
  @DisplayName("userId와 cardId로 UserCard 조회 성공")
  void findByUserIdAndCardId_success() {

    //given
    User user = createUser();
    Card card = createCard();
    UserCard userCard =
        UserCard.builder()
            .cardToken("token999")
            .cardNumber("1111-2222-3333-4444")
            .isDefaultCard(false)
            .user(user)
            .card(card)
            .build();

    em.persist(user);
    em.persist(card);
    em.persist(userCard);
    em.flush();

    //when
    Optional<UserCard> result =
        userCardRepository.findByUserIdAndCardId(user.getId(), card.getId());

    //then
    assertThat(result).isPresent();
    assertThat(result.get().getCardToken()).isEqualTo("token999");
  }

  @Test
  @DisplayName("cardToken 존재 여부 확인")
  void existsByCardToken_success() {

    //given
    User user = createUser();
    Card card = createCard();
    UserCard userCard =
        UserCard.builder()
            .cardToken("exist-token")
            .cardNumber("0000-0000-0000-0000")
            .isDefaultCard(false)
            .user(user)
            .card(card)
            .build();

    em.persist(user);
    em.persist(card);
    em.persist(userCard);
    em.flush();

    //when
    boolean exists = userCardRepository.existsByCardToken("exist-token");


    //then
    assertThat(exists).isTrue();
  }

  private User createUser() {
    return User.builder()
        .name("홍길동")
        .username("hong")
        .password("pwd123")
        .personalAuthKey("auth-key")
        .phone("01012345678")
        .birthDate("1990.01.01")
        .status(UserStatus.ACTIVE)
        .role(UserRole.USER)
        .build();
  }

  private Benefit createBenefit() {
    return Benefit.builder()
        .title("커피 할인")
        .description("스타벅스 30% 할인")
        .benefitType(BenefitType.DISCOUNT)
        .hasAdditionalCondition(false)
        .cardBenefits(new ArrayList<>())
        .benefitConditions(new ArrayList<>())
        .discounts(new ArrayList<>())
        .build();
  }

  private Card createCard() {
    return Card.builder()
        .name("우리카드 카페")
        .type(CardType.CREDIT)
        .imageUrl("http://image.url")
        .annualFee(10000L)
        .cardBenefits(new ArrayList<>())
        .company(CardCompany.WOORI)
        .build();
  }
}
