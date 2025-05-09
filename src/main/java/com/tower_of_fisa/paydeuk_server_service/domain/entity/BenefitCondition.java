package com.tower_of_fisa.paydeuk_server_service.domain.entity;

import com.tower_of_fisa.paydeuk_server_service.common.BaseEntity;
import com.tower_of_fisa.paydeuk_server_service.domain.Enum.BenefitConditionCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "benefit_condition")
public class BenefitCondition extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "value")
    private Long value;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private BenefitConditionCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benefit_id", nullable = false)
    private Benefit benefit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spending_range_id")
    private SpendingRange spendingRange;

}