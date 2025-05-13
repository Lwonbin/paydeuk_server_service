package com.tower_of_fisa.paydeuk_server_service.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "MerchantTransactionTrendResponse : 일별 거래 추이 응답 스키마")
public class MerchantTransactionTrendResponse {

  @Schema(description = "거래 날짜 (yyyy-MM-dd)", example = "2024-05-09")
  private LocalDate date;

  @Schema(description = "해당 날짜의 총 거래 금액 (단위: 원)", example = "152000")
  private Long transactionAmount;

  @Schema(description = "해당 날짜의 거래 건수", example = "18")
  private Long transactionCount;
}
