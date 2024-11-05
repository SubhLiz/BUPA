package com.incture.bupa.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.incture.bupa.entities.BPAuditLog;

@Repository
public interface BPAuditLogRepository extends JpaRepository<BPAuditLog,UUID> {
    List<BPAuditLog> findByRequestId(String requestId);

    List<BPAuditLog> findByRequestIdOrderByUpdatedOnDesc(String requestId);
    
    @Query(value = "select serial_id_seq.NEXTVAL from dummy", nativeQuery = true)
	Integer getNextRequestID();
}