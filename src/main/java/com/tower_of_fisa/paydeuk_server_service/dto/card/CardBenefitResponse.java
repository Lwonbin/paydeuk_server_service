package com.tower_of_fisa.paydeuk_server_service.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "카드 혜택 응답")
public class CardBenefitResponse {
    @Schema(description = "혜택 내용", example = "영화 할인 20%")
    private final String content;
} 