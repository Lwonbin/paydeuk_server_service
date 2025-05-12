package com.tower_of_fisa.paydeuk_server_service.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tower_of_fisa.paydeuk_server_service.common.response.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtProvider jwtProvider;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String accessToken = jwtProvider.generateAccessToken(userDetails.getUser());
    String refreshToken = jwtProvider.generateRefreshToken(userDetails.getUser());

    String redirectUrl;
    String role = userDetails.getRoleName();

    if ("ADMIN".equals(role)) {
      redirectUrl = "/admin/merchants/stats";
    } else if ("USER".equals(role)) {
      redirectUrl = "/dashboard";
    } else {
      redirectUrl = "/";
    }

    Map<String, String> tokenMap =
        Map.of(
            "accessToken", accessToken,
            "refreshToken", refreshToken,
            "redirectUrl", redirectUrl);

    CommonResponse<Map<String, String>> commonResponse =
        new CommonResponse<>(true, HttpStatus.OK, "로그인에 성공했습니다.", tokenMap);

    response.setStatus(HttpStatus.OK.value());
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(commonResponse));
  }
}
