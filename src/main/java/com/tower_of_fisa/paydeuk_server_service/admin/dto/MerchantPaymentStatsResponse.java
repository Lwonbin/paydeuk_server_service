package com.tower_of_fisa.paydeuk_server_service.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(title = "TransactionStatisticsResponse : 전체 거래 통계 응답 스키마")
public class MerchantPaymentStatsResponse {
  @Schema(description = "총 거래 건수", example = "250")
  private int totalTransactionCount;

  @Schema(description = "총 거래 건수 전일 대비 변화율 (%)", example = "12.5")
  private double transactionCountChangeRate;

  @Schema(description = "총 거래 금액 (단위: 원)", example = "5000000")
  private long totalTransactionAmount;

  @Schema(description = "총 거래 금액 전일 대비 변화율 (%)", example = "8.75")
  private double transactionAmountChangeRate;

  @Schema(description = "평균 거래 금액 (단위: 원)", example = "20000")
  private int averageTransactionAmount;

  @Schema(description = "평균 거래 금액 전일 대비 변화율 (%)", example = "-2.3")
  private double averageAmountChangeRate;

  @Schema(description = "활성 거래 비율 (%)", example = "87.6")
  private double activeTransactionRate;
}
