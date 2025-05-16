package com.tower_of_fisa.paydeuk_server_service.global.util.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

  private CookieUtil() {}

  public static void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
    Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
    refreshCookie.setHttpOnly(true); // XSS 공격 방지
    refreshCookie.setSecure(true); // HTTPS에서만 쿠키 전송
    refreshCookie.setPath("/"); // 전체 경로에서 사용 가능
    refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일 동안 유지

    response.addCookie(refreshCookie);
  }

  public static void deleteRefreshTokenCookie(HttpServletResponse response) {
    Cookie refreshCookie = new Cookie("refreshToken", null);
    refreshCookie.setHttpOnly(true);
    refreshCookie.setSecure(true);
    refreshCookie.setPath("/"); // 전체 경로에서 사용 가능
    refreshCookie.setMaxAge(0); // 쿠키 삭제
    response.addCookie(refreshCookie);
  }
}
