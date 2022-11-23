
package com.aloumDaum.user.exception;

import com.aloumDaum.user.model.response.ResponseModel;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestAccessDeniedHandler.class);

    @Override
    public void handle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,
                       final AccessDeniedException accessDeniedException) {
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        final ResponseModel<?> responseModel = new ResponseModel<>(HttpStatus.FORBIDDEN, "Forbidden",
                accessDeniedException.getMessage(), null);
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
