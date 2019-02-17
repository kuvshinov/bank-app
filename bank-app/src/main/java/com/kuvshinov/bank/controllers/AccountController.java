package com.kuvshinov.bank.controllers;

import com.kuvshinov.bank.dto.AccountsDto;
import com.kuvshinov.bank.dto.AmountDto;
import com.kuvshinov.bank.models.Account;
import com.kuvshinov.bank.services.AccountService;
import com.kuvshinov.bank.services.RequestParserService;
import com.kuvshinov.bank.services.ValidationService;
import com.kuvshinov.http.server.HttpRequest;
import com.kuvshinov.http.server.HttpStatus;
import com.kuvshinov.http.server.RequestHandler;
import com.kuvshinov.http.server.ResponseEntity;
import com.kuvshinov.http.server.exceptions.ValidationException;

import java.util.Objects;
import java.util.Optional;

/**
 * Handle requests to:
 *
 * GET /accounts            - return all accounts
 * POST /accounts           - create new account
 * GET /accounts/{id}       - return account
 * PUT /accounts/{id}       - add or remove some money
 * DELETE /accounts/{id}    - delete account
 *
 * @author Sergei Kuvshinov
 * @see RequestHandler
 */
public class AccountController implements RequestHandler {

    private final RequestParserService requestParserService;
    private final ValidationService validationService;
    private final AccountService accountService;

    public AccountController(RequestParserService requestParserService,
                             ValidationService validationService,
                             AccountService accountService) {
        Objects.requireNonNull(requestParserService);
        Objects.requireNonNull(validationService);
        Objects.requireNonNull(accountService);
        this.requestParserService = requestParserService;
        this.validationService = validationService;
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<?> doGet(HttpRequest request) {
        Optional<Integer> id = requestParserService.parseId(request.getPath());
        if (id.isPresent()) {
            return ResponseEntity.ok(accountService.getAccount(id.get()));
        }

        AccountsDto dto = new AccountsDto(accountService.getAccounts());
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<?> doPost(HttpRequest request) {
        AmountDto dto = requestParserService.parseBody(request.getBody(), AmountDto.class);
        validationService.validate(dto);
        if (dto.getAmount() < 0)
            throw new ValidationException("");

        ResponseEntity<Account> response = new ResponseEntity<>(HttpStatus.CREATED);
        response.setBody(accountService.createAccount(dto.getAmount()));
        return response;
    }

    @Override
    public ResponseEntity<?> doPut(HttpRequest request) {
        Optional<Integer> id = requestParserService.parseId(request.getPath());
        if (!id.isPresent()) {
            return RequestHandler.super.doPut(request);
        }

        AmountDto dto = requestParserService.parseBody(request.getBody(), AmountDto.class);
        validationService.validate(dto);

        Account account = accountService.updateAccount(id.get(), dto.getAmount());
        return ResponseEntity.ok(account);
    }

    @Override
    public ResponseEntity<?> doDelete(HttpRequest request) {
        Optional<Integer> id = requestParserService.parseId(request.getPath());
        if (!id.isPresent()) {
            return RequestHandler.super.doDelete(request);
        }
        accountService.deleteAccount(id.get());
        return ResponseEntity.ok();
    }

}
