package com.tower_of_fisa.paydeuk_server_service.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorDefineCode {
  UNCAUGHT("ERR_00", "Uncaught Exception"),
  VALID_ERROR("ERR_01", "Field Validation fail"),
  EXAMPLE_OCCURER_ERROR("ERR_02", "예제 코드에서 그냥 발생시킨 오류랍니다"),
  DUPLICATE_EXAMPLE_NAME("ERR_03", "Example로 중복된 이름을 사용할 수 없습니다"),
  AUTH_NOT_FOUND_EMAIL("ERR_04", "해당 이메일을 찾을 수 없습니다."),
  AUTHORIZATION_FAIL("ERR_05", "해당 권한이 없습니다."),
  AUTHENTICATE_FAIL("ERR_06", "권한 인증에 실패했습니다."),
  MERCHANT_NOT_FOUND("MER_01", "해당 가맹점을 찾을 수가 없습니다."),
  ;

  private final String code;
  private final String message;
}
