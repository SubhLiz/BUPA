package com.incture.bupa.entities;

import java.util.Date;
import java.util.UUID;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_ACCOUNTING_INFORMATION")
public class BPAccountingInformation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BP_ACCOUNTING_INFORMATION_ID")
	private Integer accountingInformationId;
	
	@Column(name = "BP_RECONCILLIATION_ACCOUNT_IN_GENERAL_LEDGER")
	private String reconcilliationAccountInGeneralLedger;
	
	@Column(name="BP_SORT_KEY")
	private String sortKey;
	
	@Column(name="BP_HEAD_OFFICE")
	private String headOffice;
	
	@Column(name="BP_AUTHORIZATION")
	private String authorization;
	
	@Column(name="BP_CASH_MANAGEMENT_GROUP")
	private String cashManagementGroup;
	
	@Column(name="BP_RELEASE_GROUP")
	private String releaseGroup;
	
	@Column(name="BP_MINORITY_INDICATOR")
	private String minorityIndicator;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="BP_CERTIFICATION_DATE")
	private Date certificationDate;
	
	@Column(name="BP_INTEREST_IND")
	private String interestInd;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="BP_LAST_KEY_DATE")
	private Date lastKeyDate;
	
	@Column(name="BP_INTEREST_CYCLE")
	private String interestCycle;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="BP_LAST_INTEREST_RUN")
	private Date lastInterestRun;
	
	@Column(name="BP_WTAX_CODE")
	private String wtaxCode;
	
	@Column(name="BP_EXEMPTION_NUMBER")
	private String exemptionNumber;
	
	@Column(name="BP_WHTAX_COUNTRY")
	private String whTaxCountry;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "BP_VALID_UNTIL")
	private Date validUntil; 
	
	@Column(name = "BP_RECIPIENT_TYPE")
	private String recipientType;
	
	@Column(name = "BP_EXEMPTION_AUTHORITY", length = 1)
	private String exemptionAuthority;
	
	@Column(name="BP_PREV_ACC_NO")
	private String prevAcctNo;
	
	@Column(name="BP_PERSONNEL_NUMBER")
	private String personnelNumber;
	
//	@ManyToOne
//	@JoinColumn(name = "BP_COMP_CODE_INFO_ID", referencedColumnName = "BP_COMP_CODE_INFO_ID")
//	private BPCompanyCodeInfo bpCompanyCodeInfo;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "BP_COMP_CODE_INFO_ID", referencedColumnName = "BP_COMP_CODE_INFO_ID")
	private BPCompanyCodeInfo bpCompanyCodeInfo;
}