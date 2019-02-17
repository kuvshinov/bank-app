package com.kuvshinov.bank.dto;

import com.kuvshinov.bank.validation.NotNull;

public class TransferDto {

    @NotNull
    private Integer from;
    @NotNull
    private Integer to;
    @NotNull
    private Double amount;

    public Integer getFrom() {
        return from;
    }

    public Integer getTo() {
        return to;
    }

    public Double getAmount() {
        return amount;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
