package com.tower_of_fisa.paydeuk_server_service.admin.controller;

import com.tower_of_fisa.paydeuk_server_service.admin.dto.MerchantIndividualStatsResponse;
import com.tower_of_fisa.paydeuk_server_service.admin.dto.MerchantPaymentHistoryResponse;
import com.tower_of_fisa.paydeuk_server_service.admin.dto.MerchantStatsResponse;
import com.tower_of_fisa.paydeuk_server_service.admin.dto.MerchantTransactionTrendResponse;
import com.tower_of_fisa.paydeuk_server_service.admin.service.AdminMerchantService;
import com.tower_of_fisa.paydeuk_server_service.common.response.CommonResponse;
import com.tower_of_fisa.paydeuk_server_service.common.response.SwaggerErrorResponseType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ADMIN API", description = "관리자와 관련된 API") // Swagger 그룹 이름 및 설명
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminMerchantController {

  private final AdminMerchantService adminMerchantService;

  @GetMapping("/merchants/stats")
  @Operation(summary = "ADMIN_01 : 전체 가맹점 통계 조회", description = "모든 가맹점의 통계를 조회한다.")
  public CommonResponse<MerchantStatsResponse> getTotalMerchantStatistics() {
    MerchantStatsResponse response = adminMerchantService.getTotalMerchantStatistics();
    return new CommonResponse<>(true, HttpStatus.OK, "전체 가맹점 통계 조회 성공", response);
  }

  @GetMapping("/merchants/{merchantId}/trends")
  @Operation(summary = "ADMIN_02 : 가맹점 거래 추이", description = "해당 가맹점의 거래 추이를 조회한다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가맹점 ID로 요청 시 발생",
            content = @Content(schema = @Schema(implementation = SwaggerErrorResponseType.class)))
      })
  public CommonResponse<List<MerchantTransactionTrendResponse>> getMerchantTransactionTrends(
      @Parameter(description = "가맹점 ID", example = "1") @PathVariable Long merchantId) {
    List<MerchantTransactionTrendResponse> response =
        adminMerchantService.getMerchantTransactionTrends(merchantId);
    return new CommonResponse<>(true, HttpStatus.OK, "거래 추이 조회 성공", response);
  }

  @GetMapping("/merchants/{merchantId}/stats")
  @Operation(summary = "ADMIN_03 : 가맹점별 통계 조회", description = "특정 가맹점의 통계를 조회한다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 가맹점 ID로 요청 시 발생",
            content = @Content(schema = @Schema(implementation = SwaggerErrorResponseType.class)))
      })
  public CommonResponse<MerchantIndividualStatsResponse> getMerchantStatisticsById(
      @Parameter(description = "가맹점 ID", example = "1") @PathVariable Long merchantId) {
    MerchantIndividualStatsResponse response =
        adminMerchantService.getMerchantStatsById(merchantId);
    return new CommonResponse<>(true, HttpStatus.OK, "가맹점 통계 조회 성공", response);
  }

  @GetMapping("/payments")
  @Operation(summary = "ADMIN_04 : 전체 가맹점 결제 내역 조회", description = "전체 가맹점의 결제 내역을 조회한다.")
  public CommonResponse<List<MerchantPaymentHistoryResponse>> getAllMerchantPaymentHistories() {
    List<MerchantPaymentHistoryResponse> response =
        adminMerchantService.getAllMerchantPaymentHistories();
    return new CommonResponse<>(true, HttpStatus.OK, "전체 결제 내역 조회 성공", response);
  }
}
