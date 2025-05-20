package com.tower_of_fisa.paydeuk_server_service.admin.repository;

import com.tower_of_fisa.paydeuk_server_service.admin.dto.MerchantPaymentResponse;
import com.tower_of_fisa.paydeuk_server_service.admin.dto.SingleMerchantPaymentResponse;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.Payment;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
          value = """
        SELECT
            DATE(p.created_at) AS date,
            COALESCE(SUM(p.amount), 0) AS total_amount,
            COUNT(*) AS total_count
        FROM payment p
        WHERE p.payment_success = true
          AND p.created_at >= CURDATE() - INTERVAL 6 DAY
        GROUP BY DATE(p.created_at)
        ORDER BY DATE(p.created_at)
        """,
          nativeQuery = true)
  List<Object[]> findWeeklyTrendsForAllMerchants();


  int countByMerchantIdAndPaymentSuccessTrue(Long merchantId);

  @Query(
      "SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.merchant.id = :merchantId AND"
          + " p.paymentSuccess = true")
  Long sumAmountByMerchantIdAndPaymentSuccessTrue(@Param("merchantId") Long merchantId);

  @Query(
      """
      SELECT new com.tower_of_fisa.paydeuk_server_service.admin.dto.MerchantPaymentResponse(
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
  Page<MerchantPaymentResponse> findAllPaymentHistories(Pageable pageable);

  @Query(
      "SELECT p FROM Payment p "
          + "JOIN FETCH p.userCard uc "
          + "JOIN FETCH uc.card c "
          + "JOIN FETCH p.merchant m "
          + "JOIN FETCH p.cardBenefit cb "
          + "JOIN FETCH cb.benefit b "
          + "WHERE uc.user.id = :userId "
          + "ORDER BY p.createdAt DESC")
  Page<Payment> findPaymentHistoryByUserId(@Param("userId") Long userId, Pageable pageable);

  @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentSuccess = true AND p.createdAt < :before")
  int countSuccessfulPaymentsBefore(@Param("before") LocalDateTime before);

  @Query(
      "SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentSuccess = true AND"
          + " p.createdAt BETWEEN :start AND :end")
  Long sumSuccessfulPaymentsBetween(
      @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);



  @Query("SELECT COUNT(p) FROM Payment p WHERE p.merchant.id = :merchantId AND p.paymentSuccess = true AND p.createdAt < :before")
  int countByMerchantIdAndPaymentSuccessTrueBefore(
          @Param("merchantId") Long merchantId,
          @Param("before") LocalDateTime before
  );

  @Query(
          "SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
                  "WHERE p.merchant.id = :merchantId AND p.paymentSuccess = true " +
                  "AND p.createdAt BETWEEN :start AND :end"
  )
  Long sumSuccessfulPaymentsForMerchantBetween(
          @Param("merchantId") Long merchantId,
          @Param("start") LocalDateTime start,
          @Param("end") LocalDateTime end
  );

  @Query("""
        SELECT new com.tower_of_fisa.paydeuk_server_service.admin.dto.SingleMerchantPaymentResponse(
                   p.id, uc.cardNumber, c.name, p.createdAt, p.amount, p.paymentSuccess
               )
        FROM Payment p
        JOIN p.userCard uc
        JOIN uc.card c
        WHERE p.merchant.id = :merchantId
        ORDER BY p.createdAt DESC
        """)
  Page<SingleMerchantPaymentResponse> findSingleMerchantPaymentsByMerchantId(
          @Param("merchantId") Long merchantId,
          Pageable pageable
  );


}
