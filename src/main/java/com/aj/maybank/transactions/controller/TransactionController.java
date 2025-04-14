package com.aj.maybank.transactions.controller;


import com.aj.maybank.transactions.entity.Transaction;
import com.aj.maybank.transactions.service.TransactionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<Page<Transaction>> getTransactions(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) String description,
            Pageable pageable) {
        return ResponseEntity.ok(
                transactionService.getTransactions(customerId, accountNumber, description, pageable)
        );
    }

    @PutMapping(path = "/{id}", consumes = "text/plain")
    public ResponseEntity<Transaction> updateDescription(
            @PathVariable Long id,
            @RequestBody String newDescription) {
        return ResponseEntity.ok(
                transactionService.updateDescription(id, newDescription)
        );
    }
}