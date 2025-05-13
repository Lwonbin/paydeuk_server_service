package com.tower_of_fisa.paydeuk_server_service.admin.service;

import com.tower_of_fisa.paydeuk_server_service.admin.dto.*;
import com.tower_of_fisa.paydeuk_server_service.admin.repository.MerchantRepository;
import com.tower_of_fisa.paydeuk_server_service.admin.repository.PaymentRepository;
import com.tower_of_fisa.paydeuk_server_service.common.ErrorDefineCode;
import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.NoSuchElementFoundException404;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.Merchant;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMerchantService {

  private final MerchantRepository merchantRepository;
  private final PaymentRepository paymentRepository;

  /**
   * [전체 가맹점 통계 조회] 전체 가맹점 수, 결제 성공 건수, 전체 결제 금액 및 평균 결제 금액을 조회합니다.
   *
   * @return MerchantStatsResponse - 전체 통계를 담은 DTO
   */
  public MerchantStatsResponse getTotalMerchantStatistics() {
    int merchantCount = (int) merchantRepository.count(); // 총 가맹점 수
    int transactionCount = paymentRepository.countByPaymentSuccessTrue(); // 결제 성공 건수
    Long totalTransactionAmount = paymentRepository.sumAmountByPaymentSuccessTrue(); // 총 거래 금액

    int averageTransactionAmount =
        (transactionCount == 0) ? 0 : (int) (totalTransactionAmount / transactionCount); // 평균 거래 금액

    return new MerchantStatsResponse(
        merchantCount, transactionCount, totalTransactionAmount, averageTransactionAmount);
  }

  /**
   * [가맹점 주간 거래 추이 조회] 특정 가맹점의 최근 7일 간 일별 거래 금액 및 건수를 반환합니다. 거래 기록이 없는 날짜는 0으로 채웁니다.
   *
   * @param merchantId Long - 가맹점 ID
   * @return List<MerchantTransactionTrendResponse> - 날짜별 거래 추이 리스트
   */
  public List<MerchantTransactionTrendResponse> getMerchantTransactionTrends(Long merchantId) {

    merchantRepository
        .findById(merchantId)
        .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.MERCHANT_NOT_FOUND));

    List<Object[]> rawData = paymentRepository.findWeeklyTrendsNative(merchantId);

    Map<LocalDate, MerchantTransactionTrendResponse> trendMap =
        rawData.stream()
            .collect(
                Collectors.toMap(
                    row -> ((Date) row[0]).toLocalDate(),
                    row ->
                        new MerchantTransactionTrendResponse(
                            ((Date) row[0]).toLocalDate(),
                            ((Number) row[1]).longValue(),
                            ((Number) row[2]).longValue())));

    List<MerchantTransactionTrendResponse> completeTrends = new ArrayList<>();
    LocalDate today = LocalDate.now();

    for (int i = 6; i >= 0; i--) {
      LocalDate targetDate = today.minusDays(i);
      MerchantTransactionTrendResponse trend =
          trendMap.getOrDefault(
              targetDate, new MerchantTransactionTrendResponse(targetDate, 0L, 0L));
      completeTrends.add(trend);
    }

    return completeTrends;
  }

  /**
   * [개별 가맹점 통계 조회] 특정 가맹점의 결제 건수, 총 거래 금액, 평균 거래 금액, 수수료율을 반환합니다.
   *
   * @param merchantId Long - 가맹점 ID
   * @return MerchantIndividualStatsResponse - 가맹점 단건 통계 DTO
   */
  public MerchantIndividualStatsResponse getMerchantStatsById(Long merchantId) {
    Merchant merchant =
        merchantRepository
            .findById(merchantId)
            .orElseThrow(
                () -> new NoSuchElementFoundException404(ErrorDefineCode.MERCHANT_NOT_FOUND));

    int transactionCount = paymentRepository.countByMerchantIdAndPaymentSuccessTrue(merchantId);
    Long totalAmount = paymentRepository.sumAmountByMerchantIdAndPaymentSuccessTrue(merchantId);

    int avgAmount = transactionCount == 0 ? 0 : (int) (totalAmount / transactionCount);

    return new MerchantIndividualStatsResponse(
        transactionCount, totalAmount, avgAmount, merchant.getCommissionRate());
  }

  /**
   * [전체 가맹점 결제 내역 조회] 모든 가맹점의 결제 기록을 조회하여 가맹점명, 카드 유형 등 상세 정보와 함께 반환합니다.
   *
   * @return List<MerchantPaymentHistoryResponse> - 결제 내역 리스트
   */
  public List<MerchantPaymentHistoryResponse> getAllMerchantPaymentHistories() {
    return paymentRepository.findAllPaymentHistories();
  }

  /**
   * [Merchant 탐색] Merchant DB에서 가맹점 정보를 조호힌다.
   *
   * @return List<MerchantAllResponse> - 가맹점 정보 리스트
   */
  public List<MerchantAllResponse> getAllMerchants() {
    return merchantRepository.findAllMerchantsWithPayment().stream()
        .map(
            result -> {
              Merchant merchant = (Merchant) result[0];
              Long transactionCount = (Long) result[1];
              Long totalAmount = (Long) result[2];

              return MerchantAllResponse.from(merchant, transactionCount, totalAmount);
            })
        .toList();
  }

  /**
   * [Merchant 탐색] merchantId를 통해 Merchant DB를 탐색 하여 탐색 결과를 반환한다.
   *
   * @param merchantId Long - 가맹점 ID
   * @return MerchantByIdResponse - 해당 가맹점 정보
   */
  public MerchantByIdResponse getMerchantById(Long merchantId) {
    Merchant merchant =
        merchantRepository
            .findById(merchantId)
            .orElseThrow(
                () -> new NoSuchElementFoundException404(ErrorDefineCode.MERCHANT_NOT_FOUND));
    return MerchantByIdResponse.from(merchant);
  }

  /**
   * [Merchant Update] merchantId를 통해 Merchant DB를 탐색 하여 탐색 결과의 활성화 상태 값을 변경한다.
   *
   * @param merchantId Long - 가맹점 ID
   * @param isActive boolean - update할려는 상태 값 void
   */
  @Transactional
  public void updateMerchantStatus(Long merchantId, boolean isActive) {
    Merchant merchant =
        merchantRepository
            .findById(merchantId)
            .orElseThrow(
                () -> new NoSuchElementFoundException404(ErrorDefineCode.MERCHANT_NOT_FOUND));
    merchant.changeIsActive(isActive);
    merchantRepository.save(merchant);
  }

  /**
   * [Merchant delete] merchantId를 통해 Merchant DB를 탐색 하여 탐색 결과의 삭제 상태 값을 변경한다.
   *
   * @param merchantId Long - 가맹점 ID void
   */
  @Transactional
  public void deleteMerchant(Long merchantId) {
    Merchant merchant =
        merchantRepository
            .findById(merchantId)
            .orElseThrow(
                () -> new NoSuchElementFoundException404(ErrorDefineCode.MERCHANT_NOT_FOUND));
    merchant.changeIsDeleted(true);
    merchantRepository.save(merchant);
  }
}
