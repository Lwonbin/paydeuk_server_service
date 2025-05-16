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
      "SELECT m, COUNT(p.id) as transactionCount, COALESCE(SUM(p.amount), 0) as totalAmount "
          + "FROM Merchant m "
          + "LEFT JOIN Payment p ON m.id = p.merchant.id "
          + "GROUP BY m.id")
  Page<Object[]> findAllMerchantsWithPayment(Pageable pageable);

  @Query("SELECT COUNT(m) FROM Merchant m WHERE m.isActive = true")
  long countActiveMerchants();
}
