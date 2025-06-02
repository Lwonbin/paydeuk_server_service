package com.tower_of_fisa.paydeuk_server_service.user.repository;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.UserStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByPersonalAuthKey(String personalAuthKey);

  @Query(
"""
  SELECT u FROM User u
  WHERE u.role = 'USER'
    AND (:status IS NULL OR u.status = :status)
    AND (:search IS NULL
         OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
         OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
""")
  Page<User> findByRole(
      Pageable pageable, @Param("status") UserStatus status, @Param("search") String search);

  Optional<User> findByUsername(String username);
}
