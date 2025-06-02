package com.tower_of_fisa.paydeuk_server_service.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.global.config.exception.custom.exception.BadRequestException400;
import com.tower_of_fisa.paydeuk_server_service.global.config.s3.S3Service;
import com.tower_of_fisa.paydeuk_server_service.user.dto.PaymentPinCodeRequest;
import com.tower_of_fisa.paydeuk_server_service.user.dto.SetNewPaymentPinCodeRequest;
import com.tower_of_fisa.paydeuk_server_service.user.dto.UserBenefitResponse;
import com.tower_of_fisa.paydeuk_server_service.user.dto.UserProfileImageResponse;
import com.tower_of_fisa.paydeuk_server_service.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

@ActiveProfiles("test")
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @InjectMocks private UserService userService;

  @Mock private UserRepository userRepository;

  @Mock private S3Service s3Service;

  @Mock private RedisTemplate<String, String> redisTemplate;

  @Mock private ValueOperations<String, String> valueOperations;

  @Mock private BCryptPasswordEncoder passwordEncoder;

  @Mock private PaymentPinCodeValidator paymentPinCodeValidator;

  @Test
  @DisplayName("유저 프로필 이미지를 새 이미지로 교체하고 저장한다")
  void updateProfileImage_shouldReplaceImageAndReturnUrl() throws Exception {
    // given
    Long userId = 1L;
    MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[10]);
    User mockUser =
        User.builder().id(userId).imageUrl("https://bucket.s3.amazonaws.com/old.jpg").build();

    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
    given(s3Service.uploadProfileImage(image, userId))
        .willReturn("https://bucket.s3.amazonaws.com/new.jpg");

    // when
    UserProfileImageResponse response = userService.updateProfileImage(userId, image);

    // then
    assertEquals("https://bucket.s3.amazonaws.com/new.jpg", response.getImageUrl());
    verify(s3Service).deleteImage("old.jpg");
    verify(s3Service).uploadProfileImage(image, userId);
    verify(userRepository).save(mockUser);
  }

  @Test
  @DisplayName("결제 비밀번호가 없는 유저에게 새 비밀번호를 설정한다")
  void setPaymentPinCode() {
    // given
    Long userId = 1L;
    String rawPin = "609285";
    String birth = "000512";
    PaymentPinCodeRequest request = PaymentPinCodeRequest.builder().paymentPinCode(rawPin).build();
    User user = User.builder().id(userId).birthDate(birth).build();

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(passwordEncoder.encode(rawPin)).willReturn("encoded");
    given(paymentPinCodeValidator.isValid(rawPin, birth)).willReturn(true);

    // when
    userService.setPaymentPinCode(userId, request);

    // then
    assertEquals("encoded", user.getPaymentPinCode());
  }

  @Test
  @DisplayName("결제 비밀번호가 없는 유저에게 새 비밀번호를 설정하는 경우 형식에 맞지 않는 비밀번호로 설정하면 에러가 발생한다.")
  void setPaymentPinCodeError() {
    // given
    Long userId = 1L;
    String rawPinError = "123456";
    String birth = "000512";
    PaymentPinCodeRequest request =
        PaymentPinCodeRequest.builder().paymentPinCode(rawPinError).build();
    User user = User.builder().id(userId).birthDate(birth).build();

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(paymentPinCodeValidator.isValid(rawPinError, birth)).willReturn(false);

    // when & then
    assertThrows(
        BadRequestException400.class, () -> userService.setPaymentPinCode(userId, request));
  }

  @Test
  @DisplayName("기존 결제 비밀번호를 새 비밀번호로 변경한다")
  void setNewPaymentPinCode() {
    // given
    Long userId = 1L;
    String oldPin = "152934";
    String newPin = "105984";
    String birth = "000512";
    SetNewPaymentPinCodeRequest request =
        SetNewPaymentPinCodeRequest.builder().newPaymentPinCode(newPin).build();
    User user = User.builder().id(userId).birthDate(birth).paymentPinCode(oldPin).build();

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(passwordEncoder.encode(newPin)).willReturn("encodedNewPin");
    given(paymentPinCodeValidator.isValid(newPin, oldPin, birth)).willReturn(true);

    // when
    userService.setNewPaymentPinCode(userId, request);

    // then
    assertEquals("encodedNewPin", user.getPaymentPinCode());
  }

  @Test
  @DisplayName("입력된 결제 비밀번호가 기존 비밀번호와 일치하는지 검증한다")
  void verifyPaymentPinCode() {
    // given
    Long userId = 1L;
    String rawPin = "1234";
    String encodedPin = "$2a$10$abc";
    PaymentPinCodeRequest request = PaymentPinCodeRequest.builder().paymentPinCode(rawPin).build();
    User user = User.builder().id(userId).paymentPinCode(encodedPin).build();

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(passwordEncoder.matches(rawPin, encodedPin)).willReturn(true);

    // when & then
    assertDoesNotThrow(() -> userService.verifyPaymentPinCode(userId, request));
  }

  @Test
  @DisplayName("Redis와 DB를 조회하여 유저의 혜택 정보를 가져온다")
  void getUserBenefits() {
    // given
    Long userId = 1L;
    User user = User.builder().id(userId).name("홍길동").build();
    String currentMonth = String.format("%02d", LocalDate.now().getMonthValue());
    String lastMonth = String.format("%02d", LocalDate.now().minusMonths(1).getMonthValue());

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(redisTemplate.opsForValue()).willReturn(valueOperations);
    given(valueOperations.get("user_benefit:1:" + currentMonth)).willReturn("3000");
    given(valueOperations.get("user_benefit:1:" + lastMonth)).willReturn("5000");

    // when
    UserBenefitResponse response = userService.getUserBenefits(userId);

    // then
    assertEquals("홍길동", response.getName());
    assertEquals(5000, response.getLastMonthSum());
    assertEquals(3000, response.getCurrentMonthSum());
  }
}
