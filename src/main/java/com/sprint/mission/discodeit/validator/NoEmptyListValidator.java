package com.sprint.mission.discodeit.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class NoEmptyListValidator implements ConstraintValidator<NoEmptyList, List<?>> {

    @Override
    public boolean isValid(List<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !value.isEmpty();
    }
}
