package com.sprint.mission.discodeit.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoBlankIfPresentValidator implements ConstraintValidator<NoBlankIfPresent, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value.trim().isEmpty() || value.isBlank()) {
            return false;
        }
        return true;
    }
}
