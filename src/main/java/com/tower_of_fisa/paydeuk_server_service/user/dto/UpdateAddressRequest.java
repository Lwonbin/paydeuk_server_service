package com.tower_of_fisa.paydeuk_server_service.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAddressRequest {
  @Size(max = 30)
  @Schema(description = "주소", example = "서울 마포구 월드컵북로 434 상암 IT Tower")
  private String address;
}
