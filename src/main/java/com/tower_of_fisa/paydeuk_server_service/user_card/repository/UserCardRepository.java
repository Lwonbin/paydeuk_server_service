package com.tower_of_fisa.paydeuk_server_service.user_card.repository;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.UserCard;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCardRepository extends JpaRepository<UserCard, Long> {
  @Query(
      "SELECT uc FROM UserCard uc "
          + "JOIN FETCH uc.card c "
          + "JOIN FETCH c.cardBenefits cb "
          + "JOIN FETCH cb.benefit b "
          + "WHERE uc.user.id = :userId")
  List<UserCard> findByUserIdWithCardAndBenefits(@Param("userId") Long userId);

  List<UserCard> findByUserId(Long userId);

  Optional<UserCard> findByUserIdAndCardId(Long userId, Long cardId);
}
