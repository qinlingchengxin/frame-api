package net.ys.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
public class AnnotationAspect {

    /**
     * 使用注解方式需要在xml中添加以下配置以开启自动代理
     * <aop:aspectj-autoproxy/>
     */

    //定义切点
    @Pointcut("execution(public * net.ys.service..*(..))")
    public void pt() {
    }

    @Before("pt()")
    public void beforeMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        System.out.println("The method " + methodName + " begins with " + Arrays.asList(args));
    }

    /**
     * 无论该方法是否出现异常
     */
    @After("pt()")
    public void afterMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        System.out.println("The method " + methodName + " ends with " + Arrays.asList(args));
    }

    /**
     * 方法正常结束后执行的代码
     * 返回通知是可以访问到方法的返回值的
     */
    @AfterReturning(value = "pt()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("The method " + methodName + " return with " + result);
    }

    /**
     * 可以访问到异常对象，可以指定在出现特定异常时在执行通知代码
     */
    @AfterThrowing(value = "pt()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("The method " + methodName + " occurs exception: " + ex);
    }

    /**
     * 返回值即为目标方法的返回值，开启后返回通知无法获取返回值
     */
    @Around("pt()")
    public Object aroundMethod(ProceedingJoinPoint pjd) {
        Object result;
        String methodName = pjd.getSignature().getName();
        //执行目标方法
        try {
            //前置通知
            System.out.println("The method " + methodName + " begins with " + Arrays.asList(pjd.getArgs()));
            result = pjd.proceed();
            //返回通知
            System.out.println("The method " + methodName + " ends with " + Arrays.asList(pjd.getArgs()));
        } catch (Throwable e) {
            //异常通知
            System.out.println("The method " + methodName + " occurs expection : " + e);
            throw new RuntimeException(e);
        }
        //后置通知
        System.out.println("The method " + methodName + " ends");
        return result;
    }
}