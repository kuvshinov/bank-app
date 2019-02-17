package com.kuvshinov.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuvshinov.bank.services.AccountService;
import com.kuvshinov.bank.services.RequestParserService;
import com.kuvshinov.bank.services.ValidationService;
import com.kuvshinov.bank.services.impl.AccountServiceImpl;
import com.kuvshinov.bank.services.impl.RequestParserServiceImpl;
import com.kuvshinov.bank.services.impl.ValidationServiceImpl;
import com.kuvshinov.http.server.Server;
import com.kuvshinov.bank.controllers.AccountController;
import com.kuvshinov.bank.controllers.TransferController;
import com.kuvshinov.bank.dao.AccountDao;
import com.kuvshinov.bank.dao.impl.InMemoryAccountDaoImpl;

public class Bootstrap {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Cannot parse port value. The default value will be used");
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        AccountDao accountDao = new InMemoryAccountDaoImpl();
        AccountService accountService = new AccountServiceImpl(accountDao);
        ValidationService validationService = new ValidationServiceImpl();
        RequestParserService requestParserService = new RequestParserServiceImpl(objectMapper);
        AccountController accountController = new AccountController(requestParserService, validationService, accountService);
        TransferController transferController = new TransferController(requestParserService, validationService, accountService);
        Server.bootstrap()
                .port(port)
                .addRequestHandler("/accounts", accountController)
                .addRequestHandler("/accounts/transfer", transferController)
                .start();
    }
}
