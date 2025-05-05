package com.tower_of_fisa.paydeuk_server_service.config.exception.custom;


import com.tower_of_fisa.paydeuk_server_service.response.ErrorDefineCode;
import lombok.Getter;

@Getter
public class BasicCustomException500 extends RuntimeException {
    private ErrorDefineCode code;

    public BasicCustomException500(ErrorDefineCode code) {
        super(code.getMessage());
        this.code = code;

    }
}


