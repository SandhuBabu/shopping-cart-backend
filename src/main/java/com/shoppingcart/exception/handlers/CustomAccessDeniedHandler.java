package com.shoppingcart.exception.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        Map<String, Object> errMap = new HashMap<>();
        errMap.put("message", accessDeniedException.getMessage());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonError = objectMapper.writeValueAsString(errMap);

        // Set the content type to application/json
        response.setContentType("application/json");
        response.setStatus(401);

        // Write the JSON response to the HttpServletResponse
        PrintWriter out = response.getWriter();
        out.print(jsonError);
        out.flush();
    }
}
