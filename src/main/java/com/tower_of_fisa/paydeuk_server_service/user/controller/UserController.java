package com.tower_of_fisa.paydeuk_server_service.user.controller;

import com.tower_of_fisa.paydeuk_server_service.common.response.CommonResponse;
import com.tower_of_fisa.paydeuk_server_service.common.response.EmptyResponse;
import com.tower_of_fisa.paydeuk_server_service.config.security.CustomUserDetails;
import com.tower_of_fisa.paydeuk_server_service.user.dto.UpdateProfileRequest;
import com.tower_of_fisa.paydeuk_server_service.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "회원", description = "사용자 정보 관련 API")
public class UserController {

    private final UserService userService;

    @PatchMapping("/profile")
    @Operation(summary = "USER_01 : 내 정보 변경", description = "사용자 정보중 이메일, 주소를 수정합니다.")
    public CommonResponse<EmptyResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UpdateProfileRequest request) {

        userService.updateProfile(userDetails.getId(), request);
        return new CommonResponse<>(true, HttpStatus.OK, "내 정보 변경에 성공했습니다.", new EmptyResponse());
    }
}
