package com.incture.bupa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.bupa.entities.BPPaymentTransactions;
@Repository
public interface BPPaymentTransactionsRepository extends JpaRepository<BPPaymentTransactions, Integer>{

}
