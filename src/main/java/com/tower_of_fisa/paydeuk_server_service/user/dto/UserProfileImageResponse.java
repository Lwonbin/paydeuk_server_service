package com.tower_of_fisa.paydeuk_server_service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UserProfileImageResponse {
  private String imageUrl;
}
