package com.incture.bupa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.incture.bupa.entities.BPDMSAttachments;
@Repository
public interface BPDMSAttachmentsRepository extends JpaRepository<BPDMSAttachments, String>{

	List<BPDMSAttachments> findByRequestID(String requestID);

	BPDMSAttachments findByDocumentID(String attachmentId);
	@Transactional
	@Modifying
	@Query("DELETE FROM BPDMSAttachments a WHERE a.bpGeneralData.requestId = :requestId")
	void deleteByRequestId(String requestId);
	@Query("SELECT a FROM BPDMSAttachments a WHERE a.bpGeneralData.requestId = :requestId")
	List<BPDMSAttachments> findByBpRequestID(String requestId);

}
