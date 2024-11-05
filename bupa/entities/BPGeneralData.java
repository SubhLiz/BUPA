package com.incture.bupa.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import com.incture.bupa.dto.BPCommentsDto;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Where(clause = "IS_DELETED='N'")
@Table(name="BP_GENERAL_DATA")
public class BPGeneralData {
	
	@Id
	@Column(name="BP_REQUEST_ID")
	private String requestId;
	
//	@Column(name="BP_VENDOR_NO")
//	private String vendorNo;
//	
//	@Column(name="BP_VENDOR_ACC_GRP")
//	private String vendorAccountGrp;
	@Column(name="BP_BUPA_NO")
	private String bupaNo;
	
	@Column(name="BP_BUPA_ACC_GRP")
	private String bupaAccountGrp;
	
	@Column(name="BP_NAME1")
	private String name1;
	
	@Column(name="BP_NAME2")
	private String name2;
	
	@Column(name="BP_NAME3")
	private String name3;
	
	@Column(name="BP_NAME4")
	private String name4;
	
	@Column(name="BP_REQUEST_TYPE_ID")
	private Integer requestTypeId;

	@Column(name = "BP_STATUS_ID")
	private Integer statusId;
	
	@Column(name="BP_SEARCH_TERM_1")
	private String searchTerm1;
	
	@Column(name="BP_SEARCH_TERM_2")
	private String searchTerm2;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="BIRTH_DATE")
	private Date birthDate;

	@Column(name = "IS_DELETED" , length = 1)
	private Character isDeleted = 'N';
	
	@Column(name="BP_TITLE")
	private String title;
	
	@Column(name="BP_NATURAL_PERSON")
	private String naturalPer;
//added
	@Column(name = "BP_INDUSTRY")
	private String industry;

	@Column(name = "BP_SUPPLIER_ACCOUNT_ID")
	private String supplierAccountId;

	@Column(name = "BP_CENTRAL_DELETION_FLAG")
	private String centralDeletionFlag;

	@Column(name = "BP_CENTRAL_POSTING_BLOCK")
	private String centralPostingBlock;

	@Column(name = "BP_CENTRAL_PURCHASING_BLOCK")
	private String centralPurchasingBlock;

	@Column(name = "BP_CENTRAL_DELETION_BLOCK")
	private String centralDeletionBlock;

	@Column(name = "BP_PO_BOX")
	private String poBox;

	@Column(name = "BP_PO_POSTAL_CODE")
	private String poPostalCode;
	
	@Column(name = "BP_PO_COMPANY_POSTAL_CODE")
	private String poCompanyPostalCode;

	@Column(name = "BP_SUPPLIER_URL")
	private String supplierURL;

	@Column(name = "BP_CORPORATE_GROUP_KEY")
	private String corporateGroupKey;

	@Column(name = "BP_CREDIT_INFORMATION_NUMBER")
	private String creditInformationNumber;

	@Column(name = "BP_RECORD_CREATION_USER")
	private String recordCreationUser;

	@Column(name = "BP_RECORD_CREATION_DATE")
	private String recordCreationDate;

	@Column(name = "BP_SECOND_TELEPHONE_NUMBER")
	private String secondTelephoneNumber;

	@Column(name = "BP_BLOCK_FUNCTION")
	private String blockFunction;
	
	@Column(name="BP_CREATED_BY")
	private String createdBy;
	
	@Column(name="BP_REQUESTOR_EMAIL")
	private String requestorEmail;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="BP_CREATED_ON")
	private Date createdOn;
	
	@Column(name="BP_ALTERNATIVE_PAYEE")
	private String alternativePayee;
	
	@Column(name="BP_DME_INDICATOR")
	private String dmeIndicator;
	
	@Column(name="BP_INSTRUCTION_KEY")
	private String instructionKey;
	
	@Column(name="BP_ISR_NUMBER")
	private String isrNumber;
	
	@Column(name="BP_INDIVIDUAL_ENTRIES")
	private Boolean individualEntries;
	
	@Column(name="BP_ENTRIES_FOR_REFERNCE")
	private Boolean entriesForReference;
	
	@Column(name="BP_CONTACT_PERSON_NAME")
	private String contactPersonName;
	
	@Column(name="BP_SYSTEM_ID")
	private String systemId;
	
	@Column(name="BP_VENDOR_TYPE")
	private String vendorType;

	@Column(name = "BP_IS_DRAFT")
	private Boolean isDraft;
	
	@Column(name="BP_PO_TYPE")
	private String poType;

	@Column(name = "BP_IS_REQUEST_DETAIL")
	private Boolean isRequestDetail;
	
	@Column(name="BP_EXTEND_COMPANY_CODE")
	private Boolean extendCompanyCode;
	
	@Column(name="BP_EXTEND_PURCHASE_ORG")
	private Boolean extendPurchaseOrg;
	
	@Column(name="BP_EXTEND_ADDITIONAL_DATA")
	private Boolean extendAdditionalData;
	
	@Column(name="BP_SKIP_BANK_VALIDATION")
	private Boolean skipBankValidation;
	
	
	@Column(name="BP_SUB_PROCESS_TYPE")
	private String subProcessType;
	
	
	
