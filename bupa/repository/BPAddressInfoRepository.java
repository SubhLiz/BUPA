package com.incture.bupa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.incture.bupa.entities.BPAddressInfo;
@Repository
public interface BPAddressInfoRepository extends JpaRepository<BPAddressInfo, Integer>{
	@Query(value="select * from BP_ADDRESS_INFO  where BP_REQUEST_ID=:requestId ",nativeQuery=true)
	List<BPAddressInfo> findByRequestId(@Param("requestId")Integer requestId);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM BPAddressInfo a WHERE a.bpGeneralData.requestId = :requestId")
	void deleteByRequestId(String requestId);
}
