package com.tower_of_fisa.paydeuk_server_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PaydeukServerServiceApplicatio {

  public static void main(String[] args) {
    SpringApplication.run(PaydeukServerServiceApplication.class, args);
  }
}
