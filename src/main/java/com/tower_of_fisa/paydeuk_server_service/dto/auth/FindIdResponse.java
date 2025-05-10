package com.tower_of_fisa.paydeuk_server_service.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "아이디 찾기 : Response 스키마")
public class FindIdResponse {
  @Schema(description = "사용자 아이디", example = "user123")
  private String username;
}
