package org.spring.web.experiments.controller;

import org.spring.web.experiments.model.Statistic;
import org.spring.web.experiments.model.Transaction;
import org.spring.web.experiments.service.TransactionStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionStatisticsController {

    @Autowired
    private TransactionStatisticsService service;

    @PostMapping("/transactions")
    public ResponseEntity<Void> registerTransaction(@RequestBody Transaction transaction) {
        if (service.registerTransaction(transaction)) return new ResponseEntity<>(HttpStatus.CREATED);
        else return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/statistics")
    public Statistic currentStatistic() {
        return service.readCurrentTransactionsStatistics();
    }
}
