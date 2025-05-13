package com.tower_of_fisa.paydeuk_server_service.admin.dto;

import com.tower_of_fisa.paydeuk_server_service.domain.enums.CardType;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.MerchantCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "MerchantPaymentHistoryResponse : 가맹점 결제 내역 응답 스키마")
public class MerchantPaymentHistoryResponse {

  @Schema(description = "결제 ID (PK)", example = "1001")
  private Long paymentId;

  @Schema(description = "가맹점 이름", example = "스타벅스")
  private String merchantName;

  @Schema(description = "카드 타입", example = "CREDIT")
  private CardType cardType;

  @Schema(description = "가맹점 카테고리", example = "FOOD_BEVERAGE")
  private MerchantCategory category;

  @Schema(description = "결제 발생 시각", example = "2024-05-09T14:23:00")
  private LocalDateTime createdAt;

  @Schema(description = "결제 금액 (단위: 원)", example = "12000")
  private Long transactionAmount;

  @Schema(description = "결제 성공 여부", example = "true")
  private boolean status;

  // DTO Projection을 위한 생성자
  public MerchantPaymentHistoryResponse(
      Long paymentId,
      String merchantName,
      CardType cardType,
      MerchantCategory category,
      LocalDateTime createdAt,
      Number transactionAmount,
      Boolean status) {
    this.paymentId = paymentId;
    this.merchantName = merchantName;
    this.cardType = cardType;
    this.category = category;
    this.createdAt = createdAt;
    this.transactionAmount = (transactionAmount != null) ? transactionAmount.longValue() : 0L;
    this.status = Boolean.TRUE.equals(status);
  }
}
