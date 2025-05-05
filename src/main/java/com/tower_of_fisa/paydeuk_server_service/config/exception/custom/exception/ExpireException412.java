package com.tower_of_fisa.paydeuk_server_service.config.exception.custom.exception;

import com.tower_of_fisa.paydeuk_server_service.config.exception.custom.BasicCustomException500;
import com.tower_of_fisa.paydeuk_server_service.response.ErrorDefineCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * 412 : 시간이 만료된 자원에 접근할 때
 */
@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class ExpireException412 extends BasicCustomException500 {
    public ExpireException412(ErrorDefineCode code) {
        super(code);
    }
}