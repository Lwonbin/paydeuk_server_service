package com.tower_of_fisa.paydeuk_server_service.card.controller;

import com.tower_of_fisa.paydeuk_server_service.common.response.CommonResponse;
import com.tower_of_fisa.paydeuk_server_service.config.security.CustomUserDetails;
import com.tower_of_fisa.paydeuk_server_service.dto.card.MyCardResponse;
import com.tower_of_fisa.paydeuk_server_service.dto.payment.PaymentHistoryResponse;
import com.tower_of_fisa.paydeuk_server_service.card.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
@Tag(name = "카드", description = "카드 관련 API")
public class CardController {

  private final CardService cardService;

  @GetMapping("/my")
  @Operation(summary = "CARD_01 : 내 카드 목록 조회", description = "사용자가 보유한 카드 목록을 조회합니다.")
  public CommonResponse<List<MyCardResponse>> getMyCards(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    List<MyCardResponse> cards = cardService.getMyCards(userDetails.getId());
    return new CommonResponse<>(true, HttpStatus.OK, "내 카드 리스트 조회에 성공했습니다.", cards);
  }

  @GetMapping("/my/payment")
  @Operation(summary = "CARD_02 : 내 결제 내역 조회", description = "사용자의 결제 내역을 조회합니다.")
  public CommonResponse<List<PaymentHistoryResponse>> getPaymentHistory(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    List<PaymentHistoryResponse> payments = cardService.getPaymentHistory(userDetails.getId());
    return new CommonResponse<>(true, HttpStatus.OK, "결제 내역 조회에 성공했습니다.", payments);
  }
}
