package com.tower_of_fisa.paydeuk_server_service.admin.dto.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Schema(description = "가맹점 상태 변경 요청")
public class StatusRequest {

  @NotNull(message = "가맹점 상태는 필수입니다")
  @Schema(description = "활성화 여부", example = "true")
  private boolean status;
}
