package com.tower_of_fisa.paydeuk_server_service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponse {
  private String name;
  private String birth;
  private String phoneNumber;
  private String email;
  private String address;
  private String imageUrl;
}
