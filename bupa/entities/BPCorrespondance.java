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
@Table(name = "BP_CORRESPONDANCE")
public class BPCorrespondance {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BP_CORRESPONDANCE_ID")
	private Integer correspondanceId;
	
	@Column(name="BP_DUNN_PROCEDURE")
	private String dunnProcedure;
	
	@Column(name="BP_DUNNING_BLOCK")
	private String dunningBlock;
	
	@Column(name="BP_DUNN_RECEPIENT")
	private String dunnRecepient;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="BP_LEGAL_DUNN_PROC")
	private Date legalDunnProc;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="BP_LAST_DUNNED")
	private Date lastDunned;
	
	@Column(name="BP_DUNNING_LEVEL")
	private Integer dunningLevel;
	
	@Column(name="BP_GROUPING_CLERK")
	private String dunningClerk;
	
	@Column(name="BP_LOCAL_PROCESS")
	private Boolean localProcess;
	
	@Column(name="BP_GROUPING_KEYS")
	private String groupingKeys;
	
	@Column(name = "BP_ACC_CLERK")
	private String accountingClerk;
	
	@Column(name="BP_ACCW_VENDOR")
	private String acctwVendor;
	
	@Column(name="BP_CLERK_AT_VENDOR")
	private String clerkAtVendor;
	
	@Column(name = "BP_ACTING_CLERK_TEL_NO")
	private String actingClerksTelephone;
	
	@Column(name="BP_CLERK_FAX")
	private String clerkFax;
	
	@Column(name="BP_CLERK_INTERNET")
	private String clerkInternet;
	
	@Column(name="BP_ACCOUNT_MEMO")
	private String accountMemo;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "BP_COMP_CODE_INFO_ID", referencedColumnName = "BP_COMP_CODE_INFO_ID")
	private BPCompanyCodeInfo bpCompanyCodeInfo;
}