package com.kuvshinov.bank.services.impl;

import com.kuvshinov.bank.dto.AmountDto;
import com.kuvshinov.http.server.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationServiceImplTest {

    private ValidationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ValidationServiceImpl();
    }

    @Test
    void shouldThrowExceptionIfFieldIsNull() {
        AmountDto dto = new AmountDto();

        assertThrows(ValidationException.class, () -> service.validate(dto));
    }

}