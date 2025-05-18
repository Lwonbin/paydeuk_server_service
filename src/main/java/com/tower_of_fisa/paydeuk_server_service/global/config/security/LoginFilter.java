package com.tower_of_fisa.paydeuk_server_service.global.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tower_of_fisa.paydeuk_server_service.auth.dto.SigninRequest;
import com.tower_of_fisa.paydeuk_server_service.global.common.ErrorDefineCode;
import com.tower_of_fisa.paydeuk_server_service.global.config.exception.custom.exception.InvalidJsonFormatException400;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
  private final ObjectMapper objectMapper;
  private final AuthenticationManager authenticationManager;
  private final HandlerExceptionResolver resolver;

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

    try {
      SigninRequest signinRequest =
          objectMapper.readValue(request.getInputStream(), SigninRequest.class);

      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(
              signinRequest.getUsername(), signinRequest.getPassword());
      return authenticationManager.authenticate(authToken);

    } catch (IOException e) {
      resolver.resolveException(
          request, response, null, new InvalidJsonFormatException400(ErrorDefineCode.INVALID_JSON));
      return null;
    }
  }
}
