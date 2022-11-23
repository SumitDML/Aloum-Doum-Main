package com.aloumDaum.user.model.request;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class OTPRequest {

    @Email
    @NotBlank(message ="Email cannot be null or Empty")
    private String email;


}
