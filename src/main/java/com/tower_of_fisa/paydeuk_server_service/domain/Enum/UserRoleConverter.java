package com.tower_of_fisa.paydeuk_server_service.domain.Enum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * UserRole Enum과 데이터베이스 문자열 간의 변환을 처리하는 컨버터 이 컨버터는 다음과 같은 기능을 제공합니다: 1. 데이터베이스에 저장 시: UserRole
 * enum의 name() 값을 그대로 저장 2. 데이터베이스에서 조회 시: 문자열을 대문자로 변환하여 UserRole enum으로 매핑 예시: - DB의 'user' ->
 * UserRole.USER - DB의 'USER' -> UserRole.USER
 */
@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {

  /**
   * UserRole enum을 데이터베이스 컬럼 값으로 변환
   *
   * @param attribute 변환할 UserRole enum
   * @return 데이터베이스에 저장될 문자열 (enum의 name 값)
   */
  @Override
  public String convertToDatabaseColumn(UserRole attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.name();
  }

  /**
   * 데이터베이스 컬럼 값을 UserRole enum으로 변환
   *
   * @param dbData 데이터베이스에서 조회한 문자열
   * @return 변환된 UserRole enum (대소문자 구분 없이 처리)
   */
  @Override
  public UserRole convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    try {
      return UserRole.valueOf(dbData.toUpperCase());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
