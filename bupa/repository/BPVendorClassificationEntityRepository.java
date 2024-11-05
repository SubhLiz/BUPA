package com.incture.bupa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.incture.bupa.entities.BPVendorClassificationEntity;

@Repository
public interface BPVendorClassificationEntityRepository extends JpaRepository<BPVendorClassificationEntity, Integer>{
}
