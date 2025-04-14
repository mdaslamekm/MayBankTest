package com.aj.maybank.transactions.service;

import com.aj.maybank.transactions.entity.Transaction;
import com.aj.maybank.transactions.exception.ConcurrentUpdateException;
import com.aj.maybank.transactions.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Page<Transaction> getTransactions(String customerId, String accountNumber, String description, Pageable pageable) {
        return transactionRepository.findByCriteria(customerId, accountNumber, description, pageable);
    }

    public Transaction updateDescription(Long id, String newDescription) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transaction.setDescription(newDescription);
        try {
            return transactionRepository.save(transaction);
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new ConcurrentUpdateException("Concurrent update detected. Please retry.");
        }

    }
}
