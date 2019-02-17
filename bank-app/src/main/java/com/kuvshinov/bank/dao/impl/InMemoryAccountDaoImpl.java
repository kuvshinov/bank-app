package com.kuvshinov.bank.dao.impl;

import com.kuvshinov.bank.dao.AccountDao;
import com.kuvshinov.bank.models.Account;
import com.kuvshinov.http.server.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * InMemory implementation of {@link AccountDao}.
 * Thread save.
 *
 * @author Sergei Kuvshinov
 */
public class InMemoryAccountDaoImpl implements AccountDao {

    private Map<Integer, Account> accounts = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger();

    @Override
    public Account save(Account account) {
        Objects.requireNonNull(account);
        if (account.getId() == null) {
            account.setId(counter.incrementAndGet());
        }
        accounts.put(account.getId(), account);
        return account;
    }

    @Override
    public Optional<Account> findById(Integer id) {
        Objects.requireNonNull(id);
        return Optional.ofNullable(accounts.get(id));
    }

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public void delete(Account account) {
        Objects.requireNonNull(account);
        accounts.remove(account.getId());
    }

}
