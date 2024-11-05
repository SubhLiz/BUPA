package com.incture.bupa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.bupa.entities.BPWithholdingTax;
@Repository
public interface BPWithholdingTaxRepository extends JpaRepository<BPWithholdingTax, Integer>{

}
