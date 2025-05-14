package com.tower_of_fisa.paydeuk_server_service.user.repository;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByPersonalAuthKey(String personalAuthKey);

  Optional<User> findByUsername(String username);
}
