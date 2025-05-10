package com.tower_of_fisa.paydeuk_server_service.repository;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    @Query("SELECT m, COUNT(p.id) as transactionCount, COALESCE(SUM(p.amount), 0) as totalAmount " +
           "FROM Merchant m " +
           "LEFT JOIN Payment p ON m.id = p.merchant.id " +
           "GROUP BY m.id")
    List<Object[]> findAllMerchantsWithPayment();
} 