package com.tower_of_fisa.paydeuk_server_service.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "비밀번호 재설정 요청 스키마")
public class ResetPasswordRequest {

  @NotBlank(message = "비밀번호를 입력해주세요.")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
      message = "비밀번호는 8자 이상의 영문, 숫자, 특수문자를 포함해야 합니다.")
  @Schema(
      description = "새로운 비밀번호",
      example = "newPassword123!",
      pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$")
  private String password;
}
