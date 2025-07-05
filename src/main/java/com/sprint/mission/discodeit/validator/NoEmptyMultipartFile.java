package com.sprint.mission.discodeit.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Constraint(validatedBy = NoEmptyMultipartFileValidator.class)
public @interface NoEmptyMultipartFile {
    String message() default "파일이 null이거나 비어있을 수 없습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
