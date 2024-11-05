package com.incture.bupa.dto;

public class BPRequestAddressDto {
	
	private BPRequestMainAddressDto mainAddress;
	private BPRequestOrderingAddressDto orderingAddress;
	private BPRequestRemittanceAddressDto remittanceAddress;
	private boolean isOrderingChecked;
	private boolean isRemittanceChecked;
	public BPRequestMainAddressDto getMainAddress() {
		return mainAddress;
	}
	public void setMainAddress(BPRequestMainAddressDto mainAddress) {
		this.mainAddress = mainAddress;
	}
	public BPRequestOrderingAddressDto getOrderingAddress() {
		return orderingAddress;
	}
	public void setOrderingAddress(BPRequestOrderingAddressDto orderingAddress) {
		this.orderingAddress = orderingAddress;
	}
	public BPRequestRemittanceAddressDto getRemittanceAddress() {
		return remittanceAddress;
	}
	public void setRemittanceAddress(BPRequestRemittanceAddressDto remittanceAddress) {
		this.remittanceAddress = remittanceAddress;
	}
	
	
	public boolean isOrderingChecked() {
		return isOrderingChecked;
	}
	public void setOrderingChecked(boolean isOrderingChecked) {
		this.isOrderingChecked = isOrderingChecked;
	}
	public boolean isRemittanceChecked() {
		return isRemittanceChecked;
	}
	public void setRemittanceChecked(boolean isRemittanceChecked) {
		this.isRemittanceChecked = isRemittanceChecked;
	}
	@Override
	public String toString() {
		return "BPRequestAddressDto [isOrderingChecked=" + isOrderingChecked + ", isRemittanceChecked="
				+ isRemittanceChecked + ", mainAddress=" + mainAddress + ", orderingAddress=" + orderingAddress
				+ ", remittanceAddress=" + remittanceAddress + "]";
	}
	
}
