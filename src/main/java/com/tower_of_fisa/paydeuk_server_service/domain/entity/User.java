package com.tower_of_fisa.paydeuk_server_service.domain.entity;

import com.tower_of_fisa.paydeuk_server_service.common.BaseEntity;
import com.tower_of_fisa.paydeuk_server_service.domain.Enum.UserRole;
import com.tower_of_fisa.paydeuk_server_service.domain.Enum.UserStatus;
import com.tower_of_fisa.paydeuk_server_service.domain.Enum.UserRoleConverter;
import com.tower_of_fisa.paydeuk_server_service.domain.Enum.UserStatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "name", length = 10, nullable = false)
  private String name;

  @Column(name = "username", length = 20, nullable = false)
  private String username;

  @Column(name = "password", length = 50, nullable = false)
  private String password;

  @Column(name = "personal_auth_key", length = 100, nullable = false)
  private String personalAuthKey;

  @Column(name = "phone", length = 20, nullable = false)
  private String phone;

  @Column(name = "email", length = 30)
  private String email;
  
  @Column(name = "pay_password", length = 50, nullable = false)
  private String payPassword;
  
  @Column(name = "address", length = 30)
  private String address;
  
  @Convert(converter = UserRoleConverter.class) // Enum을 DB에 저장하기 위한 컨버터
  @Column(name = "role", nullable = false)
  private UserRole role;
  
  @Column(name = "birth_date", length = 10, nullable = false) // ex 2000.05.12 (10자리)
  private String birthDate;

  @Convert(converter = UserStatusConverter.class) // Enum을 DB에 저장하기 위한 컨버터
  @Column(name = "status", nullable = false)
  private UserStatus status;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserCard> userCards = new ArrayList<>();
}
