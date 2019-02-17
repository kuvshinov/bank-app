package com.kuvshinov.bank.services.impl;

import com.kuvshinov.bank.services.ValidationService;
import com.kuvshinov.bank.validation.NotNull;
import com.kuvshinov.http.server.exceptions.ValidationException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ValidationService}.
 *
 * @author Sergei Kuvshinov
 */
public class ValidationServiceImpl implements ValidationService {

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        List<String> errors = new ArrayList<>();
        for (Field field : fields) {
            if (field.getAnnotation(NotNull.class) != null) {
                field.setAccessible(true);
                if (isNull(o, field)) {
                    errors.add(String.format("field %s cannot be null", field.getName()));
                }
            }
        }
        if (!errors.isEmpty()) {
            String message = errors.stream().collect(Collectors.joining(","));
            throw new ValidationException(message);
        }
    }

    private boolean isNull(Object o, Field f) {
        Object value;
        try {
            value = f.get(o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return value == null;
    }

}
