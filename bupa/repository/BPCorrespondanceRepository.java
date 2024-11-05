package com.incture.bupa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.bupa.entities.BPCorrespondance;
@Repository
public interface BPCorrespondanceRepository extends JpaRepository<BPCorrespondance, Integer>{

}
