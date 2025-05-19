package com.tower_of_fisa.paydeuk_server_service.user_card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "내 카드 정보 응답")
public class MyCardResponse {

  @Schema(description = "카드 ID", example = "1")
  private Long id;

  @Schema(description = "카드명", example = "신한 플래티넘")
  private final String cardName;

  @Schema(description = "카드번호 뒷 4자리", example = "1234")
  private final String cardNumber;

  @Schema(description = "카드 이미지 URL", example = "https://example.com/card.png")
  private final String imageUrl;

  @Schema(description = "대표카드", example = "true")
  private final Boolean isDefaultCard;

  @Schema(description = "카드 혜택 목록")
  private final List<CardBenefitResponse> cardBenefits;
}
