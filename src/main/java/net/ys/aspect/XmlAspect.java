package net.ys.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.springframework.stereotype.Component;

/**
 * User: NMY
 * Date: 19-4-7
 */
@Component
public class XmlAspect {

    //前置通知
    public void before(JoinPoint jp) {
        Object[] args = jp.getArgs();//获取参数
        System.out.println("before-----" + args.length);
        Class clz = jp.getTarget().getClass();//类
        Signature signature = jp.getSignature(); // 通过JoinPoint对象获取更多信息
        String name = signature.getName();//方法
        System.out.println("1 -- before...[" + clz + "]...[" + name + "]...");
    }

    //返回通知，如果开起around，则retVal无法获取
    public void afterReturn(JoinPoint jp, Object retVal) {
        Class clz = jp.getTarget().getClass();
        Signature signature = jp.getSignature();
        String name = signature.getName();
        System.out.println("afterReturn...[" + clz + "]...[" + name + "]...[" + retVal + "]...");
    }

    //异常通知
    public void afterThrow(JoinPoint jp, Exception ex) {
        System.out.println("afterThrow-------" + ex.getMessage());
    }

    //后置通知
    public void after(JoinPoint jp) {
        System.out.println("after-------");
    }

    //环绕通知
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around begin  ");
        Object result = joinPoint.proceed();
        System.out.println("around after  " + result);
    }
}
