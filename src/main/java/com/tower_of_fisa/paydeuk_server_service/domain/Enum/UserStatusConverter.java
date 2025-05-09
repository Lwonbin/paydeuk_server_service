package com.tower_of_fisa.paydeuk_server_service.domain.Enum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * UserStatus Enum과 데이터베이스 문자열 간의 변환을 처리하는 컨버터 이 컨버터는 다음과 같은 기능을 제공합니다: 1. 데이터베이스에 저장 시: UserStatus
 * enum의 name() 값을 그대로 저장 2. 데이터베이스에서 조회 시: 문자열을 대문자로 변환하여 UserStatus enum으로 매핑
 *
 * <p>예시: - DB의 'active' -> UserStatus.ACTIVE - DB의 'ACTIVE' -> UserStatus.ACTIVE
 *
 * <p>주의사항: - 데이터베이스의 값이 enum에 정의되지 않은 경우 null을 반환
 */
@Converter(autoApply = true)
public class UserStatusConverter implements AttributeConverter<UserStatus, String> {

  /**
   * UserStatus enum을 데이터베이스 컬럼 값으로 변환
   *
   * @param attribute 변환할 UserStatus enum
   * @return 데이터베이스에 저장될 문자열 (enum의 name 값)
   */
  @Override
  public String convertToDatabaseColumn(UserStatus attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.name();
  }

  /**
   * 데이터베이스 컬럼 값을 UserStatus enum으로 변환
   *
   * @param dbData 데이터베이스에서 조회한 문자열
   * @return 변환된 UserStatus enum (대소문자 구분 없이 처리)
   */
  @Override
  public UserStatus convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    try {
      return UserStatus.valueOf(dbData.toUpperCase());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
