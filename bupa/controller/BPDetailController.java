package com.incture.bupa.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.naming.NamingException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.incture.bupa.constants.AppConstants;
import com.incture.bupa.dto.*;

import com.incture.bupa.exceptions.ExecutionFault;
import com.incture.bupa.exceptions.InvalidInputFault;
import com.incture.bupa.exceptions.NoResultFault;
import com.incture.bupa.exceptions.NonUniqueRecordFault;
import com.incture.bupa.service.BPDetailService;
import com.incture.bupa.service.CPIService;
import com.mashape.unirest.http.exceptions.UnirestException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin("*")
@RequestMapping("/vendorDetails")
public class BPDetailController {
	
	@Autowired
    private BPDetailService bpDetailsService;
	
	@ApiOperation(value = "Create BP Vendor Details", response = CreateorUpdateBPResponseDto.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully added vendor details to the table"),
			@ApiResponse(code = 401, message = "You are not authorized to load document"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")})
	@PostMapping(path = "/createVendorDetails")
	public ResponseEntity<ServiceResponse<CreateorUpdateBPResponseDto>> createBPDetails(@RequestBody BPRequestGeneralDataDto bpRequestVendorDetailsDto)  {
		ServiceResponse<CreateorUpdateBPResponseDto> response = new ServiceResponse<>();
		
		  try { CreateorUpdateBPResponseDto createResponse=
		  bpDetailsService.createBPDetails(bpRequestVendorDetailsDto);
		  response.setData(createResponse); return ResponseEntity.ok().body(response);
		 
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
	
	
	@ApiOperation(value = "Get Vendor Details By Request ID", response = BPRequestGeneralDataDto.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully fetched vendor details from the table"),
			@ApiResponse(code = 401, message = "You are not authorized to load document"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")})
	@GetMapping(path = "/getVendorDetailsByRequestId/{requestId}")
	public ResponseEntity<ServiceResponse<BPRequestGeneralDataDto>> getBPDetails(@PathVariable("requestId")String requestId) throws InvalidInputFault, NoResultFault, NonUniqueRecordFault {
		ServiceResponse<BPRequestGeneralDataDto> response = new ServiceResponse<>();
		try {
				response.setData(bpDetailsService.getBPDetailsByRequestId(requestId));
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage(AppConstants.FAIL_MESSAGE); 
			response.setError(e.getMessage());
			response.setErrorCode(500);
			response.setStatus("FAILED");
			System.err.println("BPDetailController.getVendorDetailsByRequestid()" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	@ApiOperation(value = "Delete Vendor Details By Request ID", response = ResponseDto.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully deleted vendor details from the table"),
			@ApiResponse(code = 401, message = "You are not authorized to load document"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")})
	@DeleteMapping(path="/deleteVendorDetails/{requestId}")
	public ResponseDto deleteBPDetails(@PathVariable("requestId")String requestId){

		return bpDetailsService.deleteBPDetails(requestId);
	}
	@ApiOperation(value = "Update Vendor Details By Request ID", response = ResponseDto.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully updated vendor details"),
			@ApiResponse(code = 401, message = "You are not authorized to load document"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")})
	@PostMapping(path="/updateDraftStatus/{requestId}/{statusId}")
	public ResponseDto updateDraftStatus(@PathVariable("requestId")String requestId,@PathVariable("statusId")int statusId){

		return bpDetailsService.updateDraftStatus(requestId,statusId);
	}
	    
	
}