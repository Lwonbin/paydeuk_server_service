package com.tower_of_fisa.paydeuk_server_service.user.controller;

import com.tower_of_fisa.paydeuk_server_service.common.response.CommonResponse;
import com.tower_of_fisa.paydeuk_server_service.common.response.EmptyResponse;
import com.tower_of_fisa.paydeuk_server_service.common.response.swagger_response.SwaggerResponseExample;
import com.tower_of_fisa.paydeuk_server_service.config.security.CustomUserDetails;
import com.tower_of_fisa.paydeuk_server_service.user.dto.SetNewPaymentPinCodeRequest;
import com.tower_of_fisa.paydeuk_server_service.user.dto.SetPaymentPinCodeRequest;
import com.tower_of_fisa.paydeuk_server_service.user.dto.UpdateProfileRequest;
import com.tower_of_fisa.paydeuk_server_service.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "2- User API", description = "사용자 정보 관련 API")
public class UserController {

  private final UserService userService;

  @PatchMapping("/profile")
  @Operation(summary = "USER_01 : 내 정보 변경", description = "사용자 정보중 이메일, 주소를 수정합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "가맹점 삭제 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content =
                @Content(examples = {@ExampleObject(value = SwaggerResponseExample.USER_404)}))
      })
  public CommonResponse<EmptyResponse> updateProfile(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody @Valid UpdateProfileRequest request) {

    userService.updateProfile(userDetails.getId(), request);
    return new CommonResponse<>(true, HttpStatus.OK, "내 정보 변경에 성공했습니다.", new EmptyResponse());
  }

  /*
   실제 서비스에서는 userId를 통해 User의 존재 여부를 확인해야 하는 API가 많지만, 대부분의 경우 해당 userId는 필터단에서 이미 인증되어 있으므로, 사용자 미존재 상황이 아닌 "권한이 없습니다" 에러만 확인할 수 있습니다.
   이에 따라 실제 User의 존재 여부를 직접 확인하고, Swagger에서 404 에러 응답 예시를 명확히 표현하기 위한 전용 API를 작성하였습니다.
  */
  @GetMapping("/{userId}/check")
  @Operation(summary = "USER_02 : 사용자 존재 여부 확인", description = "사용자 ID로 사용자의 존재 여부를 확인합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "사용자가 존재함"),
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = SwaggerResponseExample.USER_404)))
      })
  public CommonResponse<EmptyResponse> checkUserExists(@PathVariable Long userId) {
    userService.checkUserExists(userId);
    return new CommonResponse<>(true, HttpStatus.OK, "사용자가 존재합니다.", new EmptyResponse());
  }

  @PostMapping("/payment-pin")
  @Operation(summary = "USER_04 : 간편 결제 비밀번호 설정", description = "간편 결제 비밀번호를 설정합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "간편 결제 비밀번호 설정 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content =
                @Content(examples = {@ExampleObject(value = SwaggerResponseExample.USER_404)}))
      })
  public CommonResponse<EmptyResponse> setPaymentPinCode(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody @Valid SetPaymentPinCodeRequest request) {
    userService.setPaymentPinCode(userDetails.getId(), request);
    return new CommonResponse<>(true, HttpStatus.OK, "간편 결제 비밀번호를 설정하였습니다.", new EmptyResponse());
  }

  @PatchMapping("/payment-pin")
  @Operation(summary = "USER_05 : 간편 결제 비밀번호 변경", description = "간편 결제 비밀번호를 변경합니다.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "간편 결제 비밀번호 변경 성공"),
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content =
                @Content(examples = {@ExampleObject(value = SwaggerResponseExample.USER_404)}))
      })
  public CommonResponse<EmptyResponse> setNewPaymentPinCode(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody @Valid SetNewPaymentPinCodeRequest request) {
    userService.setNewPaymentPinCode(userDetails.getId(), request);
    return new CommonResponse<>(true, HttpStatus.OK, "간편 결제 비밀번호를 변경하였습니다.", new EmptyResponse());
  }
}
