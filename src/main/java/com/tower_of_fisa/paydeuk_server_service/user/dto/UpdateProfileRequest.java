package com.tower_of_fisa.paydeuk_server_service.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateProfileRequest {

    @Email
    private String email;

    @Size(max = 30)
    private String address;
}
