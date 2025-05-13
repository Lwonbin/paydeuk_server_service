package com.tower_of_fisa.paydeuk_server_service.service;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.CardBenefit;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.UserCard;
import com.tower_of_fisa.paydeuk_server_service.dto.card.CardBenefitResponse;
import com.tower_of_fisa.paydeuk_server_service.dto.card.MyCardResponse;
import com.tower_of_fisa.paydeuk_server_service.repository.UserCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {
    private final UserCardRepository userCardRepository;
    /**
     * [user-card 탐색] user-card table를 탐색 하여 탐색 결과를 반환한다.
     *
     * @param userId Long - 유저 ID
     * @return MyCardResponse - 유저가 가지고 있는 카드정보와 카드 혜택들 조회
     */
    public List<MyCardResponse> getMyCards(Long userId) {
        List<UserCard> userCards = userCardRepository.findByUserIdWithCardAndBenefits(userId);

        return userCards.stream()
                .map(userCard -> MyCardResponse.builder()
                        .cardName(userCard.getCard().getName())
                        .cardNumber(userCard.getCardNumber().substring(userCard.getCardNumber().length() - 4))
                        .isDefaultCard(userCard.getIsDefaultCard())
                        .cardBenefits(userCard.getCard().getCardBenefits().stream()
                                .map(this::convertToCardBenefitResponse)
                                .toList())
                        .build())
                .toList();
    }




    /**
     * [카드 혜택 변환] 카드 혜택을 CardBenefitResponse로 변환한다.
     *
     * @param cardBenefit CardBenefit - 카드 혜택
     * @return CardBenefitResponse - 카드 혜택 응답 DTO
     */
    private CardBenefitResponse convertToCardBenefitResponse(CardBenefit cardBenefit) {
        return CardBenefitResponse.builder()
                .content(cardBenefit.getBenefit().getDescription())
                .build();
    }
} 