package com.tower_of_fisa.paydeuk_server_service.admin.repository;

import com.tower_of_fisa.paydeuk_server_service.admin.dto.merchant.MerchantPaymentHistoryResponse;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
  int countByPaymentSuccessTrue();

  @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentSuccess = true")
  Long sumAmountByPaymentSuccessTrue();

  @Query(
      value =
          """
          SELECT
              DATE(p.created_at) AS date,
              COALESCE(SUM(p.amount), 0) AS total_amount,
              COUNT(*) AS total_count
          FROM payment p
          WHERE p.merchant_id = :merchantId
            AND p.payment_success = true
            AND p.created_at >= CURDATE() - INTERVAL 6 DAY
          GROUP BY DATE(p.created_at)
          ORDER BY DATE(p.created_at)
          """,
      nativeQuery = true)
  List<Object[]> findWeeklyTrendsNative(@Param("merchantId") Long merchantId);

  int countByMerchantIdAndPaymentSuccessTrue(Long merchantId);

  @Query(
      "SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.merchant.id = :merchantId AND"
          + " p.paymentSuccess = true")
  Long sumAmountByMerchantIdAndPaymentSuccessTrue(@Param("merchantId") Long merchantId);

  @Query(
      """
      SELECT new com.tower_of_fisa.paydeuk_server_service.admin.dto.MerchantPaymentHistoryResponse(
          p.id,
          m.name,
          c.type,
          m.category,
          p.createdAt,
          p.amount,
          p.paymentSuccess
      )
      FROM Payment p
      JOIN p.merchant m
      JOIN p.userCard uc
      JOIN uc.card c
      """)
  List<MerchantPaymentHistoryResponse> findAllPaymentHistories();
}
