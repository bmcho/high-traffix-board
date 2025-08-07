package com.bmcho.hightrafficboard.aspect;

import com.bmcho.hightrafficboard.anntation.PasswordEncryption;
import com.bmcho.hightrafficboard.exception.PasswordSecurityException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Modifier;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class PasswordEncryptionAspect {

    private final PasswordEncoder passwordEncoder;

    @Around("execution(* com.bmcho.hightrafficboard.controller..*.*(..))")
    public Object passwordEncryptionAspect(ProceedingJoinPoint joinPoint) throws Throwable{

        Arrays.stream(joinPoint.getArgs())
            .forEach(this::findEncryption);

        return joinPoint.proceed();
    }

    public void findEncryption(Object o) {
        if (ObjectUtils.isEmpty(o)) {
            return;
        }

        FieldUtils.getAllFieldsList(o.getClass())
            .stream()
            .filter(filter -> !(Modifier.isFinal(filter.getModifiers()) && Modifier.isStatic(filter.getModifiers())))
            .forEach(field -> {
                try{
                    boolean target = field.isAnnotationPresent(PasswordEncryption.class);
                    if (!target) {
                        return;
                    }

                    Object encryptionField = FieldUtils.readField(field, o, true);
                    if (!(encryptionField instanceof String)) {
                        return;
                    }

                    String encrypted = passwordEncoder.encode((String) encryptionField);
                    FieldUtils.writeField(field, o, encrypted);

                } catch (Exception e) {
                    throw new PasswordSecurityException.PasswordEncryptionException();
                }
            });

    }

}
