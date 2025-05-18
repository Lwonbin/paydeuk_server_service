package com.tower_of_fisa.paydeuk_server_service.domain.entity;

import com.tower_of_fisa.paydeuk_server_service.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "spending_range")
public class SpendingRange extends BaseEntity {
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "min_spending")
  private Long minSpending;

  @Column(name = "max_spending")
  private Long maxSpending;
}
