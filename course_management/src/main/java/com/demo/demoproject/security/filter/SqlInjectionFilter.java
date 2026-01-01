package com.demo.demoproject.security.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SqlInjectionFilter extends OncePerRequestFilter {

    private static final Pattern SQL_PATTERN = Pattern.compile(
        "('.+--)|(--)|(%7C)|(;)|(%3B)|(/\\*.*\\*/)|\\b(select|update|delete|insert|drop|alter|truncate)\\b",
        Pattern.CASE_INSENSITIVE
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        for(String[] values: request.getParameterMap().values()) {
            for(String value: values) {
                if(SQL_PATTERN.matcher(value).find()) {
                    response.sendError(400, "Malicious SQL input detected");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}
