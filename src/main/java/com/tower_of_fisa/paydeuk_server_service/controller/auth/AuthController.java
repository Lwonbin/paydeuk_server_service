package com.tower_of_fisa.paydeuk_server_service.controller.auth;

import com.tower_of_fisa.paydeuk_server_service.common.response.CommonResponse;
import com.tower_of_fisa.paydeuk_server_service.common.response.SwaggerErrorResponseType;
import com.tower_of_fisa.paydeuk_server_service.dto.auth.FindIdRequest;
import com.tower_of_fisa.paydeuk_server_service.dto.auth.FindIdResponse;
import com.tower_of_fisa.paydeuk_server_service.dto.auth.FindPasswordRequest;
import com.tower_of_fisa.paydeuk_server_service.dto.auth.ResetPasswordRequest;
import com.tower_of_fisa.paydeuk_server_service.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth API", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/find-id")
  @Operation(
      summary = "AUTH_01 : 아이디 찾기",
      description = "본인인증 후 발급받은 personal_auth_key를 통해 사용자의 아이디를 조회한다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "아이디 찾기 성공",
            content = {@Content(schema = @Schema(implementation = FindIdResponse.class))}),
        @ApiResponse(
            responseCode = "404",
            description = "일치하는 사용자를 찾을 수 없음",
            content = {@Content(schema = @Schema(implementation = FindIdResponse.class))})
      })
  public CommonResponse<FindIdResponse> findId(@Valid @RequestBody FindIdRequest request) {
    FindIdResponse result = authService.findId(request.getPersonalAuthKey());
    return new CommonResponse<>(true, HttpStatus.OK, "아이디 찾기에 성공했습니다", result);
  }

  @PostMapping("/find-password")
  @Operation(summary = "AUTH_02 : 비밀번호 찾기", description = "사용자의 이름과 아이디를 입력받아 본인인증을 수행합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = {@Content(schema = @Schema(implementation = SwaggerErrorResponseType.class))})
      })
  public CommonResponse<Void> findPassword(@Valid @RequestBody FindPasswordRequest request) {
    authService.findPassword(request);
    return new CommonResponse<>(true, HttpStatus.OK, "본인인증이 완료되었습니다.", null);
  }

  @PostMapping("/reset-password")
  @Operation(summary = "AUTH_03 : 비밀번호 재설정", description = "본인인증이 완료된 사용자의 비밀번호를 재설정합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = {
              @Content(schema = @Schema(implementation = SwaggerErrorResponseType.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "기존 비밀번호와 동일한 비밀번호",
            content = {@Content(schema = @Schema(implementation = SwaggerErrorResponseType.class))})
      })
  public CommonResponse<Void> resetPassword(
      @RequestParam String username, @Valid @RequestBody ResetPasswordRequest request) {
    authService.resetPassword(username, request);
    return new CommonResponse<>(true, HttpStatus.OK, "비밀번호가 성공적으로 변경되었습니다.", null);
  }
}
