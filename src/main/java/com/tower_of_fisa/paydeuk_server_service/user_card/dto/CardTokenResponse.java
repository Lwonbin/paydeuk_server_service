package com.tower_of_fisa.paydeuk_server_service.user_card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(description = "카드 등록 응답")
public class CardTokenResponse {
  @Schema(description = "카드 ID", example = "1")
  private final Long cardId;

  @Schema(description = "카드 토큰", example = "RF1yH2cquzK5T2vJ7TLHs5ygCtQ1Fchd")
  private final String cardToken;
}
