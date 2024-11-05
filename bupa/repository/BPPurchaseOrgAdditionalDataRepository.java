package com.incture.bupa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.bupa.entities.BPPurchaseOrgAdditionalData;
@Repository
public interface BPPurchaseOrgAdditionalDataRepository extends JpaRepository<BPPurchaseOrgAdditionalData, Integer>{

}
