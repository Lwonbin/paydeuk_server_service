package com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception;

import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.BasicCustomException500;
import com.tower_of_fisa.paydeuk_server_service.response.ErrorDefineCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 401 : 인증 실패
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthCredientialException401 extends BasicCustomException500 {
    public AuthCredientialException401(ErrorDefineCode code) {
        super(code);
    }
}