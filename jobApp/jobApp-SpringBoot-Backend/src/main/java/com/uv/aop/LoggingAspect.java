package com.uv.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {
	
	private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
	
	@Before("execution(* com.uv.service.JobService.getJob(..))")
	public void logMethodCall(JoinPoint jp) {
		logger.info("method called " + jp.getSignature().getName());
	}
	
	@After("execution(* com.uv.service.JobService.getJob(..))")
	public void logMethodExecuted(JoinPoint jp) {
		logger.info("method Executed " + jp.getSignature().getName());
	}
}
