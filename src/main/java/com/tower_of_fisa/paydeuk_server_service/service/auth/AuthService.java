package com.tower_of_fisa.paydeuk_server_service.service.auth;

import com.tower_of_fisa.paydeuk_server_service.common.ErrorDefineCode;
import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.NoSuchElementFoundException404;
import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.BadRequestException400;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.dto.auth.FindIdResponse;
import com.tower_of_fisa.paydeuk_server_service.dto.auth.FindPasswordRequest;
import com.tower_of_fisa.paydeuk_server_service.dto.auth.ResetPasswordRequest;
import com.tower_of_fisa.paydeuk_server_service.repository.UserRepository;
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
    User user = userRepository.findByUsername(request.getUsername())
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
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.USER_NOT_FOUND));

    // 기존 비밀번호와 동일한지 확인
    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BadRequestException400(ErrorDefineCode.PASSWORD_SAME_AS_CURRENT);
    }

    user.setPassword(passwordEncoder.encode(request.getPassword()));
    userRepository.save(user);
  }
}
