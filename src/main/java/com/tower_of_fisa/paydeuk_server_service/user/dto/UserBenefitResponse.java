package com.tower_of_fisa.paydeuk_server_service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserBenefitResponse {
  private String name;
  private int lastMonthSum;
  private int currentMonthSum;
}
