package com.sprint.mission.discodeit.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class RequiredMultipartFileIfPresentValidator implements ConstraintValidator<RequiredMultipartFileIfPresent, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return !value.isEmpty();
    }
}
