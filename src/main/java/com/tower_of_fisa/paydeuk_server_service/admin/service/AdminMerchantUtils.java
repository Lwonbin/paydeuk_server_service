package com.tower_of_fisa.paydeuk_server_service.admin.service;

import com.tower_of_fisa.paydeuk_server_service.admin.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class AdminMerchantUtils {

    private final PaymentRepository paymentRepository;
    /**
     * 평균 거래 금액 계산
     */
    public int calculateAverageAmount(Long totalAmount, int transactionCount) {
        return (transactionCount == 0) ? 0 : (int) (totalAmount / transactionCount);
    }

    /**
     * 최근 24시간 거래 건수 계산 (전체)
     */
    public int calculateRecent24hTransactionCount(int totalCount) {
        LocalDateTime from24hAgo = LocalDateTime.now().minusHours(24);
        int countUntil24hAgo = paymentRepository.countSuccessfulPaymentsBefore(from24hAgo);
        return totalCount - countUntil24hAgo;
    }

    /**
     * 최근 24시간 거래 건수 계산 (개별 가맹점)
     */
    public int calculateRecent24hTransactionCount(int totalCount, Long merchantId) {
        LocalDateTime from24hAgo = LocalDateTime.now().minusHours(24);
        int countUntil24hAgo = paymentRepository.countByMerchantIdAndPaymentSuccessTrueBefore(merchantId, from24hAgo);
        return totalCount - countUntil24hAgo;
    }

    /**
     * 전월 대비 거래 금액 증감율 계산 (개별 가맹점)
     */
    public double calculateMonthlyChangeRate(Long merchantId, Long thisMonthTotalAmount) {
        LocalDate firstDayOfThisMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate firstDayOfLastMonth = firstDayOfThisMonth.minusMonths(1);
        LocalDate lastDayOfLastMonth = firstDayOfThisMonth.minusDays(1);

        Long lastMonthTotal = paymentRepository.sumSuccessfulPaymentsForMerchantBetween(
                merchantId,
                firstDayOfLastMonth.atStartOfDay(),
                lastDayOfLastMonth.atTime(LocalTime.MAX)
        );

        return (lastMonthTotal != null && lastMonthTotal > 0)
                ? ((double) (thisMonthTotalAmount - lastMonthTotal) / lastMonthTotal) * 100
                : 0.0;
    }

    /**
     * 전월 대비 거래 금액 증감율 계산 (전체)
     */
    public double calculateMonthlyChangeRate(Long totalAmount) {
        LocalDate firstDayOfThisMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate firstDayOfLastMonth = firstDayOfThisMonth.minusMonths(1);
        LocalDate lastDayOfLastMonth = firstDayOfThisMonth.minusDays(1);

        Long lastMonthTotal = paymentRepository.sumSuccessfulPaymentsBetween(
                firstDayOfLastMonth.atStartOfDay(),
                lastDayOfLastMonth.atTime(LocalTime.MAX)
        );

        return (lastMonthTotal != null && lastMonthTotal > 0)
                ? ((double) (totalAmount - lastMonthTotal) / lastMonthTotal) * 100
                : 0.0;
    }
}
