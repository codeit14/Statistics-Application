package com.n26.services;

import com.n26.entities.Transaction;

/**
 * Service responsible for CRUD on transactions
 */
public interface TransactionService {
    void addTransaction(Transaction transaction, long evictionTimeInSeconds);
    void deleteAllTransactions();
}
