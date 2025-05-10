package com.tower_of_fisa.paydeuk_server_service.controller;

import com.tower_of_fisa.paydeuk_server_service.common.response.CommonResponse;
import com.tower_of_fisa.paydeuk_server_service.common.response.EmptyResponse;
import com.tower_of_fisa.paydeuk_server_service.common.response.swagger_response.SwaggerResponseExample;
import com.tower_of_fisa.paydeuk_server_service.dto.merchant.MerchantAllResponse;
import com.tower_of_fisa.paydeuk_server_service.dto.merchant.MerchantByIdResponse;
import com.tower_of_fisa.paydeuk_server_service.dto.merchant.StatusRequest;
import com.tower_of_fisa.paydeuk_server_service.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/merchants")
@RequiredArgsConstructor
@Tag(name = "관리자용 가맹점 관리 API", description = "관리자용 가맹점 관리 API")
public class MerchantController {
  private final MerchantService merchantService;

  @GetMapping
  @Operation(summary = "가맹점 목록 조회", description = "등록된 모든 가맹점의 목록을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "가맹점 목록 조회 성공")
  public CommonResponse<List<MerchantAllResponse>> getAllMerchants() {
    List<MerchantAllResponse> merchants = merchantService.getAllMerchants();
    return new CommonResponse<>(true, HttpStatus.OK, "가맹점 목록 조회에 성공했습니다.", merchants);
  }

  @Operation(summary = "가맹점 상세 조회", description = "특정 가맹점의 상세 정보를 조회합니다.")
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
    MerchantByIdResponse merchantByIdResponse = merchantService.getMerchantById(merchantId);
    return new CommonResponse<>(true, HttpStatus.OK, "가맹점 상세 조회에 성공했습니다.", merchantByIdResponse);
  }

  @PatchMapping("/{merchantId}/status")
  @Operation(summary = "가맹점 상태 변경", description = "가맹점의 활성화/비활성화 상태를 변경합니다.")
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
    merchantService.updateMerchantStatus(merchantId, statusRequest.isStatus());
    String message = statusRequest.isStatus() ? "가맹점이 활성화되었습니다." : "가맹점이 비활성화되었습니다.";
    return new CommonResponse<>(true, HttpStatus.OK, message, new EmptyResponse());
  }

  @PatchMapping("/{merchantId}/delete")
  @Operation(summary = "가맹점 삭제", description = "가맹점을 삭제합니다.")
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
    merchantService.deleteMerchant(merchantId);
    return new CommonResponse<>(true, HttpStatus.OK, "가맹점이 성공적으로 삭제되었습니다.", new EmptyResponse());
  }
}
