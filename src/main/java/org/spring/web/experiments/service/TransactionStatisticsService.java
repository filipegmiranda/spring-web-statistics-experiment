package org.spring.web.experiments.service;

import org.spring.web.experiments.model.Statistic;
import org.spring.web.experiments.model.Transaction;

public interface TransactionStatisticsService {

    int SECONDS_THRESHOLD = 60;

    /**
     * Registers a new transaction as long it is within a 60 seconds window to the current time
     * returning true, if it was, false otherwise(transaction outside last 60 seconds)
     * @param transaction
     * @return
     */
    boolean registerTransaction(Transaction transaction);


    /**
     * Returns the Current statistics aggregation over the defined window of 60 seconds
     * @see Statistic
     * @return
     */
    Statistic readCurrentTransactionsStatistics();

}
