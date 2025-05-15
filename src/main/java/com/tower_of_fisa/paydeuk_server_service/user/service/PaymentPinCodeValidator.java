package com.tower_of_fisa.paydeuk_server_service.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentPinCodeValidator {
  private final BCryptPasswordEncoder passwordEncoder;

  public boolean isValid(String paymentPinCode, String birthDate) {
    return checkAllSameDigits(paymentPinCode)
        && checkBirthDate(paymentPinCode, birthDate)
        && checkNotSequentialDigits(paymentPinCode);
  }

  public boolean isValid(String newPaymentPinCode, String oldPaymentPinCode, String birthDate) {
    return checkAllSameDigits(newPaymentPinCode)
        && checkBirthDate(newPaymentPinCode, birthDate)
        && checkNotSequentialDigits(newPaymentPinCode)
        && !passwordEncoder.matches(newPaymentPinCode, oldPaymentPinCode);
  }

  private boolean checkAllSameDigits(String paymentPinCode) {
    char firstChar = paymentPinCode.charAt(0);
    for (int i = 1; i < paymentPinCode.length(); i++) {
      if (paymentPinCode.charAt(i) != firstChar) {
        return true;
      }
    }
    return false;
  }

  private boolean checkBirthDate(String paymentPinCode, String birthDate) {
    return !paymentPinCode.equals(birthDate);
  }

  private boolean checkNotSequentialDigits(String paymentPinCode) {
    boolean ascending = true;
    boolean descending = true;

    for (int i = 0; i < paymentPinCode.length() - 1; i++) {
      int curr = paymentPinCode.charAt(i) - '0';
      int next = paymentPinCode.charAt(i + 1) - '0';

      if (next != curr + 1) ascending = false;
      if (next != curr - 1) descending = false;
    }

    return !(ascending || descending);
  }
}
