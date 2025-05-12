package com.tower_of_fisa.paydeuk_server_service.service.auth;

import com.tower_of_fisa.paydeuk_server_service.common.ErrorDefineCode;
import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.AlreadyExistElementException409;
import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.AuthCredientialException401;
import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.BadRequestException400;
import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.NoSuchElementFoundException404;
import com.tower_of_fisa.paydeuk_server_service.config.security.JwtProvider;
import com.tower_of_fisa.paydeuk_server_service.domain.Enum.UserRole;
import com.tower_of_fisa.paydeuk_server_service.domain.Enum.UserStatus;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.dto.auth.FindIdResponse;
import com.tower_of_fisa.paydeuk_server_service.dto.auth.FindPasswordRequest;
import com.tower_of_fisa.paydeuk_server_service.dto.auth.ResetPasswordRequest;
import com.tower_of_fisa.paydeuk_server_service.dto.auth.SignupRequest;
import com.tower_of_fisa.paydeuk_server_service.repository.UserRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;

  /**
   * [아이디 찾기] 본인인증 후 발급받은 personal_auth_key를 통해 사용자의 아이디를 조회한다.
   *
   * @param personalAuthKey 본인인증 후 발급받은 personal_auth_key
   * @return FindIdResponse 사용자 아이디
   */
  public FindIdResponse findId(String personalAuthKey) {
    User user =
        userRepository
            .findByPersonalAuthKey(personalAuthKey)
            .orElseThrow(
                () -> new NoSuchElementFoundException404(ErrorDefineCode.AUTH_NOT_FOUND_EMAIL));

    return new FindIdResponse(user.getUsername());
  }

  /**
   * [비밀번호 찾기] 사용자의 이름과 아이디를 확인하여 본인인증을 수행할 수 있도록 한다.
   *
   * @param request 사용자 이름과 아이디를 포함한 요청
   */
  public void findPassword(FindPasswordRequest request) {
    User user =
        userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));

    if (!user.getName().equals(request.getName())) {
      throw new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND);
    }
  }

  /**
   * [비밀번호 재설정] 본인인증이 완료된 사용자의 비밀번호를 재설정한다.
   *
   * @param username 사용자 아이디
   * @param request 새로운 비밀번호를 포함한 요청
   */
  @Transactional
  public void resetPassword(String username, ResetPasswordRequest request) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));

    // 기존 비밀번호와 동일한지 확인
    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BadRequestException400(ErrorDefineCode.PASSWORD_SAME_AS_CURRENT);
    }

    user.changePassword(passwordEncoder.encode(request.getPassword()));
    userRepository.save(user);
  }

  /**
   * [회원가입] 신규 유저 정보를 저장한다.
   *
   * @param request SignupRequestDto - 추가할 유저 정보를 담은 DTO 객체
   * @return Long - 추가된 유저의 ID
   */
  @Transactional
  public void registerUser(SignupRequest request) {
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new AlreadyExistElementException409(ErrorDefineCode.DUPLICATE_EXAMPLE_NAME);
    }

    User user =
        User.builder()
            .name(request.getName())
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .phone(request.getPhone())
            .email(request.getEmail())
            .role(UserRole.USER)
            .birthDate(request.getBirthdate())
            .status(UserStatus.ACTIVE)
            .build();

    userRepository.save(user);
  }

  /**
   * [토큰 재발급] Refresh 토큰으로 Access 토큰과 Refresh 토큰을 재발급한다.
   *
   * @param refreshToken String - 검증할 Refresh 토큰
   * @return Map<String, String> - 재발급하는 Access 토큰과 Refresh 토큰
   */
  public Map<String, String> refreshAccessToken(String refreshToken) {

    if (!jwtProvider.validateRefreshToken(refreshToken)) {
      throw new AuthCredientialException401(ErrorDefineCode.AUTHENTICATE_FAIL);
    }

    String username = jwtProvider.extractUsername(refreshToken);
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new AuthCredientialException401(ErrorDefineCode.AUTH_NOT_FOUND_EMAIL));

    String newAccessToken = jwtProvider.generateAccessToken(user);
    String newRefreshToken = jwtProvider.generateRefreshToken(user);

    return Map.of(
        "accessToken", newAccessToken,
        "refreshToken", newRefreshToken);
  }
}
