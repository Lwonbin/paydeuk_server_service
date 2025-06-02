package com.tower_of_fisa.paydeuk_server_service.config;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.UserRole;
import com.tower_of_fisa.paydeuk_server_service.global.config.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithCustomMockUserSecurityContextFactory
    implements WithSecurityContextFactory<WithCustomMockUser> {

  @Override
  public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    // 1. User 엔티티 직접 생성
    User user =
        User.builder()
            .id(annotation.id())
            .email(annotation.email())
            .username("mockuser") // getUsername() 대응
            .password("password") // getPassword() 대응
            .role(UserRole.USER) // enum 사용 시 적절히 맞추기
            .build();

    // 2. CustomUserDetails로 감싸기
    CustomUserDetails principal = new CustomUserDetails(user);

    // 3. SecurityContext에 주입
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

    return context;
  }
}
