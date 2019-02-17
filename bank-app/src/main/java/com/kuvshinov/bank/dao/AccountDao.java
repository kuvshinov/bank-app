package com.kuvshinov.bank.dao;

import com.kuvshinov.bank.models.Account;

import java.util.List;
import java.util.Optional;

/**
 * Data access object for {@link Account}.
 *
 * @author Sergei Kuvshinov
 */
public interface AccountDao {

    /**
     * Save or update account.
     *
     * @param account - new or updated account.
     * @return saved account.
     * @throws NullPointerException if <i>id</i> is null.
     */
    Account save(Account account);

    /**
     * Find account by specific id.
     *
     * @param id - account id, cannot be null.
     * @return {@link Optional} empty if account doesn't exists and {@link Optional} with account otherwise.
     * @throws NullPointerException if <i>id</i> is null.
     */
    Optional<Account> findById(Integer id);

    /**
     * Return all accounts.
     *
     * @return list of {@link Account}; not null.
     */
    List<Account> findAll();

    /**
     * Delete account.
     *
     * @param account - {@link Account} to delete.
     * @throws NullPointerException if <i>account</i> is null.
     */
    void delete(Account account);

}
