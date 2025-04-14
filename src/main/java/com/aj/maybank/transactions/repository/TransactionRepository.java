package com.aj.maybank.transactions.repository;

import com.aj.maybank.transactions.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE " +
            "(:customerId IS NULL OR t.customerId = :customerId) AND " +
            "(:accountNumber IS NULL OR t.accountNumber = :accountNumber) AND " +
            "(:description IS NULL OR t.description LIKE %:description%)")
    Page<Transaction> findByCriteria(
            @Param("customerId") String customerId,
            @Param("accountNumber") String accountNumber,
            @Param("description") String description,
            Pageable pageable);
}