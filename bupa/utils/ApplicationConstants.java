package com.incture.bupa.utils;

import org.springframework.beans.factory.annotation.Value;

public interface ApplicationConstants {
	public final static String SUCCESS = "Success";
	public final static String FAILURE = "Failure";
	public final static String CODE_SUCCESS = "0";
	public final static String CODE_FAILURE = "1";
	public final static String CREATED_SUCCESS = "created successfully";
	public final static String CREATE_FAILURE = "creation failed";
	public final static String UPDATE_SUCCESS = "updated successfully";
	public final static String UPDATE_FAILURE = "updation failed";
	public final static String FETCHED_SUCCESS = "fetched successfully";
	public final static String FETCHED_FAILURE = "fetching failed";
	public final static String REJECT_SUCCESS="Rejected Successfully";
	public final static String REJECT_FAILURE="Rejection failed";
	public final static String DELETE_SUCCESS = "Deleted Successfully";
	public final static String DELETE_FAILURE = "Deletion failed";
	String DMS_DESTINATION_NAME = "DOCUMENT_MANAGEMENT_SERVICES";	
		
	public static class DMS
	{

		public static final String BROWSER_URL = "https://api-sdm-di.cfapps.us21.hana.ondemand.com/browser";

		public static final String GRANT_TYPE = "client_credentials";

		public static final String SCOPE = "generate-ads-output";
		
	} 
	}
