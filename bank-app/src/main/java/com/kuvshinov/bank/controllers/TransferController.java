package com.kuvshinov.bank.controllers;

import com.kuvshinov.bank.dto.TransferDto;
import com.kuvshinov.bank.services.AccountService;
import com.kuvshinov.bank.services.RequestParserService;
import com.kuvshinov.bank.services.ValidationService;
import com.kuvshinov.http.server.HttpRequest;
import com.kuvshinov.http.server.RequestHandler;
import com.kuvshinov.http.server.ResponseEntity;

import java.util.Objects;

/**
 * Handle requests to:
 *
 * PUT /accounts/transfer - transfer money from one account to another
 *
 * @author Sergei Kuvshinov
 * @see RequestHandler
 */
public class TransferController implements RequestHandler {

    private final RequestParserService requestParserService;
    private final ValidationService validationService;
    private final AccountService accountService;

    public TransferController(RequestParserService requestParserService,
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
    public ResponseEntity<?> doPut(HttpRequest request) {
        TransferDto dto = requestParserService.parseBody(request.getBody(), TransferDto.class);
        validationService.validate(dto);
        accountService.transfer(dto.getFrom(), dto.getTo(), dto.getAmount());
        return ResponseEntity.ok();
    }
}
