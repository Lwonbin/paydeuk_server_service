package com.tower_of_fisa.paydeuk_server_service.admin.service;

import com.tower_of_fisa.paydeuk_server_service.admin.dto.*;
import com.tower_of_fisa.paydeuk_server_service.admin.repository.MerchantRepository;
import com.tower_of_fisa.paydeuk_server_service.admin.repository.PaymentRepository;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.Merchant;
import com.tower_of_fisa.paydeuk_server_service.global.common.ErrorDefineCode;
import com.tower_of_fisa.paydeuk_server_service.global.config.exception.custom.exception.NoSuchElementFoundException404;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMerchantService {

  private final MerchantRepository merchantRepository;
  private final PaymentRepository paymentRepository;
  private final AdminMerchantUtils adminMerchantUtils;

  /**
   * [전체 가맹점 통계 조회]
   * 전체 가맹점 수, 결제 성공 건수, 전체 거래 금액, 평균 거래 금액,
   * 최근 24시간 거래 증가 수, 전월 대비 거래 금액 증감율을 반환한다.
   */
  public MerchantStatsResponse getTotalMerchantStatistics() {
    int merchantCount = (int) merchantRepository.count(); // 전체 가맹점 수
    int activeMerchantCount = (int) merchantRepository.countActiveMerchants(); // 활성 가맹점 수
    int transactionCount = paymentRepository.countByPaymentSuccessTrue(); // 전체 거래 건수
    Long totalTransactionAmount = paymentRepository.sumAmountByPaymentSuccessTrue(); // 총 거래 금액

    int averageTransactionAmount = adminMerchantUtils.calculateAverageAmount(totalTransactionAmount, transactionCount); // 평균 거래 금액
    int recent24hCount = adminMerchantUtils.calculateRecent24hTransactionCount(transactionCount); // 최근 24시간 거래 수
    double percentChange = adminMerchantUtils.calculateMonthlyChangeRate(totalTransactionAmount); // 전월 대비 거래 금액 증감율

    return new MerchantStatsResponse(
            merchantCount,
            transactionCount,
            totalTransactionAmount,
            averageTransactionAmount,
            activeMerchantCount,
            recent24hCount,
            percentChange
    );
  }

  /**
   * [개별 가맹점 통계 조회]
   * 특정 가맹점의 거래 건수, 총 거래 금액, 평균 거래 금액,
   * 최근 24시간 거래 수, 전월 대비 거래 금액 증감율, 수수료율을 반환한다.
   */
  public MerchantIndividualStatsResponse getMerchantStatsById(Long merchantId) {
    Merchant merchant = merchantRepository
            .findById(merchantId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.MERCHANT_NOT_FOUND));

    int transactionCount = paymentRepository.countByMerchantIdAndPaymentSuccessTrue(merchantId);
    Long totalAmount = paymentRepository.sumAmountByMerchantIdAndPaymentSuccessTrue(merchantId);

    int avgAmount = adminMerchantUtils.calculateAverageAmount(totalAmount, transactionCount);
    int recent24hCount = adminMerchantUtils.calculateRecent24hTransactionCount(transactionCount, merchantId);
    double percentChange = adminMerchantUtils.calculateMonthlyChangeRate(merchantId, totalAmount);

    return new MerchantIndividualStatsResponse(
            transactionCount, totalAmount, avgAmount, merchant.getCommissionRate(), recent24hCount, percentChange
    );
  }

  /**
   * [가맹점 주간 거래 추이 조회]
   * 특정 가맹점의 최근 7일간 일별 거래 금액 및 건수를 반환한다.
   * 거래가 없는 날짜는 0으로 채워진다.
   */
  public List<MerchantTransactionTrendResponse> getTotalMerchantTransactionTrends() {
    List<Object[]> rawData = paymentRepository.findWeeklyTrendsForAllMerchants();

    Map<LocalDate, MerchantTransactionTrendResponse> trendMap = rawData.stream()
            .collect(Collectors.toMap(
                    row -> ((Date) row[0]).toLocalDate(),
                    row -> new MerchantTransactionTrendResponse(
                            ((Date) row[0]).toLocalDate(),
                            ((Number) row[1]).longValue(),
                            ((Number) row[2]).longValue()
                    )
            ));

    List<MerchantTransactionTrendResponse> completeTrends = new ArrayList<>();
    LocalDate today = LocalDate.now();

    for (int i = 6; i >= 0; i--) {
      LocalDate targetDate = today.minusDays(i);
      completeTrends.add(trendMap.getOrDefault(targetDate, new MerchantTransactionTrendResponse(targetDate, 0L, 0L)));
    }

    return completeTrends;
  }


  /**
   * [전체 가맹점 결제 내역 조회]
   * 모든 결제 내역을 조회하여 가맹점명, 카드 유형 등 상세 정보와 함께 반환한다.
   */
  public Page<MerchantPaymentHistoryResponse> getAllMerchantPaymentHistories(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    return paymentRepository.findAllPaymentHistories(pageable);
  }

  /**
   * [가맹점 목록 페이징 조회]
   * 전체 가맹점을 페이징 처리하여 거래 건수 및 총 금액과 함께 반환한다.
   */
  public Page<MerchantAllResponse> getAllMerchants(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    return merchantRepository
            .findAllMerchantsWithPayment(pageable)
            .map(result -> {
              Merchant merchant = (Merchant) result[0];
              Long transactionCount = (Long) result[1];
              Long totalAmount = (Long) result[2];
              return MerchantAllResponse.from(merchant, transactionCount, totalAmount);
            });
  }

  /**
   * [개별 가맹점 상세 정보 조회]
   * merchantId로 가맹점을 조회하고 정보를 반환한다.
   */
  public MerchantByIdResponse getMerchantById(Long merchantId) {
    Merchant merchant = merchantRepository
            .findById(merchantId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.MERCHANT_NOT_FOUND));
    return MerchantByIdResponse.from(merchant);
  }

  /**
   * [가맹점 상태 변경]
   * 활성/비활성 상태를 업데이트한다.
   */
  @Transactional
  public void updateMerchantStatus(Long merchantId, boolean isActive) {
    Merchant merchant = merchantRepository
            .findById(merchantId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.MERCHANT_NOT_FOUND));
    merchant.changeIsActive(isActive);
    merchantRepository.save(merchant);
  }

  /**
   * [가맹점 삭제 처리]
   * 해당 가맹점을 논리적으로 삭제 처리한다.
   */
  @Transactional
  public void deleteMerchant(Long merchantId) {
    Merchant merchant = merchantRepository
            .findById(merchantId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.MERCHANT_NOT_FOUND));
    merchant.changeIsDeleted(true);
    merchantRepository.save(merchant);
  }

}
