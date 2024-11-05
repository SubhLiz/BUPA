package com.incture.bupa.repository;

import com.incture.bupa.entities.BPMobilePhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BPMobilePhoneRepository extends JpaRepository<BPMobilePhone, Integer> {
	@Transactional
	@Modifying
	@Query("DELETE FROM BPMobilePhone a WHERE a.bpCommunication.communicationId = :communicationId")
	void deleteByRequestId(Integer communicationId);

}
