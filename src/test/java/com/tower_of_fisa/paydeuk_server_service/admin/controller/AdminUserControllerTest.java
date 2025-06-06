package com.tower_of_fisa.paydeuk_server_service.admin.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tower_of_fisa.paydeuk_server_service.admin.dto.UserListResponse;
import com.tower_of_fisa.paydeuk_server_service.admin.dto.UserStatsResponse;
import com.tower_of_fisa.paydeuk_server_service.admin.service.AdminUserService;
import com.tower_of_fisa.paydeuk_server_service.global.config.security.CustomAccessDeniedHandler;
import com.tower_of_fisa.paydeuk_server_service.global.config.security.CustomAuthenticationEntryPoint;
import com.tower_of_fisa.paydeuk_server_service.global.config.security.CustomUserDetailsService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AdminUserController.class)
@MockBean(JpaMetamodelMappingContext.class)
class AdminUserControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired ObjectMapper objectMapper;

    @MockBean AdminUserService adminUserService;
    @MockBean CustomUserDetailsService customUserDetailsService;
    @MockBean CustomAuthenticationEntryPoint authenticationEntryPoint;
    @MockBean CustomAccessDeniedHandler accessDeniedHandler;

    @Test
    @DisplayName("ADMIN_USER_01: 사용자 목록 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_success() throws Exception {
        //given
        var pageImpl = new PageImpl<>(List.of(new UserListResponse()));
        given(adminUserService.getAllUsers(1, 5, "전체", "기본 정렬", ""))
                .willReturn(pageImpl);

        //when & then
        mockMvc
                .perform(
                        get("/api/admin/users")
                                .param("page", "1")
                                .param("size", "5")
                                .param("status", "전체")
                                .param("sort", "기본 정렬")
                                .param("search", ""))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN_USER_02: 사용자 통계 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void getUserStats_success() throws Exception {

        //given
        given(adminUserService.getUserStats())
                .willReturn(new UserStatsResponse());

        //when & then
        mockMvc.perform(get("/api/admin/users/stats"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}