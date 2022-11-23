package com.aloumDaum.user.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {

    @Email
    @NotBlank(message = "Email cannot be null or Empty")
    private String email;

   @NotBlank(message = "Password cannot be null or Empty")
    private String password;

    @NotBlank(message = "otp cannot be null or Empty")
    private String otp;
}
