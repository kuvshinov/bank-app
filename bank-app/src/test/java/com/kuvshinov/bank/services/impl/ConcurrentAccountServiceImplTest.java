package com.kuvshinov.bank.services.impl;

import com.kuvshinov.bank.dao.AccountDao;
import com.kuvshinov.bank.dao.impl.InMemoryAccountDaoImpl;
import com.kuvshinov.bank.models.Account;
import com.kuvshinov.bank.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcurrentAccountServiceImplTest {

    private AccountDao accountDao;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountDao = new InMemoryAccountDaoImpl();
        accountService = new AccountServiceImpl(accountDao);
        Account first = new Account();
        first.setAmount(100d);
        Account second = new Account();
        second.setAmount(100d);
        accountDao.save(first);
        accountDao.save(second);
    }


    @Test
    void shouldPutAndReceiveMoney() throws InterruptedException {
        Thread consumer = new Thread(() -> accountService.updateAccount(1, -20d));
        Thread producer = new Thread(() -> accountService.updateAccount(1, 40d));

        consumer.start();
        producer.start();

        consumer.join();
        producer.join();

        Account acc = accountService.getAccount(1);
        assertEquals(120d, acc.getAmount());
    }

    @Test
    void shouldTransferMoney() throws BrokenBarrierException, InterruptedException {
        Account first = accountService.getAccount(1);
        Account second = accountService.getAccount(2);
        int threadCount = 20;

        CyclicBarrier barrier = new CyclicBarrier(threadCount + 1);
        Runnable from1to2 = () -> {
            try {
                barrier.await();
                accountService.transfer(1, 2, 10d);
                barrier.await();
            } catch (Exception e) {
            }
        };
        Runnable from2to1 = () -> {
            try {
                barrier.await();
                accountService.transfer(2, 1, 5d);
                barrier.await();
            } catch (Exception e) {
            }
        };

        for (int i = 0; i < threadCount / 2; i++) {
            new Thread(from1to2).start();
            new Thread(from2to1).start();
        }

        barrier.await();
        barrier.await();

        assertEquals(50d, first.getAmount());
        assertEquals(150d, second.getAmount());
    }
}
