package com.tower_of_fisa.paydeuk_server_service.auth.controller;

import com.tower_of_fisa.paydeuk_server_service.auth.dto.VerificationResponse;
import com.tower_of_fisa.paydeuk_server_service.auth.service.VerifyService;
import com.tower_of_fisa.paydeuk_server_service.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 본인인증 imp_uid를 통해 Iamport API와 통신하여 인증 정보를 조회하는 컨트롤러 - 인증 결과로 사용자 이름, 생년월일, 전화번호, personalAuthKey
 * 반환
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "1 - Auth API", description = "인증 관련 API")
public class VerifyController {

  private final VerifyService verifyService;

  /**
   * imp_uid를 기반으로 Iamport 본인인증 결과를 조회한다.
   *
   * @param impUid Iamport에서 제공하는 인증 식별자
   * @return CommonResponse<VerificationResponse> 본인인증 결과 포함
   */
  @GetMapping("/verification")
  @Operation(
      summary = "AUTH_07 : 본인인증 결과 조회",
      description = "imp_uid를 이용하여 iamport로부터 본인인증 결과를 조회한다.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "본인인증 결과 조회 성공",
            content = @Content(schema = @Schema(implementation = VerificationResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "인증 정보를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = VerificationResponse.class)))
      })
  public CommonResponse<VerificationResponse> verifyIdentity(
      @RequestParam("imp_uid") @NotBlank String impUid) {
    VerificationResponse response = verifyService.verifyIdentity(impUid);
    return new CommonResponse<>(true, HttpStatus.OK, "본인인증 결과 조회 성공", response);
  }
}
