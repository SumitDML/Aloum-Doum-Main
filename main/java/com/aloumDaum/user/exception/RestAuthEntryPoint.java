
package com.aloumDaum.user.exception;

import com.aloumDaum.user.model.response.ResponseModel;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

public class RestAuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestAuthEntryPoint.class);

    @Override
    public void commence(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,
                         final AuthenticationException authException) {
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        final ResponseModel<?> responseModel = new ResponseModel<>(HttpStatus.UNAUTHORIZED, "Unauthorized",
                authException.getMessage(), null);
        try (OutputStream out = httpServletResponse.getOutputStream()) {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            mapper.setSerializationInclusion(Include.NON_NULL);
            mapper.writeValue(out, responseModel);
            out.flush();
        } catch (Exception exception) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Exception while writing response : " + exception);
            }
        }
    }
}
