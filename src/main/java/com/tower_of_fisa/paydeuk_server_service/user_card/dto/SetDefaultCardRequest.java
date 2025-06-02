package com.tower_of_fisa.paydeuk_server_service.user_card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "대표카드 설정 요청")
public class SetDefaultCardRequest {
  @Schema(description = "카드 ID", example = "1")
  private Long cardId;
}
