package com.incture.bupa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.incture.bupa.entities.BPContactInformation;

@Repository
public interface BPContactInformationRepository extends JpaRepository<BPContactInformation, Integer>{
	@Transactional
	@Modifying
	@Query("DELETE FROM BPContactInformation a WHERE a.bpGeneralData.requestId = :requestId")
	void deleteByRequestId(String requestId);
//    @Transactional
//    @Modifying
//    @Query("DELETE BPContactInformation c WHERE c.bpGeneralData.requestId = ?1")
//    void deleteByRequestId(Integer requestId);
}
