package com.incture.bupa.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.incture.bupa.dto.BPEmailDto;
import com.incture.bupa.service.EmailNotificationService;
import com.incture.bupa.service.TestService;
import com.incture.bupa.service.WorkRuleService;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@CrossOrigin("*")
@RequestMapping("/test/v1")
public class TestController {

	@Autowired
	private WorkRuleService ruleService;
	@Autowired
	private EmailNotificationService emailNotificationService;
	
	@Autowired
	private TestService testService;

	@GetMapping
	ResponseEntity<BPEmailDto> getUser(@RequestBody BPEmailDto BPEmailDto) {
		BPEmailDto obj = new BPEmailDto();
		obj.setDoNotUse(BPEmailDto.isDoNotUse());
		obj.setEmailAddress(BPEmailDto.getEmailAddress());
		return new ResponseEntity<BPEmailDto>(obj, HttpStatus.OK);
	}

	@GetMapping(path = "/workrules")
	ResponseEntity<JsonNode> getDataFromRules() throws UnirestException {
		String workflowApprover = "GMDM_Approver_Egypt";
		JsonNode obj = ruleService.getDataFromRules(workflowApprover);
		return new ResponseEntity<JsonNode>(obj, HttpStatus.OK);
	}

	@GetMapping(path = "/workrules/email")
	ResponseEntity<JsonNode> getDataFromEmailRules() throws UnirestException {
		int workflowApprover = 18;
		JsonNode obj = emailNotificationService.getDataFromEmailRules(workflowApprover);
		return new ResponseEntity<JsonNode>(obj, HttpStatus.OK);
	}
	

	@GetMapping("/odata")
	public ResponseEntity<Object> getDataFromOdata() throws ClientProtocolException, IOException {
		return testService.getDataFromOdata();
	}
	
	 
	
//	@GetMapping("/iasUserDetails")
//	public ResponseEntity<String> getIasUserDetails() throws ClientProtocolException, IOException {
//		return testService.getIasUserDetails();
//	}

	@GetMapping(path = "/smarty")
	ResponseEntity<?> getData() throws UnirestException {
		String apiKey = "176017907803652814";
		String add="https://us-street.api.smartystreets.com/street-address?auth-id=3c286507-0524-a8c8-de59-cdcb8db6f781&auth-token=BexKJLKHkHXUWm7hZCOh&street=2335+S+State+St&city=Provo&state=UT&candidates=10&match=enhanced";
//		String addressURL = "https://us-street.api.smartystreets.com/street-address?key=" + apiKey
//				+ "&street=2335+S+State+St&city=Provo&state=UT&candidates=10&match=enhanced";

		try {
			URL url = new URL(add);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Set headers here if required, such as Origin or Referer

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder response = new StringBuilder();

			while ((line = reader.readLine()) != null) {
				response.append(line);
			}

			reader.close();
			connection.disconnect();

			System.out.println(response.toString());
			return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
			}

//	@GetMapping(path="/workrules/cpiemail")
//	 void sendMailThroughCPI() throws UnirestException, JsonProcessingException{
//		emailNotificationService.mailCPI();
//	}
	@GetMapping("/getToken")
	public String method(@RequestHeader(name = "Authorization", required = false) String authorization) {
		return authorization;
	}
}
