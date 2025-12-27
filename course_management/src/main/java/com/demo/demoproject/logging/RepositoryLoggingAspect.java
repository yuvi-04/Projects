package com.demo.demoproject.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.demo.demoproject.util.SecurityUtils;

@Aspect
@Component
public class RepositoryLoggingAspect {
    private static final Logger dbLogger = LoggerFactory.getLogger("DB_LOGGER");

    @AfterReturning("execution(* com.demo.demoproject.repo..*.save(..)) || execution(* com.demo.demoproject.repo..*.delete*(..)) || execution(* com.demo.demoproject.repo..*.remove*(..))")
    public void afterRepoReturning(JoinPoint jp) {
        String user = SecurityUtils.currentUsername();
        String method = jp.getSignature().toShortString();
        Object[] args = jp.getArgs();
        dbLogger.debug("[DB] user={} repoMethod={} args={}", user, method, args);
    }

    @After("execution(* com.demo.demoproject.repo..*(..))")
    public void afterAnyRepo(JoinPoint jp) {
        dbLogger.trace("[DB TRACE] method={}", jp.getSignature().toShortString());
    }
}
