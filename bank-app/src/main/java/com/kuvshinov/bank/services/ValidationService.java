package com.kuvshinov.bank.services;

/**
 * Validate objects using specific annotations
 *
 * @author Sergei Kuvshinov
 * @see com.kuvshinov.bank.validation
 */
public interface ValidationService {

    /**
     * Validate object to restrictions.
     *
     * @param o - object for check.
     * @throws com.kuvshinov.http.server.exceptions.ValidationException if object not valid.
     */
    void validate(Object o);

}
