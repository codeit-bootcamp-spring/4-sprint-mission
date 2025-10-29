package com.sprint.mission.discodeit.aop;

import com.sprint.mission.discodeit.util.LogParameterFormatter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class MethodLoggingAspect {

    private final ThreadLocal<Long> startTimeHolder = new ThreadLocal<>();

    @Before("execution(* com.sprint.mission.discodeit.service.*.*(..))")
    public void methodCallLogging(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        String parameterStr = LogParameterFormatter.getFormattedParameters(args);

        startTimeHolder.set(System.currentTimeMillis());

        log.info("Method call: (class={}, method={}, parameters=({})", className, methodName, parameterStr);
    }

    @AfterReturning("execution(* com.sprint.mission.discodeit.service.*.*(..))")
    public void methodReturnLogging(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        //실행 시간 계산
        Long startTime = startTimeHolder.get();
        long executionTime = 0;

        if(startTime != null) {
            executionTime = System.currentTimeMillis() - startTime;
            startTimeHolder.remove();
        }

        log.info("Method return: (class : {}, method : {}, executionTime = {}ms)", className, methodName, executionTime);
    }

}
