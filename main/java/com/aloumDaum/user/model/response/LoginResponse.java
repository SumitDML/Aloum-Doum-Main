package com.aloumDaum.user.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private boolean isExistingUser;
}
