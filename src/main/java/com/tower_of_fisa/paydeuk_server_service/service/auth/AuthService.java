package com.tower_of_fisa.paydeuk_server_service.service.auth;

import com.tower_of_fisa.paydeuk_server_service.common.ErrorDefineCode;
import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception.NoSuchElementFoundException404;
import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import com.tower_of_fisa.paydeuk_server_service.dto.auth.FindIdResponse;
import com.tower_of_fisa.paydeuk_server_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
  private final UserRepository userRepository;

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
}
