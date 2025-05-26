package com.tower_of_fisa.paydeuk_server_service.user_card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "카드 토큰 발급 요청")
public class CardTokenRequest {

  @Schema(description = "사용자 이름", example = "임지섭")
  private final String userName;

  @Schema(description = "사용자 생년월일", example = "1999.09.01")
  private final String userBirthDate;

  @Schema(description = "사용자 전화번호", example = "010-3164-6358")
  private final String userPhone;

  @Schema(description = "카드 번호", example = "1111222233334445")
  private final String cardNumber;

  @Schema(description = "카드 만료 월", example = "12")
  private final String month;

  @Schema(description = "카드 만료 연도", example = "26")
  private final String year;

  @Schema(description = "카드 CVC", example = "123")
  private final String cvc;

  @Schema(description = "카드 비밀번호 앞 두자리", example = "00")
  private final String pinPrefix;
}
