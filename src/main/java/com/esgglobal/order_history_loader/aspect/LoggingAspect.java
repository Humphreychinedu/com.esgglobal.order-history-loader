package com.esgglobal.order_history_loader.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class
LoggingAspect {

    // Pointcut for all methods in controllers
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerLayer() {}

    // Pointcut for all methods in the service layer
    @Pointcut("execution(* com.esgglobal.order_history_loader.service.impl..*(..))")
    public void serviceLayer() {}

    // Combine both controller and service layers
    @Pointcut("controllerLayer() || serviceLayer()")
    public void applicationLayer() {}

    @Before("applicationLayer()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Entering: {} with arguments = {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "applicationLayer()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        log.info("Exiting: {} with result = {}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(pointcut = "applicationLayer()", throwing = "e")
    public void logException(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {} with cause = '{}'", joinPoint.getSignature(), e.getMessage(), e);
    }

}
