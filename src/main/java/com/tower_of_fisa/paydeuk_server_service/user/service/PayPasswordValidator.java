package com.tower_of_fisa.paydeuk_server_service.user.service;

import org.springframework.stereotype.Component;

@Component
public class PayPasswordValidator {

  public boolean isValid(String payPassword, String birthDate) {
    return checkAllSameDigits(payPassword)
        && checkBirthDate(payPassword, birthDate)
        && checkNotSequentialDigits(payPassword);
  }

  public boolean isValid(String newPaymentPinCode, String oldPaymentPinCode, String birthDate) {
    return checkAllSameDigits(newPaymentPinCode)
        && checkBirthDate(newPaymentPinCode, birthDate)
        && checkNotSequentialDigits(newPaymentPinCode)
        && !newPaymentPinCode.equals(oldPaymentPinCode);
  }

  private boolean checkAllSameDigits(String payPassword) {
    char firstChar = payPassword.charAt(0);
    for (int i = 1; i < payPassword.length(); i++) {
      if (payPassword.charAt(i) != firstChar) {
        return true;
      }
    }
    return false;
  }

  private boolean checkBirthDate(String payPassword, String birthDate) {
    return !payPassword.equals(birthDate);
  }

  private boolean checkNotSequentialDigits(String payPassword) {
    boolean ascending = true;
    boolean descending = true;

    for (int i = 0; i < payPassword.length() - 1; i++) {
      int curr = payPassword.charAt(i) - '0';
      int next = payPassword.charAt(i + 1) - '0';

      if (next != curr + 1) ascending = false;
      if (next != curr - 1) descending = false;
    }

    return !(ascending || descending);
  }
}
