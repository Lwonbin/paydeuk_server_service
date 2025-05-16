package com.tower_of_fisa.paydeuk_server_service.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageInfo {
  private int pageNumber;
  private int pageSize;
}
