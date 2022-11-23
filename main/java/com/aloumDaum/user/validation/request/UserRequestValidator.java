package com.aloumDaum.user.validation.request;
import com.aloumDaum.user.model.request.LoginRequest;
import com.aloumDaum.user.model.response.ResponseModel;
import com.aloumDaum.user.validation.groups.LoginInfoValidationGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class UserRequestValidator {

    @Autowired
    private Validator validator;


    public ResponseModel<?> validateLoginRequest(LoginRequest loginRequest) {
        Set<ConstraintViolation<LoginRequest>> validate = validator.validate(loginRequest,
                LoginInfoValidationGroup.class);
        return getLoginResponse(validate);
    }

    private ResponseModel<?> getLoginResponse(
            Set<ConstraintViolation<LoginRequest>> validate) {

        if (!validate.isEmpty()) {
            Map<String, String> errors = new HashMap<String, String>();
            for (ConstraintViolation<LoginRequest> constraintViolation : validate) {
                if (constraintViolation.getPropertyPath().toString() != null
                        && !constraintViolation.getPropertyPath().toString().isEmpty())
                    errors.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
                else {
                    String[] result = constraintViolation.getMessage().split(":");
                    return new ResponseModel<>(HttpStatus.BAD_REQUEST, result[1], null, null);
                }
            }
            return new ResponseModel<>(HttpStatus.BAD_REQUEST, "Validation errors", null, null);
        }
        return null;
    }
}

