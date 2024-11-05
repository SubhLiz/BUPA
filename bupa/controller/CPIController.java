package com.incture.bupa.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.incture.bupa.constants.AppConstants;
import com.incture.bupa.dto.BPCreationFromWorkflowRequest;
import com.incture.bupa.dto.BPRequestGeneralDataDto;
import com.incture.bupa.dto.BusinessPartnerResponse;
import com.incture.bupa.dto.CPIVendorDetailsDto;
import com.incture.bupa.dto.ServiceResponse;
import com.incture.bupa.service.CPIService;
import com.mashape.unirest.http.exceptions.UnirestException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/v1/cpi")
public class CPIController {

    @Autowired
    private CPIService cpiService;

    @ApiOperation(value = "Post through CPI for Vendor Creation", response = BusinessPartnerResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully fetched vendor details from the table"),
			@ApiResponse(code = 401, message = "You are not authorized to load document"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")})
    @PostMapping(path="/createVendor",consumes="application/json",produces="application/json")
    public ResponseEntity<ServiceResponse<BusinessPartnerResponse>> createVendor(@RequestBody BPCreationFromWorkflowRequest bpCreationFromWorkflowRequest) throws ClientProtocolException, IOException {
//        return cpiService.createVendorInfo();
        ServiceResponse<BusinessPartnerResponse> response = new ServiceResponse<>();
    	try {
				response.setData(cpiService.createVendorInfo(bpCreationFromWorkflowRequest));
			return ResponseEntity.ok().body(response);
		} catch (JsonProcessingException| UnirestException e) {
			e.printStackTrace();
			response.setMessage(AppConstants.FAIL_MESSAGE); 
			response.setError(e.getMessage());
			response.setErrorCode(500);
			response.setStatus("FAILED");
			System.err.println("BPDetailController.getVendorDetailsByRequestid()" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
    }
    
    @ApiOperation(value = "Post through CPI for Vendor Change", response = BusinessPartnerResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully fetched vendor details from the table"),
			@ApiResponse(code = 401, message = "You are not authorized to load document"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")})
    @PostMapping(path="/changeVendor",consumes="application/json",produces="application/json")
    public ResponseEntity<ServiceResponse<BusinessPartnerResponse>> changeVendor(@RequestBody BPCreationFromWorkflowRequest bpCreationFromWorkflowRequest) throws ClientProtocolException, IOException {
//        return cpiService.createVendorInfo();
        ServiceResponse<BusinessPartnerResponse> response = new ServiceResponse<>();
    	try {
				response.setData(cpiService.changeVendorInfo(bpCreationFromWorkflowRequest));
			return ResponseEntity.ok().body(response);
		} catch (JsonProcessingException| UnirestException e) {
			e.printStackTrace();
			response.setMessage(AppConstants.FAIL_MESSAGE); 
			response.setError(e.getMessage());
			response.setErrorCode(500);
			response.setStatus("FAILED");
			System.err.println("BPDetailController.getVendorDetailsByRequestid()" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
    }
    @ApiOperation(value = "Post through CPI for Vendor Change", response = BusinessPartnerResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully fetched vendor details from the table"),
			@ApiResponse(code = 401, message = "You are not authorized to load document"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")})
    @PostMapping(path="/extendVendor",consumes="application/json",produces="application/json")
    public ResponseEntity<ServiceResponse<BusinessPartnerResponse>> extendVendor(@RequestBody BPCreationFromWorkflowRequest bpCreationFromWorkflowRequest) throws ClientProtocolException, IOException {
//        return cpiService.createVendorInfo();
        ServiceResponse<BusinessPartnerResponse> response = new ServiceResponse<>();
    	try {
				response.setData(cpiService.extendVendorInfo(bpCreationFromWorkflowRequest));
			return ResponseEntity.ok().body(response);
		} catch (JsonProcessingException| UnirestException e) {
			e.printStackTrace();
			response.setMessage(AppConstants.FAIL_MESSAGE); 
			response.setError(e.getMessage());
			response.setErrorCode(500);
			response.setStatus("FAILED");
			System.err.println("BPDetailController.getVendorDetailsByRequestid()" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
    }
//    @PostMapping("/createVendor1")
//    public CPIVendorDetailsDto createVendor1() throws JsonProcessingException, UnirestException {
//        return cpiService.createVendorInfo1();
//    }
//    @ApiOperation(value = "Post through CPI", response = BPRequestGeneralDataDto.class)
//	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully fetched vendor details from the table"),
//			@ApiResponse(code = 401, message = "You are not authorized to load document"),
//			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")})
//    @PostMapping("/createVendor")
//    public ResponseEntity<ServiceResponse<BusinessPartnerResponse>> createVendor()  {
//    	ServiceResponse<BusinessPartnerResponse> response = new ServiceResponse<>();
//    	try {
//				response.setData(cpiService.createVendorInfo());
//			return ResponseEntity.ok().body(response);
//		} catch (JsonProcessingException| UnirestException e) {
//			e.printStackTrace();
//			response.setMessage(AppConstants.FAIL_MESSAGE); 
//			response.setError(e.getMessage());
//			response.setErrorCode(500);
//			response.setStatus("FAILED");
//			System.err.println("BPDetailController.getVendorDetailsByRequestid()" + e.getMessage());
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//		}
//    }
    //testing

}