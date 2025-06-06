package com.tower_of_fisa.paydeuk_server_service.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.tower_of_fisa.paydeuk_server_service.admin.dto.*;
import com.tower_of_fisa.paydeuk_server_service.admin.repository.MerchantRepository;
import com.tower_of_fisa.paydeuk_server_service.admin.repository.PaymentRepository;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.Merchant;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.CardType;
import com.tower_of_fisa.paydeuk_server_service.domain.enums.MerchantCategory;
import com.tower_of_fisa.paydeuk_server_service.global.common.ErrorDefineCode;
import com.tower_of_fisa.paydeuk_server_service.global.config.exception.custom.exception.NoSuchElementFoundException404;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class AdminMerchantServiceTest {
  @Mock private MerchantRepository merchantRepository;

  @Mock private PaymentRepository paymentRepository;

  @Mock private AdminMerchantUtils adminMerchantUtils;

  @InjectMocks private AdminMerchantService adminMerchantService;

  @DisplayName("전체 가맹점 통계를 조회한다.")
  @Test
  void testGetTotalMerchantStatistics() {
    // given
    given(merchantRepository.count()).willReturn(5L);
    given(paymentRepository.countByPaymentSuccessTrue()).willReturn(10);
    given(paymentRepository.sumAmountByPaymentSuccessTrue()).willReturn(1000L);
    given(adminMerchantUtils.calculateAverageAmount(1000L, 10)).willReturn(100);
    given(merchantRepository.countActiveMerchants()).willReturn(4L);
    given(adminMerchantUtils.calculateRecent24hTransactionCount(10)).willReturn(2);
    given(adminMerchantUtils.calculateMonthlyChangeRate(1000L)).willReturn(5.5);

    // when
    MerchantStatsResponse result = adminMerchantService.getTotalMerchantStatistics();

    // then
    assertThat(result.getTotalMerchantCount()).isEqualTo(5);
    assertThat(result.getTotalTransactionCount()).isEqualTo(10);
    assertThat(result.getTotalTransactionAmount()).isEqualTo(1000L);
    assertThat(result.getAverageTransactionAmount()).isEqualTo(100);
    assertThat(result.getActiveMerchantCount()).isEqualTo(4);
    assertThat(result.getRecent24hTransactionIncrease()).isEqualTo(2);
    assertThat(result.getTransactionAmountChangePercent()).isEqualTo(5.5);
  }

  @Test
  @DisplayName("개별 가맹점 통계를 조회한다.")
  void getMerchantStatsById() {
    // given
    Long id = 1L;
    Merchant merchant =
        Merchant.builder()
            .id(id)
            .name("shop")
            .isActive(true)
            .commissionRate("1.5")
            .businessNumber("123")
            .category(MerchantCategory.CULTURE)
            .managerName("man")
            .managerPhone("010")
            .phone("02")
            .build();
    given(merchantRepository.findById(id)).willReturn(Optional.of(merchant));
    given(paymentRepository.countByMerchantIdAndPaymentSuccessTrue(id)).willReturn(3);
    given(paymentRepository.sumAmountByMerchantIdAndPaymentSuccessTrue(id)).willReturn(300L);
    given(adminMerchantUtils.calculateAverageAmount(300L, 3)).willReturn(100);
    given(adminMerchantUtils.calculateRecent24hTransactionCount(3, id)).willReturn(1);
    given(adminMerchantUtils.calculateMonthlyChangeRate(id, 300L)).willReturn(7.0);

    // when
    MerchantIndividualStatsResponse result = adminMerchantService.getMerchantStatsById(id);

    // then
    assertThat(result.getTransactionCount()).isEqualTo(3);
    assertThat(result.getTotalTransactionAmount()).isEqualTo(300L);
    assertThat(result.getAverageTransactionAmount()).isEqualTo(100);
    assertThat(result.getCommissionRate()).isEqualTo("1.5");
    assertThat(result.getRecent24hTransactionCount()).isEqualTo(1);
    assertThat(result.getPercentChange()).isEqualTo(7.0);
    assertThat(result.isStatus()).isTrue();

    // given
    Long invalidId = 999L;
    given(merchantRepository.findById(invalidId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> adminMerchantService.getMerchantStatsById(invalidId))
        .isInstanceOf(NoSuchElementFoundException404.class)
        .hasMessageContaining(ErrorDefineCode.MERCHANT_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("거래 건수와 금액 기준으로 상위 가맹점 통계를 반환한다")
  void getTopMerchantStats() {
    // given
    Merchant m1 = Merchant.builder().id(1L).name("m1").build();
    Merchant m2 = Merchant.builder().id(2L).name("m2").build();
    Merchant m3 = Merchant.builder().id(3L).name("m3").build();
    Merchant m4 = Merchant.builder().id(4L).name("m4").build();
    given(merchantRepository.findAll()).willReturn(List.of(m1, m2, m3, m4));

    given(paymentRepository.countByMerchantIdAndPaymentSuccessTrue(1L)).willReturn(10);
    given(paymentRepository.countByMerchantIdAndPaymentSuccessTrue(2L)).willReturn(5);
    given(paymentRepository.countByMerchantIdAndPaymentSuccessTrue(3L)).willReturn(10);
    given(paymentRepository.countByMerchantIdAndPaymentSuccessTrue(4L)).willReturn(0);

    given(paymentRepository.sumAmountByMerchantIdAndPaymentSuccessTrue(1L)).willReturn(1000L);
    given(paymentRepository.sumAmountByMerchantIdAndPaymentSuccessTrue(2L)).willReturn(2000L);
    given(paymentRepository.sumAmountByMerchantIdAndPaymentSuccessTrue(3L)).willReturn(1500L);
    given(paymentRepository.sumAmountByMerchantIdAndPaymentSuccessTrue(4L)).willReturn(300L);

    // when
    List<MerchantTopStatsResponse> result = adminMerchantService.getTopMerchantStats();

    // then
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getMerchantId()).isEqualTo(3L);
    assertThat(result.get(1).getMerchantId()).isEqualTo(1L);
    assertThat(result.get(2).getMerchantId()).isEqualTo(2L);
  }

  @ParameterizedTest
  @MethodSource("provideSorts")
  @DisplayName("정렬 옵션별 전체 가맹점 결제 내역을 조회한다")
  void getAllMerchantPaymentHistories(String sort, String field, Sort.Direction direction) {
    // given
    int page = 1;
    int size = 5;
    String status = "모든 상태";
    String search = "스타";

    MerchantPaymentResponse payment =
        new MerchantPaymentResponse(
            1L, "shop", CardType.CREDIT, MerchantCategory.CULTURE, LocalDateTime.now(), 1000, true);
    PageImpl<MerchantPaymentResponse> pageResult = new PageImpl<>(List.of(payment));
    ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
    given(paymentRepository.findAllPaymentHistories(any(Pageable.class), eq(status), eq(search)))
        .willReturn(pageResult);

    // when
    Page<MerchantPaymentResponse> result =
        adminMerchantService.getAllMerchantPaymentHistories(page, size, status, sort, search);

    // then
    assertThat(result.getTotalElements()).isEqualTo(1);
    verify(paymentRepository).findAllPaymentHistories(captor.capture(), eq(status), eq(search));
    Pageable used = captor.getValue();
    assertThat(used.getPageNumber()).isZero();
    assertThat(used.getPageSize()).isEqualTo(size);
    Sort.Order order = used.getSort().getOrderFor(field);
    assertThat(order).isNotNull();
    assertThat(order.getDirection()).isEqualTo(direction);
  }

  @Test
  @DisplayName("가맹점 상태를 업데이트한다")
  void updateMerchantStatus() {
    // given
    Long id = 1L;
    Merchant merchant = Merchant.builder().id(id).isActive(false).build();
    given(merchantRepository.findById(id)).willReturn(Optional.of(merchant));

    // when
    adminMerchantService.updateMerchantStatus(id, true);

    // then
    assertThat(merchant.getIsActive()).isTrue();
    verify(merchantRepository).save(merchant);

    // given
    Long invalidId = 999L;
    given(merchantRepository.findById(invalidId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> adminMerchantService.updateMerchantStatus(invalidId, true))
        .isInstanceOf(NoSuchElementFoundException404.class)
        .hasMessageContaining(ErrorDefineCode.MERCHANT_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("가맹점을 삭제 처리한다")
  void deleteMerchant() {
    // given
    Long id = 1L;
    Merchant merchant = Merchant.builder().id(id).isDeleted(false).build();
    given(merchantRepository.findById(id)).willReturn(Optional.of(merchant));

    // when
    adminMerchantService.deleteMerchant(id);

    // then
    assertThat(merchant.isDeleted()).isTrue();
    verify(merchantRepository).save(merchant);

    // given
    Long invalidId = 999L;
    given(merchantRepository.findById(invalidId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> adminMerchantService.deleteMerchant(invalidId))
        .isInstanceOf(NoSuchElementFoundException404.class)
        .hasMessageContaining(ErrorDefineCode.MERCHANT_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("가맹점 상세 정보를 조회한다.")
  void getMerchantById() {
    // given
    Long id = 1L;
    Merchant merchant =
        Merchant.builder()
            .id(id)
            .name("shop")
            .businessNumber("123")
            .managerName("man")
            .managerPhone("010")
            .phone("02")
            .category(MerchantCategory.CULTURE)
            .build();
    given(merchantRepository.findById(id)).willReturn(Optional.of(merchant));

    // when
    MerchantByIdResponse result = adminMerchantService.getMerchantById(id);

    // then
    assertThat(result.getMerchantName()).isEqualTo("shop");
    assertThat(result.getCategory()).isEqualTo("CULTURE");
    assertThat(result.getBusinessNumber()).isEqualTo("123");
    assertThat(result.getManagerName()).isEqualTo("man");
    assertThat(result.getManagerPhone()).isEqualTo("010");
    assertThat(result.getBusinessPhone()).isEqualTo("02");

    // given
    Long invalidId = 999L;
    given(merchantRepository.findById(invalidId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> adminMerchantService.getMerchantById(invalidId))
        .isInstanceOf(NoSuchElementFoundException404.class)
        .hasMessageContaining(ErrorDefineCode.MERCHANT_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("전체 결제 통계 정보를 반환한다")
  void getMerchantPaymentStats() {
    // given
    given(paymentRepository.count()).willReturn(20L);
    given(adminMerchantUtils.calculateTransactionCountChangeRate()).willReturn(10.5);
    given(paymentRepository.sumTotalAmount()).willReturn(5000L);
    given(adminMerchantUtils.calculateTransactionAmountChangeRate()).willReturn(-3.2);
    given(adminMerchantUtils.calculateAverageAmount(5000L, 20)).willReturn(250);
    given(adminMerchantUtils.calculateAverageAmountChangeRate()).willReturn(1.5);
    given(adminMerchantUtils.calculateActiveTransactionRate(20)).willReturn(95.0);

    // when
    MerchantPaymentStatsResponse result = adminMerchantService.getMerchantPaymentStats();

    // then
    assertThat(result.getTotalTransactionCount()).isEqualTo(20);
    assertThat(result.getTransactionCountChangeRate()).isEqualTo(10.5);
    assertThat(result.getTotalTransactionAmount()).isEqualTo(5000L);
    assertThat(result.getTransactionAmountChangeRate()).isEqualTo(-3.2);
    assertThat(result.getAverageTransactionAmount()).isEqualTo(250);
    assertThat(result.getAverageAmountChangeRate()).isEqualTo(1.5);
    assertThat(result.getActiveTransactionRate()).isEqualTo(95.0);
  }

  @Test
  @DisplayName("최근 7일간의 거래 추이를 반환하며 누락된 날짜는 0으로 채운다")
  void getTotalMerchantTransactionTrends() {
    // given
    LocalDate today = LocalDate.now();
    List<Object[]> raw = new ArrayList<>();
    raw.add(new Object[] {Date.valueOf(today), 100L, 2L});
    raw.add(new Object[] {Date.valueOf(today.minusDays(2)), 200L, 4L});
    raw.add(new Object[] {Date.valueOf(today.minusDays(4)), 300L, 6L});
    given(paymentRepository.findWeeklyTrendsForAllMerchants()).willReturn(raw);

    // when
    List<MerchantTransactionTrendResponse> result =
        adminMerchantService.getTotalMerchantTransactionTrends();

    // then
    assertThat(result).hasSize(7);
    // index for today
    int idxToday = 6;
    assertThat(result.get(idxToday).getTransactionAmount()).isEqualTo(100L);
    assertThat(result.get(idxToday).getTransactionCount()).isEqualTo(2L);
    int idxMinus2 = 6 - 2;
    assertThat(result.get(idxMinus2).getTransactionAmount()).isEqualTo(200L);
    assertThat(result.get(idxMinus2).getTransactionCount()).isEqualTo(4L);
    int idxMinus4 = 6 - 4;
    assertThat(result.get(idxMinus4).getTransactionAmount()).isEqualTo(300L);
    assertThat(result.get(idxMinus4).getTransactionCount()).isEqualTo(6L);
  }

  @Test
  @DisplayName("특정 가맹점의 결제 내역을 페이지에 맞게 조회한다")
  void getSingleMerchantPayment() {
    // given
    Long merchantId = 1L;
    Merchant merchant = Merchant.builder().id(merchantId).build();
    given(merchantRepository.findById(merchantId)).willReturn(Optional.of(merchant));

    SingleMerchantPaymentResponse payment =
        new SingleMerchantPaymentResponse(1L, "1234", "카드", LocalDateTime.now(), 1000, true);
    PageImpl<SingleMerchantPaymentResponse> pageResult = new PageImpl<>(List.of(payment));
    ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
    given(
            paymentRepository.findSingleMerchantPaymentsByMerchantId(
                eq(merchantId), any(Pageable.class)))
        .willReturn(pageResult);

    // when
    Page<SingleMerchantPaymentResponse> result =
        adminMerchantService.getSingleMerchantPayment(merchantId, 1, 5);

    // then
    assertThat(result.getTotalElements()).isEqualTo(1);
    verify(paymentRepository)
        .findSingleMerchantPaymentsByMerchantId(eq(merchantId), captor.capture());
    Pageable used = captor.getValue();
    assertThat(used.getPageNumber()).isZero();
    assertThat(used.getPageSize()).isEqualTo(5);

    // given
    Long invalidId = 999L;
    given(merchantRepository.findById(invalidId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> adminMerchantService.getSingleMerchantPayment(invalidId, 1, 5))
        .isInstanceOf(NoSuchElementFoundException404.class)
        .hasMessageContaining(ErrorDefineCode.MERCHANT_NOT_FOUND.getMessage());
  }

  @ParameterizedTest
  @MethodSource("provideStatuses")
  @DisplayName("필터링 된 가맹점 목록을 조회한다")
  void getAllMerchants(String status, String expectedCode) {
    // given
    int page = 1;
    int size = 3;
    String sort = "이름순";
    String search = "shop";

    Merchant merchant =
        Merchant.builder()
            .id(1L)
            .name("shop")
            .isActive(true)
            .commissionRate("1.0")
            .businessNumber("123")
            .category(MerchantCategory.CULTURE)
            .managerName("man")
            .managerPhone("010")
            .phone("02")
            .build();

    List<Object[]> rows = new ArrayList<>();
    rows.add(new Object[] {merchant, 5L, 1000L});
    PageImpl<Object[]> pageResult = new PageImpl<>(rows);
    ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
    if (expectedCode == null) {
      given(
              merchantRepository.findAllMerchantsWithPayment(
                  any(Pageable.class), isNull(), eq(sort), eq(search)))
          .willReturn(pageResult);
    } else {
      given(
              merchantRepository.findAllMerchantsWithPayment(
                  any(Pageable.class), eq(expectedCode), eq(sort), eq(search)))
          .willReturn(pageResult);
    }

    // when
    Page<MerchantAllResponse> result =
        adminMerchantService.getAllMerchants(page, size, status, sort, search);

    // then
    assertThat(result.getTotalElements()).isEqualTo(1);
    verify(merchantRepository)
        .findAllMerchantsWithPayment(
            captor.capture(),
            expectedCode == null ? isNull() : eq(expectedCode),
            eq(sort),
            eq(search));
    Pageable used = captor.getValue();
    assertThat(used.getPageNumber()).isZero();
    assertThat(used.getPageSize()).isEqualTo(size);
  }

  private static Stream<Arguments> provideStatuses() {
    return Stream.of(
        Arguments.of("활성", "ACTIVE"), Arguments.of("비활성", "INACTIVE"), Arguments.of("모든 상태", null));
  }

  private static Stream<Arguments> provideSorts() {
    return Stream.of(
        Arguments.of("금액높은순", "amount", Sort.Direction.DESC),
        Arguments.of("금액낮은순", "amount", Sort.Direction.ASC),
        Arguments.of("최신순", "createdAt", Sort.Direction.DESC),
        Arguments.of("오래된순", "createdAt", Sort.Direction.ASC),
        Arguments.of("기타", "createdAt", Sort.Direction.DESC));
  }
}
