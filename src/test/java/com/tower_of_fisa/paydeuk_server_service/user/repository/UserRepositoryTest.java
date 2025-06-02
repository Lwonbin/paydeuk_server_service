package com.tower_of_fisa.paydeuk_server_service.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.UserRole;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.UserStatus;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {

  @Autowired UserRepository userRepository;

  @Autowired EntityManager em;

  @Test
  @DisplayName("개인 인증 키로 유저 조회")
  void findByPersonalAuthKey_success() {
    User user =
        User.builder()
            .name("홍길동")
            .username("testUser")
            .password("test1234")
            .personalAuthKey("auth123")
            .phone("01012345678")
            .birthDate("1990.01.01")
            .status(UserStatus.ACTIVE)
            .role(UserRole.USER)
            .build();

    userRepository.save(user);

    Optional<User> found = userRepository.findByPersonalAuthKey("auth123");

    assertThat(found).isPresent();
    assertThat(found.get().getUsername()).isEqualTo("testUser");
  }

  @Test
  @DisplayName("ROLE이 USER인 유저 페이징 조회")
  void findByRole_success() {
    User user1 =
        User.builder()
            .name("홍길동1")
            .username("u1")
            .password("test1234")
            .personalAuthKey("auth123-1")
            .phone("01012345671")
            .birthDate("1990.01.01")
            .status(UserStatus.ACTIVE)
            .role(UserRole.USER)
            .build();

    User user2 =
        User.builder()
            .name("홍길동2")
            .username("u2")
            .password("test1234")
            .personalAuthKey("auth123-2")
            .phone("01012345672")
            .birthDate("1991.01.01")
            .status(UserStatus.ACTIVE)
            .role(UserRole.USER)
            .build();

    User admin =
        User.builder()
            .name("홍길동3")
            .username("admin")
            .password("test1234")
            .personalAuthKey("auth123-3")
            .phone("01012345673")
            .birthDate("1985.05.05")
            .status(UserStatus.ACTIVE)
            .role(UserRole.ADMIN)
            .build();

    userRepository.save(user1);
    userRepository.save(user2);
    userRepository.save(admin);

    // status, search 둘 다 null로 전달 → 전체 USER만 조회
    Page<User> page = userRepository.findByRole(PageRequest.of(0, 10), null, null);

    assertThat(page.getContent()).hasSize(2);
    assertThat(page.getContent()).allMatch(u -> u.getRole() == UserRole.USER);
  }

  @Test
  @DisplayName("username으로 유저 조회")
  void findByUsername_success() {
    User user =
        User.builder()
            .name("홍길동1")
            .username("u1")
            .password("test1234")
            .personalAuthKey("auth123-1")
            .phone("01012345671")
            .birthDate("1990.01.01")
            .status(UserStatus.ACTIVE)
            .role(UserRole.USER)
            .build();

    userRepository.save(user);

    Optional<User> found = userRepository.findByUsername("u1");

    assertThat(found).isPresent();
    assertThat(found.get().getUsername()).isEqualTo("u1");
  }
}
