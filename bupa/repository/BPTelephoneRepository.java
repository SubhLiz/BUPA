package com.incture.bupa.repository;

import com.incture.bupa.entities.BPTelephone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BPTelephoneRepository extends JpaRepository<BPTelephone, Integer> {
	@Transactional
	@Modifying
	@Query("DELETE FROM BPTelephone a WHERE a.bpCommunication.communicationId = :communicationId")
	void deleteByRequestId(Integer communicationId);

}
