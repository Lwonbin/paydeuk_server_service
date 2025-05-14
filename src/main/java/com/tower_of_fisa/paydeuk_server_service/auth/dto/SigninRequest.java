package com.tower_of_fisa.paydeuk_server_service.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "SigninRequestDto : 로그인 요청 스키마")
public class SigninRequest {

  @NotBlank(message = "아이디를 입력해주세요.")
  @Schema(description = "사용자 아이디", example = "user")
  private String username;

  @NotBlank(message = "비밀번호를 입력해주세요.")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
      message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 포함한 8~20자여야 합니다.")
  @Schema(description = "비밀번호", example = "Aa1234!@")
  private String password;
}
