package com.mishes.pago.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RUTValidation.class)
public @interface RUT {

    String message() default "Formato de RUT no valido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
