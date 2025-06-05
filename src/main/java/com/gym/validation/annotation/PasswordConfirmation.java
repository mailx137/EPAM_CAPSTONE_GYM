package com.gym.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gym.validation.impl.PasswordConfirmationValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = {PasswordConfirmationValidator.class})
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordConfirmation {
    String message() default "{validation.password_confirmation.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}