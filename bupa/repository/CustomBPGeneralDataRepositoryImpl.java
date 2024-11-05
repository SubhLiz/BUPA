package com.incture.bupa.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.incture.bupa.entities.BPGeneralData;
import com.incture.bupa.utils.HelperClass;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public class CustomBPGeneralDataRepositoryImpl implements CustomBPGeneralDataRepository {

    private final EntityManager entityManager;

    @Autowired
    public CustomBPGeneralDataRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BPGeneralData> filterRequest(
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
    ) {
    	StringBuilder sb = new StringBuilder();
    	sb.append("select DISTINCT c from BPGeneralData c "
    			+ "left join c.bpBankInformation b "
        		+ "left join c.bpCompanyCodeInfo cc "
        		+ "left join c.bpPurchasingOrgDetail po "
        		+ "left join c.bpContactInformation ci "
        		+ "left join c.bpCommunication comm "
        		+ "left join comm.bpTelephone tel "
        		+ "left join comm.bpEmail em "
    			+ "where (:createdBy is null or LOWER(c.createdBy) like LOWER(CONCAT('%', :createdBy, '%'))) "
        		+ "and (:name1 is null or LOWER(c.name1) like LOWER(CONCAT('%', :name1, '%')) or LOWER(c.name2) like LOWER(CONCAT('%', :name1, '%')) or LOWER(c.name3) like LOWER(CONCAT('%', :name1, '%')) or LOWER(c.name4) like LOWER(CONCAT('%', :name1, '%'))) "
                + "and (:bupaNo is null or c.bupaNo = :bupaNo) "
                + "and (:requestId is null or c.requestId = :requestId) "
                + "and (:requestTypeId is null or c.requestTypeId = :requestTypeId) "
                + "and (:statusId is null or c.statusId = :statusId) "
                + "and (:systemId is null or c.systemId = :systemId) "
                );
    	
    	if(!HelperClass.isEmpty(searchTerm1)) {
    		if(searchType.get("searchTerm1").equalsIgnoreCase("equals")) {
    			sb.append("and (:searchTerm1 is null or c.searchTerm1 = : searchTerm1) ");
    		}
    		else if(searchType.get("searchTerm1").equalsIgnoreCase("contains")) {
    			sb.append("and (:searchTerm1 is null or LOWER(c.searchTerm1) like LOWER(CONCAT('%', :searchTerm1, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(bupaAccountGrp)){
    		if(searchType.get("bupaAccountGrp").equalsIgnoreCase("equals")) {
    			sb.append("and (:bupaAccountGrp is null or c.bupaAccountGrp = : bupaAccountGrp) ");
    		}
    		else if(searchType.get("bupaAccountGrp").equalsIgnoreCase("contains")) {
    			sb.append("and (:bupaAccountGrp is null or LOWER(c.bupaAccountGrp) like LOWER(CONCAT('%', :bupaAccountGrp, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(searchTerm2)){
    		if(searchType.get("searchTerm2").equalsIgnoreCase("equals")) {
    			sb.append("and (:searchTerm2 is null or c.searchTerm2 = : searchTerm2) ");
    		}
    		else if(searchType.get("searchTerm2").equalsIgnoreCase("contains")) {
    			sb.append("and (:searchTerm2 is null or LOWER(c.searchTerm2) like LOWER(CONCAT('%', :searchTerm2, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(district)){
    		if(searchType.get("district").equalsIgnoreCase("equals")) {
    			sb.append("and (:district is null or c.bpAddressInfo.district = : district) ");
    		}
    		else if(searchType.get("district").equalsIgnoreCase("contains")) {
    			sb.append("and (:district is null or LOWER(c.bpAddressInfo.district) like LOWER(CONCAT('%', :district, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(country)){
    		if(searchType.get("country").equalsIgnoreCase("equals")) {
    			sb.append("and (:country is null or c.bpAddressInfo.country = : country) ");
    		}
    		else if(searchType.get("country").equalsIgnoreCase("contains")) {
    			sb.append("and (:country is null or LOWER(c.bpAddressInfo.country) like LOWER(CONCAT('%', :country, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(region)){
    		if(searchType.get("region").equalsIgnoreCase("equals")) {
    			sb.append("and (:region is null or c.bpAddressInfo.region = : region) ");
    		}
    		else if(searchType.get("region").equalsIgnoreCase("contains")) {
    			sb.append("and (:region is null or LOWER(c.bpAddressInfo.region) like LOWER(CONCAT('%', :region, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(email)){
    		if(searchType.get("email").equalsIgnoreCase("equals")) {
    			sb.append("and (:email is null or em.emailAddress = :email) ");
    		}
    		else if(searchType.get("email").equalsIgnoreCase("contains")) {
    			sb.append("and (:email is null or LOWER(em.emailAddress) like LOWER(CONCAT('%', :email, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(telephone)){
    		if(searchType.get("telephone").equalsIgnoreCase("equals")) {
    			sb.append("and (:telephone is null or tel.telephone = :telephone) ");
    		}
    		else if(searchType.get("telephone").equalsIgnoreCase("contains")) {
    			sb.append("and (:telephone is null or LOWER(tel.telephone) like LOWER(CONCAT('%', :telephone, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(contactPerson)){
    		if(searchType.get("contactPerson").equalsIgnoreCase("equals")) {
    			sb.append("and (:contactPerson is null or ci.firstName = :contactPerson) ");
    		}
    		else if(searchType.get("contactPerson").equalsIgnoreCase("contains")) {
    			sb.append("and (:contactPerson is null or LOWER(ci.firstName) like LOWER(CONCAT('%', :contactPerson, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(bankAccount)){
    		if(searchType.get("bankAccount").equalsIgnoreCase("equals")) {
    			sb.append("and (:bankAccountNo is null or b.bankAccountNo = :bankAccountNo) ");
    		}
    		else if(searchType.get("bankAccount").equalsIgnoreCase("contains")) {
    			sb.append("and (:bankAccountNo is null or LOWER(b.bankAccountNo) like LOWER(CONCAT('%', :bankAccountNo, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(iban)){
    		if(searchType.get("iban").equalsIgnoreCase("equals")) {
    			sb.append("and (:iban is null or b.iban = :iban) ");
    		}
    		else if(searchType.get("iban").equalsIgnoreCase("contains")) {
    			sb.append("and (:iban is null or LOWER(b.iban) like LOWER(CONCAT('%', :iban, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(purchasingOrganization)){
    		if(searchType.get("purchasingOrganization").equalsIgnoreCase("equals")) {
    			sb.append("and (:purchasingOrg is null or po.purchasingOrg = :purchasingOrg) ");
    		}
    		else if(searchType.get("purchasingOrganization").equalsIgnoreCase("contains")) {
    			sb.append("and (:purchasingOrg is null or LOWER(po.purchasingOrg) like LOWER(CONCAT('%', :purchasingOrg, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(companyCode)){
    		if(searchType.get("companyCode").equalsIgnoreCase("equals")) {
    			sb.append("and (:companyCode is null or cc.companyCode = :companyCode) ");
    		}
    		else if(searchType.get("companyCode").equalsIgnoreCase("contains")) {
    			sb.append("and (:companyCode is null or LOWER(cc.companyCode) like LOWER(CONCAT('%', :companyCode, '%'))) ");
    		}
    	}
    	sb.append("and (:createdOnTimeStamp is null or c.createdOn between :createdOnTimeStamp and :createdOnTimeRange) "
                 + "order by cast(c.requestId as int) desc");
    	System.out.println(sb.toString());
        TypedQuery<BPGeneralData>query = entityManager.createQuery(sb.toString(), BPGeneralData.class);

        query.setParameter("createdBy",createdBy);
        query.setParameter("name1",name1);
        query.setParameter("createdOnTimeStamp", createdOnTimeStamp);
        query.setParameter("createdOnTimeRange", createdOnTimeRange);
        query.setParameter("bupaNo", bupaNo);
        query.setParameter("requestId", requestId);
        query.setParameter("requestTypeId", requestTypeId);
        query.setParameter("statusId", statusId);
        query.setParameter("systemId", systemId);
        
		if (!HelperClass.isEmpty(searchTerm1)) {
			query.setParameter("searchTerm1", searchTerm1);
		}
		if (!HelperClass.isEmpty(searchTerm2)) {
			query.setParameter("searchTerm2", searchTerm2);
			
		}
		if (!HelperClass.isEmpty(country)) {
			query.setParameter("country", country);
			
		}
		if (!HelperClass.isEmpty(bupaAccountGrp)) {
			query.setParameter("bupaAccountGrp", bupaAccountGrp);
			
		}
		if (!HelperClass.isEmpty(district)) {
			query.setParameter("district", district);
		}
		if (!HelperClass.isEmpty(region)) {
			query.setParameter("region", region);
		}
		if (!HelperClass.isEmpty(email)) {
			query.setParameter("email", email);
		}
		if (!HelperClass.isEmpty(telephone)) {
			query.setParameter("telephone", telephone);
		}
		if (!HelperClass.isEmpty(contactPerson)) {
			query.setParameter("contactPerson", contactPerson);
		}
		if (!HelperClass.isEmpty(iban)) {
			query.setParameter("iban", iban);
		}
		if (!HelperClass.isEmpty(purchasingOrganization)) {
			query.setParameter("purchasingOrg", purchasingOrganization);
		}
		if (!HelperClass.isEmpty(bankAccount)) {
			query.setParameter("bankAccountNo", bankAccount);
		}
		if (!HelperClass.isEmpty(companyCode)) {
			query.setParameter("companyCode", companyCode);
		}
		StringBuilder countQueryString = new StringBuilder();
		countQueryString.append("select COUNT(DISTINCT c) from BPGeneralData c "
    			+ "left join c.bpBankInformation b "
        		+ "left join c.bpCompanyCodeInfo cc "
        		+ "left join c.bpPurchasingOrgDetail po "
        		+ "left join c.bpContactInformation ci "
        		+ "left join c.bpCommunication comm "
        		+ "left join comm.bpTelephone tel "
        		+ "left join comm.bpEmail em "
    			+ "where (:createdBy is null or LOWER(c.createdBy) like LOWER(CONCAT('%', :createdBy, '%'))) "
        		+ "and (:name1 is null or LOWER(c.name1) like LOWER(CONCAT('%', :name1, '%'))or LOWER(c.name2) like LOWER(CONCAT('%', :name1, '%')) or LOWER(c.name3) like LOWER(CONCAT('%', :name1, '%')) or LOWER(c.name4) like LOWER(CONCAT('%', :name1, '%'))) "
                + "and (:bupaNo is null or c.bupaNo = :bupaNo) "
                + "and (:requestId is null or c.requestId = :requestId) "
                + "and (:requestTypeId is null or c.requestTypeId = :requestTypeId) "
                + "and (:statusId is null or c.statusId = :statusId) "
                + "and (:systemId is null or c.systemId = :systemId) "
                );
    	
    	if(!HelperClass.isEmpty(searchTerm1)) {
    		if(searchType.get("searchTerm1").equalsIgnoreCase("equals")) {
    			countQueryString.append("and (:searchTerm1 is null or c.searchTerm1 = : searchTerm1) ");
    		}
    		else if(searchType.get("searchTerm1").equalsIgnoreCase("contains")) {
    			countQueryString.append("and (:searchTerm1 is null or LOWER(c.searchTerm1) like LOWER(CONCAT('%', :searchTerm1, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(searchTerm2)){
    		if(searchType.get("searchTerm2").equalsIgnoreCase("equals")) {
    			countQueryString.append("and (:searchTerm2 is null or c.searchTerm2 = : searchTerm2) ");
    		}
    		else if(searchType.get("searchTerm2").equalsIgnoreCase("contains")) {
    			countQueryString.append("and (:searchTerm2 is null or LOWER(c.searchTerm2) like LOWER(CONCAT('%', :searchTerm2, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(bupaAccountGrp)){
    		if(searchType.get("bupaAccountGrp").equalsIgnoreCase("equals")) {
    			sb.append("and (:bupaAccountGrp is null or c.bupaAccountGrp = : bupaAccountGrp) ");
    		}
    		else if(searchType.get("bupaAccountGrp").equalsIgnoreCase("contains")) {
    			sb.append("and (:bupaAccountGrp is null or LOWER(c.bupaAccountGrp) like LOWER(CONCAT('%', :bupaAccountGrp, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(district)){
    		if(searchType.get("district").equalsIgnoreCase("equals")) {
    			countQueryString.append("and (:district is null or c.bpAddressInfo.district = : district) ");
    		}
    		else if(searchType.get("district").equalsIgnoreCase("contains")) {
    			countQueryString.append("and (:district is null or LOWER(c.bpAddressInfo.district) like LOWER(CONCAT('%', :district, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(region)){
    		if(searchType.get("region").equalsIgnoreCase("equals")) {
    			countQueryString.append("and (:region is null or c.bpAddressInfo.region = : region) ");
    		}
    		else if(searchType.get("region").equalsIgnoreCase("contains")) {
    			countQueryString.append("and (:region is null or LOWER(c.bpAddressInfo.region) like LOWER(CONCAT('%', :region, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(country)){
    		if(searchType.get("country").equalsIgnoreCase("equals")) {
    			sb.append("and (:country is null or c.bpAddressInfo.country = : country) ");
    		}
    		else if(searchType.get("country").equalsIgnoreCase("contains")) {
    			sb.append("and (:country is null or LOWER(c.bpAddressInfo.country) like LOWER(CONCAT('%', :country, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(email)){
    		if(searchType.get("email").equalsIgnoreCase("equals")) {
    			countQueryString.append("and (:email is null or em.emailAddress = :email) ");
    		}
    		else if(searchType.get("email").equalsIgnoreCase("contains")) {
    			countQueryString.append("and (:email is null or LOWER(em.emailAddress) like LOWER(CONCAT('%', :email, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(telephone)){
    		if(searchType.get("telephone").equalsIgnoreCase("equals")) {
    			countQueryString.append("and (:telephone is null or tel.telephone = :telephone) ");
    		}
    		else if(searchType.get("telephone").equalsIgnoreCase("contains")) {
    			countQueryString.append("and (:telephone is null or LOWER(tel.telephone) like LOWER(CONCAT('%', :telephone, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(contactPerson)){
    		if(searchType.get("contactPerson").equalsIgnoreCase("equals")) {
    			countQueryString.append("and (:contactPerson is null or ci.firstName = :contactPerson) ");
    		}
    		else if(searchType.get("contactPerson").equalsIgnoreCase("contains")) {
    			countQueryString.append("and (:contactPerson is null or LOWER(ci.firstName) like LOWER(CONCAT('%', :contactPerson, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(bankAccount)){
    		if(searchType.get("bankAccount").equalsIgnoreCase("equals")) {
    			countQueryString.append("and (:bankAccountNo is null or b.bankAccountNo = :bankAccountNo) ");
    		}
    		else if(searchType.get("bankAccount").equalsIgnoreCase("contains")) {
    			countQueryString.append("and (:bankAccountNo is null or LOWER(b.bankAccountNo) like LOWER(CONCAT('%', :bankAccountNo, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(iban)){
    		if(searchType.get("iban").equalsIgnoreCase("equals")) {
    			countQueryString.append("and (:iban is null or b.iban = :iban) ");
    		}
    		else if(searchType.get("iban").equalsIgnoreCase("contains")) {
    			countQueryString.append("and (:iban is null or LOWER(b.iban) like LOWER(CONCAT('%', :iban, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(purchasingOrganization)){
    		if(searchType.get("purchasingOrganization").equalsIgnoreCase("equals")) {
    			countQueryString.append("and (:purchasingOrg is null or po.purchasingOrg = :purchasingOrg) ");
    		}
    		else if(searchType.get("purchasingOrganization").equalsIgnoreCase("contains")) {
    			countQueryString.append("and (:purchasingOrg is null or LOWER(po.purchasingOrg) like LOWER(CONCAT('%', :purchasingOrg, '%'))) ");
    		}
    	}
    	if(!HelperClass.isEmpty(companyCode)){
    		if(searchType.get("companyCode").equalsIgnoreCase("equals")) {
    			countQueryString.append("and (:companyCode is null or cc.companyCode = :companyCode) ");
    		}
    		else if(searchType.get("companyCode").equalsIgnoreCase("contains")) {
    			countQueryString.append("and (:companyCode is null or LOWER(cc.companyCode) like LOWER(CONCAT('%', :companyCode, '%'))) ");
    		}
    	}
    	countQueryString.append("and (:createdOnTimeStamp is null or c.createdOn between :createdOnTimeStamp and :createdOnTimeRange) "
              );
    	System.out.println(countQueryString.toString());
        TypedQuery<Long>countQuery = entityManager.createQuery(countQueryString.toString(), Long.class);

        countQuery.setParameter("createdBy",createdBy);
        countQuery.setParameter("name1",name1);
        countQuery.setParameter("createdOnTimeStamp", createdOnTimeStamp);
        countQuery.setParameter("createdOnTimeRange", createdOnTimeRange);
        countQuery.setParameter("bupaNo", bupaNo);
        countQuery.setParameter("requestId", requestId);
        countQuery.setParameter("requestTypeId", requestTypeId);
        countQuery.setParameter("statusId", statusId);
        countQuery.setParameter("systemId", systemId);
        
		if (!HelperClass.isEmpty(searchTerm1)) {
			countQuery.setParameter("searchTerm1", searchTerm1);
		}
		if (!HelperClass.isEmpty(searchTerm2)) {
			countQuery.setParameter("searchTerm2", searchTerm2);
			
		}
		if (!HelperClass.isEmpty(country)) {
			query.setParameter("country", country);
			
		}
		if (!HelperClass.isEmpty(bupaAccountGrp)) {
			query.setParameter("bupaAccountGrp", bupaAccountGrp);
			
		}
		if (!HelperClass.isEmpty(district)) {
			countQuery.setParameter("district", district);
		}
		if (!HelperClass.isEmpty(region)) {
			countQuery.setParameter("region", region);
		}
		if (!HelperClass.isEmpty(email)) {
			countQuery.setParameter("email", email);
		}
		if (!HelperClass.isEmpty(telephone)) {
			countQuery.setParameter("telephone", telephone);
		}
		if (!HelperClass.isEmpty(contactPerson)) {
			countQuery.setParameter("contactPerson", contactPerson);
		}
		if (!HelperClass.isEmpty(iban)) {
			countQuery.setParameter("iban", iban);
		}
		if (!HelperClass.isEmpty(purchasingOrganization)) {
			countQuery.setParameter("purchasingOrg", purchasingOrganization);
		}
		if (!HelperClass.isEmpty(bankAccount)) {
			countQuery.setParameter("bankAccountNo", bankAccount);
		}
		if (!HelperClass.isEmpty(companyCode)) {
			countQuery.setParameter("companyCode", companyCode);
		}
//		String countQueryString = "SELECT COUNT(c) FROM BPGeneralData c ";
//		TypedQuery<Long> countQuery = entityManager.createQuery(countQueryString, Long.class);
		long totalCount = countQuery.getSingleResult();
		
        // Execute the query with pagination
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
        

        List<BPGeneralData> results = query.getResultList();
        System.out.println(totalCount);
        return new PageImpl<>(results, pageable,totalCount);
    }


}
