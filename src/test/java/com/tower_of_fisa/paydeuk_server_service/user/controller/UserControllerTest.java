package com.tower_of_fisa.paydeuk_server_service.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tower_of_fisa.paydeuk_server_service.config.WithCustomMockUser;
import com.tower_of_fisa.paydeuk_server_service.global.config.security.CustomAccessDeniedHandler;
import com.tower_of_fisa.paydeuk_server_service.global.config.security.CustomAuthenticationEntryPoint;
import com.tower_of_fisa.paydeuk_server_service.global.config.security.CustomUserDetailsService;
import com.tower_of_fisa.paydeuk_server_service.user.dto.*;
import com.tower_of_fisa.paydeuk_server_service.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private UserService userService;
  @MockBean private CustomUserDetailsService customUserDetailsService;

  @MockBean private CustomAuthenticationEntryPoint authenticationEntryPoint;

  @MockBean private CustomAccessDeniedHandler accessDeniedHandler;

  @DisplayName("USER_01: 주소 변경 성공")
  @Test
  @WithCustomMockUser(id = 1L, email = "test@example.com")
  void updateAddress_success() throws Exception {

    //given
    UpdateAddressRequest request = new UpdateAddressRequest("서울시 강남구");
    BDDMockito.willDoNothing().given(userService).updateAddress(any(), any());


    //when & then
    mockMvc
        .perform(
            patch("/api/user/profile/address")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("USER_02: 이메일 변경 성공")
  @Test
  @WithCustomMockUser(id = 1L, email = "test@example.com")
  void updateEmail_success() throws Exception {

    //given
    UpdateEmailRequest request = new UpdateEmailRequest("test@example.com");
    BDDMockito.willDoNothing().given(userService).updateEmail(any(), any());


    //when & then
    mockMvc
        .perform(
            patch("/api/user/profile/email")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("USER_05: 간편 결제 비밀번호 설정 성공")
  @Test
  @WithCustomMockUser(id = 1L, email = "test@example.com")
  void setPaymentPinCode_success() throws Exception {

    //given
    PaymentPinCodeRequest request = new PaymentPinCodeRequest("1234");
    BDDMockito.willDoNothing().given(userService).setPaymentPinCode(any(), any());

    //when & then
    mockMvc
        .perform(
            post("/api/user/payment-pin-code")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("USER_06: 간편 결제 비밀번호 변경 성공")
  @Test
  @WithCustomMockUser(id = 1L, email = "test@example.com")
  void setNewPaymentPinCode_success() throws Exception {

    //given
    SetNewPaymentPinCodeRequest request = new SetNewPaymentPinCodeRequest("5678");
    BDDMockito.willDoNothing().given(userService).setNewPaymentPinCode(any(), any());

    //when & then
    mockMvc
        .perform(
            patch("/api/user/payment-pin-code")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("USER_07: 간편 결제 비밀번호 검증 성공")
  @Test
  @WithCustomMockUser(id = 1L, email = "test@example.com")
  void verifyPaymentPinCode_success() throws Exception {

    //given
    PaymentPinCodeRequest request = new PaymentPinCodeRequest("1234");
    BDDMockito.willDoNothing().given(userService).verifyPaymentPinCode(any(), any());


    //when & then
    mockMvc
        .perform(
            post("/api/user/payment-pin-code/verify")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @DisplayName("USER_08: 내 정보 조회 성공")
  @Test
  @WithCustomMockUser(id = 1L, email = "test@example.com")
  void getUserProfile_success() throws Exception {

    //given
    UserInfoResponse dummyResponse =
        new UserInfoResponse(
            "홍길동", "000512", "01033399037", "test@example.com", "서울시 강남구", "image");

    BDDMockito.given(userService.getUserInfo(any())).willReturn(dummyResponse);


    //when & then
    mockMvc.perform(get("/api/user/profile")).andDo(print()).andExpect(status().isOk());
  }

  @DisplayName("USER_09: 수혜 혜택 조회 성공")
  @Test
  @WithCustomMockUser(id = 1L, email = "test@example.com")
  void getUserBenefits_success() throws Exception {

    //given

    UserBenefitResponse dummyResponse = new UserBenefitResponse("홍길동", 100, 200);
    BDDMockito.given(userService.getUserBenefits(any())).willReturn(dummyResponse);

    //when & then
    mockMvc.perform(get("/api/user/benefits")).andDo(print()).andExpect(status().isOk());
  }

  @DisplayName("USER_04: 사용자 존재 여부 확인 성공")
  @Test
  @WithCustomMockUser(id = 1L, email = "test@example.com")
  void checkUserExists_success() throws Exception {

    //given
    BDDMockito.willDoNothing().given(userService).checkUserExists(any());

    //when & then
    mockMvc.perform(get("/api/user/1/check")).andDo(print()).andExpect(status().isOk());
  }

  @DisplayName("USER_03: 프로필 이미지 변경 성공")
  @Test
  @WithCustomMockUser(id = 1L, email = "test@example.com")
  void updateImage_success() throws Exception {

    //given
    MockMultipartFile imageFile =
        new MockMultipartFile("image", "profile.jpg", "image/jpeg", "fake-image".getBytes());

    BDDMockito.given(userService.updateProfileImage(any(), any()))
        .willReturn(new UserProfileImageResponse("image-url"));

    //when & then
    mockMvc
        .perform(
            multipart("/api/user/profile/image")
                .file(imageFile)
                .with(csrf())
                .with(
                    request -> {
                      request.setMethod("PATCH");
                      return request;
                    })
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andDo(print())
        .andExpect(status().isOk());
  }
}
