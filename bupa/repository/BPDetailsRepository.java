package com.incture.bupa.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.incture.bupa.entities.BPGeneralData;

@Repository
@Transactional
public interface BPDetailsRepository extends JpaRepository<BPGeneralData, String>{

	BPGeneralData findByRequestId(String requestId);

    @Query("select b.bupaNo,b.bupaAccountGrp from BPGeneralData b where b.requestId=?1")
    List<Object[]> findDetails(Integer requestId);

    @Query(value = "select request_id_seq.NEXTVAL from dummy", nativeQuery = true)
	Integer getNextRequestID();

    @Query(value = "select DISTINCT c from BPGeneralData c "
    		+ "left join c.bpBankInformation b "
    		+ "left join c.bpCompanyCodeInfo cc "
    		+ "left join c.bpPurchasingOrgDetail po "
    		+ "left join c.bpContactInformation ci "
    		+ "left join c.bpCommunication comm "
    		+ "left join comm.bpTelephone tel "
    		+ "left join comm.bpEmail em "
    		+ "where (:createdBy is null or LOWER(c.createdBy) like LOWER(CONCAT('%', :createdBy, '%'))) "
            + "and (:name1 is null or LOWER(c.name1) like LOWER(CONCAT('%', :name1, '%'))) "
            + "and (:searchTerm1 is null or LOWER(c.searchTerm1) like LOWER(CONCAT('%', :searchTerm1, '%'))) "
            + "and (:searchTerm2 is null or LOWER(c.searchTerm2) like LOWER(CONCAT('%', :searchTerm2, '%'))) "
            + "and (:district is null or LOWER(c.bpAddressInfo.district) like LOWER(CONCAT('%', :district, '%'))) "
            + "and (:region is null or LOWER(c.bpAddressInfo.region) like LOWER(CONCAT('%', :region, '%'))) "
            + "and (:bankAccountNo is null or LOWER(b.bankAccountNo) like LOWER(CONCAT('%', :bankAccountNo, '%'))) "
            + "and (:iban is null or LOWER(b.iban) like LOWER(CONCAT('%', :iban, '%'))) "
            + "and (:companyCode is null or LOWER(cc.companyCode) like LOWER(CONCAT('%', :companyCode, '%'))) "
            + "and (:purchasingOrg is null or LOWER(po.purchasingOrg) like LOWER(CONCAT('%', :purchasingOrg, '%'))) "
            + "and (:contactPerson is null or LOWER(ci.firstName) like LOWER(CONCAT('%', :contactPerson, '%'))) "
            + "and (:telephone is null or LOWER(tel.telephone) like LOWER(CONCAT('%', :telephone, '%'))) "
            + "and (:email is null or LOWER(em.emailAddress) like LOWER(CONCAT('%', :email, '%'))) "
            + "and (:requestId is null or c.requestId = :requestId) "
            + "and (:requestTypeId is null or c.requestTypeId = :requestTypeId) "
            + "and (:statusId is null or c.statusId = :statusId) "
            + "and (:bupaNo is null or c.bupaNo = :bupaNo) "
            + "and (:systemId is null or c.systemId = :systemId) "
            + "and (:createdOn is null or c.createdOn between :createdOn and :currentTime) "
            + "order by c.requestId desc")
   Page<BPGeneralData> filterRequest(@Param("createdBy") String createdBy,
                                     @Param("name1") String name1,
                                     @Param("requestId") String requestId,
                                     @Param("requestTypeId") Integer requestTypeId,
                                     @Param("statusId") Integer statusId,
                                     @Param("bupaNo") String bupaNo,
                                     @Param("searchTerm1") String searchTerm1,
                                     @Param("searchTerm2") String searchTerm2,
                                     @Param("district") String district,
                                     @Param("region") String region,
                                     @Param("bankAccountNo") String bankAccountNo,
                                     @Param("iban") String iban,
                                     @Param("companyCode") String companyCode,
                                     @Param("purchasingOrg") String purchasingOrg,
                                     @Param("contactPerson") String contactPerson,
                                     @Param("telephone") String telephone,
                                     @Param("email") String email,
                                     @Param("systemId") String systemId,
                                     
                                     @Param("createdOn") Timestamp createdOn,
                                     @Param("currentTime") Timestamp currentTime,
                                     Pageable paging);
//    @Query(value = "from BPGeneralData c "
//            + "where (:requestId is null or c.requestId = :requestId)  "
//            + "and (:requestType is null or c.requestType = :requestType) "
////            + "and (:businessPartnerId is null or c.businessPartnerId = :businessPartnerId) "
////            + "and (:businessPartnerName is null or c.businessPartnerName = :businessPartnerName) "
////            + "and (:status is null or c.status = :status) "
//            + "and (:createdBy is null or c.createdBy = :createdBy) ")
//    BPGeneralData filterRequestBench(@Param("requestId") String requestId,
//                                           @Param("requestType") String requestType,
////                                           @Param("businessPartnerId") String businessPartnerId,
////                                           @Param("businessPartnerName") String businessPartnerName,
////                                           @Param("status") String status,
//                                           @Param("createdBy") String createdBy);


    
    @Query("select b from BPGeneralData b where b.blockFunction=?1")
    List<BPGeneralData>filterGeneralData(String requestId);
    
    @Modifying
    @Query("UPDATE BPGeneralData b SET b.bupaNo = :vendorId WHERE b.requestId = :requestId")
	void updateVendorNo(String requestId, String vendorId);
    
    @Modifying
    @Query("UPDATE BPGeneralData b SET b.statusId = :statusId WHERE b.requestId = :requestId")
	void updateStatusId(String requestId, Integer statusId);

    @Query(value = "select distinct m.createdBy from BPGeneralData m"
            + " where m.createdBy is not null and m.createdBy != '' ")
    public Set<String> getCreatedByList();
}
