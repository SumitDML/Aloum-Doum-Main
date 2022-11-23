package com.aloumDaum.user.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseModel<T> {

    private final Integer status;

    private final String message;

    private final String details;

    private final T data;

    private List<String> errors;


    public ResponseModel(final HttpStatus httpStatus, final String message, final String details, final T data) {
        this(Integer.valueOf(httpStatus.value()), message, details, data);
    }

    public ResponseModel(final Integer httpStatus, final String message, final String details, final T data) {
        this.status = httpStatus;
        this.message = message;
        this.details = details;
        this.data = data;
    }


    public ResponseModel(final HttpStatus httpStatus, final String message, final String details, final T data, final List<String> errors) {
        this(Integer.valueOf(httpStatus.value()), message, details, data);
        this.errors = errors;
    }

    public ResponseModel(final Integer httpStatus, final String message, final String details, final T data, final List<String> errors) {
        this(httpStatus, message, details, data);
        this.errors = errors;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public T getResponse() {
        return data;
    }

    public List<String> getErrors() {
        return errors;
    }
}
