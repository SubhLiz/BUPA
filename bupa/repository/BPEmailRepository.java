package com.incture.bupa.repository;

import com.incture.bupa.entities.BPEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BPEmailRepository extends JpaRepository<BPEmail, Integer> {
	@Transactional
	@Modifying
	@Query("DELETE FROM BPEmail a WHERE a.bpCommunication.communicationId = :communicationId")
	void deleteByRequestId(Integer communicationId);

}
