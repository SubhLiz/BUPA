package com.incture.bupa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.bupa.entities.BPUserAction;

@Repository
public interface BPUserActionRepository extends JpaRepository<BPUserAction, String>{

}