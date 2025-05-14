package com.tower_of_fisa.paydeuk_server_service.card.repository;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

}
