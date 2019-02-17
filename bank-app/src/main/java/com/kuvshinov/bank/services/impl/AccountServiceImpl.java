package com.kuvshinov.bank.services.impl;

import com.kuvshinov.bank.services.AccountService;
import com.kuvshinov.http.server.exceptions.NotFoundException;
import com.kuvshinov.bank.dao.AccountDao;
import com.kuvshinov.bank.models.Account;
import com.kuvshinov.http.server.exceptions.ValidationException;

import java.util.List;

/**
 * InMemory implementation of {@link AccountService}.
 * Thread safe.
 *
 * @author Sergei Kuvshinov
 */
public class AccountServiceImpl implements AccountService {

    private static final String AMOUNT_IS_EMPTY_MESSAGE = "Amount cannot be empty";
    private static final String AMOUNT_IS_NEGATIVE = "Amount cannot be negative";

    private final AccountDao accountDao;

    public AccountServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account getAccount(Integer id) {
        return accountDao.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Cannot find account with id %d", id)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Account> getAccounts() {
        return accountDao.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account createAccount(Double amount) {
        Account account = new Account();
        account.setAmount(amount);
        return accountDao.save(account);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account updateAccount(Integer accountId, Double amount) {
        if (amount == null) {
            throw new ValidationException(AMOUNT_IS_EMPTY_MESSAGE);
        }
        Account account = getAccount(accountId);
        synchronized (account) {
            if (amount > 0) {
                account.inc(amount);
            } else {
                if (account.getAmount() + amount < 0) {
                    throw new ValidationException("Operation not possible. Check the balance");
                }
                account.dec(-amount);
            }
        }
        return accountDao.save(account);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAccount(Integer id) {
        Account account = getAccount(id);
        accountDao.delete(account);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transfer(Integer accountFrom, Integer accountTo, Double amount) {
        if (amount == null) {
            throw new ValidationException(AMOUNT_IS_EMPTY_MESSAGE);
        }
        if (amount < 0) {
            throw new ValidationException(AMOUNT_IS_NEGATIVE);
        }
        Account from = getAccount(accountFrom);
        Account to = getAccount(accountTo);
        Object lock1 = accountFrom > accountTo ? from : to;
        Object lock2 = accountFrom > accountTo ? to : from;
        synchronized (lock1) {
            synchronized (lock2) {
                if (from.getAmount() < amount) {
                    throw new ValidationException("Operation not possible. Check the balance");
                }
                from.dec(amount);
                to.inc(amount);
                accountDao.save(from);
                accountDao.save(to);
            }
        }
    }

}
