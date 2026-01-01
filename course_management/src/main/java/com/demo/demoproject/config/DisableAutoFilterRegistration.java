package com.demo.demoproject.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.demo.demoproject.security.filter.GlobalRateLimiter;
import com.demo.demoproject.security.filter.SqlInjectionFilter;

@Configuration
public class DisableAutoFilterRegistration {

    @Bean
    public FilterRegistrationBean<SqlInjectionFilter> disableSqlFilter(SqlInjectionFilter filter) {
        FilterRegistrationBean<SqlInjectionFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setEnabled(false);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<GlobalRateLimiter> disableRateLimiter(GlobalRateLimiter filter) {
        FilterRegistrationBean<GlobalRateLimiter> reg = new FilterRegistrationBean<>(filter);
        reg.setEnabled(false);
        return reg;
    }
}
