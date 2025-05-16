package com.tower_of_fisa.paydeuk_server_service.admin.controller;

import com.tower_of_fisa.paydeuk_server_service.admin.dto.*;
import com.tower_of_fisa.paydeuk_server_service.admin.service.AdminMerchantService;
import com.tower_of_fisa.paydeuk_server_service.common.response.CommonResponse;
import com.tower_of_fisa.paydeuk_server_service.common.response.EmptyResponse;
import com.tower_of_fisa.paydeuk_server_service.common.response.SwaggerErrorResponseType;
import com.tower_of_fisa.paydeuk_server_service.common.response.swagger_response.SwaggerResponseExample;
import com.tower_of_fisa.paydeuk_server_service.global.dto.CustomPageResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "5 - Admin Merchant API", description = "관리자용 가맹점 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/merchants")
public class AdminMerchantController {

  private final AdminMerchantService adminMerchantService;

  @GetMapping("/stats")
  @Operation(summary = "ADMIN_01 : 전체 가맹점 통계 조회", description = "모든 가맹점의 통계를 조회한다.")
  public CommonResponse<MerchantStatsResponse> getTotalMerchantStatistics() {
    MerchantStatsResponse response = adminMerchantService.getTotalMerchantStatistics();
    return new CommonResponse<>(true, HttpStatus.OK, "전체 가맹점 통계 조회 성공", response);
  }

  @GetMapping("/payments")
  @Operation(summary = "ADMIN_02 : 전체 가맹점 결제 내역 조회", description = "전체 가맹점의 결제 내역을 조회한다.")
  public CommonResponse<List<MerchantPaymentHistoryResponse>> getAllMerchantPaymentHistories() {
    List<MerchantPaymentHistoryResponse> response =
        adminMerchantService.getAllMerchantPaymentHistories();
    return new CommonResponse<>(true, HttpStatus.OK, "전체 결제 내역 조회 성공", response);
  }

  @GetMapping("/{merchantId}/trends")
  @Operation(summary = "ADMIN_03 : 가맹점 거래 추이", description = "해당 가맹점의 거래 추이를 조회한다.")
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

  @GetMapping("/{merchantId}/stats")
  @Operation(summary = "ADMIN_04 : 가맹점별 통계 조회", description = "특정 가맹점의 통계를 조회한다.")
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

  @GetMapping
  @Operation(summary = "ADMIN_05 : 가맹점 목록 조회", description = "등록된 모든 가맹점의 목록을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "가맹점 목록 조회 성공")
  public CommonResponse<CustomPageResDto<MerchantAllResponse>> getAllMerchants(
      @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") int page,
      @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "5") int size) {
    Page<MerchantAllResponse> merchants = adminMerchantService.getAllMerchants(page, size);
    return new CommonResponse<>(
        true, HttpStatus.OK, "가맹점 목록 조회에 성공했습니다.", CustomPageResDto.fromPage(merchants));
  }

  @Operation(summary = "ADMIN_06 : 가맹점 상세 조회", description = "특정 가맹점의 상세 정보를 조회합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "가맹점 상세 정보 조회 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "가맹점을 찾을 수 없음",
            content =
                @Content(examples = {@ExampleObject(value = SwaggerResponseExample.MERCHANT_404)}))
      })
  @GetMapping("/{merchantId}")
  public CommonResponse<MerchantByIdResponse> getMerchantById(
      @Parameter(description = "가맹점 ID", required = true) @PathVariable Long merchantId) {
    MerchantByIdResponse merchantByIdResponse = adminMerchantService.getMerchantById(merchantId);
    return new CommonResponse<>(true, HttpStatus.OK, "가맹점 상세 조회에 성공했습니다.", merchantByIdResponse);
  }

  @PatchMapping("/{merchantId}/status")
  @Operation(summary = "ADMIN_07 : 가맹점 상태 변경", description = "가맹점의 활성화/비활성화 상태를 변경합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "가맹점 상태 변경 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "가맹점을 찾을 수 없음",
            content =
                @Content(examples = {@ExampleObject(value = SwaggerResponseExample.MERCHANT_404)}))
      })
  public CommonResponse<EmptyResponse> updateMerchantStatus(
      @PathVariable Long merchantId, @RequestBody StatusRequest statusRequest) {
    adminMerchantService.updateMerchantStatus(merchantId, statusRequest.isStatus());
    String message = statusRequest.isStatus() ? "가맹점이 활성화되었습니다." : "가맹점이 비활성화되었습니다.";
    return new CommonResponse<>(true, HttpStatus.OK, message, new EmptyResponse());
  }

  @PatchMapping("/{merchantId}/delete")
  @Operation(summary = "ADMIN_08 : 가맹점 삭제", description = "가맹점을 삭제합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "가맹점 삭제 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "가맹점을 찾을 수 없음",
            content =
                @Content(examples = {@ExampleObject(value = SwaggerResponseExample.MERCHANT_404)}))
      })
  public CommonResponse<EmptyResponse> deleteMerchant(@PathVariable Long merchantId) {
    adminMerchantService.deleteMerchant(merchantId);
    return new CommonResponse<>(true, HttpStatus.OK, "가맹점이 성공적으로 삭제되었습니다.", new EmptyResponse());
  }
}
