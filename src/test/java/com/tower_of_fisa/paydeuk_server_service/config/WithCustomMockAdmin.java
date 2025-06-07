package com.tower_of_fisa.paydeuk_server_service.config;

import java.lang.annotation.*;
import org.springframework.security.test.context.support.WithSecurityContext;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithCustomMockAdminSecurityContextFactory.class)
public @interface WithCustomMockAdmin {
  long id() default 1L;

  String email() default "admin@example.com";
}
