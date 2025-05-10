package com.tower_of_fisa.paydeuk_server_service.controller.auth;

import com.tower_of_fisa.paydeuk_server_service.common.ErrorDefineCode;
import com.tower_of_fisa.paydeuk_server_service.common.response.CommonResponse;
import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.NoSuchElementFoundException404;
import com.tower_of_fisa.paydeuk_server_service.dto.auth.VerificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * 본인인증 imp_uid를 통해 Iamport API와 통신하여 인증 정보를 조회하는 컨트롤러 - 인증 결과로 사용자 이름, 생년월일, 전화번호, personalAuthKey
 * 반환
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "인증 관련 API")
public class VerifyController {

  @Value("${iamport.api.key}")
  private String apiKey;

  @Value("${iamport.api.secret}")
  private String apiSecret;

  /**
   * imp_uid를 기반으로 Iamport 본인인증 결과를 조회한다.
   *
   * @param impUid Iamport에서 제공하는 인증 식별자
   * @return CommonResponse<VerificationResponse> 본인인증 결과 포함
   */
  @GetMapping("/verification")
  @Operation(
      summary = "AUTH_02 : 본인인증 결과 조회",
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

    log.info("imp_uid 수신됨: {}", impUid);

    // Iamport 액세스 토큰 발급
    String accessToken = getAccessToken();

    // 발급된 토큰을 사용하여 본인인증 정보 조회 및 DTO 매핑
    VerificationResponse response = getCertificationResult(impUid, accessToken);

    log.info("본인인증 결과: {}", response);

    // 공통 응답 형식으로 결과 반환
    return new CommonResponse<>(true, HttpStatus.OK, "본인인증 결과 조회 성공", response);
  }

  /**
   * Iamport API로부터 액세스 토큰을 발급받는다.
   *
   * @return 액세스 토큰 문자열
   */
  private String getAccessToken() {
    RestTemplate restTemplate = new RestTemplate();
    String url = "https://api.iamport.kr/users/getToken";

    // 요청 헤더 설정 (Content-Type: application/json)
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // 요청 본문 구성 (apiKey, apiSecret 포함)
    String json =
        """
        {
            "imp_key": "%s",
            "imp_secret": "%s"
        }
        """
            .formatted(apiKey, apiSecret);

    HttpEntity<String> request = new HttpEntity<>(json, headers);

    // Iamport에 POST 요청 → 액세스 토큰 응답 수신
    ResponseEntity<Map<String, Object>> response =
        restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {});

    // 응답에서 액세스 토큰 추출 후 반환
    return (String) extractAccessToken(response);
  }

  /**
   * 액세스 토큰 응답 객체에서 토큰 값을 추출한다.
   *
   * @param response API 응답
   * @return 추출된 액세스 토큰
   */
  private Object extractAccessToken(ResponseEntity<Map<String, Object>> response) {
    // 응답 바디 확인
    Map<String, Object> responseBody = response.getBody();
    if (responseBody == null) {
      throw new NoSuchElementFoundException404(ErrorDefineCode.VERIFICATION_TOKEN_NULL);
    }

    // "response" 필드 확인
    Object responseObj = responseBody.get("response");
    if (responseObj == null) {
      throw new NoSuchElementFoundException404(ErrorDefineCode.VERIFICATION_RESPONSE_NULL);
    }

    // 맵으로 형변환
    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap = (Map<String, Object>) responseObj;

    // 액세스 토큰 값 추출
    Object accessToken = responseMap.get("access_token");
    if (accessToken == null) {
      throw new NoSuchElementFoundException404(ErrorDefineCode.VERIFICATION_ACCESS_TOKEN_NULL);
    }

    return accessToken;
  }

  /**
   * Iamport API를 통해 인증 상세 정보를 조회하고 필요한 필드를 추출한다.
   *
   * @param impUid imp_uid 인증 식별자
   * @param accessToken 액세스 토큰
   * @return 인증 결과 정보 (이름, 생년월일, 전화번호, 고유키 등)
   */
  private VerificationResponse getCertificationResult(String impUid, String accessToken) {
    RestTemplate restTemplate = new RestTemplate();
    String url = "https://api.iamport.kr/certifications/" + impUid;

    // Authorization 헤더에 액세스 토큰 추가
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", accessToken);

    HttpEntity<Void> entity = new HttpEntity<>(headers);

    // GET 요청으로 본인인증 정보 조회
    ResponseEntity<Map<String, Object>> response =
        restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

    // 응답 바디 확인
    Map<String, Object> responseBody = response.getBody();
    if (responseBody == null) {
      throw new NoSuchElementFoundException404(ErrorDefineCode.VERIFICATION_RESULT_NULL);
    }

    // "response" 필드 추출
    Object responseObj = responseBody.get("response");
    if (responseObj == null) {
      throw new NoSuchElementFoundException404(ErrorDefineCode.VERIFICATION_RESPONSE_NULL);
    }

    // 상세 필드 Map으로 변환
    @SuppressWarnings("unchecked")
    Map<String, Object> fullResult = (Map<String, Object>) responseObj;

    // 필요한 필드만 추출하여 DTO에 매핑
    return new VerificationResponse(
        (Boolean) fullResult.get("certified"),
        (String) fullResult.get("birthday"),
        (String) fullResult.get("name"),
        (String) fullResult.get("phone"),
        (String) fullResult.get("unique_key"));
  }
}
