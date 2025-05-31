package com.tower_of_fisa.paydeuk_server_service.user_card.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BenefitResponse {
  private final Long id;
  private final String title;
  private final String description;
  private final String benefitType;
  private final Boolean hasAdditionalCondition;
  private final List<BenefitConditionResponse> benefitConditions;
}
