package com.incture.bupa.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BP_PURCHASING_ORG_DETAIL")
public class BPPurchasingOrgDetail {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BP_PURCHASING_ORG_DETAIL_ID")
	private Integer purchaseId;
	
	@Column(name = "BP_PURCHASING_ORG")
	private String purchasingOrg;
	
	@Column(name="BP_EXTEND")
	private Boolean extend;
	
	@Column(name="BP_EXTEND_ADDITIONAL_DATA")
	private Boolean extendAdditionalData;
	
	@Column(name="BP_ORDERING_ADDRESS_CHECK")
	private Boolean orderingAddressCheck;
	
	@Column(name="BP_REMITTANCE_ADDRESS_CHECK")
	private Boolean remittanceAddressCheck;

	@Column(name = "BP_ESTIMATED_ANNUAL_SPEND")
	private String estimatedAnnualSpend;

	@Column(name = "BP_SOURCING_CATEGORY")
	private String sourcingCategory;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "bpPurchasingOrgDetail", fetch = FetchType.LAZY)
	private BPPurchaseOrg bpPurchaseOrg;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "bpPurchasingOrgDetail", fetch = FetchType.LAZY)
	private List<BPPurchaseOrgAdditionalData> bpPurchaseOrgAdditionalData = new ArrayList<>();

	@OneToMany(mappedBy = "bpPurchasingOrgDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<BPBusinessPartnerAddressInfo> bpBusinessPartnerAddressInfo = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "BP_REQUEST_ID",referencedColumnName = "BP_REQUEST_ID")
//    @JsonBackReference
	private BPGeneralData bpGeneralData;
}