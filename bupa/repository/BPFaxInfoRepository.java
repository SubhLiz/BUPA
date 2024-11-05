package com.incture.bupa.repository;

import com.incture.bupa.entities.BPFaxInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BPFaxInfoRepository extends JpaRepository<BPFaxInfo, Integer> {
	@Transactional
	@Modifying
	@Query("DELETE FROM BPFaxInfo a WHERE a.bpCommunication.communicationId = :communicationId")
	void deleteByRequestId(Integer communicationId);

}
