package com.demo.demoproject.security.filter;

import java.io.IOException;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class GlobalRateLimiter extends OncePerRequestFilter {

    private final RateLimiter rateLimiter;

    public GlobalRateLimiter(RateLimiterRegistry registry) {
        this.rateLimiter = registry.rateLimiter("globalLimiter");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Supplier<Boolean> supplier = RateLimiter.decorateSupplier(rateLimiter, () -> true);
        try {
            supplier.get();
        } catch(RequestNotPermitted ex) {
            response.setStatus(429);
            response.getWriter().write("Too Many Request");
            return;
        }
        filterChain.doFilter(request, response);
    }

}
