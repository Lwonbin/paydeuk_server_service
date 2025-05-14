package com.tower_of_fisa.paydeuk_server_service.auth.service;

import com.tower_of_fisa.paydeuk_server_service.auth.dto.VerificationResponse;
import com.tower_of_fisa.paydeuk_server_service.common.ErrorDefineCode;
import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.NoSuchElementFoundException404;
import com.tower_of_fisa.paydeuk_server_service.user.repository.UserRepository;
import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyService {

  @Value("${iamport.api.key}")
  private String apiKey;

  @Value("${iamport.api.secret}")
  private String apiSecret;

  private final UserRepository userRepository;

  /**
   * imp_uid를 기반으로 Iamport 본인인증 결과를 조회한다.
   *
   * @param impUid Iamport에서 제공하는 인증 식별자
   * @return VerificationResponse 본인인증 결과
   */
  public VerificationResponse verifyIdentity(String impUid) {
    log.info("imp_uid 수신됨: {}", impUid);

    // Iamport 액세스 토큰 발급
    String accessToken = getAccessToken();

    // 발급된 토큰을 사용하여 본인인증 정보 조회 및 DTO 매핑
    VerificationResponse response = getCertificationResult(impUid, accessToken);

    // unique_key로 중복 체크
    boolean isDuplicate =
        userRepository.findByPersonalAuthKey(response.getPersonalAuthKey()).isPresent();
    response.setDuplicate(isDuplicate);

    log.info("본인인증 결과: {}", response);

    return response;
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

    URI url =
        UriComponentsBuilder.fromHttpUrl("https://api.iamport.kr/certifications")
            .pathSegment(impUid) // 안전하게 경로 삽입됨
            .build()
            .toUri();

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
        (String) fullResult.get("unique_key"),
        false); // 초기값은 false로 설정
  }
}
