package com.tower_of_fisa.paydeuk_server_service.admin.service;

import com.tower_of_fisa.paydeuk_server_service.admin.repository.PaymentRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminMerchantUtils {

  private final PaymentRepository paymentRepository;

  /** 평균 거래 금액 계산 */
  public int calculateAverageAmount(Long totalAmount, int transactionCount) {
    return (transactionCount == 0) ? 0 : (int) (totalAmount / transactionCount);
  }

  /** 최근 24시간 거래 건수 계산 (전체) */
  public int calculateRecent24hTransactionCount(int totalCount) {
    LocalDateTime from24hAgo = LocalDateTime.now().minusHours(24);
    int countUntil24hAgo = paymentRepository.countSuccessfulPaymentsBefore(from24hAgo);
    return totalCount - countUntil24hAgo;
  }

  /** 최근 24시간 거래 건수 계산 (개별 가맹점) */
  public int calculateRecent24hTransactionCount(int totalCount, Long merchantId) {
    LocalDateTime from24hAgo = LocalDateTime.now().minusHours(24);
    int countUntil24hAgo =
        paymentRepository.countByMerchantIdAndPaymentSuccessTrueBefore(merchantId, from24hAgo);
    return totalCount - countUntil24hAgo;
  }

  /** 전월 대비 거래 금액 증감율 계산 (개별 가맹점) */
  public double calculateMonthlyChangeRate(Long merchantId, Long thisMonthTotalAmount) {
    LocalDate firstDayOfThisMonth = LocalDate.now().withDayOfMonth(1);
    LocalDate firstDayOfLastMonth = firstDayOfThisMonth.minusMonths(1);
    LocalDate lastDayOfLastMonth = firstDayOfThisMonth.minusDays(1);

    Long lastMonthTotal =
        paymentRepository.sumSuccessfulPaymentsForMerchantBetween(
            merchantId,
            firstDayOfLastMonth.atStartOfDay(),
            lastDayOfLastMonth.atTime(LocalTime.MAX));

    return (lastMonthTotal != null && lastMonthTotal > 0)
        ? ((double) (thisMonthTotalAmount - lastMonthTotal) / lastMonthTotal) * 100
        : 0.0;
  }

  /** 전월 대비 거래 금액 증감율 계산 (전체) */
  public double calculateMonthlyChangeRate(Long totalAmount) {
    LocalDate firstDayOfThisMonth = LocalDate.now().withDayOfMonth(1);
    LocalDate firstDayOfLastMonth = firstDayOfThisMonth.minusMonths(1);
    LocalDate lastDayOfLastMonth = firstDayOfThisMonth.minusDays(1);

    Long lastMonthTotal =
        paymentRepository.sumSuccessfulPaymentsBetween(
            firstDayOfLastMonth.atStartOfDay(), lastDayOfLastMonth.atTime(LocalTime.MAX));

    return (lastMonthTotal != null && lastMonthTotal > 0)
        ? ((double) (totalAmount - lastMonthTotal) / lastMonthTotal) * 100
        : 0.0;
  }

  /** 변화율 계산. 이전 값 대비 현재 값의 변화율(%)을 계산한다. */
  public double calculateChangeRate(double prev, double current) {
    double result;
    if (prev == 0) {
      result = current == 0 ? 0.0 : 100.0;
    } else {
      result = ((current - prev) / prev) * 100.0;
    }
    return roundToOneDecimal(result);
  }

  /** 소수점 첫째 자리까지 반올림된 값을 반환한다. */
  public double roundToOneDecimal(double value) {
    return Math.round(value * 10.0) / 10.0;
  }

  /** 오늘과 어제의 거래 건수를 비교하여 변화율(%)을 계산한다. */
  public double calculateTransactionCountChangeRate() {
    LocalDateTime todayStart = LocalDate.now().atStartOfDay();
    LocalDateTime todayEnd = todayStart.plusDays(1);
    LocalDateTime yesterdayStart = todayStart.minusDays(1);

    int todayCount = paymentRepository.countByCreatedAtBetween(todayStart, todayEnd);
    int yesterdayCount = paymentRepository.countByCreatedAtBetween(yesterdayStart, todayStart);

    return calculateChangeRate(yesterdayCount, todayCount);
  }

  /** 오늘과 어제의 거래 금액을 비교하여 변화율(%)을 계산한다. */
  public double calculateTransactionAmountChangeRate() {
    LocalDateTime todayStart = LocalDate.now().atStartOfDay();
    LocalDateTime todayEnd = todayStart.plusDays(1);
    LocalDateTime yesterdayStart = todayStart.minusDays(1);

    long todayAmount =
        getSafeAmount(paymentRepository.sumAmountByCreatedAtBetween(todayStart, todayEnd));
    long yesterdayAmount =
        getSafeAmount(paymentRepository.sumAmountByCreatedAtBetween(yesterdayStart, todayStart));

    return calculateChangeRate(yesterdayAmount, todayAmount);
  }

  /** Long 값이 null일 경우 0L로 반환한다. */
  public long getSafeAmount(Long value) {
    return value != null ? value : 0L;
  }

  /** 오늘과 어제의 평균 거래 금액을 비교하여 변화율(%)을 계산한다. */
  public double calculateAverageAmountChangeRate() {
    LocalDateTime todayStart = LocalDate.now().atStartOfDay();
    LocalDateTime todayEnd = todayStart.plusDays(1);
    LocalDateTime yesterdayStart = todayStart.minusDays(1);

    int todayCount = paymentRepository.countByCreatedAtBetween(todayStart, todayEnd);
    int yesterdayCount = paymentRepository.countByCreatedAtBetween(yesterdayStart, todayStart);

    long todayAmount =
        getSafeAmount(paymentRepository.sumAmountByCreatedAtBetween(todayStart, todayEnd));
    long yesterdayAmount =
        getSafeAmount(paymentRepository.sumAmountByCreatedAtBetween(yesterdayStart, todayStart));

    int todayAvg = calculateAverageAmount(todayAmount, todayCount);
    int yesterdayAvg = calculateAverageAmount(yesterdayAmount, yesterdayCount);

    return calculateChangeRate(yesterdayAvg, todayAvg);
  }

  /** 총 거래 건수 대비 성공 거래 건수 비율(%)을 계산한다. */
  public double calculateActiveTransactionRate(int totalCount) {
    if (totalCount == 0) return 0.0;
    int successCount = paymentRepository.countByPaymentSuccessTrue();
    return roundToOneDecimal(((double) successCount / totalCount) * 100.0);
  }
}
