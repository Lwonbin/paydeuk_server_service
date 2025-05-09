package com.tower_of_fisa.paydeuk_server_service.repository;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByPersonalAuthKey(String personalAuthKey);
}
