package com.sprint.mission.discodeit.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID;

public class UUIDValidator implements ConstraintValidator<ValidUUID, UUID> {

    @Override
    public boolean isValid(UUID value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        try {
            String id = value.toString();
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }
}
