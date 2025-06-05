package com.tower_of_fisa.paydeuk_server_service.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.global.config.exception.custom.exception.BadRequestException400;
import com.tower_of_fisa.paydeuk_server_service.global.config.exception.custom.exception.NoSuchElementFoundException404;
import com.tower_of_fisa.paydeuk_server_service.global.config.s3.S3Service;
import com.tower_of_fisa.paydeuk_server_service.user.dto.*;
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
  @DisplayName("기존 이미지가 null이면 삭제하지 않는다")
  void updateProfileImage_shouldNotDelete_whenImageUrlIsNull() throws Exception {
    Long userId = 1L;
    MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[10]);
    User mockUser = User.builder().id(userId).imageUrl(null).build();

    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
    given(s3Service.uploadProfileImage(image, userId))
            .willReturn("https://bucket.s3.amazonaws.com/new.jpg");

    UserProfileImageResponse response = userService.updateProfileImage(userId, image);

    assertEquals("https://bucket.s3.amazonaws.com/new.jpg", response.getImageUrl());
    verify(s3Service, never()).deleteImage(any());
  }

  @Test
  @DisplayName("기존 이미지가 S3 URL이 아니면 삭제하지 않는다")
  void updateProfileImage_shouldNotDelete_whenImageUrlIsNotAmazonS3() throws Exception {
    Long userId = 1L;
    MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[10]);
    User mockUser = User.builder().id(userId).imageUrl("https://cdn.other.com/avatar.jpg").build();

    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
    given(s3Service.uploadProfileImage(image, userId))
            .willReturn("https://bucket.s3.amazonaws.com/new.jpg");

    UserProfileImageResponse response = userService.updateProfileImage(userId, image);

    assertEquals("https://bucket.s3.amazonaws.com/new.jpg", response.getImageUrl());
    verify(s3Service, never()).deleteImage(any());
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
  @DisplayName("존재하지 않는 유저 ID로 프로필 이미지 변경 시 예외 발생")
  void updateProfileImage_shouldThrowException_whenUserNotFound() {
    // given
    Long userId = 999L;
    MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[10]);

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThrows(
            NoSuchElementFoundException404.class,
            () -> userService.updateProfileImage(userId, image)
    );
  }

  @Test
  @DisplayName("새 결제 비밀번호가 유효하지 않으면 예외 발생")
  void setNewPaymentPinCode_shouldThrowException_whenInvalid() {
    Long userId = 1L;
    String oldPin = "123456";
    String newPin = "111111";
    String birth = "000512";

    SetNewPaymentPinCodeRequest request =
            SetNewPaymentPinCodeRequest.builder().newPaymentPinCode(newPin).build();

    User user = User.builder().id(userId).birthDate(birth).paymentPinCode(oldPin).build();

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(paymentPinCodeValidator.isValid(newPin, oldPin, birth)).willReturn(false);

    assertThrows(
            BadRequestException400.class,
            () -> userService.setNewPaymentPinCode(userId, request)
    );
  }


  @Test
  @DisplayName("입력된 결제 비밀번호가 기존과 다르면 예외 발생")
  void verifyPaymentPinCode_shouldThrow_whenPinDoesNotMatch() {
    Long userId = 1L;
    String rawPin = "000000";
    String encodedPin = "$2a$10$encoded";

    PaymentPinCodeRequest request =
            PaymentPinCodeRequest.builder().paymentPinCode(rawPin).build();
    User user = User.builder().id(userId).paymentPinCode(encodedPin).build();

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(passwordEncoder.matches(rawPin, encodedPin)).willReturn(false);

    assertThrows(
            BadRequestException400.class,
            () -> userService.verifyPaymentPinCode(userId, request)
    );
  }


  @Test
  @DisplayName("Redis에 유저 혜택 정보가 없을 경우 0으로 처리된다")
  void getUserBenefits_shouldReturnZero_whenRedisMiss() {
    Long userId = 1L;
    User user = User.builder().id(userId).name("홍길동").build();
    String currentMonth = String.format("%02d", LocalDate.now().getMonthValue());
    String lastMonth = String.format("%02d", LocalDate.now().minusMonths(1).getMonthValue());

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(redisTemplate.opsForValue()).willReturn(valueOperations);
    given(valueOperations.get("user_benefit:1:" + currentMonth)).willReturn(null);
    given(valueOperations.get("user_benefit:1:" + lastMonth)).willReturn(null);

    UserBenefitResponse response = userService.getUserBenefits(userId);

    assertEquals(0, response.getLastMonthSum());
    assertEquals(0, response.getCurrentMonthSum());
  }

  @Test
  @DisplayName("유저 주소를 변경한다")
  void updateAddress_shouldChangeUserAddress() {
    // given
    Long userId = 1L;
    String newAddress = "서울시 강남구";
    UpdateAddressRequest request = new UpdateAddressRequest(newAddress);
    User user = User.builder().id(userId).build();

    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    // when
    userService.updateAddress(userId, request);

    // then
    assertEquals(newAddress, user.getAddress());
  }

  @Test
  @DisplayName("주소 요청이 null이면 변경하지 않는다")
  void updateAddress_shouldDoNothing_whenAddressIsNull() {
    Long userId = 1L;
    UpdateAddressRequest request = new UpdateAddressRequest(null);
    User user = User.builder().id(userId).address("기존주소").build();

    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    userService.updateAddress(userId, request);

    assertEquals("기존주소", user.getAddress());
  }

  @Test
  @DisplayName("유저 이메일을 변경한다")
  void updateEmail_shouldChangeUserEmail() {
    Long userId = 1L;
    String newEmail = "user@example.com";
    UpdateEmailRequest request = new UpdateEmailRequest(newEmail);
    User user = User.builder().id(userId).build();

    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    userService.updateEmail(userId, request);

    assertEquals(newEmail, user.getEmail());
  }

  @Test
  @DisplayName("이메일 요청이 null이면 변경하지 않는다")
  void updateEmail_shouldDoNothing_whenEmailIsNull() {
    Long userId = 1L;
    UpdateEmailRequest request = new UpdateEmailRequest(null);
    User user = User.builder().id(userId).email("old@example.com").build();

    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    userService.updateEmail(userId, request);

    assertEquals("old@example.com", user.getEmail());
  }


  @Test
  @DisplayName("존재하지 않는 유저 ID로 주소 변경 시 예외 발생")
  void updateAddress_shouldThrow_whenUserNotFound() {
    Long userId = 999L;
    UpdateAddressRequest request = new UpdateAddressRequest("주소");

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThrows(
            NoSuchElementFoundException404.class,
            () -> userService.updateAddress(userId, request)
    );
  }

  @Test
  @DisplayName("존재하지 않는 유저 ID로 이메일 변경 시 예외 발생")
  void updateEmail_shouldThrow_whenUserNotFound() {
    Long userId = 999L;
    UpdateEmailRequest request = new UpdateEmailRequest("user@example.com");

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThrows(
            NoSuchElementFoundException404.class,
            () -> userService.updateEmail(userId, request)
    );
  }

  @Test
  @DisplayName("유저가 존재하면 checkUserExists는 예외 없이 통과한다")
  void checkUserExists_shouldNotThrow_whenUserExists() {
    Long userId = 1L;
    given(userRepository.findById(userId)).willReturn(Optional.of(User.builder().id(userId).build()));

    assertDoesNotThrow(() -> userService.checkUserExists(userId));
  }

  @Test
  @DisplayName("유저가 존재하지 않으면 checkUserExists는 예외를 던진다")
  void checkUserExists_shouldThrow_whenUserNotFound() {
    Long userId = 999L;
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThrows(
            NoSuchElementFoundException404.class,
            () -> userService.checkUserExists(userId)
    );
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

  @Test
  @DisplayName("유저 정보를 정상적으로 조회한다")
  void getUserInfo_shouldReturnUserInfo() {
    // given
    Long userId = 1L;
    User user = User.builder()
            .id(userId)
            .name("홍길동")
            .birthDate("000512")
            .phone("01012345678")
            .email("hong@example.com")
            .address("서울시 강남구")
            .imageUrl("https://example.com/image.jpg")
            .build();

    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    // when
    UserInfoResponse response = userService.getUserInfo(userId);

    // then
    assertEquals("홍길동", response.getName());
    assertEquals("000512", response.getBirth());
    assertEquals("01012345678", response.getPhoneNumber());
    assertEquals("hong@example.com", response.getEmail());
    assertEquals("서울시 강남구", response.getAddress());
    assertEquals("https://example.com/image.jpg", response.getImageUrl());
  }

  @Test
  @DisplayName("이미 결제 비밀번호가 설정된 유저가 다시 설정 시 예외 발생")
  void setPaymentPinCode_shouldThrow_whenPinAlreadyExists() {
    // given
    Long userId = 1L;
    String existingPin = "$2a$10$hashed";
    PaymentPinCodeRequest request =
            PaymentPinCodeRequest.builder().paymentPinCode("609285").build();

    User user = User.builder()
            .id(userId)
            .birthDate("000512")
            .paymentPinCode(existingPin) // 이미 설정됨
            .build();

    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    // when & then
    assertThrows(
            BadRequestException400.class,
            () -> userService.setPaymentPinCode(userId, request)
    );
  }



  @Test
  @DisplayName("존재하지 않는 유저 ID로 정보 조회 시 예외 발생")
  void getUserInfo_shouldThrow_whenUserNotFound() {
    Long userId = 999L;
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThrows(NoSuchElementFoundException404.class, () -> userService.getUserInfo(userId));
  }

}
