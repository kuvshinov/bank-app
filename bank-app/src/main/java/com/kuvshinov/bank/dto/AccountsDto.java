package com.kuvshinov.bank.dto;

import com.kuvshinov.bank.models.Account;

import java.util.List;

public class AccountsDto {

    private List<Account> accounts;

    public AccountsDto(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}
