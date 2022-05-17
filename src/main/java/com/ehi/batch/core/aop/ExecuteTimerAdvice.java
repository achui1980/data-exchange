package com.ehi.batch.core.aop;

import com.ehi.batch.core.exception.SSHException;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author portz
 * @date 05/11/2022 17:22
 */
@Aspect
@Component
@Order(100)
@Slf4j
public class ExecuteTimerAdvice {
    @Pointcut(value = "@annotation(com.ehi.batch.core.annotation.ExecuteTimer)")
    public void executeTimer() {

    }

    @Before("executeTimer()")
    public void beforeAdvice(JoinPoint joinPoint) {
    }

    @After("executeTimer()")
    public void afterAdvice(JoinPoint joinPoint) {
    }

    @Around("executeTimer()")
    public void aroundAdvice(ProceedingJoinPoint proceedingJoinPoint) {
        Stopwatch g_sw = Stopwatch.createUnstarted();
        g_sw.start();
        String className = proceedingJoinPoint.getTarget().getClass().getName();
        String method = proceedingJoinPoint.getSignature().getName();
        Object[] args = proceedingJoinPoint.getArgs();
        try {
            proceedingJoinPoint.proceed(args);
        } catch (Throwable t) {
            //log.error("executeTimer aop error", t);
            throw new SSHException("error", t);
        } finally {
            g_sw.stop();
            log.info("method: {}.{} complete in {}", className, method, g_sw);
        }

    }

}

