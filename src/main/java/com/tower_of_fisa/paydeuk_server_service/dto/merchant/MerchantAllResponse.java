package com.tower_of_fisa.paydeuk_server_service.dto.merchant;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.Merchant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "가맹점 정보 응답")
public class MerchantAllResponse {
    @Schema(description = "가맹점 ID", example = "1")
    private final Long id;

    @Schema(description = "가맹점명", example = "스타벅스")
    private final String merchantName;

    @Schema(description = "카테고리", example = "FOOD_BEVERAGE")
    private final String category;

    @Schema(description = "거래 횟수", example = "150")
    private final Long transactionCount;

    @Schema(description = "거래 금액", example = "1500000")
    private final Long transactionAmount;

    @Schema(description = "활성화 여부", example = "true")
    private final boolean status;

    public static MerchantAllResponse from(Merchant merchant,long transactionCount, long transactionAmount) {
        return MerchantAllResponse.builder()
                .id(merchant.getId())
                .merchantName(merchant.getName())
                .category(merchant.getCategory().name())
                .status(merchant.getIsActive())
                .transactionCount(transactionCount)
                .transactionAmount(transactionAmount)
                .build();
    }
} 