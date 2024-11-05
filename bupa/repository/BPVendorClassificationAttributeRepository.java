package com.incture.bupa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.bupa.entities.BPVendorClassificationAttribute;
import com.incture.bupa.entities.BPVendorClassificationEntity;
@Repository
public interface BPVendorClassificationAttributeRepository extends JpaRepository<BPVendorClassificationAttribute, Integer>{

}
