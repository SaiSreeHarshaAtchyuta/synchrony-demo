package com.synchroncydemo.grocery.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingPerformanceAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingPerformanceAspect.class);

    @Around("@annotation(org.springframework.cache.annotation.Cacheable)")
    public Object logCacheMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        logger.info("Method executed in {} ms", elapsedTime);
        return result;
    }
}
