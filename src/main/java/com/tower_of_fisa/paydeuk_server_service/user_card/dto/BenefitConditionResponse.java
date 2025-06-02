package com.tower_of_fisa.paydeuk_server_service.user_card.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BenefitConditionResponse {
  private final Long id;
  private final Long value;
  private final String category;
}
