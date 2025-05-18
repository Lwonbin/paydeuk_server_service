package com.tower_of_fisa.paydeuk_server_service.user.service;

import com.tower_of_fisa.paydeuk_server_service.common.ErrorDefineCode;
import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.NoSuchElementFoundException404;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.user.dto.UpdateAddressRequest;
import com.tower_of_fisa.paydeuk_server_service.user.dto.UpdateEmailRequest;
import com.tower_of_fisa.paydeuk_server_service.user.dto.UserBenefitResponse;
import com.tower_of_fisa.paydeuk_server_service.user.dto.UserInfoResponse;
import com.tower_of_fisa.paydeuk_server_service.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final RedisTemplate<String, String> redisTemplate;

  /**
   * [내 정보 변경] 사용자의 주소를 변경한다.
   *
   * @param userId 인증된 사용자 ID
   * @param request 변경할 주소 정보를 담은 요청 DTO
   */
  @Transactional
  public void updateAddress(Long userId, UpdateAddressRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));

    if (request.getAddress() != null) user.changeAddress(request.getAddress());
  }

  /**
   * [내 정보 변경] 사용자의 이메일을 변경한다.
   *
   * @param userId 인증된 사용자 ID
   * @param request 변경할 이메일 정보를 담은 요청 DTO
   */
  @Transactional
  public void updateEmail(Long userId, UpdateEmailRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));

    if (request.getEmail() != null) user.changeEmail(request.getEmail());
  }

  public void checkUserExists(Long userId) {
    userRepository
        .findById(userId)
        .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));
  }

  /**
   * [내 정보 변경] 사용자의 이메일을 변경한다.
   *
   * @param userId 인증된 사용자 ID
   */
  public UserInfoResponse getUserInfo(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));

    return new UserInfoResponse(
        user.getName(), user.getBirthDate(), user.getPhone(), user.getEmail(), user.getAddress());
  }

  /**
   * [사용자 수혜 혜택 조회] 사용자의 이번달 총 수혜 혜택을 조회한다.
   *
   * @param userId 인증된 사용자 ID
   */
  public UserBenefitResponse getUserBenefits(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));

    String name = user.getName();

    int currentMonthBenefit = getMonthlyBenefit(userId, getCurrentMonth());
    int lastMonthBenefit = getMonthlyBenefit(userId, getLastMonth());

    return new UserBenefitResponse(name, lastMonthBenefit, currentMonthBenefit);
  }

  /** 해당 사용자와 월에 해당하는 수혜 금액을 Redis에서 조회한다. */
  private int getMonthlyBenefit(Long userId, String month) {
    // key 이름 ex) user_benefit:2:05 -> userId가 2인 user 5월에 받은 총 혜택 금액
    String redisKey = String.format("user_benefit:%d:%s", userId, month);
    String value = redisTemplate.opsForValue().get(redisKey);
    return value != null ? Integer.parseInt(value) : 0;
  }

  /** 현재 월을 MM 형식으로 반환 (예: "05") */
  private String getCurrentMonth() {
    return String.format("%02d", LocalDate.now().getMonthValue());
  }

  /** 지난 달을 MM 형식으로 반환 (예: "04") */
  private String getLastMonth() {
    return String.format("%02d", LocalDate.now().minusMonths(1).getMonthValue());
  }
}
