package com.tower_of_fisa.paydeuk_server_service.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "결제 내역 응답")
public class PaymentHistoryResponse {
    @Schema(description = "결제 id", example = "1")
    private final Long id;

    @Schema(description = "가맹점명", example = "스타벅스")
    private final String shopName;

    @Schema(description = "카드명", example = "현대카드 M")
    private final String cardName;

    @Schema(description = "결제 금액", example = "4500")
    private final Integer transactionAmount;

    @Schema(description = "할인 금액", example = "450")
    private final Integer discountAmount;

    @Schema(description = "적용된 혜택", example = "국내외 가맹점 1.5% M포인트 적립")
    private final String applicationBenefit;

    @Schema(description = "결제 일시", example = "2024-03-20T14:30:00")
    private final LocalDateTime createdAt;
} 