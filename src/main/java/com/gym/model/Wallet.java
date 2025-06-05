package com.gym.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Wallet {
    private long id;
    private long accountId;
    private BigDecimal balance;
}
