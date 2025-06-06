package com.tower_of_fisa.paydeuk_server_service.admin.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tower_of_fisa.paydeuk_server_service.admin.dto.*;
import com.tower_of_fisa.paydeuk_server_service.admin.service.AdminMerchantService;
import com.tower_of_fisa.paydeuk_server_service.config.WithCustomMockAdmin;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.CardType;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.MerchantCategory;
import com.tower_of_fisa.paydeuk_server_service.global.config.security.CustomAccessDeniedHandler;
import com.tower_of_fisa.paydeuk_server_service.global.config.security.CustomAuthenticationEntryPoint;
import com.tower_of_fisa.paydeuk_server_service.global.config.security.CustomUserDetailsService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AdminMerchantController.class)
@MockBean(JpaMetamodelMappingContext.class)
class AdminMerchantControllerTest {
  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @MockBean AdminMerchantService adminMerchantService;
  @MockBean CustomUserDetailsService customUserDetailsService;
  @MockBean CustomAuthenticationEntryPoint authenticationEntryPoint;
  @MockBean CustomAccessDeniedHandler accessDeniedHandler;

  @Test
  @DisplayName("ADMIN_01: 전체 가맹점 통계 조회")
  @WithCustomMockAdmin
  void getTotalMerchantStatistics_success() throws Exception {
    MerchantStatsResponse dummy = new MerchantStatsResponse(5, 10, 1000L, 100, 4, 2, 5.0);
    BDDMockito.given(adminMerchantService.getTotalMerchantStatistics()).willReturn(dummy);

    mockMvc.perform(get("/api/admin/merchants/stats")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @DisplayName("ADMIN_02: 전체 가맹점 결제 내역 조회")
  @WithCustomMockAdmin
  void getAllMerchantPaymentHistories_success() throws Exception {
    int page = 1;
    int size = 5;
    String statusParam = "모든 상태";
    String sort = "최신순";
    String search = "";
    MerchantPaymentResponse payment =
        new MerchantPaymentResponse(
            1L, "shop", CardType.CREDIT, MerchantCategory.CULTURE, LocalDateTime.now(), 100, true);
    PageImpl<MerchantPaymentResponse> pageResult =
        new PageImpl<>(List.of(payment), PageRequest.of(page - 1, size), 1);
    BDDMockito.given(
            adminMerchantService.getAllMerchantPaymentHistories(
                page, size, statusParam, sort, search))
        .willReturn(pageResult);

    mockMvc
        .perform(
            get("/api/admin/merchants/payments")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("status", statusParam)
                .param("sort", sort)
                .param("search", search))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("ADMIN_03: 개별 가맹점 결제 내역 조회")
  @WithCustomMockAdmin
  void getSingleMerchantPayment_success() throws Exception {
    long merchantId = 1L;
    int page = 1;
    int size = 5;
    SingleMerchantPaymentResponse payment =
        new SingleMerchantPaymentResponse(1L, "1234", "card", LocalDateTime.now(), 1000, true);
    PageImpl<SingleMerchantPaymentResponse> pageResult =
        new PageImpl<>(List.of(payment), PageRequest.of(page - 1, size), 1);
    BDDMockito.given(adminMerchantService.getSingleMerchantPayment(merchantId, page, size))
        .willReturn(pageResult);

    mockMvc
        .perform(
            get("/api/admin/merchants/{merchantId}/payments", merchantId)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("ADMIN_04: 전체 가맹점 거래 추이")
  @WithCustomMockAdmin
  void getMerchantTransactionTrends_success() throws Exception {
    MerchantTransactionTrendResponse trend =
        new MerchantTransactionTrendResponse(LocalDate.now(), 100L, 2L);
    BDDMockito.given(adminMerchantService.getTotalMerchantTransactionTrends())
        .willReturn(List.of(trend));

    mockMvc.perform(get("/api/admin/merchants/trends")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @DisplayName("ADMIN_05: 가맹점별 통계 조회")
  @WithCustomMockAdmin
  void getMerchantStatisticsById_success() throws Exception {
    MerchantIndividualStatsResponse res =
        new MerchantIndividualStatsResponse(1, 100L, 50, "1.0", 2, 5.0, true);
    BDDMockito.given(adminMerchantService.getMerchantStatsById(1L)).willReturn(res);

    mockMvc.perform(get("/api/admin/merchants/1/stats")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @DisplayName("ADMIN_06: 상위 가맹점 통계 조회")
  @WithCustomMockAdmin
  void getTopMerchantStats_success() throws Exception {
    MerchantTopStatsResponse top = new MerchantTopStatsResponse(1L, "shop", 5L, 1000L);
    BDDMockito.given(adminMerchantService.getTopMerchantStats()).willReturn(List.of(top));

    mockMvc
        .perform(get("/api/admin/merchants/top-stats"))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("ADMIN_07: 가맹점 목록 조회")
  @WithCustomMockAdmin
  void getAllMerchants_success() throws Exception {
    int page = 1;
    int size = 5;
    String statusParam = "모든 상태";
    String sort = "최신순";
    String search = "";
    MerchantAllResponse merchant =
        MerchantAllResponse.builder()
            .id(1L)
            .merchantName("shop")
            .category("CULTURE")
            .transactionCount(1L)
            .transactionAmount(100L)
            .status(true)
            .build();
    PageImpl<MerchantAllResponse> pageResult =
        new PageImpl<>(List.of(merchant), PageRequest.of(page - 1, size), 1);
    BDDMockito.given(adminMerchantService.getAllMerchants(page, size, statusParam, sort, search))
        .willReturn(pageResult);

    mockMvc
        .perform(
            get("/api/admin/merchants")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("status", statusParam)
                .param("sort", sort)
                .param("search", search))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("ADMIN_08: 가맹점 상세 조회")
  @WithCustomMockAdmin
  void getMerchantById_success() throws Exception {
    MerchantByIdResponse res =
        MerchantByIdResponse.builder()
            .merchantName("shop")
            .category("CULTURE")
            .businessNumber("123")
            .managerName("man")
            .managerPhone("010")
            .businessPhone("02")
            .build();
    BDDMockito.given(adminMerchantService.getMerchantById(1L)).willReturn(res);

    mockMvc.perform(get("/api/admin/merchants/1")).andDo(print()).andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  @DisplayName("ADMIN_09: 가맹점 상태 변경")
  @WithCustomMockAdmin
  void updateMerchantStatus_success(boolean active) throws Exception {
    BDDMockito.willDoNothing().given(adminMerchantService).updateMerchantStatus(1L, active);

    String content = "{\"status\":" + active + "}";
    String expectedMessage = active ? "가맹점이 활성화되었습니다." : "가맹점이 비활성화되었습니다.";

    mockMvc
        .perform(
            patch("/api/admin/merchants/1/status")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(expectedMessage));
  }

  @Test
  @DisplayName("ADMIN_10: 가맹점 삭제")
  @WithCustomMockAdmin
  void deleteMerchant_success() throws Exception {
    BDDMockito.willDoNothing().given(adminMerchantService).deleteMerchant(1L);

    mockMvc
        .perform(patch("/api/admin/merchants/1/delete").with(csrf()))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("ADMIN_11: 가맹점 결제 통계 조회")
  @WithCustomMockAdmin
  void getMerchantPaymentStats_success() throws Exception {
    MerchantPaymentStatsResponse res =
        new MerchantPaymentStatsResponse(1, 1.0, 100L, 2.0, 50, 3.0, 99.0);
    BDDMockito.given(adminMerchantService.getMerchantPaymentStats()).willReturn(res);

    mockMvc
        .perform(get("/api/admin/merchants/payments/stats"))
        .andDo(print())
        .andExpect(status().isOk());
  }
}
