package com.n26.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class Transaction {
    private BigDecimal amount;
    private long timestamp;
}
