package ch.usi.si.seart.server.aspect;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Aspect
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfigurationServiceSynchronizationAspect {

    ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Around("execution(* ch.usi.si.seart.server.service.ConfigurationService.get(..))")
    public Object synchronizeGet(ProceedingJoinPoint joinPoint) throws Throwable {
        return synchronize(joinPoint, readWriteLock.readLock());
    }

    @Around("execution(* ch.usi.si.seart.server.service.ConfigurationService.update(..))")
    public Object synchronizeUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        return synchronize(joinPoint, readWriteLock.writeLock());
    }

    private Object synchronize(ProceedingJoinPoint joinPoint, Lock lock) throws Throwable {
        lock.lock();
        try {
            return joinPoint.proceed();
        } finally {
            lock.unlock();
        }
    }
}
