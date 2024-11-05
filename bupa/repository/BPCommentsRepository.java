package com.incture.bupa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.incture.bupa.entities.BPComments;

@Repository
public interface BPCommentsRepository extends JpaRepository<BPComments, Integer>{
	@Transactional
	@Modifying
	@Query("DELETE FROM BPComments a WHERE a.bpGeneralData.requestId = :requestId")
	void deleteByRequestId(String requestId);

}
