package com.tower_of_fisa.paydeuk_server_service.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "User : Stats Response 스키마")
public class UserStatsResponse {
  @Schema(description = "총 사용자 수", example = "100")
  private long totalUsers;

  @Schema(description = "활성 사용자 수", example = "80")
  private long activeUsers;

  @Schema(description = "비활성 사용자 수", example = "20")
  private long inactiveUsers;

  @Schema(description = "이번 달 신규 가 수", example = "10")
  private long newUsers;
}
