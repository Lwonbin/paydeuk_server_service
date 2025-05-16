package com.tower_of_fisa.paydeuk_server_service.user_card.controller;

import com.tower_of_fisa.paydeuk_server_service.global.common.response.CommonResponse;
import com.tower_of_fisa.paydeuk_server_service.global.common.response.EmptyResponse;
import com.tower_of_fisa.paydeuk_server_service.global.common.response.swagger_response.SwaggerResponseExample;
import com.tower_of_fisa.paydeuk_server_service.global.config.security.CustomUserDetails;
import com.tower_of_fisa.paydeuk_server_service.user_card.dto.AddCardRequest;
import com.tower_of_fisa.paydeuk_server_service.user_card.dto.MyCardResponse;
import com.tower_of_fisa.paydeuk_server_service.user_card.dto.PaymentHistoryResponse;
import com.tower_of_fisa.paydeuk_server_service.user_card.dto.SetDefaultCardRequest;
import com.tower_of_fisa.paydeuk_server_service.user_card.service.UserCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
@Tag(name = "3 - Card API", description = "카드 관련 API")
public class UserCardController {

  private final UserCardService userCardService;

  @GetMapping("/my")
  @Operation(summary = "CARD_01 : 내 카드 목록 조회", description = "사용자가 보유한 카드 목록을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "카드 리스트 조회에 성공")
  public CommonResponse<List<MyCardResponse>> getMyCards(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    List<MyCardResponse> cards = userCardService.getMyCards(userDetails.getId());
    return new CommonResponse<>(true, HttpStatus.OK, "내 카드 리스트 조회에 성공했습니다.", cards);
  }

  @GetMapping("/my/payment")
  @Operation(summary = "CARD_02 : 내 결제 내역 조회", description = "사용자의 결제 내역을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "결제 내역 조회에 성공")
  public CommonResponse<List<PaymentHistoryResponse>> getPaymentHistory(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    List<PaymentHistoryResponse> payments = userCardService.getPaymentHistory(userDetails.getId());
    return new CommonResponse<>(true, HttpStatus.OK, "결제 내역 조회에 성공했습니다.", payments);
  }

  @PostMapping
  @Operation(summary = "CARD_03 : 카드 등록", description = "사용자가 카드를 등록합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "카드 등록 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "카드를 찾을 수 없음",
            content =
                @Content(examples = {@ExampleObject(value = SwaggerResponseExample.CARD_404)}))
      })
  public CommonResponse<EmptyResponse> addCard(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody AddCardRequest addCardRequest) {
    userCardService.addCard(userDetails.getId(), addCardRequest);
    return new CommonResponse<>(true, HttpStatus.OK, "카드가 등록되었습니다.", new EmptyResponse());
  }

  @PostMapping("/default")
  @Operation(summary = "CARD_04 : 대표카드 설정", description = "사용자의 대표카드를 설정합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "대표카드 설정 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "카드를 찾을 수 없음",
            content =
                @Content(examples = {@ExampleObject(value = SwaggerResponseExample.CARD_404)}))
      })
  public CommonResponse<EmptyResponse> setDefaultCard(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody SetDefaultCardRequest req) {

    userCardService.setDefaultCard(userDetails.getId(), req.getCardId());
    return new CommonResponse<>(true, HttpStatus.OK, "대표카드가 설정되었습니다.", new EmptyResponse());
  }

  @PatchMapping("/default")
  @Operation(summary = "CARD_05 : 대표카드 변경", description = "사용자의 대표카드를 변경합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "대표카드 변경 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "카드를 찾을 수 없음",
            content =
                @Content(examples = {@ExampleObject(value = SwaggerResponseExample.CARD_404)}))
      })
  public CommonResponse<EmptyResponse> updateDefaultCard(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody SetDefaultCardRequest req) {
    userCardService.updateDefaultCard(userDetails.getId(), req.getCardId());
    return new CommonResponse<>(true, HttpStatus.OK, "대표카드가 변경되었습니다.", new EmptyResponse());
  }
}
