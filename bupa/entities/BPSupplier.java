//package com.incture.bupa.entities;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.OneToMany;
//import javax.persistence.OneToOne;
//import javax.persistence.Table;
//
//@Entity
//@Table(name = "BP_SUPPLIER")
//public class BPSupplier {
//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "BP_SUPPLIER_ID")
//	private Integer supplierId;
//	
//	@OneToMany(mappedBy="bpSupplier", cascade = CascadeType.ALL)
//	private List<BPCompanyCodeInfo> bpCompanyCodeInfo=new ArrayList<>();
//	
//	@OneToMany(mappedBy="bpSupplier", cascade = CascadeType.ALL)
//	private List<BPPurchasingOrgDetail> bpPurchasingOrgDetail=new ArrayList<>();
//	
//	@OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "bp_request_id", referencedColumnName = "BP_REQUEST_ID")
//	private BPGeneralData bpGeneralData;
//	
//	public BPSupplier() {
//		super();
//		// TODO Auto-generated constructor stub
//	}
//
//	public BPSupplier(Integer supplierId, List<BPCompanyCodeInfo> bpCompanyCodeInfo,
//			List<BPPurchasingOrgDetail> bpPurchasingOrgDetail, BPGeneralData bpGeneralData) {
//		super();
//		this.supplierId = supplierId;
//		this.bpCompanyCodeInfo = bpCompanyCodeInfo;
//		this.bpPurchasingOrgDetail = bpPurchasingOrgDetail;
//		this.bpGeneralData = bpGeneralData;
//	}
//
//	public Integer getSupplierId() {
//		return supplierId;
//	}
//
//	public void setSupplierId(Integer supplierId) {
//		this.supplierId = supplierId;
//	}
//
//	public List<BPCompanyCodeInfo> getBpCompanyCodeInfo() {
//		return bpCompanyCodeInfo;
//	}
//
//	public void setBpCompanyCodeInfo(List<BPCompanyCodeInfo> bpCompanyCodeInfo) {
//		this.bpCompanyCodeInfo = bpCompanyCodeInfo;
//	}
//
//	public List<BPPurchasingOrgDetail> getBpPurchasingOrgDetail() {
//		return bpPurchasingOrgDetail;
//	}
//
//	public void setBpPurchasingOrgDetail(List<BPPurchasingOrgDetail> bpPurchasingOrgDetail) {
//		this.bpPurchasingOrgDetail = bpPurchasingOrgDetail;
//	}
//
//	public BPGeneralData getBpGeneralData() {
//		return bpGeneralData;
//	}
//
//	public void setBpGeneralData(BPGeneralData bpGeneralData) {
//		this.bpGeneralData = bpGeneralData;
//	}
//
//	@Override
//	public String toString() {
//		return "BPSupplier [supplierId=" + supplierId + ", bpCompanyCodeInfo=" + bpCompanyCodeInfo
//				+ ", bpPurchasingOrgDetail=" + bpPurchasingOrgDetail + ", bpGeneralData=" + bpGeneralData + "]";
//	}
//	
//	
//}
