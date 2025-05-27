package com.tower_of_fisa.paydeuk_server_service.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(title = "SingleMerchantPaymentResponse : 개별 가맹점 결제 내역 응답 스키마")
public class SingleMerchantPaymentResponse {

  @Schema(description = "결제 ID (PK)", example = "1001")
  private Long paymentId;

  @Schema(description = "카드 번호", example = "1234")
  private String cardNumber;

  @Schema(description = "카드 이름", example = "현대카드 M")
  private String cardName;

  @Schema(description = "결제 발생 시각", example = "2024-05-09T14:23:00")
  private LocalDateTime createdAt;

  @Schema(description = "결제 금액 (단위: 원)", example = "12000")
  private Integer transactionAmount;

  @Schema(description = "결제 성공 여부", example = "true")
  private Boolean status;
}
