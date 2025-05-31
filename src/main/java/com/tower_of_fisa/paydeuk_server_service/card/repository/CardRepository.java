package com.tower_of_fisa.paydeuk_server_service.card.repository;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.Card;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.MerchantCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
  @Query(
      "SELECT DISTINCT c FROM Card c "
          + "JOIN c.cardBenefits cb "
          + "JOIN cb.benefit b "
          + "JOIN b.merchant m "
          + "WHERE m.category = :category")
  List<Card> findCardsByMerchantCategory(@Param("category") MerchantCategory category);

  @Query(
      "SELECT sr.minSpending FROM Card c "
          + "JOIN c.cardBenefits cb "
          + "JOIN cb.benefit b "
          + "JOIN b.discounts d "
          + "JOIN d.spendingRange sr "
          + "WHERE c.id = :cardId "
          + "ORDER BY sr.minSpending ASC "
          + "LIMIT 1")
  Long findMinSpendingByCardId(@Param("cardId") Long cardId);
}
