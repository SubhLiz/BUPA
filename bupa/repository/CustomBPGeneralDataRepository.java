package com.incture.bupa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.incture.bupa.entities.BPGeneralData;

import java.sql.Timestamp;
import java.util.Map;

@Repository
public interface CustomBPGeneralDataRepository {
    Page<BPGeneralData> filterRequest(
    	String createdBy,
        String name1,
        String bupaNo,
        String requestId,
        Integer requestTypeId,
        Integer statusId,
        String systemId,
        String searchTerm1,
        String searchTerm2,
        String district,
        String region,
        String country,
        String bupaAccountGrp,
        String email,
        String telephone,
        String contactPerson,
        String bankAccount,
        String iban,
        String purchasingOrganization,
        String companyCode,
        Timestamp createdOnTimeStamp,
        Timestamp createdOnTimeRange,
        Pageable pageable,
        Map<String,String>searchType
    );
}
