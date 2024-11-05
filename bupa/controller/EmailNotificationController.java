package com.incture.bupa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.incture.bupa.constants.AppConstants;
import com.incture.bupa.dto.ServiceResponse;
import com.incture.bupa.service.EmailNotificationService;
import com.incture.bupa.utils.MailDto;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin("*")
public class EmailNotificationController {
	
	@Autowired
    EmailNotificationService emailNotificationService;
	
	
	@ApiOperation(value = "Send Email Notification", response = ServiceResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully added vendor details to the table"),
			@ApiResponse(code = 401, message = "You are not authorized to load document"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")})
	@PostMapping(path = "/emailNotificationForTask")
	// Changes Done : Author - Dheeraj Kumar ( Added Company Code , Vendor Number , Country Name , Purchasing Org )
	//Sprint-2
	//test
	public ResponseEntity<ServiceResponse> emailNotificationForTask(@RequestParam(required=false) String taskName,@RequestParam(required=false) String processType,@RequestParam(required=false) String requestId,@RequestParam(required=false) String vendorAccountGroup,@RequestParam(required=false) String businessPartnerName,@RequestParam(required=false) String companyCode,@RequestParam(required=false) String businessPartnerNumber,@RequestParam(required=false) String countryName,@RequestParam(required=false) String purchasingOrg,@RequestBody String userEmail)  {	
		ServiceResponse response = new ServiceResponse<>();
		try {
			MailDto mailDto=new MailDto();
			mailDto.setTaskDescription(taskName);
			mailDto.setRequestId(requestId);
			mailDto.setVendorAccountGroup(vendorAccountGroup);
			mailDto.setBusinessPartnerName(businessPartnerName);
			mailDto.setProcessType(processType);
			// Changes Done : Author - Dheeraj Kumar ( Added Company Code , Vendor Number , Country Name , Purchasing Org )
			mailDto.setCompanyCode(companyCode);
			mailDto.setBusinessPartnerNumber(businessPartnerNumber);
			mailDto.setCountryName(countryName);
			mailDto.setPurchasingOrg(purchasingOrg);
			ServiceResponse emailNotification= emailNotificationService.sendMailForTask(mailDto,userEmail);
			response.setData(emailNotification);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage(AppConstants.FAIL_MESSAGE); 
			response.setError(e.getMessage());
			response.setErrorCode(500);
			response.setStatus("FAILED");
			System.err.println("BPDetailController.createVendorDetails()" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	@ApiOperation(value = "Send Rejection Email Notification", response = ServiceResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully added vendor details to the table"),
			@ApiResponse(code = 401, message = "You are not authorized to load document"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")})
	@PostMapping(path = "/emailRejectionNotification/{notificationId}")
	public ResponseEntity<ServiceResponse> emailRejectioNotification(@PathVariable int notificationId,@RequestBody MailDto mailDto)  {
		ServiceResponse response = new ServiceResponse<>();
		try {
			mailDto.setNotificationCode(notificationId);
			ServiceResponse emailNotification= emailNotificationService.sendMail(mailDto);
			response.setData(emailNotification);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage(AppConstants.FAIL_MESSAGE); 
			response.setError(e.getMessage());
			response.setErrorCode(500);
			response.setStatus("FAILED");
			System.err.println("BPDetailController.createVendorDetails()" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
