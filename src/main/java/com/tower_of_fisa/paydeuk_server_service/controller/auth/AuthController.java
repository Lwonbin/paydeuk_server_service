package com.tower_of_fisa.paydeuk_server_service.controller.auth;

import com.tower_of_fisa.paydeuk_server_service.common.response.CommonResponse;
import com.tower_of_fisa.paydeuk_server_service.dto.auth.FindIdRequest;
import com.tower_of_fisa.paydeuk_server_service.dto.auth.FindIdResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
