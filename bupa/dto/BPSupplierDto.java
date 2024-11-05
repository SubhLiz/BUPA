package com.incture.bupa.dto;

import java.util.ArrayList;

public class BPSupplierDto {
	private Integer supplierId;
	private ArrayList<BPPurchasingOrgDetailDto>bpPurchasingOrgDetail;
	private ArrayList<BPCompanyCodeInfoDto> bpCompanyCodeInfo;
	
	public Integer getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}
	public ArrayList<BPPurchasingOrgDetailDto> getBpPurchasingOrgDetail() {
		return bpPurchasingOrgDetail;
	}
	public void setBpPurchasingOrgDetail(ArrayList<BPPurchasingOrgDetailDto> bpPurchasingOrgDetail) {
		this.bpPurchasingOrgDetail = bpPurchasingOrgDetail;
	}
	public ArrayList<BPCompanyCodeInfoDto> getBpCompanyCodeInfo() {
		return bpCompanyCodeInfo;
	}
	public void setBpCompanyCodeInfo(ArrayList<BPCompanyCodeInfoDto> bpCompanyCodeInfo) {
		this.bpCompanyCodeInfo = bpCompanyCodeInfo;
	}
	@Override
	public String toString() {
		return "BPSupplierDto [supplierId=" + supplierId + ", bpPurchasingOrgDetail=" + bpPurchasingOrgDetail
				+ ", bpCompanyCodeInfo=" + bpCompanyCodeInfo + "]";
	}
	
}