//	@JsonManagedReference
//	@OneToMany(mappedBy="bpGeneralData", cascade = CascadeType.ALL)
//	private List<BPAddressInfo> bpAddressInfo=new ArrayList<>();
	
	@OneToMany(mappedBy="bpGeneralData", cascade = CascadeType.ALL)
	private List<BPBankInformation>bpBankInformation=new ArrayList<>();
	
	@OneToMany (mappedBy = "bpGeneralData", cascade = CascadeType.ALL)
    private List<BPContactInformation> bpContactInformation = new ArrayList<>();
	
	@OneToMany(mappedBy = "bpGeneralData", cascade = CascadeType.ALL)
	private List<BPControlData> bpControlData=new ArrayList<>();
	
//	@OneToOne(cascade = CascadeType.ALL, mappedBy = "bpGeneralData", fetch = FetchType.LAZY)
//	private BPSupplier bpSupplier;
	
	
//	@OneToOne(cascade = CascadeType.ALL, mappedBy = "bpGeneralData", fetch = FetchType.LAZY)
//	private BPCompanyCodeInfo bpCompanyCodeInfo;
	
//	@OneToOne(cascade = CascadeType.ALL, mappedBy = "bpGeneralData", fetch = FetchType.LAZY)
//	private BPPurchasingOrgDetail bpPurchasingOrgDetail;
	@OneToMany(mappedBy = "bpGeneralData", cascade = CascadeType.ALL)
	private List<BPCompanyCodeInfo> bpCompanyCodeInfo=new ArrayList<>();
	
//	@OneToOne(mappedBy = "bpGeneralData")
//    private BPCompanyCodeInfo bpCompanyCodeInfo;
	
	@OneToMany(mappedBy = "bpGeneralData", cascade = CascadeType.ALL)
	private List<BPPurchasingOrgDetail> bpPurchasingOrgDetail=new ArrayList<>();
	
	@OneToMany(mappedBy = "bpGeneralData", cascade = CascadeType.ALL)
	private List<BPVendorClassificationEntity> bpVendorClassificationEntity=new ArrayList<>();
	
//	@OneToOne(mappedBy = "bpGeneralData")
//    private BPPurchasingOrgDetail bpPurchasingOrgDetail;
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "bpGeneralData", fetch = FetchType.LAZY)
	private BPCommunication bpCommunication;
	
//	@OneToOne(mappedBy = "bpGeneralData")
//    private BPCommunication bpCommunication;
	
	@OneToMany(mappedBy = "bpGeneralData", cascade = CascadeType.ALL)
	private List<BPDMSAttachments> bpDmsAttachments=new ArrayList<>();
	
	
	
	@OneToMany(mappedBy = "bpGeneralData", cascade = CascadeType.ALL)
	private List<BPComments> bpComments=new ArrayList<>();

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "bpGeneralData", fetch = FetchType.LAZY)
	private BPAddressInfo bpAddressInfo;
	
	
	
}
