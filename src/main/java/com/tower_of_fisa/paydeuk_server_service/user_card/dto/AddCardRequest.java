package com.tower_of_fisa.paydeuk_server_service.user_card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "카드 추가 요청")
public class AddCardRequest {
  @Schema(description = "카드 번호", example = "1234567890123456")
  private String cardNumber;

  @Schema(description = "만료 월", example = "12")
  private String month;

  @Schema(description = "만료 연도", example = "25")
  private String year;

  @Schema(description = "CVC", example = "123")
  private String cvc;

  @Schema(description = "카드 앞 두자리", example = "12")
  private String pinPrefix;
}
