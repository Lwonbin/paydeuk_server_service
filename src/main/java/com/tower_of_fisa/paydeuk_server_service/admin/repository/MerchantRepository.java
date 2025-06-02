package com.tower_of_fisa.paydeuk_server_service.admin.repository;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

  @Query(
      "SELECT m, COUNT(p.id) AS transactionCount, COALESCE(SUM(p.amount), 0) AS totalAmount FROM"
          + " Merchant m LEFT JOIN Payment p ON m.id = p.merchant.id WHERE (:status IS NULL OR"
          + " m.isActive = CASE WHEN :status = 'ACTIVE' THEN true WHEN :status = 'INACTIVE' THEN"
          + " false ELSE m.isActive END) AND (:search IS NULL OR LOWER(m.name) LIKE"
          + " LOWER(CONCAT('%', :search, '%'))) GROUP BY m.id ORDER BY   CASE WHEN :sort = '거래건수순'"
          + " THEN COUNT(p.id) END DESC,   CASE WHEN :sort = '금액순' THEN SUM(p.amount) END DESC,  "
          + " CASE WHEN :sort = '이름순' THEN m.name END ASC")
  Page<Object[]> findAllMerchantsWithPayment(
      Pageable pageable, String status, String sort, String search);

  @Query("SELECT COUNT(m) FROM Merchant m WHERE m.isActive = true")
  long countActiveMerchants();
}
