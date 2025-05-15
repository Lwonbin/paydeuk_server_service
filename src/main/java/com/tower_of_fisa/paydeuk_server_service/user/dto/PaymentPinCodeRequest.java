package com.tower_of_fisa.paydeuk_server_service.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PaymentPinCodeRequest {
  @Size(max = 50)
  @Schema(description = "간편결제비밀번호", example = "051425")
  private String paymentPinCode;
}
