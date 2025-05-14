package com.tower_of_fisa.paydeuk_server_service.user_card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
@Builder
@Getter
@Schema(description = "카드 등록 요청")
public class AddCardResponse {
    @Schema(description = "카드 ID", example = "1")
    private Long cardId;
    @Schema(description = "카드 이미지", example = "s3.amazonaws.com/paydeuk/card/1.png")
    private String cardImage;
}
