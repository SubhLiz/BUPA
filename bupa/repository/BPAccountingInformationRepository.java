package com.incture.bupa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.bupa.entities.BPAccountingInformation;

@Repository
public interface BPAccountingInformationRepository extends JpaRepository<BPAccountingInformation, Integer>{

}
