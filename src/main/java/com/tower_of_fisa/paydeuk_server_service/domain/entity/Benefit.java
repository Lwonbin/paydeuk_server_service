package com.tower_of_fisa.paydeuk_server_service.domain.entity;

import com.tower_of_fisa.paydeuk_server_service.domain.enums.BenefitType;
import com.tower_of_fisa.paydeuk_server_service.global.common.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "benefit")
public class Benefit extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "description", length = 100)
  private String description;

  @Column(name = "title", length = 100)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(name = "benefit_type", nullable = false)
  private BenefitType benefitType;

  @Column(name = "has_additional_condition", nullable = false)
  private Boolean hasAdditionalCondition;

  @OneToMany(mappedBy = "benefit", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BenefitCondition> benefitConditions = new ArrayList<>();

  @OneToMany(mappedBy = "benefit", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CardBenefit> cardBenefits = new ArrayList<>();

  @OneToMany(mappedBy = "benefit", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Discount> discounts = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "merchant_id")
  private Merchant merchant;
}
