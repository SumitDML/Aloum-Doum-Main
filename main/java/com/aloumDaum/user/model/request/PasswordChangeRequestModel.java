package com.aloumDaum.user.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class  PasswordChangeRequestModel {

    private String oldPassword;
    private String newPassword;
}
