package com.tower_of_fisa.paydeuk_server_service.user.service;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.global.common.ErrorDefineCode;
import com.tower_of_fisa.paydeuk_server_service.global.config.exception.custom.exception.BadRequestException400;
import com.tower_of_fisa.paydeuk_server_service.global.config.exception.custom.exception.NoSuchElementFoundException404;
import com.tower_of_fisa.paydeuk_server_service.global.config.s3.S3Service;
import com.tower_of_fisa.paydeuk_server_service.user.dto.*;
import com.tower_of_fisa.paydeuk_server_service.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PaymentPinCodeValidator paymentPinCodeValidator;
  private final BCryptPasswordEncoder passwordEncoder;
  private final RedisTemplate<String, String> redisTemplate;
  private final S3Service s3Service;
  /**
   * [내 정보 변경] 사용자의 프로필 이미지를 변경한다.
   *
   * @param userId 인증된 사용자 ID
   * @param image 변경할 이미지 정보를 담은 MultipartFile
   *
   * @return 변경된 이미지 URL을 담은 응답 DTO (UserProfileImageResponse)
   */
  @Transactional
  public UserProfileImageResponse updateProfileImage(Long userId, MultipartFile image) throws IOException {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));

    // 기존 이미지 삭제
    if (user.getImageUrl() != null && user.getImageUrl().contains(".amazonaws.com")) {
      String key = extractKeyFromUrl(user.getImageUrl());
      s3Service.deleteImage(key);
    }
    // 어디서 용량크기 에러가 터지는지? 확인후 거기에 따른 에러 코드 설정 및 프론트에서 막기    yml파일 변경ㅎ 실험
    // 새 이미지 업로드
    String newImageUrl = s3Service.uploadProfileImage(image,userId);
    user.setImageUrl(newImageUrl);
    userRepository.save(user);
    return UserProfileImageResponse.builder()
            .imageUrl(newImageUrl)
            .build();
  }

  private String extractKeyFromUrl(String url) {
    return url.substring(url.lastIndexOf("/") + 1);
  }

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
   * [간편 비밀 번호 설정] 사용자의 간편 비밀 번호를 설정합니다.
   *
   * @param userId 인증된 사용자 ID
   * @param request 설정할 간편결제비밀번호를 담은 요청 DTO
   */
  @Transactional
  public void setPaymentPinCode(Long userId, PaymentPinCodeRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));
    String paymentPinCode = request.getPaymentPinCode();

    if (user.getPaymentPinCode() != null)
      throw new BadRequestException400(ErrorDefineCode.ALREADY_HAS_PIN_CODE);

    if (paymentPinCodeValidator.isValid(paymentPinCode, user.getBirthDate()))
      user.changePaymentPinCode(passwordEncoder.encode(paymentPinCode));
    else throw new BadRequestException400(ErrorDefineCode.INVALID_PAYMENT_PIN_CODE);
  }

  /**
   * [간편 결제 비밀번호 변경] 사용자의 간편 비밀 번호를 변경합니다.
   *
   * @param userId 인증된 사용자 ID
   * @param request 변경할 간편결제비밀번호를 담은 요청 DTO
   */
  @Transactional
  public void setNewPaymentPinCode(Long userId, SetNewPaymentPinCodeRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));
    String newPaymentPinCode = request.getNewPaymentPinCode();
    String oldPaymentPinCode = user.getPaymentPinCode();

    if (paymentPinCodeValidator.isValid(newPaymentPinCode, oldPaymentPinCode, user.getBirthDate()))
      user.changePaymentPinCode(passwordEncoder.encode(newPaymentPinCode));
    else throw new BadRequestException400(ErrorDefineCode.INVALID_PAYMENT_PIN_CODE);
  }

  /**
   * [간편 결제 비밀번호 검증] 사용자가 간편 결제 비밃번호를 변경하는 과정에서 입력하는 기존 간편 결제 비밀 번호를 검증합니다.
   *
   * @param userId 인증된 사용자 ID
   * @param request 검증할 기존 간편 결제 비밀번호를 담은 요청 DTO
   */
  public void verifyPaymentPinCode(Long userId, PaymentPinCodeRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));

    String insertedPaymentPinCode = request.getPaymentPinCode();
    String paymentPinCode = user.getPaymentPinCode();

    if (!passwordEncoder.matches(insertedPaymentPinCode, paymentPinCode))
      throw new BadRequestException400(ErrorDefineCode.WRONG_PAYMENT_PIN_CODE);
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
        user.getName(), user.getBirthDate(), user.getPhone(), user.getEmail(), user.getAddress(), user.getImageUrl());
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
