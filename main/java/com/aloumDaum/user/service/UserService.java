package com.aloumDaum.user.service;

import com.aloumDaum.user.model.request.LoginRequest;
import com.aloumDaum.user.model.request.OTPRequest;
import com.aloumDaum.user.model.request.PasswordChangeRequestModel;
import com.aloumDaum.user.model.request.ResetPasswordRequest;
import com.aloumDaum.user.model.request.UpdateRequest;
import com.aloumDaum.user.model.request.UserCreateRequestModel;
import com.aloumDaum.user.model.request.UserInfo;
import com.aloumDaum.user.model.response.Data;
import com.aloumDaum.user.model.response.LogoutResponse;
import com.aloumDaum.user.model.response.ResponseModel;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public interface UserService {

    public ResponseEntity loginUser(LoginRequest loginRequest,
                                    MultiValueMap<String, String> headers);

    public ResponseEntity<ResponseModel<?>> sendOtp(OTPRequest otpRequest);

    public ResponseEntity<ResponseModel<String>> setPassword(ResetPasswordRequest resetPasswordRequest);

    public LogoutResponse logout(String token, UserInfo userInfo);

    public ResponseEntity<ResponseModel<String>> updateUser(UpdateRequest updateRequest);

    public void createUser(UserCreateRequestModel userCreateRequest);

    public void changePassword(PasswordChangeRequestModel passwordChange, UserInfo userInfo);

    Data findAll(String userId, Pageable pageable);

}

