package com.tower_of_fisa.paydeuk_server_service.user.service;

import com.tower_of_fisa.paydeuk_server_service.common.ErrorDefineCode;
import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.NoSuchElementFoundException404;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.user.dto.SetNewPaymentPinCodeRequest;
import com.tower_of_fisa.paydeuk_server_service.user.dto.SetPayPasswordRequest;
import com.tower_of_fisa.paydeuk_server_service.user.dto.UpdateProfileRequest;
import com.tower_of_fisa.paydeuk_server_service.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PayPasswordValidator payPasswordValidator;

  /**
   * [내 정보 변경] 사용자의 이메일 또는 주소를 변경한다.
   *
   * @param userId 인증된 사용자 ID
   * @param request 변경할 이메일, 주소 정보를 담은 요청 DTO
   */
  @Transactional
  public void updateProfile(Long userId, UpdateProfileRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));

    if (request.getEmail() != null) user.changeEmail(request.getEmail());
    if (request.getAddress() != null) user.changeAddress(request.getAddress());
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
  public void setPayPassword(Long userId, SetPayPasswordRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));
    String payPassword = request.getPayPassword();

    if (payPasswordValidator.isValid(payPassword, user.getBirthDate()))
      user.changePayPassword(payPassword);
  }

  /**
   * [간편 비밀 번호 설정] 사용자의 간편 비밀 번호를 설정합니다.
   *
   * @param userId 인증된 사용자 ID
   * @param request 설정할 간편결제비밀번호를 담은 요청 DTO
   */
  public void setNewPaymentPinCode(Long userId, SetNewPaymentPinCodeRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));
    String newPayPassword = request.getNewPinCode();
    String oldPayPassword = user.getPayPassword();

    if (payPasswordValidator.isValid(newPayPassword, oldPayPassword, user.getBirthDate()))
      user.changePayPassword(newPayPassword);
  }
}
