package com.sprint.mission.discodeit.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class RequiredListIfPresentValidator implements ConstraintValidator<RequiredListIfPresent, List<?>> {
    @Override
    public boolean isValid(List<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return !value.isEmpty();
    }
}
