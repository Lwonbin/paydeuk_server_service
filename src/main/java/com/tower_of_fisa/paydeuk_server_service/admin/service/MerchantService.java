package com.tower_of_fisa.paydeuk_server_service.admin.service;

import com.tower_of_fisa.paydeuk_server_service.admin.dto.MerchantAllResponse;
import com.tower_of_fisa.paydeuk_server_service.admin.dto.MerchantByIdResponse;
import com.tower_of_fisa.paydeuk_server_service.admin.repository.MerchantRepository;
import com.tower_of_fisa.paydeuk_server_service.common.ErrorDefineCode;
import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.NoSuchElementFoundException404;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.Merchant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MerchantService {
  private final MerchantRepository merchantRepository;

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
