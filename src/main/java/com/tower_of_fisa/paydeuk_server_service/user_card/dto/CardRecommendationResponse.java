package com.tower_of_fisa.paydeuk_server_service.user_card.dto;

import com.tower_of_fisa.paydeuk_server_service.domain.enums.CardCompany;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "혜택별 카드 추천 응답")
public class CardRecommendationResponse {

  @Schema(description = "카드 ID", example = "1")
  private final Long cardId;

  @Schema(description = "카드 이름", example = "카드의정석")
  private final String cardName;

  @Schema(description = "카드 이미지 URL", example = "https://example.com/card.png")
  private final String imageUrl;

  @Schema(description = "카드 혜택 설명", example = "최대 5% 캐시백")
  private final List<BenefitResponse> benefits;

  @Schema(description = "카드 회사", example = "WOORI")
  private final CardCompany cardCompany;
}
