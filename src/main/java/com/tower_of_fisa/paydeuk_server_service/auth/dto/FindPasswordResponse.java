package com.tower_of_fisa.paydeuk_server_service.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "비밀번호 찾기 응답 스키마")
public class FindPasswordResponse {

  @Schema(
      description = "본인인증 고유 키 (personalAuthKey)",
      example =
          "uheeR/P2ECGn+AaGPqAe1LB5swI9k/TnDK98Syo7djJerBROsv0M8+OnqpkR2cgZDRMQJFG42dSIk5f5J8IV/w==")
  private String personalAuthKey;
}
