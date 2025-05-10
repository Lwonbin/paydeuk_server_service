package com.tower_of_fisa.paydeuk_server_service.dto.merchant;

import com.tower_of_fisa.paydeuk_server_service.domain.Enum.MerchantCategory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MerchantCategoryConverter implements AttributeConverter<MerchantCategory, String> {

  @Override
  public String convertToDatabaseColumn(MerchantCategory attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.name().toLowerCase();
  }

  @Override
  public MerchantCategory convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return MerchantCategory.valueOf(dbData.toUpperCase());
  }
}
