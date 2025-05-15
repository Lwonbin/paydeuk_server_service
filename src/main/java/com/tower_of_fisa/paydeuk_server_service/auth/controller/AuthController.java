package com.tower_of_fisa.paydeuk_server_service.auth.controller;

import com.tower_of_fisa.paydeuk_server_service.auth.dto.*;
import com.tower_of_fisa.paydeuk_server_service.auth.service.AuthService;
import com.tower_of_fisa.paydeuk_server_service.common.response.CommonResponse;
import com.tower_of_fisa.paydeuk_server_service.common.response.SwaggerErrorResponseType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1 - Auth API", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/find-id")
  @Operation(
      summary = "AUTH_04 : 아이디 찾기",
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
  @Operation(summary = "AUTH_05 : 비밀번호 찾기", description = "사용자의 이름과 아이디를 입력받아 본인인증을 수행합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = {@Content(schema = @Schema(implementation = SwaggerErrorResponseType.class))})
      })
  public CommonResponse<FindPasswordResponse> findPassword(
      @Valid @RequestBody FindPasswordRequest request) {
    FindPasswordResponse response = authService.findPassword(request);
    return new CommonResponse<>(true, HttpStatus.OK, "본인인증이 완료되었습니다.", response);
  }

  @PostMapping("/signup")
  @Operation(summary = "AUTH_01 : 회원가입", description = "사용자 계정을 생성합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(
            responseCode = "409",
            description = "중복된 username",
            content = {@Content(schema = @Schema(implementation = SwaggerErrorResponseType.class))})
      })
  public CommonResponse<Long> signup(@Valid @RequestBody SignupRequest request) {
    authService.registerUser(request);
    return new CommonResponse<>(true, HttpStatus.OK, "회원가입에 성공했습니다", null);
  }

  @PostMapping("/reset-password")
  @Operation(summary = "AUTH_06 : 비밀번호 재설정", description = "본인인증이 완료된 사용자의 비밀번호를 재설정합니다.")
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

  @PostMapping("/signin")
  @Operation(summary = "AUTH_02 : 로그인", description = "아이디와 비밀번호로 로그인합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(
            responseCode = "401",
            description = "권한 인증 실패",
            content = {@Content(schema = @Schema(implementation = SwaggerErrorResponseType.class))})
      })
  public void loginDocOnly(@RequestBody SigninRequest loginRequest) {
    // Swagger 문서 용도
  }

  @PostMapping("/refresh")
  @Operation(summary = "AUTH_03 : 토큰 재발급", description = "Refresh 토큰으로 Access 토큰을 재발급합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(
            responseCode = "401",
            description = "Refresh 토큰 유효하지 않음",
            content = {@Content(schema = @Schema(implementation = SwaggerErrorResponseType.class))})
      })
  public CommonResponse<Map<String, String>> refreshAccessToken(
      @RequestHeader("Authorization") String authHeader) {
    String refreshToken = authHeader.replace("Bearer ", "");
    Map<String, String> tokens = authService.refreshAccessToken(refreshToken);
    return new CommonResponse<>(true, HttpStatus.OK, "AccessToken 재발급 성공했습니다", tokens);
  }
}
