package com.demo.demoproject.logging;

import java.util.Arrays;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.demo.demoproject.util.SecurityUtils;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RequestLoggingAspect {
    private static final Logger authLogger = LoggerFactory.getLogger("AUTH_LOGGER");
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOGGER");
    private static final Logger appLogger = LoggerFactory.getLogger(RequestLoggingAspect.class);
    private static final Logger errorLogger = LoggerFactory.getLogger("ERROR_LOGGER");

    @Around("execution(public * com.demo.demoproject.controller..*(..))")
    public Object aroundController(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = sra != null ? sra.getRequest() : null;

        String method = req != null ? req.getMethod() : "N/A";
        String path = req != null ? req.getRequestURI() : "N/A";
        String user = SecurityUtils.currentUsername();

        Object[] sanitizedArgs = sanitizeArgs(pjp.getArgs());

        if(path != null && path.startsWith("/auth")) {
            authLogger.info(
                "[AUTH REQUEST] user={} method={} path={} args={}",
                user, method, path, Arrays.toString(sanitizedArgs)
            );
        } else {
            appLogger.info("[REQUEST] user={} method={} path={} controllerMethod={} args={}",
                    user, method, path, pjp.getSignature().toShortString(), Arrays.toString(sanitizedArgs));
        }

        Object result;
        try {
            result = pjp.proceed();
        } catch(Throwable ex) {
            errorLogger.error("[EXCEPTION] user={} method={} path={} controllerMethod={} error={}",
                user, method, path, pjp.getSignature().toShortString(), ex.toString(), ex);
            auditLogger.info("[AUDIT-FAIL] user={} method={} path={} controllerMethod={} args= {}",
                user, method, path, pjp.getSignature().toShortString(), Arrays.toString(sanitizedArgs));
            throw ex;
        }

        Object sanitizedResult = sanitizeResult(result);

        if("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            MethodSignature sig = (MethodSignature) pjp.getSignature();
            String controllerMethod  =sig.getDeclaringType().getSimpleName() + "." + sig.getName();
            auditLogger.info("[AUDIT] user={} method={} controllerMethods={} path={} args={} result={}",
                user, method, controllerMethod, path, Arrays.toString(sanitizedArgs), sanitizedResult
            );
        }

        return result;
    }

    private Object[] sanitizeArgs(Object[] args) {
        if (args == null) return null;
        return Arrays.stream(args).map(this::sanitizeObject).toArray();
    }

    private Object sanitizeResult(Object result) {
        return sanitizeObject(result);
    }

    private Object sanitizeObject(Object o) {
        if (o == null) return null;
        String fullName = o.getClass().getName();
        String simple = o.getClass().getSimpleName();

        if (fullName.contains("AuthController$LoginRequest") || "LoginRequest".equals(simple)
                || fullName.contains("AuthController$SignupRequest") || "SignupRequest".equals(simple)) {
            try {
                Method mUser = o.getClass().getMethod("username");
                String username = (String) mUser.invoke(o);
                return simple + "[username=" + username + ", password=***]";
            } catch (Exception ex) {
                return simple + "[REDACTED]";
            }
        }

        if (fullName.contains("AuthController$LoginResponse") || "LoginResponse".equals(simple)) {
            return simple + "[token=***]";
        }

        try {
            Field[] fields = o.getClass().getDeclaredFields();
            if (fields.length == 0) return o;
            StringBuilder sb = new StringBuilder();
            sb.append(simple).append("[");
            boolean first = true;
            for (Field f : fields) {
                f.setAccessible(true);
                if (!first) sb.append(", ");
                first = false;
                String name = f.getName();
                Object val;
                try { val = f.get(o); } catch (Exception ex) { val = "N/A"; }
                if ("password".equalsIgnoreCase(name) || "token".equalsIgnoreCase(name)) {
                    sb.append(name).append("=***");
                } else {
                    sb.append(name).append("=").append(val);
                }
            }
            sb.append("]");
            return sb.toString();
        } catch (Exception ex) {
            return o;
        }
    }
}
