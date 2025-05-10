package com.tower_of_fisa.paydeuk_server_service.dto.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "MerchantStatsResponse : 전체 가맹점 통계 응답 스키마")
public class MerchantStatsResponse {

  @Schema(description = "총 가맹점 수", example = "235")
  private int merchantCount;

  @Schema(description = "전체 결제 건수 (성공 기준)", example = "4892")
  private int transactionCount;

  @Schema(description = "전체 결제 금액 합계 (단위: 원)", example = "103040000")
  private long totalTransactionAmount;

  @Schema(description = "전체 평균 결제 금액 (단위: 원)", example = "21069")
  private int averageTransactionAmount;
}
