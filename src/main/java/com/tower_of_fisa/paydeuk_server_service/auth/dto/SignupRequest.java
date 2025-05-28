package com.tower_of_fisa.paydeuk_server_service.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "SignupRequestDto : 회원가입 요청 스키마")
public class SignupRequest {

  @NotBlank(message = "이름을 입력해주세요.")
  @Schema(description = "이름", example = "홍길동")
  private String name;

  @NotBlank(message = "생년월일을 입력해주세요.")
  @Pattern(regexp = "^\\d{4}\\.\\d{2}\\.\\d{2}$", message = "생년월일 형식은 yyyy.MM.dd 입니다.")
  @Schema(description = "생년월일", example = "2000.05.12")
  private String birthdate;

  @NotBlank(message = "전화번호를 입력해주세요.")
  @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$", message = "전화번호는 010-XXXX-XXXX 형식이어야 합니다.")
  @Schema(description = "전화번호", example = "010-1234-5678")
  private String phone;

  @NotBlank(message = "아이디를 입력해주세요.")
  @Schema(description = "사용자 아이디", example = "hong123")
  private String username;

  @NotBlank(message = "이메일을 입력해주세요.")
  @Email(message = "이메일 형식에 맞지 않습니다.")
  @Schema(description = "이메일", example = "hong@example.com")
  private String email;

  @NotBlank(message = "비밀번호를 입력해주세요.")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
      message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 포함한 8~20자여야 합니다.")
  @Schema(description = "비밀번호", example = "Aa1234!@")
  private String password;

  @NotBlank(message = "사용자 식별키가 필요합니다.")
  @Schema(description = "본인인증 후 발급된 개인 식별키", example = "uheeR/P2ECGn+...")
  private String personalAuthKey;

  @Schema(description = "간편 결제 비밀번호", example = "191559")
  private String paymentPinCode;
}
