package com.aloumDaum.user.controller;

import com.aloumDaum.user.exception.ValidationException;
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
import com.aloumDaum.user.service.UserService;
import com.aloumDaum.user.validation.request.UserRequestValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserRequestValidator userRequestValidator;

    @Autowired
    private UserService userService;

    private static final String READ_WRITE = "hasPermission('manage_users', 'view & edit')";

    private static final String READ = "hasPermission('manage_users', 'view')";


    @PostMapping(path = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseModel> login(@Valid @RequestBody LoginRequest loginRequest, @RequestHeader MultiValueMap<String, String> headers) {
        ResponseModel responseModel = null;
        try {
            return userService.loginUser(loginRequest, headers);
        } catch (ValidationException exception) {
            responseModel = new ResponseModel(400, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/sendOtp")
    public ResponseEntity<ResponseModel<?>> genOtp(@Valid @RequestBody OTPRequest otpRequest) {
        ResponseModel responseModel = null;
        try {
            return userService.sendOtp(otpRequest);
        } catch (EntityNotFoundException exception) {
            responseModel = new ResponseModel(404, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/setPassword")
    public ResponseEntity<ResponseModel<String>> forgotPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        ResponseModel responseModel = null;
        try {
            return userService.setPassword(resetPasswordRequest);
        } catch (EntityNotFoundException exception) {
            responseModel = new ResponseModel(404, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.NOT_FOUND);
        }
        catch (ValidationException exception) {
            responseModel = new ResponseModel(400, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize(READ_WRITE)
    @PostMapping(path = "/create")
    public ResponseEntity<ResponseModel> signupUser(@RequestBody final UserCreateRequestModel userDetails) {
        ResponseModel responseModel = null;
        try {
            userService.createUser(userDetails);
            responseModel = new ResponseModel(OK, "User created hbhjuh successfully.", null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.CREATED);
        } catch (ValidationException | ConstraintViolationException exception) {
            responseModel = new ResponseModel(1001, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = true) String authorization, @RequestBody UserInfo userInfo) {
        ResponseModel responseModel = null;
        try {
            LogoutResponse logoutResponse = userService.logout(authorization, userInfo);
            if (logoutResponse.isSuccess() == true) {
                return ResponseEntity.ok(logoutResponse);
            }
            return new ResponseEntity<>(logoutResponse, HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException exception) {
            responseModel = new ResponseModel(404, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/update")
    public ResponseEntity<ResponseModel<String>> updateUser(@Valid @RequestBody UpdateRequest updateRequest) {
        ResponseModel responseModel = null;
        try {
            return userService.updateUser(updateRequest);
        } catch (EntityNotFoundException exception) {
            responseModel = new ResponseModel(404, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.NOT_FOUND);
        }
        catch (ValidationException exception){
            responseModel = new ResponseModel(400, exception.getMessage(), null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping(path = "/change-password")
    public ResponseEntity<ResponseModel> changePassword(@RequestBody final PasswordChangeRequestModel passChangeDto, @RequestHeader("user-info") String userInfo) {
        ResponseModel responseModel = null;
        UserInfo user = null;
        try {
            user = new ObjectMapper().readValue(userInfo, UserInfo.class);

            userService.changePassword(passChangeDto, user);
            responseModel = new ResponseModel(OK, "Password Changed successfully", null, null);
            return new ResponseEntity<>(responseModel, OK);

        } catch (ValidationException | ConstraintViolationException exception) {
            responseModel = new ResponseModel(1001, "Incorrect Password", null, null);
            return new ResponseEntity<>(responseModel, HttpStatus.UNAUTHORIZED);

        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize(READ)
    @GetMapping(value = "/getAllUsers")
    public ResponseEntity<ResponseModel> getAll(@RequestParam(required = false) String userId, final Pageable pageable) {
        final Data data = userService.findAll(userId, pageable);
        final ResponseModel responseModel = new ResponseModel(OK, "Operation completed successfully.", null,
                data);

        return new ResponseEntity<>(responseModel, OK);
    }

}
