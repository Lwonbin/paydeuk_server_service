package com.tower_of_fisa.paydeuk_server_service.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "내 카드 정보 응답")
public class MyCardResponse {
    @Schema(description = "카드명", example = "신한 플래티넘")
    private final String cardName;

    @Schema(description = "카드번호 뒷 4자리", example = "1234")
    private final String cardNumber;

    @Schema(description = "대표카드", example = "true")
    private final Boolean isDefaultCard;

    @Schema(description = "카드 혜택 목록")
    private final List<CardBenefitResponse> cardBenefits;
} 