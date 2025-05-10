package com.tower_of_fisa.paydeuk_server_service.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "비밀번호 찾기 요청 스키마")
public class FindPasswordRequest {

  @NotBlank(message = "이름을 입력해주세요.")
  @Schema(description = "사용자 이름", example = "홍길동")
  private String name;

  @NotBlank(message = "아이디를 입력해주세요.")
  @Schema(description = "사용자 아이디", example = "user123")
  private String username;
}
