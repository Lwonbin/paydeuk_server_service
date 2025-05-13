package com.tower_of_fisa.paydeuk_server_service.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "본인인증 결과 : Response 스키마")
public class VerificationResponse {
  @Schema(description = "인증 성공 여부", example = "true")
  private Boolean certified;

  @Schema(description = "생년월일", example = "19900101")
  private String birthday;

  @Schema(description = "이름", example = "홍길동")
  private String name;

  @Schema(description = "전화번호", example = "01012345678")
  private String phone;

  @Schema(
      description = "개인 인증 키",
      example =
          "uheeR/P2ECGn+AaGPqAe1LB5swI9k/TnDK98Syo7djJerBROsv0M8+OnqpkR2cgZDRMQJFG42dSIk5f5J8IV/w==")
  private String personalAuthKey;

  @Schema(description = "이미 가입된 사용자 여부", example = "false")
  private Boolean duplicate;
}
