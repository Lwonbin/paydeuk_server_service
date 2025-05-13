package com.tower_of_fisa.paydeuk_server_service.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "아이디 찾기 : Request 스키마")
public class FindIdRequest {
  @NotBlank(message = "personal_auth_key는 필수 입력값입니다.")
  @Schema(
      description = "본인인증 후 발급받은 personal_auth_key",
      example =
          "uheeR/P2ECGn+AaGPqAe1LB5swI9k/TnDK98Syo7djJerBROsv0M8+OnqpkR2cgZDRMQJFG42dSIk5f5J8IV/w==")
  private String personalAuthKey;
}
