package com.tower_of_fisa.paydeuk_server_service.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private final CustomUserDetailsService userDetailsService;
  private final JwtProvider jwtProvider;
  private final LoginSuccessHandler successHandler;
  private final LoginFailureHandler failureHandler;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;
  private final CustomAccessDeniedHandler accessDeniedHandler;
  private final HandlerExceptionResolver handlerExceptionResolver;

  private static final String[] AUTH_WHITELIST = {
    "/v3/api-docs",
    "/v3/api-docs/**",
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/swagger-ui/index.html",
    "/swagger-resources/**",
    "/webjars/**"
  };

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
    builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    return builder.build();
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

    // 로그인 필터 설정
    LoginFilter loginFilter =
        new LoginFilter(new ObjectMapper(), authenticationManager, handlerExceptionResolver);
    loginFilter.setAuthenticationSuccessHandler(successHandler);
    loginFilter.setAuthenticationFailureHandler(failureHandler);
    loginFilter.setFilterProcessesUrl("/api/auth/signin");

    http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()
                    .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/card/**")
                    .hasRole("USER")
                    .requestMatchers("/api/user/**")
                    .hasRole("USER"))
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(
            new JwtAuthorizationFilter(jwtProvider, userDetailsService, handlerExceptionResolver),
            UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            e ->
                e.authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler));

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
