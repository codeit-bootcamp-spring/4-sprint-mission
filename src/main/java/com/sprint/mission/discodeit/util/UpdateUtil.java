package com.sprint.mission.discodeit.util;

import java.util.Optional;
import java.util.function.Consumer;

public final class UpdateUtil {

    private UpdateUtil() {}

    public static <T> boolean updateIfChanged(T newValue, T oldValue, Consumer<T> setter, boolean anyValueUpdated) {

        return Optional.ofNullable(newValue)
                .filter(n -> !n.equals(oldValue))
                .map(n -> {
                    setter.accept(n);
                    return true;
                }).orElse(anyValueUpdated);
    }
}
