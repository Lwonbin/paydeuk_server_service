package com.tower_of_fisa.paydeuk_server_service.admin.service;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.UserStatus;
import com.tower_of_fisa.paydeuk_server_service.admin.dto.UserListResponse;
import com.tower_of_fisa.paydeuk_server_service.admin.dto.UserStatsResponse;
import com.tower_of_fisa.paydeuk_server_service.auth.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
  public List<UserListResponse> getAllUsers() {
    return userRepository.findAll().stream().map(this::convertToDto).toList();
  }

  /**
   * [사용자 통계 조회] 전체 사용자 수, 활성 사용자 수, 비활성 사용자 수를 조회한다.
   *
   * @return UserStatsResponse - 사용자 통계 정보
   */
  public UserStatsResponse getUserStats() {
    List<User> users = userRepository.findAll();
    int totalUsers = users.size();
    int activeUsers =
        (int) users.stream().filter(user -> user.getStatus() == UserStatus.ACTIVE).count();
    int inactiveUsers = totalUsers - activeUsers;

    return new UserStatsResponse(totalUsers, activeUsers, inactiveUsers);
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
