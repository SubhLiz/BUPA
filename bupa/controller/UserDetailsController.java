package com.incture.bupa.controller;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.incture.bupa.constants.AppConstants;
import com.incture.bupa.dto.BPUserActionDto;
import com.incture.bupa.dto.BPUserDetailsDto;
import com.incture.bupa.dto.ServiceResponse;
import com.incture.bupa.service.BPUserDetailsService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin("*")
public class UserDetailsController {

    @Autowired
    private BPUserDetailsService userDetailsService;

    @GetMapping("/getUserList")
    public void getUserList() {
         userDetailsService.getUserList();
    }

    @GetMapping("/saveUsername")
    public void saveUsername() {
        userDetailsService.saveUserName();
    }

    @GetMapping("/getUserDetails")
    public List<BPUserDetailsDto> getUserDetails() {
        return userDetailsService.getAllUserDetails();
    }
    @GetMapping("/btpUserDetails")
    public JSONObject btpUserDetails(@RequestParam String endPoint) {
    	return userDetailsService.btpUserDetails(1,100);
    }
    
    @GetMapping("/fetchUserDetails")
    public Map<String,String> fetchUserDetails() {
    	return userDetailsService.fetchUserDetails();
    }
    @ApiOperation(value = "Get workflow mail address", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully fetched correct mail addresses"),
			@ApiResponse(code = 401, message = "You are not authorized to load document"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")})
	@PostMapping("/getUserMail")
	public ResponseEntity<ServiceResponse<String>> getUserMail(@RequestBody Map<String, String> values)  {
		ServiceResponse<String> response = new ServiceResponse<>();
		try {
			String userMail= userDetailsService.getUserMail(values.get("userEmailList"));
			response.setData(userMail);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage(AppConstants.FAIL_MESSAGE); 
			response.setError(e.getMessage());
			response.setErrorCode(500);
			response.setStatus("FAILED");
			System.err.println("AuditLogController.saveAndDeleteAuditLogInfo()" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
    @PostMapping("/saveUserAction")
	public ResponseEntity<ServiceResponse<String>> saveUserAction(@RequestBody BPUserActionDto bpUserActionDto)  {
		ServiceResponse<String> response = new ServiceResponse<>();
		try {
			String userActionResponse= userDetailsService.saveUserAction(bpUserActionDto);
			response.setData(userActionResponse);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage(AppConstants.FAIL_MESSAGE); 
			response.setError(e.getMessage());
			response.setErrorCode(500);
			response.setStatus("FAILED");
			System.err.println("AuditLogController.saveAndDeleteAuditLogInfo()" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
