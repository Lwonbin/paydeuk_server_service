package com.tower_of_fisa.paydeuk_server_service.admin.controller;

import com.tower_of_fisa.paydeuk_server_service.common.response.CommonResponse;
import com.tower_of_fisa.paydeuk_server_service.admin.dto.UserListResponse;
import com.tower_of_fisa.paydeuk_server_service.admin.dto.UserStatsResponse;
import com.tower_of_fisa.paydeuk_server_service.admin.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin User API", description = "관리자용 사용자 관련 API")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
  private final AdminUserService adminUserService;

  @GetMapping
  @Operation(summary = "ADMIN_USER_01 : 사용자 목록 조회", description = "전체 사용자 목록을 조회한다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "사용자 목록 조회 성공",
            content = {@Content(schema = @Schema(implementation = UserListResponse.class))})
      })
  public CommonResponse<List<UserListResponse>> getAllUsers() {
    List<UserListResponse> result = adminUserService.getAllUsers();
    return new CommonResponse<>(true, HttpStatus.OK, "사용자 목록 조회에 성공했습니다", result);
  }

  @GetMapping("/stats")
  @Operation(
      summary = "ADMIN_USER_02 : 사용자 통계 조회",
      description = "전체 사용자 수, 활성 사용자 수, 비활성 사용자 수를 조회한다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "사용자 통계 조회 성공",
            content = {@Content(schema = @Schema(implementation = UserStatsResponse.class))})
      })
  public CommonResponse<UserStatsResponse> getUserStats() {
    UserStatsResponse result = adminUserService.getUserStats();
    return new CommonResponse<>(true, HttpStatus.OK, "사용자 통계 조회에 성공했습니다", result);
  }
}
