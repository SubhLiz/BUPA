package com.incture.bupa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incture.bupa.utils.DestinationUtil;

import net.minidev.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Service
public class WorkRuleService {
	private static final Logger log = LoggerFactory.getLogger(WorkRuleService.class);
	@Autowired
	WebClient webClient;

	@Autowired
	private DestinationUtil destinationUtil;
	
	
	public JsonNode getDataFromRules(String workflowApprover) {
		 String accessToken,url=null;
		 try {
			 System.out.println("****");
			 System.out.println(workflowApprover);
			 System.out.println("****");
	            String destDetails = destinationUtil.readMdgDestination("vm-wr-services", null, null);


	            org.json.JSONObject resObj = new org.json.JSONObject(destDetails);
	            log.info("Json object from destination :"+resObj);
	            log.info("Client id: "+resObj.optJSONObject("destinationConfiguration").optString("clientId"));
	            log.info("clientSecret : "+resObj.optJSONObject("destinationConfiguration").optString("clientSecret"));
	            log.info("tokenServiceURL: "+resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));
	            log.info("url: "+resObj.optJSONObject("destinationConfiguration").optString("URL"));
	            accessToken =getAccessToken(resObj.optJSONObject("destinationConfiguration").optString("clientId")
	                    ,resObj.optJSONObject("destinationConfiguration").optString("clientSecret")
	                    ,resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));

	            url=resObj.optJSONObject("destinationConfiguration").optString("URL")+"/rest/v1/invoke-rules";

	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        }
		 String json = "{\n" +
	                "  \"decisionTableId\": null,\n" +
	                "  \"decisionTableName\": \"WORKFLOW_EMAIL_ID_RULES\",\n" +
	                "  \"version\": \"v1\",\n"+
	                "  \"conditions\": [\n" +
	                "    {\n" +
	                "      \"WORKFLOW_EMAIL_ID.VM_WOKFLOW_APPROVER\": \""+"GMDM_Approver_Egypt"+"\"\n" +
	                "    }\n" +
	                "  ],\n" +
	                "  \"systemFilters\": null,\n" +
	                "  \"rulePolicy\": null,\n" +
	                "  \"validityDate\": null\n" +
	                "}";
		 JsonNode ruleResponse = webClient.post()
	                .uri(url)
	                .headers(h -> h.setBearerAuth(accessToken))
	                .header("Content-Type", "application/json")
	                .body(BodyInserters.fromValue(json))
	                .retrieve()
	                .bodyToMono(JsonNode.class)
	                .block();

	        log.info("Response : "+ ruleResponse);

	        return ruleResponse;
//		 return null;
	}
	public String getAccessToken(String clientid, String clientsecret, String tokenUrl) throws JsonMappingException, JsonProcessingException{

		RestTemplate template = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		com.nimbusds.jose.util.Base64 encode = com.nimbusds.jose.util.Base64.encode(clientid + ":" + clientsecret);
		headers.add("Authorization", "Basic " + encode.toString());
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = template.postForEntity(tokenUrl+"?grant_type=client_credentials", entity, String.class);

		return new ObjectMapper().readTree(response.getBody()).get("access_token").asText();
    }
}