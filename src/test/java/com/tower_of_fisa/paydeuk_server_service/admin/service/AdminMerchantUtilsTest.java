package com.tower_of_fisa.paydeuk_server_service.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import com.tower_of_fisa.paydeuk_server_service.admin.repository.PaymentRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AdminMerchantUtilsTest {

  @Mock private PaymentRepository paymentRepository;

  private AdminMerchantUtils utils;

  @BeforeEach
  void setUp() {
    utils = new AdminMerchantUtils(paymentRepository);
  }

  @ParameterizedTest
  @DisplayName("거래 건수에 따라 평균 금액을 계산한다")
  @CsvSource({
    "1000, 0, 0", // 거래 건수 0 → 평균 0
    "1000, 5, 200" // 거래 건수 5 → 평균 200
  })
  void calculateAverageAmount(long totalAmount, int count, int expected) {
    int result = utils.calculateAverageAmount(totalAmount, count);
    assertThat(result).isEqualTo(expected);
  }

  @Test
  @DisplayName("최근 24시간 거래 건수를 계산한다")
  void calculateRecent24hTransactionCount() {
    given(paymentRepository.countSuccessfulPaymentsBefore(any(LocalDateTime.class))).willReturn(8);
    int result = utils.calculateRecent24hTransactionCount(10);
    assertThat(result).isEqualTo(2);
  }

  @Test
  @DisplayName("개별 가맹점의 최근 24시간 거래 건수를 계산한다")
  void calculateRecent24hTransactionCount_individual() {
    given(
            paymentRepository.countByMerchantIdAndPaymentSuccessTrueBefore(
                eq(1L), any(LocalDateTime.class)))
        .willReturn(3);
    int result = utils.calculateRecent24hTransactionCount(5, 1L);
    assertThat(result).isEqualTo(2);
  }

  @Test
  @DisplayName("총 거래 금액의 전월 대비 증감율을 계산한다")
  void calculateMonthlyChangeRate_total() {
    // 정상 케이스: 전월 거래 금액 100L → 변화율 100%
    given(paymentRepository.sumSuccessfulPaymentsBetween(any(), any())).willReturn(100L);
    double result1 = utils.calculateMonthlyChangeRate(200L);
    assertThat(result1).isEqualTo(100.0);

    // 예외 케이스 1: 전월 거래 금액 null
    given(paymentRepository.sumSuccessfulPaymentsBetween(any(), any())).willReturn(null);
    double result2 = utils.calculateMonthlyChangeRate(200L);
    assertThat(result2).isZero();

    // 예외 케이스 2: 전월 거래 금액 0
    given(paymentRepository.sumSuccessfulPaymentsBetween(any(), any())).willReturn(0L);
    double result3 = utils.calculateMonthlyChangeRate(200L);
    assertThat(result3).isZero();
  }

  @Test
  @DisplayName("개별 가맹점의 전월 대비 거래 금액 증감율을 계산한다")
  void calculateMonthlyChangeRate_individual() {
    // 정상 케이스: 전월 거래 금액이 50L → 변화율 100%
    given(
            paymentRepository.sumSuccessfulPaymentsForMerchantBetween(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
        .willReturn(50L);
    double result1 = utils.calculateMonthlyChangeRate(1L, 100L);
    assertThat(result1).isEqualTo(100.0);

    // 예외 케이스 1: 전월 거래 금액 null
    given(paymentRepository.sumSuccessfulPaymentsForMerchantBetween(eq(1L), any(), any()))
        .willReturn(null);
    double result2 = utils.calculateMonthlyChangeRate(1L, 100L);
    assertThat(result2).isZero();

    // 예외 케이스 2: 전월 거래 금액 0
    given(paymentRepository.sumSuccessfulPaymentsForMerchantBetween(eq(1L), any(), any()))
        .willReturn(0L);
    double result3 = utils.calculateMonthlyChangeRate(1L, 100L);
    assertThat(result3).isZero();
  }

  @Test
  @DisplayName("오늘과 어제의 거래 건수 변화율을 계산한다")
  void calculateTransactionCountChangeRate() {
    given(
            paymentRepository.countByCreatedAtBetween(
                any(LocalDateTime.class), any(LocalDateTime.class)))
        .willReturn(10, 5);
    double result = utils.calculateTransactionCountChangeRate();
    assertThat(result).isEqualTo(100.0);
  }

  @Test
  @DisplayName("오늘과 어제의 거래 금액 변화율을 계산한다")
  void calculateTransactionAmountChangeRate() {
    given(
            paymentRepository.sumAmountByCreatedAtBetween(
                any(LocalDateTime.class), any(LocalDateTime.class)))
        .willReturn(200L, 100L);
    double result = utils.calculateTransactionAmountChangeRate();
    assertThat(result).isEqualTo(100.0);
  }

  @Test
  @DisplayName("성공 거래 비율을 계산한다")
  void calculateActiveTransactionRate() {
    given(paymentRepository.countByPaymentSuccessTrue()).willReturn(8);
    double result = utils.calculateActiveTransactionRate(10);
    assertThat(result).isEqualTo(80.0);
  }

  @Test
  @DisplayName("총 거래 건수가 없으면 활성 비율은 0을 반환한다")
  void calculateActiveTransactionRate_zero() {
    double result = utils.calculateActiveTransactionRate(0);
    assertThat(result).isZero();
  }

  @Test
  @DisplayName("null 값을 안전하게 변환한다")
  void getSafeAmount() {
    assertThat(utils.getSafeAmount(null)).isZero();
    assertThat(utils.getSafeAmount(5L)).isEqualTo(5L);
  }

  @Test
  @DisplayName("변화율 계산시 이전 값이 0이면 100 또는 0을 반환한다")
  void calculateChangeRate_zeroPrev() {
    double result1 = utils.calculateChangeRate(0, 10);
    double result2 = utils.calculateChangeRate(0, 0);
    assertThat(result1).isEqualTo(100.0);
    assertThat(result2).isEqualTo(0.0);
  }

  @Test
  @DisplayName("오늘과 어제의 평균 거래 금액 변화율을 계산한다")
  void calculateAverageAmountChangeRate() {
    given(
            paymentRepository.countByCreatedAtBetween(
                any(LocalDateTime.class), any(LocalDateTime.class)))
        .willReturn(4, 2);
    given(
            paymentRepository.sumAmountByCreatedAtBetween(
                any(LocalDateTime.class), any(LocalDateTime.class)))
        .willReturn(400L, 100L);
    double result = utils.calculateAverageAmountChangeRate();
    assertThat(result).isEqualTo(100.0);
  }
}
