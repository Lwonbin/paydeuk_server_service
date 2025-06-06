package com.tower_of_fisa.paydeuk_server_service.user.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class PaymentPinCodeValidatorTest {
  private PaymentPinCodeValidator validator;
  private BCryptPasswordEncoder passwordEncoder;
  private final String birthDate = "2000.05.12";
  private final String dummyEncoded =
      "$2a$10$7Q9XtfuDW5VLkX7aUeavZ.T3LScVnrF7Dc1f9VqUlzR9U2QTuXeK2"; // dummy

  @BeforeEach
  void setUp() {
    passwordEncoder = new BCryptPasswordEncoder();
    validator = new PaymentPinCodeValidator(passwordEncoder);
  }

  @Test
  @DisplayName("2인자 isValid: 정상 결제 비밀번호는 통과한다")
  void twoArg_validPin() {
    assertTrue(validator.isValid("609285", birthDate));
  }

  @Test
  @DisplayName("2인자 isValid: 생일과 같거나, 연속 숫자, 동일 숫자면 실패한다")
  void twoArg_invalidCases() {
    assertFalse(validator.isValid("000512", birthDate)); // 생일
    assertFalse(validator.isValid("123456", birthDate)); // 오름차순
    assertFalse(validator.isValid("111111", birthDate)); // 동일 숫자
  }

  @Test
  @DisplayName("올바른 결제 비밀번호는 통과한다")
  void validPin() {
    assertTrue(validator.isValid("609285", dummyEncoded, birthDate));
  }

  @Test
  @DisplayName("모든 숫자가 동일하면 실패한다")
  void sameDigitsFail() {
    assertFalse(validator.isValid("111111", dummyEncoded, birthDate));
  }

  @Test
  @DisplayName("생년월일과 동일하면 실패한다")
  void matchesBirthDateFail() {
    assertFalse(validator.isValid("000512", dummyEncoded, birthDate));
  }

  @Test
  @DisplayName("오름차순 연속 숫자는 실패한다")
  void sequentialAscendingFail() {
    assertFalse(validator.isValid("123456", dummyEncoded, birthDate));
  }

  @Test
  @DisplayName("내림차순 연속 숫자는 실패한다")
  void sequentialDescendingFail() {
    assertFalse(validator.isValid("654321", dummyEncoded, birthDate));
  }

  @Test
  @DisplayName("새 비밀번호가 기존 비밀번호와 같다면 실패한다")
  void newPinSameAsOldFail() {
    String oldEncoded = passwordEncoder.encode("609285");
    assertFalse(validator.isValid("609285", oldEncoded, birthDate));
  }

  @Test
  @DisplayName("새 비밀번호가 모든 조건을 만족하면 통과한다")
  void newPinValid() {
    String oldEncoded = passwordEncoder.encode("123789");
    assertTrue(validator.isValid("609285", oldEncoded, birthDate));
  }

  @Test
  @DisplayName("notAllSame만 false면 실패")
  void allSameFailOnly() {
    String oldEncoded = passwordEncoder.encode("123456");
    assertFalse(validator.isValid("111111", oldEncoded, birthDate));
  }

  @Test
  @DisplayName("notBirthDate만 false면 실패")
  void birthDateFailOnly() {
    String oldEncoded = passwordEncoder.encode("123456");
    assertFalse(validator.isValid("000512", oldEncoded, birthDate));
  }

  @Test
  @DisplayName("notSequential만 false면 실패")
  void sequentialFailOnly() {
    String oldEncoded = passwordEncoder.encode("123456");
    assertFalse(validator.isValid("123456", oldEncoded, birthDate));
  }

  @Test
  @DisplayName("notSameAsOld만 false면 실패")
  void sameAsOldFailOnly() {
    String oldEncoded = passwordEncoder.encode("609285");
    assertFalse(validator.isValid("609285", oldEncoded, birthDate));
  }
}
