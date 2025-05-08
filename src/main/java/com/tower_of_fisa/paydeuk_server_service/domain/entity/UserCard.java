package com.tower_of_fisa.paydeuk_server_service.domain.entity;

import com.tower_of_fisa.paydeuk_server_service.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_card")
public class UserCard extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "card_token", length = 40, nullable = false)
    private String cardToken;

    @Column(name = "card_number", length = 40, nullable = false)
    private String cardNumber;


    @Column(name = "is_default_card", nullable = false)
    private Boolean isDefaultCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;


    @OneToMany(mappedBy = "userCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

}