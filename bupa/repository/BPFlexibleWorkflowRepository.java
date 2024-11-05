package com.incture.bupa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.bupa.entities.BPFlexibleWorkflow;
@Repository
public interface BPFlexibleWorkflowRepository extends JpaRepository<BPFlexibleWorkflow, Integer>{

}
