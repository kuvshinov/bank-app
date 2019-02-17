package com.kuvshinov.bank.services;

import com.kuvshinov.bank.models.Account;

import java.util.List;

/**
 * Provide actions for {@link Account}.
 *
 * @author Sergei Kuvshinov.
 */
public interface AccountService {

    /**
     * Get account by id.
     *
     * @param id - account's id.
     * @return {@link Account}
     */
    Account getAccount(Integer id);

    /**
     * Get all accounts.
     *
     * @return list of {@link Account}; not null.
     */
    List<Account> getAccounts();

    /**
     * Create new account.
     *
     * @param amount - the balance of new account, cannot be null.
     * @return created {@link Account} with updated id.
     */
    Account createAccount(Double amount);

    /**
     * Update account's balance.
     *
     * @param accountId - id of account.
     * @param amount - count that should be added or removed from account.
     * @return updated {@link Account}.
     */
    Account updateAccount(Integer accountId, Double amount);

    /**
     * Delete existing account.
     *
     * @param id - account's id.
     */
    void deleteAccount(Integer id);

    /**
     * Transfer money from one account to another.
     *
     * @param accountFrom - account's id for withdraw.
     * @param accountTo - account's id for deposit.
     * @param amount - positive value.
     * @throws com.kuvshinov.http.server.exceptions.ValidationException if amount is null or negative value.
     */
    void transfer(Integer accountFrom, Integer accountTo, Double amount);
}
