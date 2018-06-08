package org.spring.web.experiments.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
  Represents a transaction that will be analysed
 */
@Data
@AllArgsConstructor
public class Transaction {
    private double amount;
    private long timestamp;
}
