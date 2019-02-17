package com.kuvshinov.bank.controllers;

import com.kuvshinov.bank.dto.TransferDto;
import com.kuvshinov.bank.services.AccountService;
import com.kuvshinov.bank.services.RequestParserService;
import com.kuvshinov.bank.services.ValidationService;
import com.kuvshinov.http.server.HttpRequest;
import com.kuvshinov.http.server.HttpStatus;
import com.kuvshinov.http.server.ResponseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransferControllerTest {

    @Mock
    private ValidationService validationService;

    @Mock
    private AccountService accountService;

    @Mock
    private RequestParserService requestParserService;

    @Mock
    private HttpRequest request;

    @InjectMocks
    private TransferController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldTransferMoney() {
        String body = "";
        TransferDto dto = new TransferDto();
        when(request.getBody())
                .thenReturn(body);
        when(requestParserService.parseBody(body, TransferDto.class))
                .thenReturn(dto);

        ResponseEntity response = controller.doPut(request);

        verify(validationService, times(1)).validate(dto);
        verify(accountService, times(1)).transfer(dto.getFrom(), dto.getTo(), dto.getAmount());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNull(response.getBody());
    }
}