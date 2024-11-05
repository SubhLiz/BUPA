package com.incture.bupa.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_COMPANY_CODE_INFO")
public class BPCompanyCodeInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BP_COMP_CODE_INFO_ID")
	private Integer companyCodeId;

	@Column(name = "BP_COMPANY_CODE")
	private String companyCode;
	
	@Column(name="BP_WH_TAX_COUNTRY")
	private String whTaxCountry;
	
	@Column(name="BP_EXTEND")
	private Boolean extend;
	
	@Column(name="BP_COMPANY_COUNTRY")
	private String companyCountry;

	@Column(name = "BP_BRANCH_CODE")
	private String branchCode;

	@Column(name = "BP_BRANCH_CODE_DESCRIPTION")
	private String branchCodeDescription;

	@OneToMany(mappedBy="bpCompanyCodeInfo", cascade = CascadeType.ALL)
	private List<BPWithholdingTax> bpWithholdingTax=new ArrayList<>();

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "bpCompanyCodeInfo", fetch = FetchType.LAZY)
	private BPAccountingInformation bpAccountingInformation;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "bpCompanyCodeInfo", fetch = FetchType.LAZY)
	private BPPaymentTransactions bpPaymentTransaction;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "bpCompanyCodeInfo", fetch = FetchType.LAZY)
	private BPCorrespondance bpCorrespondance;
	
	@ManyToOne
	@JoinColumn(name = "BP_REQUEST_ID",referencedColumnName = "BP_REQUEST_ID")
//    @JsonBackReference
    private BPGeneralData bpGeneralData;
}