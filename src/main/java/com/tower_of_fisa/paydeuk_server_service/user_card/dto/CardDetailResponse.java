package com.tower_of_fisa.paydeuk_server_service.user_card.dto;

import com.tower_of_fisa.paydeuk_server_service.domain.enums.CardCompany;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "카드 상세 정보 응답")
public class CardDetailResponse {

  @Schema(description = "카드 이름", example = "카드의정석")
  private final String cardName;

  @Schema(description = "카드 이미지 URL", example = "https://example.com/card.png")
  private final String imageUrl;

  @Schema(description = "카드 회사", example = "WOORI")
  private final CardCompany cardCompany;

  @Schema(description = "연회비", example = "10,000원")
  private final Long annualFee;

  @Schema(description = "최소 사용금액", example = "300,000원")
  private final String minSpending;

  @Schema(description = "카드 혜택 목록")
  private final List<BenefitResponse> benefits;
}
