package com.tower_of_fisa.paydeuk_server_service.user_card.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tower_of_fisa.paydeuk_server_service.config.WithCustomMockUser;
import com.tower_of_fisa.paydeuk_server_service.global.config.security.CustomUserDetailsService;
import com.tower_of_fisa.paydeuk_server_service.user_card.dto.*;
import com.tower_of_fisa.paydeuk_server_service.user_card.service.UserCardService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserCardController.class)
@MockBean(JpaMetamodelMappingContext.class)
class UserCardControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @MockBean UserCardService userCardService;

  @MockBean private CustomUserDetailsService customUserDetailsService;

  @DisplayName("CARD_01: 내 카드 목록 조회")
  @Test
  @WithCustomMockUser(id = 1L, email = "test@example.com")
  void getMyCards_success() throws Exception {
    MyCardResponse dummyResponse =
        MyCardResponse.builder()
            .id(1L)
            .cardName("신한 플래티넘")
            .cardNumber("1234")
            .imageUrl("https://example.com/card.png")
            .isDefaultCard(true)
            .cardBenefits(List.of())
            .build();

    BDDMockito.given(userCardService.getMyCards(any())).willReturn(List.of(dummyResponse));

    mockMvc.perform(get("/api/card/my")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @DisplayName("CARD_02: 내 결제 내역 조회 성공")
  @WithCustomMockUser(id = 1L, email = "test@example.com")
  void getPaymentHistory_success() throws Exception {
    // given
    int page = 1;
    int size = 5;
    String sort = "createdAt,desc";

    PaymentHistoryResponse dummyPayment =
        PaymentHistoryResponse.builder()
            .id(1L)
            .shopName("스타벅스")
            .cardName("현대카드 M")
            .transactionAmount(4500)
            .discountAmount(450)
            .applicationBenefit("국내외 가맹점 1.5% M포인트 적립")
            .createdAt(LocalDateTime.of(2024, 3, 20, 14, 30))
            .build();

    PageImpl<PaymentHistoryResponse> pageResult =
        new PageImpl<>(List.of(dummyPayment), PageRequest.of(page - 1, size), 1);

    BDDMockito.given(
            userCardService.getPaymentHistory(
                eq(1L), eq(page), eq(size), eq(sort), isNull(), isNull()))
        .willReturn(pageResult);

    // when & then
    mockMvc
        .perform(
            get("/api/card/my/payment")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("sort", sort)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("CARD_03: 카드 등록")
  @Test
  @WithCustomMockUser(id = 1L, email = "test@example.com")
  void addCard_success() throws Exception {
    AddCardRequest dummyRequest =
        AddCardRequest.builder()
            .cardNumber("1111222233334440")
            .month("12")
            .year("26")
            .cvc("123")
            .pinPrefix("00")
            .build();

    BDDMockito.willDoNothing().given(userCardService).addCard(any(), any());

    mockMvc
        .perform(
            post("/api/card")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dummyRequest)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("CARD_04: 대표카드 설정")
  @Test
  @WithCustomMockUser(id = 1L, email = "test@example.com")
  void setDefaultCard_success() throws Exception {
    SetDefaultCardRequest request = new SetDefaultCardRequest(1L);
    BDDMockito.willDoNothing().given(userCardService).setDefaultCard(any(), any());

    mockMvc
        .perform(
            post("/api/card/default")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("CARD_05: 대표카드 변경")
  @Test
  @WithCustomMockUser(id = 1L, email = "test@example.com")
  void updateDefaultCard_success() throws Exception {
    SetDefaultCardRequest request = new SetDefaultCardRequest(2L);
    BDDMockito.willDoNothing().given(userCardService).updateDefaultCard(any(), any());

    mockMvc
        .perform(
            patch("/api/card/default")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("CARD_06: 카드 삭제")
  @Test
  @WithCustomMockUser(id = 1L, email = "test@example.com")
  void deleteCard_success() throws Exception {
    BDDMockito.willDoNothing().given(userCardService).deleteCard(any(), eq(1L));

    mockMvc.perform(delete("/api/card/1").with(csrf())).andDo(print()).andExpect(status().isOk());
  }
}
