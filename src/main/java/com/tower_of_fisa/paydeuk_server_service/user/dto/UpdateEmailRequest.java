package com.tower_of_fisa.paydeuk_server_service.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmailRequest {

  @Email
  @Schema(description = "이메일", example = "abc1234@naver.com")
  private String email;
}
