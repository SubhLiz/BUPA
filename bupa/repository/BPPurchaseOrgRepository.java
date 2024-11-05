package com.incture.bupa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.bupa.entities.BPPurchaseOrg;
@Repository
public interface BPPurchaseOrgRepository extends JpaRepository<BPPurchaseOrg, Integer>{

}
