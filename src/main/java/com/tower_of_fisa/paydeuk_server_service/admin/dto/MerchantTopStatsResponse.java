package com.tower_of_fisa.paydeuk_server_service.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "MerchantTopStatsResponse : 상위 가맹점 통계 응답 스키마")
public class MerchantTopStatsResponse {

  @Schema(description = "가맹점 ID", example = "1")
  private Long merchantId;

  @Schema(description = "가맹점명", example = "스타벅스")
  private String merchantName;

  @Schema(description = "총 거래 건수", example = "152")
  private Long transactionCount;

  @Schema(description = "총 거래 금액 (단위: 원)", example = "1050000")
  private Long totalAmount;
}
