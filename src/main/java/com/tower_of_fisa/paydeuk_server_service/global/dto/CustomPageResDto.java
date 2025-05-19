package com.tower_of_fisa.paydeuk_server_service.global.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Page;
@Data
@Getter
@AllArgsConstructor
@Schema(description = "커스텀 페이지 응답 DTO")
public class CustomPageResDto<T> {

  @Schema(description = "현재 페이지의 데이터 리스트")
  private List<T> content;

  @Schema(description = "페이지 정보")
  private PageInfo pageable;

  @Schema(description = "전체 페이지 수", example = "4")
  private int totalPages;

  @Schema(description = "전체 요소 수", example = "16")
  private long totalElements;

  public static <T> CustomPageResDto<T> fromPage(Page<T> page) {
    return new CustomPageResDto<>(
            page.getContent(),
            new PageInfo(page.getNumber() + 1, page.getSize()), // 1부터 시작
            page.getTotalPages(),
            page.getTotalElements()
    );
  }
}