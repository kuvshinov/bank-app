package com.kuvshinov.bank.dao.impl;

import com.kuvshinov.bank.dao.AccountDao;
import com.kuvshinov.bank.models.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAccountDaoImplTest {

    private AccountDao accountDao;

    @BeforeEach
    void setUp() {
        accountDao = new InMemoryAccountDaoImpl();
    }

    @Test
    void shouldFindAll() {
        assertNotNull(accountDao.findAll());
    }

    @Nested
    class SaveAccountTest {

        @Test
        void shouldSaveNewAccount() {
            Account account = new Account();

            accountDao.save(account);

            assertNotNull(account.getId());
        }

        @Test
        void shouldThrowExceptionIfAccountIsNull() {
            assertThrows(NullPointerException.class, () -> accountDao.save(null));
        }
    }

    @Nested
    class FindByIdTest {

        @Test
        void shouldFindAccountById() {
            Integer id = 1;
            Account account = new Account();
            account.setId(id);
            accountDao.save(account);

            Optional<Account> result = accountDao.findById(id);

            assertNotNull(result);
            assertTrue(result.isPresent());
            assertEquals(account, result.get());
        }

        @Test
        void shouldReturnEmptyIfAccountNotFound() {
            Integer id = 1;

            Optional<Account> result = accountDao.findById(id);

            assertNotNull(result);
            assertFalse(result.isPresent());
        }

        @Test
        void shouldThrowExceptionIfIdIsNull() {
            assertThrows(NullPointerException.class, () -> accountDao.findById(null));
        }
    }

    @Nested
    class DeleteTest {

        @Test
        void shouldDeleteAccount() {
            Account account = accountDao.save(new Account());

            accountDao.delete(account);

            assertFalse(accountDao.findById(account.getId()).isPresent());
        }

        @Test
        void shouldThrowExceptionIfAccountIsNull() {
            assertThrows(NullPointerException.class, () -> accountDao.delete(null));
        }
    }

    @Test
    void testMultiThreadSave() throws InterruptedException {
        int count1 = 20, count2 = 20;
        Thread createNewFirst = new Thread(() -> {
            for (int i = 0; i < count1; i++) {
                Account account = new Account();
                account.setAmount(i * Math.random());
                accountDao.save(account);
            }
        });
        Thread createNewSecond = new Thread(() -> {
            for (int i = 0; i < count2; i++) {
                Account account = new Account();
                account.setAmount(i * Math.random());
                accountDao.save(account);
            }
        });

        createNewFirst.start();
        createNewSecond.start();

        createNewFirst.join();
        createNewSecond.join();

        List<Account> accounts = accountDao.findAll();

        assertEquals(count1 + count2, accounts.size());

        for (int i = 1; i <= count1 + count2; i++) {
            int finalI = i;
            accounts.stream().map(Account::getId).anyMatch(id -> id == finalI);
        }

    }
}