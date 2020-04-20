package net.ys.component;

import net.ys.util.LogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class SystemAspect {

    @Around(value = "execution(* net.ys.controller..*.*(..))", argNames = "pjp")
    public Object validator(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        String methodName = pjp.getTarget().getClass().getSimpleName() + "." + pjp.getSignature().getName();

        String newLine = "\r\n";
        StringBuffer sb = new StringBuffer("SystemAspect------>method:[" + methodName + "]" + newLine);
        for (int i = 0; i < args.length; i++) {
            sb.append(i + "------>" + args[i] + newLine);
        }
        LogUtil.debug(sb.toString());

        long start = System.currentTimeMillis();
        Object obj = pjp.proceed(args);
        long useTime = System.currentTimeMillis() - start;
        LogUtil.debug("SystemAspect------>method:[" + methodName + "]\t use time:[" + useTime + "ms]");
        return obj;
    }
}