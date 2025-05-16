package com.tower_of_fisa.paydeuk_server_service.admin.service;

import com.tower_of_fisa.paydeuk_server_service.admin.dto.UserListResponse;
import com.tower_of_fisa.paydeuk_server_service.admin.dto.UserStatsResponse;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.UserRole;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.UserStatus;
import com.tower_of_fisa.paydeuk_server_service.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {
  private final UserRepository userRepository;

  /**
   * [사용자 목록 조회] 전체 사용자 목록을 조회한다.
   *
   * @return List<UserListResponse> - 사용자 목록
   */
  public Page<UserListResponse> getAllUsers(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    return userRepository.findByRole(pageable) // 일반 사용자만 필터링
        .map(this::convertToDto);
  }

  /**
   * [사용자 통계 조회] 전체 사용자 수, 활성 사용자 수, 비활성 사용자 수를 조회한다.
   *
   * @return UserStatsResponse - 사용자 통계 정보
   */
  public UserStatsResponse getUserStats() {
    List<User> users =
        userRepository.findAll().stream()
            .filter(user -> user.getRole() == UserRole.USER) // 일반 사용자만 필터링
            .toList();
    long totalUsers = users.size();
    long activeUsers = users.stream().filter(user -> user.getStatus() == UserStatus.ACTIVE).count();
    long inactiveUsers =
        users.stream().filter(user -> user.getStatus() == UserStatus.INACTIVE).count();
    long newUsers =
        users.stream()
            .filter(
                user ->
                    user.getCreatedAt() != null
                        && user.getCreatedAt().getYear() == java.time.LocalDate.now().getYear()
                        && user.getCreatedAt().getMonthValue()
                            == java.time.LocalDate.now().getMonthValue())
            .count();

    return new UserStatsResponse(totalUsers, activeUsers, inactiveUsers, newUsers);
  }

  /**
   * [User Entity → UserListResponse DTO 변환] 사용자 엔티티를 외부 노출용 응답 DTO로 변환한다. 이는 엔티티의 내부 구조나 민감 정보를 그대로
   * 노출하지 않기 위함이며, 클라이언트에게 필요한 필드만 전달하고, API 응답 형식을 통제하기 위한 목적이다.
   *
   * @param user 변환 대상 사용자 엔티티
   * @return UserListResponse 응답 DTO
   */
  private UserListResponse convertToDto(User user) {
    return new UserListResponse(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getStatus().name(),
        user.getCreatedAt());
  }
}
