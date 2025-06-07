package com.tower_of_fisa.paydeuk_server_service.config;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.UserRole;
import com.tower_of_fisa.paydeuk_server_service.global.config.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithCustomMockAdminSecurityContextFactory
    implements WithSecurityContextFactory<WithCustomMockAdmin> {

  @Override
  public SecurityContext createSecurityContext(WithCustomMockAdmin annotation) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    User user =
        User.builder()
            .id(annotation.id())
            .email(annotation.email())
            .username("admin")
            .password("password")
            .role(UserRole.ADMIN)
            .build();

    CustomUserDetails principal = new CustomUserDetails(user);
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    return context;
  }
}
