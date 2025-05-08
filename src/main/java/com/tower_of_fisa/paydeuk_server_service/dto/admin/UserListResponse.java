package com.tower_of_fisa.paydeuk_server_service.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "User : List Response 스키마")
public class UserListResponse {
    @Schema(description = "ID", example = "1")
    private Long userId;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "상태", example = "ACTIVE")
    private String status;

    @Schema(description = "가입일", example = "2024-03-20T10:00:00")
    private LocalDateTime createdAt;
} 