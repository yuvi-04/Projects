package com.demo.demoproject.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.demo.demoproject.util.SecurityUtils;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class ExceptionLoggingAspect {
    private static final Logger errorLogger = LoggerFactory.getLogger("ERROR_LOGGER");

    @AfterThrowing(pointcut = "execution(* com.demo.demoproject..*(..))", throwing = "ex")
    public void logException(JoinPoint jp, Throwable ex) {
        String user = SecurityUtils.currentUsername();
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = sra != null ? sra.getRequest() : null;
        String path = req != null ? req.getRequestURI() : "N/A";
        errorLogger.error("[EX] user={} path={} method={} location={} error={}",
                user,
                path,
                jp.getSignature().toShortString(),
                jp.getSourceLocation(),
                ex.toString(), ex);
    }
}
