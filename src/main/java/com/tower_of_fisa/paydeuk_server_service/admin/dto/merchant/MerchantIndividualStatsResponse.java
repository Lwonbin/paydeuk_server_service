package com.tower_of_fisa.paydeuk_server_service.admin.dto.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(title = "MerchantIndividualStatsResponse : 개별 가맹점 통계 응답 스키마")
public class MerchantIndividualStatsResponse {

  @Schema(description = "가맹점의 결제 성공 건수", example = "15")
  private int transactionCount;

  @Schema(description = "가맹점의 총 결제 금액 (단위: 원)", example = "250000")
  private long totalTransactionAmount;

  @Schema(description = "평균 결제 금액 (단위: 원)", example = "16666")
  private int averageTransactionAmount;

  @Schema(description = "가맹점 수수료율 (%)", example = "3.5")
  private String commissionRate;
}
