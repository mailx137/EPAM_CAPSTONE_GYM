package com.gym.dto.request;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationFormDto {
    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 6, max = 50)
    private String password;

    @NotNull
    private String confirmPassword;
}
