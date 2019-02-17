package com.kuvshinov.bank.controllers;

import com.kuvshinov.bank.dto.AccountsDto;
import com.kuvshinov.bank.dto.AmountDto;
import com.kuvshinov.bank.models.Account;
import com.kuvshinov.bank.services.AccountService;
import com.kuvshinov.bank.services.RequestParserService;
import com.kuvshinov.bank.services.ValidationService;
import com.kuvshinov.http.server.HttpRequest;
import com.kuvshinov.http.server.HttpStatus;
import com.kuvshinov.http.server.ResponseEntity;
import com.kuvshinov.http.server.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountControllerTest {

    @Mock
    private ValidationService validationService;

    @Mock
    private AccountService accountService;

    @Mock
    private RequestParserService requestParserService;

    @Mock
    private HttpRequest request;

    @InjectMocks
    private AccountController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldReturnAccountList() {
        String path = "/accounts";
        List<Account> accounts = Collections.emptyList();
        when(request.getPath())
                .thenReturn(path);
        when(requestParserService.parseId(path))
                .thenReturn(Optional.empty());
        when(accountService.getAccounts())
                .thenReturn(accounts);

        ResponseEntity response = controller.doGet(request);

        verify(accountService, times(1)).getAccounts();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        AccountsDto dto = (AccountsDto) response.getBody();
        assertNotNull(dto);
        assertEquals(accounts, dto.getAccounts());
    }

    @Test
    void shouldReturnSingleAccount() {
        String path = "/accounts/1";
        Integer id = 1;
        Account account = new Account();
        account.setId(id);
        when(request.getPath()).thenReturn(path);
        when(requestParserService.parseId(path)).thenReturn(Optional.of(id));
        when(accountService.getAccount(id)).thenReturn(account);

        ResponseEntity response = controller.doGet(request);

        verify(accountService, times(1)).getAccount(id);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(account, response.getBody());
    }

    @Nested
    class CreateNewAccountTest {

        @Test
        void shouldCreateNewAccount() {
            String body = "{\"amount\": 1}";
            AmountDto dto = new AmountDto();
            dto.setAmount(1.0);
            Account account = new Account();
            when(request.getBody())
                    .thenReturn(body);
            when(requestParserService.parseBody(body, AmountDto.class))
                    .thenReturn(dto);
            when(accountService.createAccount(dto.getAmount()))
                    .thenReturn(account);

            ResponseEntity response = controller.doPost(request);

            verify(validationService, times(1)).validate(dto);
            verify(accountService, times(1)).createAccount(dto.getAmount());
            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatus());
            assertEquals(account, response.getBody());
        }

        @Test
        void shouldThrowExceptionIfAmountIsNegative() {
            String body = "{\"amount\": -1}";
            AmountDto dto = new AmountDto();
            dto.setAmount(-1.0);
            when(request.getBody())
                    .thenReturn(body);
            when(requestParserService.parseBody(body, AmountDto.class))
                    .thenReturn(dto);

            assertThrows(ValidationException.class, () -> controller.doPost(request));
        }

    }

    @Nested
    class UpdateAccountTest {

        @Test
        void shouldUpdateAccount() {
            Integer id = 1;
            String path = "/accounts/1";
            String body = "{}";
            AmountDto dto = new AmountDto();
            Account account = new Account();
            when(request.getPath())
                    .thenReturn(path);
            when(request.getBody())
                    .thenReturn(body);
            when(requestParserService.parseId(path))
                    .thenReturn(Optional.of(id));
            when(requestParserService.parseBody(body, AmountDto.class))
                    .thenReturn(dto);
            when(accountService.updateAccount(id, dto.getAmount()))
                    .thenReturn(account);

            ResponseEntity response = controller.doPut(request);

            verify(validationService, times(1)).validate(dto);
            verify(accountService, times(1)).updateAccount(id, dto.getAmount());
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatus());
            assertEquals(account, response.getBody());
        }

        @Test
        void shouldReturnMethodNotAllowedIfIdNotPresent() {
            String path = "/accounts";
            when(request.getPath())
                    .thenReturn(path);
            when(requestParserService.parseId(path))
                    .thenReturn(Optional.empty());

            ResponseEntity response = controller.doPut(request);

            assertNotNull(response);
            assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatus());
            assertNull(response.getBody());
        }
    }

    @Nested
    class DeleteAccountTest {

        @Test
        void shouldDeleteAccount() {
            Integer id = 1;
            String path = "/accounts/1";
            when(request.getPath())
                    .thenReturn(path);
            when(requestParserService.parseId(path))
                    .thenReturn(Optional.of(id));

            ResponseEntity response = controller.doDelete(request);

            verify(accountService, times(1)).deleteAccount(id);
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatus());
            assertNull(response.getBody());
        }

        @Test
        void shouldReturnMethodNotAllowedIfIdNotPresent() {
            String path = "/accounts";
            when(request.getPath())
                    .thenReturn(path);
            when(requestParserService.parseId(path))
                    .thenReturn(Optional.empty());

            ResponseEntity response = controller.doDelete(request);

            assertNotNull(response);
            assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatus());
            assertNull(response.getBody());
        }
    }

}