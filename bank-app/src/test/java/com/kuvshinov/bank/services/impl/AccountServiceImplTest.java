package com.kuvshinov.bank.services.impl;

import com.kuvshinov.bank.dao.AccountDao;
import com.kuvshinov.bank.models.Account;
import com.kuvshinov.http.server.exceptions.NotFoundException;
import com.kuvshinov.http.server.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class AccountServiceImplTest {

    @Mock
    private AccountDao accountDao;

    @InjectMocks
    private AccountServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldReturnAllAccounts() {
        List<Account> accounts = Collections.emptyList();
        when(accountDao.findAll())
                .thenReturn(accounts);

        List<Account> result = service.getAccounts();

        assertEquals(accounts, result);
        verify(accountDao, times(1)).findAll();
    }

    @Test
    void shouldCreateNewAccount() {
        double amount = 1.0;
        ArgumentCaptor<Account> argumentCaptor = ArgumentCaptor.forClass(Account.class);

        service.createAccount(amount);

        verify(accountDao, times(1)).save(argumentCaptor.capture());
        assertEquals(amount, argumentCaptor.getValue().getAmount());
    }

    @Nested
    class GetAccountTest {

        @Test
        void shouldReturnAccount() {
            Integer id = 1;
            Account account = mock(Account.class);
            when(accountDao.findById(1))
                    .thenReturn(Optional.of(account));

            Account result = service.getAccount(id);

            verify(accountDao, times(1)).findById(id);
            assertEquals(account, result);
        }

        @Test
        void shouldThrowExceptionIfAccountNotFound() {
            Integer id = 1;
            when(accountDao.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> service.getAccount(id));
        }
    }

    @Nested
    class UpdateAccountTest {

        @Test
        void shouldIncreaseAccountBalance() {
            int id = 1;
            double amount = 1.0;
            Account account = new Account();
            account.setAmount(0);
            when(accountDao.findById(id))
                    .thenReturn(Optional.of(account));
            when(accountDao.save(account))
                    .thenReturn(account);

            Account result = service.updateAccount(id, amount);

            assertEquals(account, result);
            assertEquals(amount, account.getAmount());
            verify(accountDao, times(1)).save(account);
        }

        @Test
        void shouldDecreaseAccountBalance() {
            Integer id = 1;
            Double amount = -1.0;
            Account account = new Account();
            account.setAmount(2.0);
            when(accountDao.findById(id))
                    .thenReturn(Optional.of(account));
            when(accountDao.save(account))
                    .thenReturn(account);

            Account result = service.updateAccount(id, amount);

            assertEquals(account, result);
            assertEquals(1.0, account.getAmount());
            verify(accountDao, times(1)).save(account);
        }

        @Test
        void shouldThrowExceptionIfAccountHasNotEnoughMoneyForDec() {
            Integer id = 1;
            double amount = -1.0;
            Account account = new Account();
            account.setAmount(0);
            when(accountDao.findById(id))
                    .thenReturn(Optional.of(account));

            assertThrows(ValidationException.class, () -> service.updateAccount(id, amount));
        }

        @Test
        void shouldThrowExceptionIfAccountNotFound() {
            Integer id = 1;
            double amount = -1.0;
            when(accountDao.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> service.updateAccount(id, amount));
        }

        @Test
        void shouldThrowExceptionIfAmountIsNull() {
            Integer id = 1;
            assertThrows(ValidationException.class, () -> service.updateAccount(id, null));
        }
    }

    @Nested
    class DeleteAccountTest {

        @Test
        void shouldDeleteAccount() {
            Integer id = 1;
            Account account = mock(Account.class);
            when(accountDao.findById(id))
                    .thenReturn(Optional.of(account));

            service.deleteAccount(id);

            verify(accountDao, times(1)).findById(id);
            verify(accountDao, times(1)).delete(account);
        }

        @Test
        void shouldThrowExceptionIfAccountNotFound() {
            Integer id = 1;
            when(accountDao.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> service.deleteAccount(id));
        }
    }

    @Nested
    class TransferTest {

        @Test
        void shouldTransferMoney() {
            Integer from = 1;
            Integer to = 2;
            Double amount = 100d;
            Account first = new Account();
            first.setId(from);
            first.setAmount(200d);
            Account second = new Account();
            second.setId(to);
            second.setAmount(100d);
            when(accountDao.findById(from))
                    .thenReturn(Optional.of(first));
            when(accountDao.findById(to))
                    .thenReturn(Optional.of(second));

            service.transfer(from, to, amount);

            assertEquals(100d, first.getAmount());
            assertEquals(200d, second.getAmount());
            verify(accountDao, times(1)).save(first);
            verify(accountDao, times(1)).save(second);
        }

        @Test
        void shouldThrowExceptionIfFromNotFound() {
            Integer from = 1;
            Integer to = 2;
            Double amount = 1.0;
            when(accountDao.findById(from))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> service.transfer(from, to, amount));
        }

        @Test
        void shouldThrowExceptionIfToNotFound() {
            Integer from = 1;
            Integer to = 2;
            Double amount = 1.0;
            when(accountDao.findById(from))
                    .thenReturn(Optional.of(mock(Account.class)));
            when(accountDao.findById(to))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> service.transfer(from, to, amount));
        }

        @Test
        void shouldThrowExceptionIfAmountIsNull() {
            Integer from = 1;
            Integer to = 2;

            assertThrows(ValidationException.class, () -> service.transfer(from, to, null));
        }

        @Test
        void shouldThrowExceptionIfAmountIsNegativeValue() {
            Integer from = 1;
            Integer to = 2;
            Double amount = -2.0;

            assertThrows(ValidationException.class, () -> service.transfer(from, to, amount));
        }

        @Test
        void shouldThrowExceptionIfFromHasNotEnoughMoney() {
            Integer from = 1;
            Integer to = 2;
            Double amount = 1.0;
            Account first = mock(Account.class);
            Account second = mock(Account.class);
            when(first.getAmount())
                    .thenReturn(0.0);
            when(accountDao.findById(from))
                    .thenReturn(Optional.of(first));
            when(accountDao.findById(to))
                    .thenReturn(Optional.of(second));

            assertThrows(ValidationException.class, () -> service.transfer(from, to, amount));

        }
    }
}