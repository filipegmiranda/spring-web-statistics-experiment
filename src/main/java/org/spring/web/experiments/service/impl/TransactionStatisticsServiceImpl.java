package org.spring.web.experiments.service.impl;

import org.spring.web.experiments.model.Statistic;
import org.spring.web.experiments.model.Transaction;
import org.spring.web.experiments.service.TransactionStatisticsService;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Default Implementation of TransactionStatisticsService
 * Resolves the provision of bucketsOfStatistics about transactions
 *
 * - Define defining an array for 60 bucketsSeconds, storing the time and cumulative stats for that
 *
 * - When you get a new transaction , by using the modulos, find the array position to store it, while combining previous values in the array
 *
 * - When a request to check the bucketsOfStatistics is sent, "reduction" is done over the array of bucketsOfStatistics
 *
 * - To enable high concurrency, each array position, represents one second, and has a corresponding lock, in an array of locks(this technique is known as Lock Partition)
 *
 */
@Service
public class TransactionStatisticsServiceImpl implements TransactionStatisticsService {

    private Statistic[] bucketsOfStatistics = new Statistic[SECONDS_THRESHOLD];

    private long[] bucketsSeconds = new long[SECONDS_THRESHOLD];

    private Lock[] bucketsLock = new ReentrantLock[SECONDS_THRESHOLD];

    public TransactionStatisticsServiceImpl() {
        for (int i = 0; i < SECONDS_THRESHOLD; i++) {
            bucketsOfStatistics[i] = new Statistic();
            bucketsLock[i] = new ReentrantLock();
            bucketsSeconds[i] = Long.MIN_VALUE;
        }
    }

    @Override
    public boolean registerTransaction(Transaction transaction) {
        long current = currentSecond();
        long transactionSecond = toSecond(transaction.getTimestamp());
        if (current - transactionSecond >= SECONDS_THRESHOLD) return false;

        int index = (int)(transactionSecond % SECONDS_THRESHOLD);
        Lock lock = bucketsLock[index];
        try {
            lock.lock();
            Statistic previousStat = bucketsOfStatistics[index];
            long statisticOnSecond = bucketsSeconds[index];
            if (statisticOnSecond != transactionSecond) {
                bucketsSeconds[index] = transactionSecond;
                bucketsOfStatistics[index] = Statistic.valueOf(transaction);
            } else {
                bucketsOfStatistics[index] = reduce(previousStat, Statistic.valueOf(transaction));
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error while registering transaction", e);
        } finally {
            lock.unlock();
        }
    }


    @Override
    public Statistic readCurrentTransactionsStatistics() {
        Statistic aggregated = new Statistic();
        long stillWindow = currentSecond() - SECONDS_THRESHOLD;
        for (int i = 0; i < SECONDS_THRESHOLD; i++) {
            Lock lock = bucketsLock[i];
            try {
                lock.lock();
                if (bucketsSeconds[i] > stillWindow) {
                    aggregated = reduce(aggregated, bucketsOfStatistics[i]);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error while calculating the current real time statistics of transactions", e);
            }finally {
                lock.unlock();
            }
        }
        return aggregated;
    }



    private Statistic reduce(Statistic statistic, Statistic statistic2) {
        Statistic reduced = new Statistic();
        reduced.setCount(statistic.getCount() + statistic2.getCount());
        reduced.setMax(Math.max(statistic.getMax(), statistic2.getMax()));
        reduced.setMin(Math.min(statistic.getMin(), statistic2.getMin()));
        reduced.setSum( statistic.getSum() + statistic2.getSum());
        reduced.setAvg(reduced.getSum() / reduced.getCount());
        return reduced;
    }

    private long currentSecond() {
        return TimeUnit.MILLISECONDS.toSeconds(java.time.Clock.systemUTC().millis());
    }

    private long toSecond(long timestamp) {
        return TimeUnit.MILLISECONDS.toSeconds(timestamp);
    }
}
