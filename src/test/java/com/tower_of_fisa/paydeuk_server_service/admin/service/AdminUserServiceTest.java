package com.tower_of_fisa.paydeuk_server_service.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.tower_of_fisa.paydeuk_server_service.admin.dto.UserListResponse;
import com.tower_of_fisa.paydeuk_server_service.admin.dto.UserStatsResponse;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.UserRole;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.UserStatus;
import com.tower_of_fisa.paydeuk_server_service.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AdminUserServiceTest {

  @Mock UserRepository userRepository;

  @InjectMocks AdminUserService adminUserService;

  @Test
  @DisplayName("유저 목록을 조회하여 DTO로 변환한다")
  void getAllUsers() {
    // given
    User user =
        User.builder()
            .id(1L)
            .name("john")
            .username("john")
            .password("pw")
            .personalAuthKey("k")
            .phone("010")
            .birthDate("0000")
            .status(UserStatus.ACTIVE)
            .role(UserRole.USER)
            .build();

    when(userRepository.findByRole(any(Pageable.class), eq(UserStatus.ACTIVE), eq("")))
        .thenReturn(new PageImpl<>(List.of(user)));

    // when
    Page<UserListResponse> result = adminUserService.getAllUsers(1, 5, "활성", "기본 정렬", "");

    // then
    assertThat(result.getContent()).hasSize(1);
    UserListResponse dto = result.getContent().get(0);
    assertThat(dto.getUserId()).isEqualTo(1L);
    assertThat(dto.getName()).isEqualTo("john");
    assertThat(dto.getStatus()).isEqualTo(UserStatus.ACTIVE.name());
  }

  @Test
  @DisplayName("UserRole이 USER인 유저만 통계에 포함된다")
  void getUserStats_excludesAdminFromStats() {
    // given
    User adminUser =
        User.builder()
            .id(1L)
            .name("관리자")
            .username("admin")
            .password("p")
            .status(UserStatus.ACTIVE)
            .role(UserRole.ADMIN)
            .build();

    User inactiveUser =
        User.builder()
            .id(2L)
            .name("사용자")
            .username("user")
            .password("p")
            .status(UserStatus.INACTIVE)
            .role(UserRole.USER)
            .build();

    ReflectionTestUtils.setField(adminUser, "createdAt", java.time.LocalDateTime.now());

    when(userRepository.findAll()).thenReturn(List.of(adminUser, inactiveUser));

    // when
    UserStatsResponse stats = adminUserService.getUserStats();

    // then
    assertThat(stats.getTotalUsers()).isEqualTo(1); // USER만 포함됨
    assertThat(stats.getActiveUsers()).isEqualTo(0); // ACTIVE지만 ADMIN이라 제외됨
    assertThat(stats.getInactiveUsers()).isEqualTo(1);
    assertThat(stats.getNewUsers()).isEqualTo(0); // createdAt 있는 건 ADMIN뿐이라 제외됨
  }

  @Test
  @DisplayName("status가 빈칸일 경우 전체 조회")
  void getAllUsers_statusIsNull_returnsAll() {

    // given
    given(userRepository.findByRole(any(Pageable.class), eq(null), eq("")))
        .willReturn(new PageImpl<>(List.of()));

    // when
    Page<UserListResponse> result = adminUserService.getAllUsers(1, 5, "", "이름순", "");

    // then
    assertThat(result).isEmpty(); // 내부 실행 여부만 확인
  }

  @Test
  @DisplayName("정렬 조건이 예상 외일 경우 기본 정렬(id ASC) 적용")
  void getAllUsers_invalidSort_appliesDefaultSort() {

    // given
    given(userRepository.findByRole(any(Pageable.class), eq(UserStatus.ACTIVE), eq("")))
        .willReturn(new PageImpl<>(List.of()));

    // when
    Page<UserListResponse> result = adminUserService.getAllUsers(1, 5, "활성", "잘못된값", "");

    // then
    assertThat(result).isEmpty(); // 내부 실행 여부만 확인
  }

  @Test
  @DisplayName("비활성 상태 유저 목록 조회")
  void getAllUsers_inactiveStatus() {

    // given
    User user =
        User.builder()
            .id(2L)
            .name("inactiveUser")
            .status(UserStatus.INACTIVE)
            .role(UserRole.USER)
            .build();

    given(userRepository.findByRole(any(Pageable.class), eq(UserStatus.INACTIVE), eq("")))
        .willReturn(new PageImpl<>(List.of(user)));

    // when
    Page<UserListResponse> result = adminUserService.getAllUsers(1, 5, "비활성", "이름순", "");

    // then
    assertThat(result).hasSize(1);
    assertThat(result.getContent().get(0).getStatus()).isEqualTo(UserStatus.INACTIVE.name());
  }

  @Test
  @DisplayName("임시 상태 유저 목록 조회")
  void getAllUsers_temporaryStatus() {

    // given
    User user =
        User.builder()
            .id(3L)
            .name("tempUser")
            .status(UserStatus.TEMPORARY)
            .role(UserRole.USER)
            .build();

    given(userRepository.findByRole(any(Pageable.class), eq(UserStatus.TEMPORARY), eq("")))
        .willReturn(new PageImpl<>(List.of(user)));

    // when
    Page<UserListResponse> result = adminUserService.getAllUsers(1, 5, "임시", "이름순", "");

    // then
    assertThat(result).hasSize(1);
    assertThat(result.getContent().get(0).getStatus()).isEqualTo(UserStatus.TEMPORARY.name());
  }

  @Test
  @DisplayName("status가 '비활성'일 경우 필터링된 유저 반환")
  void getAllUsers_statusIsInactive() {

    // given
    User user =
        User.builder()
            .id(2L)
            .name("jane")
            .username("jane")
            .password("pw")
            .status(UserStatus.INACTIVE)
            .role(UserRole.USER)
            .build();

    given(userRepository.findByRole(any(Pageable.class), eq(UserStatus.INACTIVE), eq("")))
        .willReturn(new PageImpl<>(List.of(user)));

    // when
    Page<UserListResponse> result = adminUserService.getAllUsers(1, 5, "비활성", "기본 정렬", "");

    // then
    assertThat(result).hasSize(1);
    assertThat(result.getContent().get(0).getStatus()).isEqualTo(UserStatus.INACTIVE.name());
  }

  @Test
  @DisplayName("status가 '임시'일 경우 필터링된 유저 반환")
  void getAllUsers_statusIsTemporary() {

    // given
    User user =
        User.builder()
            .id(3L)
            .name("temp")
            .username("temp")
            .password("pw")
            .status(UserStatus.TEMPORARY)
            .role(UserRole.USER)
            .build();

    given(userRepository.findByRole(any(Pageable.class), eq(UserStatus.TEMPORARY), eq("")))
        .willReturn(new PageImpl<>(List.of(user)));

    // when
    Page<UserListResponse> result = adminUserService.getAllUsers(1, 5, "임시", "기본 정렬", "");

    // then
    assertThat(result).hasSize(1);
    assertThat(result.getContent().get(0).getStatus()).isEqualTo(UserStatus.TEMPORARY.name());
  }

  @Test
  @DisplayName("sort가 '가입일순'일 경우 createdAt DESC로 정렬")
  void getAllUsers_sortByCreatedAt() {

    // given
    given(userRepository.findByRole(any(Pageable.class), eq(null), eq("")))
        .willReturn(new PageImpl<>(List.of()));

    // when
    Page<UserListResponse> result = adminUserService.getAllUsers(1, 5, "", "가입일순", "");

    // then
    assertThat(result).isEmpty(); // 내부 실행 여부만 확인
  }

  @Test
  @DisplayName("sort가 '상태순'일 경우 status ASC로 정렬")
  void getAllUsers_sortByStatus() {

    // given
    given(userRepository.findByRole(any(Pageable.class), eq(null), eq("")))
        .willReturn(new PageImpl<>(List.of()));

    // when
    Page<UserListResponse> result = adminUserService.getAllUsers(1, 5, "", "상태순", "");

    // then
    assertThat(result).isEmpty(); // 내부 실행 여부만 확인
  }

  @Test
  @DisplayName("생성일이 이번 달인 사용자 수를 계산한다")
  void getUserStats_countsNewUsersThisMonth() {
    // given
    User newUser =
        User.builder()
            .id(1L)
            .name("신규유저")
            .username("new")
            .password("pass")
            .status(UserStatus.ACTIVE)
            .role(UserRole.USER)
            .build();

    // createdAt을 이번 달로 설정
    ReflectionTestUtils.setField(newUser, "createdAt", java.time.LocalDateTime.now());

    given(userRepository.findAll()).willReturn(List.of(newUser));

    // when
    UserStatsResponse stats = adminUserService.getUserStats();

    // then
    assertThat(stats.getTotalUsers()).isEqualTo(1);
    assertThat(stats.getActiveUsers()).isEqualTo(1);
    assertThat(stats.getInactiveUsers()).isEqualTo(0);
    assertThat(stats.getNewUsers()).isEqualTo(1); // 이번 달 가입자
  }

  @Test
  @DisplayName("이번 해지만 다른 달에 가입한 유저는 newUsers로 포함되지 않는다")
  void getUserStats_userCreatedThisYearButNotThisMonth_isNotNewUser() {
    // given
    User user =
        User.builder()
            .id(3L)
            .name("oldThisYear")
            .username("old")
            .password("p")
            .status(UserStatus.ACTIVE)
            .role(UserRole.USER)
            .build();

    // createdAt: 올해지만 한 달 전
    java.time.LocalDateTime lastMonth = java.time.LocalDateTime.now().minusMonths(1);
    ReflectionTestUtils.setField(user, "createdAt", lastMonth);

    given(userRepository.findAll()).willReturn(List.of(user));

    // when
    UserStatsResponse stats = adminUserService.getUserStats();

    // then
    assertThat(stats.getNewUsers()).isEqualTo(0); // 이번 달이 아니므로 제외
  }

  @Test
  @DisplayName("작년에 가입한 유저는 newUsers로 포함되지 않는다")
  void getUserStats_userCreatedLastYearisNotNewUser() {
    // given
    User user =
        User.builder()
            .id(3L)
            .name("oldThisYear")
            .username("old")
            .password("p")
            .status(UserStatus.ACTIVE)
            .role(UserRole.USER)
            .build();

    // createdAt: 올해지만 한 달 전
    java.time.LocalDateTime lastYear = java.time.LocalDateTime.now().minusYears(1);
    ReflectionTestUtils.setField(user, "createdAt", lastYear);

    given(userRepository.findAll()).willReturn(List.of(user));

    // when
    UserStatsResponse stats = adminUserService.getUserStats();

    // then
    assertThat(stats.getNewUsers()).isEqualTo(0); // 이번 달이 아니므로 제외
  }
}
