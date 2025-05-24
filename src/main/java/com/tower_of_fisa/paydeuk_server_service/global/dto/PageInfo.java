package com.tower_of_fisa.paydeuk_server_service.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "페이지 정보")
public class PageInfo {

  @Schema(description = "현재 페이지 번호 (1부터 시작)", example = "1")
  private int page;

  @Schema(description = "페이지 크기", example = "5")
  private int size;
}
