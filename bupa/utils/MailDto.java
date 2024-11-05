package com.incture.bupa.utils;

import com.incture.bupa.dto.BPCreationFromWorkflowRequest;

import lombok.Data;

@Data
public class MailDto {
	
	// Changes Done : Author - Dheeraj Kumar ( Added Company Code , vendor number , country name , Purchasing Org )
    private String requestId;
    private String requestor;
    private String taskDescription;
    private String vendorAccountGroup;
    private String businessPartnerName;
    private int notificationCode;
    private String processType;
    private String companyCode;
    private String businessPartnerNumber;
    private String countryName;
    private String purchasingOrg;
    private String userEmail;
    private String[] taskApprovers;
    private String[] managers;
    private BPCreationFromWorkflowRequest bpCreationFromWorkflowRequest;
    private  TaskOwnersDto workflowTaskDetails;
}
