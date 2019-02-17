package com.kuvshinov.bank.dto;

import com.kuvshinov.bank.validation.NotNull;

public class AmountDto {

    @NotNull
    private Double amount;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
