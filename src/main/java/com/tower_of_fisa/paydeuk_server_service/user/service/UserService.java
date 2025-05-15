package com.tower_of_fisa.paydeuk_server_service.user.service;

import com.tower_of_fisa.paydeuk_server_service.common.ErrorDefineCode;
import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.BadRequestException400;
import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.NoSuchElementFoundException404;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.user.dto.PaymentPinCodeRequest;
import com.tower_of_fisa.paydeuk_server_service.user.dto.SetNewPaymentPinCodeRequest;
import com.tower_of_fisa.paydeuk_server_service.user.dto.UpdateAddressRequest;
import com.tower_of_fisa.paydeuk_server_service.user.dto.UpdateEmailRequest;
import com.tower_of_fisa.paydeuk_server_service.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PaymentPinCodeValidator paymentPinCodeValidator;

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

    if (paymentPinCodeValidator.isValid(paymentPinCode, user.getBirthDate()))
      user.changePaymentPinCode(paymentPinCode);
  }

  /**
   * [간편 결제 비밀번호 설정] 사용자의 간편 비밀 번호를 설정합니다.
   *
   * @param userId 인증된 사용자 ID
   * @param request 설정할 간편결제비밀번호를 담은 요청 DTO
   */
  public void setNewPaymentPinCode(Long userId, SetNewPaymentPinCodeRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));
    String newPaymentPinCode = request.getNewPaymentPinCode();
    String oldPaymentPinCode = user.getPaymentPinCode();

    if (paymentPinCodeValidator.isValid(newPaymentPinCode, oldPaymentPinCode, user.getBirthDate()))
      user.changePaymentPinCode(newPaymentPinCode);
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

    if (!insertedPaymentPinCode.equals(paymentPinCode))
      throw new BadRequestException400(ErrorDefineCode.USER_NOT_FOUND);
  }
}
