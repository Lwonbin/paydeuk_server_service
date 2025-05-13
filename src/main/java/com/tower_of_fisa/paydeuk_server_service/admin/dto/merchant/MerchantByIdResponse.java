package com.tower_of_fisa.paydeuk_server_service.admin.dto.merchant;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.Merchant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "가맹점 상세 정보 응답")
public class MerchantByIdResponse {
  @Schema(description = "가맹점명", example = "스타벅스")
  private final String merchantName;

  @Schema(description = "가맹점 카테고리", example = "FOOD_BEVERAGE")
  private final String category;

  @Schema(description = "사업자등록번호", example = "123-45-67890")
  private final String businessNumber;

  @Schema(description = "담당자명", example = "홍길동")
  private final String managerName;

  @Schema(description = "담당자 연락처", example = "010-1234-5678")
  private final String managerPhone;

  @Schema(description = "가맹점 연락처", example = "02-1234-5678")
  private final String businessPhone;

  public static MerchantByIdResponse from(Merchant merchant) {
    return MerchantByIdResponse.builder()
        .merchantName(merchant.getName())
        .category(merchant.getCategory().name())
        .businessNumber(merchant.getBusinessNumber())
        .managerName(merchant.getManagerName())
        .managerPhone(merchant.getManagerPhone())
        .businessPhone(merchant.getPhone())
        .build();
  }
}
