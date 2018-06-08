package org.spring.web.experiments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * This class is a blue print of an object representing aggregations of data related to Transactions,
 * providing the statistics below for the last 60 seconds:
 *
 *
 * - sum is a double specifying the total sum of transaction value in the last 60 seconds
 *
 * - avg is a double specifying the average amount of transaction value in the last 60
 * seconds
 *
 * - max is a double specifying single highest transaction value in the last 60 seconds
 *
 * - min is a double specifying single lowest transaction value in the last 60 seconds
 *
 * - count is a long specifying the total number of transactions happened in the last 60
 * seconds
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Statistic {
    private double sum;
    private double avg;
    private double max = Long.MIN_VALUE;
    private double min = Long.MAX_VALUE;
    private long count;


    public static Statistic valueOf(Transaction transaction) {
        Statistic statistic = new Statistic();
        statistic.count = 1;
        statistic.max = transaction.getAmount();
        statistic.min = transaction.getAmount();
        statistic.sum = transaction.getAmount();
        statistic.avg = transaction.getAmount();
        return statistic;
    }
}
