package com.tower_of_fisa.paydeuk_server_service.common.response.swagger_response;

public final class SwaggerResponseExample {
  // 소나큐브 유틸클래스 이슈로 인해 생성자를 명시적으로 막아줌
  private SwaggerResponseExample() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static final String MERCHANT_404 =
      """
          {
            "success": false,
            "status": "NOT_FOUND",
            "message": "해당 가맹점을 찾을 수가 없습니다.",
            "response": {
              "errorCode": "MER_ERR_01",
              "time": "2024-01-01T00:00:00",
              "stackTrace": "com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.NoSuchElementFoundException404: 해당 가맹점을 찾을 수가 없습니다.",
              "errors": null
            }
          }
      """;

  public static final String USER_404 =
      """
          {
            "success": false,
            "status": "NOT_FOUND",
            "message": "해당 사용자를 찾을 수 없습니다.",
            "response": {
              "errorCode": "USR_01",
              "time": "2025-05-13T16:34:17.2606453",
              "stackTrace": "com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.NoSuchElementFoundException404: 해당 사용자를 찾을 수 없습니다.",
              "errors": null
            }
          }
      """;

  public static final String PAY_PASSWORD_400 =
      """
          {
            "success": false,
            "status": "BAD_REQUEST",
            "message": "규칙에 맞지 않는 비밀번호 입니다.",
            "response": {
              "errorCode": "PAY_ASSWORD_01",
              "time": "2025-05-13T16:34:17.2606453",
              "stackTrace": "com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.PayPasswordViolationException400: 규칙에 맞지 않는 비밀번호 입니다.",
              "errors": null
            }
          }
      """;
}
