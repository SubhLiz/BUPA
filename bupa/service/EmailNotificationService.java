package com.incture.bupa.service;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.incture.bupa.constants.AppConstants;
import com.incture.bupa.dto.BPRequestGeneralDataDto;
import com.incture.bupa.dto.ServiceResponse;
import com.incture.bupa.entities.BPGeneralData;
import com.incture.bupa.repository.BPDetailsRepository;
import com.incture.bupa.utils.ApplicationConstants;
import com.incture.bupa.utils.DestinationUtil;
import com.incture.bupa.utils.HelperClass;
import com.incture.bupa.utils.MailDto;
import com.incture.bupa.utils.MailRequestDto;
import com.incture.bupa.utils.MailSenderUtil;
import com.incture.bupa.utils.ObjectMapperUtils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import net.minidev.json.JSONObject;

@Service
public class EmailNotificationService {
	private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

	@Autowired
	private MailFormationService mailFormationService;

	@Autowired
	private BPUserDetailsService bpUserDetailsService;

	@Autowired
	private MailSenderUtil mailSenderUtil;

	@Autowired
	private DestinationUtil destinationUtil;

	@Autowired
	private WorkRuleService workRuleService;

	@Autowired
	private BPDetailsRepository bpVendorDetailsRepository;

	@Autowired
	WebClient webClient;

//	@Value("${cpi-email.from}")
//	private String from;
//	
//	@Value("${vendor.inboxLink}")
//	private String inboxLink;

	@Value("${spring.profiles.active}")
	private String profile;

	private static String uri;

	private static String tokenUrl;

	private static String clientId;

	private static String clientSecret;

	private static String emailFrom;

	private static String iwmInboxLink;

	private HashMap<String, String> getDestinationDetails() throws ClientProtocolException, IOException {
		HashMap<String, String> hashMap = new HashMap<>();
		String destDetails = destinationUtil.readMdgDestination("viatris-vm-java", null, null);
		System.out.println("****");

		org.json.JSONObject resObj = new org.json.JSONObject(destDetails);
		logger.info("Json object from destination :" + resObj);

		emailFrom = resObj.optJSONObject("destinationConfiguration").optString("from");
		iwmInboxLink = resObj.optJSONObject("destinationConfiguration").optString("inboxLink");
		hashMap.put("emailFrom", emailFrom);
		hashMap.put("iwmInboxLink", iwmInboxLink);

		System.out.println("****");
		return hashMap;
	}

//    public String accessToken() throws JsonMappingException, JsonProcessingException {
//		uri= ApplicationConstants.CPI_URI;
//		tokenUrl=ApplicationConstants.CPI_HOST;
//		clientId=ApplicationConstants.CPI_CLIENT_ID;
//		clientSecret=ApplicationConstants.CPI_CLIENT_SECRET;
//
//		String url ="https://"+ tokenUrl + "/oauth/token?grant_type=client_credentials";
//		RestTemplate template = new RestTemplate();
//		HttpHeaders headers = new HttpHeaders();
//		com.nimbusds.jose.util.Base64 encode = com.nimbusds.jose.util.Base64.encode(clientId + ":" + clientSecret);
//		headers.add("Authorization", "Basic " + encode.toString());
//		HttpEntity<String> entity = new HttpEntity<>(headers);
//		ResponseEntity<String> response = template.postForEntity(url, entity, String.class);
//
//		return new ObjectMapper().readTree(response.getBody()).get("access_token").asText();
//	}
	public String getAccessToken(String clientid, String clientsecret, String tokenUrl) {

		URI myURI = null;
		try {
			myURI = new URI(tokenUrl);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		URI finalMyURI = myURI;
		JSONObject authenticationResponseObject = WebClient.builder()
				.filter(basicAuthentication(clientid, clientsecret)).build().post()
				.uri(uriBuilder -> uriBuilder.scheme(finalMyURI.getScheme()).host(finalMyURI.getHost())
						.path(finalMyURI.getPath()).queryParam("grant_type", "client_credentials").build())
				.retrieve().bodyToMono(JSONObject.class).block();

		return authenticationResponseObject.get("access_token").toString();
	}

	public JsonNode getDataFromEmailRules(int notificationCode) {
		String accessToken, url = null;
		try {
			System.out.println("****");
			System.out.println(notificationCode);
			System.out.println("****");
			String destDetails = destinationUtil.readMdgDestination("vm-wr-services", null, null);

			org.json.JSONObject resObj = new org.json.JSONObject(destDetails);
			logger.info("Json object from destination :" + resObj);
			logger.info("Client id: " + resObj.optJSONObject("destinationConfiguration").optString("clientId"));
			logger.info("clientSecret : " + resObj.optJSONObject("destinationConfiguration").optString("clientSecret"));
			logger.info("tokenServiceURL: "
					+ resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));
			logger.info("url: " + resObj.optJSONObject("destinationConfiguration").optString("URL"));
			accessToken = getAccessTokenForRules(resObj.optJSONObject("destinationConfiguration").optString("clientId"),
					resObj.optJSONObject("destinationConfiguration").optString("clientSecret"),
					resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));

			url = resObj.optJSONObject("destinationConfiguration").optString("URL") + "/rest/v1/invoke-rules";

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String json = "{\n" + "    \"decisionTableId\": null,\n"
				+ "    \"decisionTableName\": \"VM_EMAIL_NOTIFICATION_RULES\",\n" + "    \"version\": \"v1\",\n"
				+ "    \"conditions\": [\n" + "        {\n" + "            \"VM_CONDITIONS.VM_NOTIFICATION_CODE\": "
				+ notificationCode + "\n" + "        }\n" + "    ],\n" + "    \"systemFilters\": null,\n"
				+ "    \"rulePolicy\": null,\n" + "    \"validityDate\": null\n" + "}";
		JsonNode ruleResponse = webClient.post().uri(url).headers(h -> h.setBearerAuth(accessToken))
				.header("Content-Type", "application/json").body(BodyInserters.fromValue(json)).retrieve()
				.bodyToMono(JsonNode.class).block();

		logger.info("Response : " + ruleResponse);

		return ruleResponse;
//		 return null;
	}

	public String getAccessTokenForRules(String clientid, String clientsecret, String tokenUrl) throws JsonMappingException, JsonProcessingException {

		RestTemplate template = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		com.nimbusds.jose.util.Base64 encode = com.nimbusds.jose.util.Base64.encode(clientid + ":" + clientsecret);
		headers.add("Authorization", "Basic " + encode.toString());
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = template.postForEntity(tokenUrl+"?grant_type=client_credentials", entity, String.class);

		return new ObjectMapper().readTree(response.getBody()).get("access_token").asText();
	}

	public void sendMailThroughCPI(MailRequestDto mailRequestDto)
			throws UnirestException, ClientProtocolException, IOException {
		String accessToken, url = null;
		HashMap<String, String> hashMap = getDestinationDetails();
		try {
			String destDetails = destinationUtil.readMdgDestination("mdg-vm-cpi", null, null);

			org.json.JSONObject resObj = new org.json.JSONObject(destDetails);
			logger.info("Json object from destination :" + resObj);
			logger.info("Client id: " + resObj.optJSONObject("destinationConfiguration").optString("clientId"));
			logger.info("clientSecret : " + resObj.optJSONObject("destinationConfiguration").optString("clientSecret"));
			logger.info("tokenServiceURL: "
					+ resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));
			logger.info("url: " + resObj.optJSONObject("destinationConfiguration").optString("URL"));
			accessToken = getAccessToken(resObj.optJSONObject("destinationConfiguration").optString("clientId"),
					resObj.optJSONObject("destinationConfiguration").optString("clientSecret"),
					resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));
			logger.info("access_token: " + accessToken);
			url = resObj.optJSONObject("destinationConfiguration").optString("URL") + "/http/Viatris/CP/Send_Email";
//	           

			JSONObject inputBody = new JSONObject();
			JSONObject inputParameters = new JSONObject();

			inputBody.put("mail_details", inputParameters);
			inputParameters.put("from", hashMap.get("emailFrom"));
//		        inputParameters.put("to","Vaibhav.Anand@viatris.com");
			inputParameters.put("to", mailRequestDto.getEmailTo());
			inputParameters.put("subject", mailRequestDto.getSubject());
			inputParameters.put("body", mailRequestDto.getBodyMessage());
			inputParameters.put("bcc", "");
			inputParameters.put("cc", "");

//		        inputBody.put("mail_details", inputParameters);
//		        inputParameters.put("from", "Vaibhav.Anand@viatris.com");
//		        inputParameters.put("to", "Vaibhav.Anand@viatris.com");
//		        inputParameters.put("subject", "Hi");
//		        inputParameters.put("body", "Hello");
//		        inputParameters.put("bcc", "");
//		        inputParameters.put("cc", "");
			String body = new ObjectMapper().writeValueAsString(inputBody);
			System.out.println("**%% ODATA" + body);
			HttpResponse<String> response = Unirest.post(url).header("authorization", "Bearer " + accessToken)
					.header("Content-Type", "application/json").header("Accept", "application/json").body(body)
					.asString();
			int status = response.getStatus();
			System.out.println(
					status + "************************************************************************************"
							+ response + "  " + response.getBody());

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public void mailCPI(int notificationCode, MailDto mailDto, String mailReceivers)
			throws UnirestException, ClientProtocolException, IOException {
		HashMap<String, String> hashMap = getDestinationDetails();
		if (!HelperClass.isEmpty(mailDto.getBpCreationFromWorkflowRequest().getRequestId())) {
			MailRequestDto mailRequestDto = new MailRequestDto();
			ServiceResponse responseMessage = new ServiceResponse<>();
			JsonNode ruleResponse = getDataFromEmailRules(notificationCode);
			if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {

				mailRequestDto.setRequestId(mailDto.getBpCreationFromWorkflowRequest().getRequestId() == null ? ""
						: mailDto.getBpCreationFromWorkflowRequest().getRequestId());
				mailRequestDto
						.setTaskDescription(mailDto.getTaskDescription() == null ? "" : mailDto.getTaskDescription());
				System.out.println("***");
				System.out.println(mailRequestDto.toString());
				System.out.println("***");
				System.out.println(ruleResponse.get("data").get("result").get(0)
						.get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_SUBJECT").asText());
				String subject = "";
				if (profile.toUpperCase(Locale.ROOT).equalsIgnoreCase("PROD")) {
					subject = ruleResponse.get("data").get("result").get(0).get("VM_EMAIL_NOTIFICATION_CODE_ACTION")
							.get(0).get("VM_EMAIL_SUBJECT").asText();
				} else {
					subject = profile.toUpperCase(Locale.ROOT) + ": " + ruleResponse.get("data").get("result").get(0)
							.get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_SUBJECT").asText();
				}

				String link = "<a href=\"" + hashMap.get("iwmInboxLink") + "\">" + "here</a>";

				subject = subject
						.replace("<Request Id>",
								mailDto.getBpCreationFromWorkflowRequest().getRequestId() == null ? ""
										: mailDto.getBpCreationFromWorkflowRequest().getRequestId())
						.replace("<vendorAccountgroup>",
								mailDto.getBpCreationFromWorkflowRequest().getVendorAccountGroup() == null ? ""
										: mailDto.getBpCreationFromWorkflowRequest().getVendorAccountGroup())
						.replace("<Business Partner name>",
								mailDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName() == null ? ""
										: mailDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName())
						.replace("<task-name>",
								mailDto.getTaskDescription() == null ? "" : mailDto.getTaskDescription());

				System.out.println(ruleResponse.get("data").get("result").get(0)
						.get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_BODY").asText());
				String body = ruleResponse.get("data").get("result").get(0).get("VM_EMAIL_NOTIFICATION_CODE_ACTION")
						.get(0).get("VM_EMAIL_BODY").asText();
				body = body
						.replace("<task-name>",
								mailDto.getTaskDescription() == null ? "" : mailDto.getTaskDescription())
						.replace("|", "<br>").replace("\'", "").replace("<Inbox Link>", link);

				mailRequestDto.setSubject(subject);
				mailRequestDto.setBodyMessage(body);

				String toMailReceivers = bpUserDetailsService.getUserMail(mailReceivers);
				mailRequestDto.setEmailTo(toMailReceivers);
				sendMailThroughCPI(mailRequestDto);
			} else {
				responseMessage.setMessage("Sending Mail Failed!!");
				responseMessage.setStatus(AppConstants.FAIL_MESSAGE_MAIL);
				responseMessage.setError(null);
				logger.error("No Response received from the rules");
			}
			mailRequestDto.setRequestId("1004");
		}
	}

	public ServiceResponse sendMail(MailDto mailDto) throws UnirestException, ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		MailRequestDto mailRequestDto = new MailRequestDto();
		ServiceResponse responseMessage = new ServiceResponse<>();
		System.out.println("****");
		System.out.println(mailDto.toString());
		System.out.println("****");

		if (mailDto.getNotificationCode() == 1) {
			mailCPI(mailDto.getNotificationCode(), mailDto,
					mailDto.getWorkflowTaskDetails().getLocalApprover_RecipientUsers());
		}
		if (mailDto.getNotificationCode() == 2 || mailDto.getNotificationCode() == 6
				|| mailDto.getNotificationCode() == 13) {
			mailCPI(mailDto.getNotificationCode(), mailDto, mailDto.getWorkflowTaskDetails().getRequestor());
		}
		if (mailDto.getNotificationCode() == 3 || mailDto.getNotificationCode() == 5
				|| mailDto.getNotificationCode() == 8 || mailDto.getNotificationCode() == 12
				|| mailDto.getNotificationCode() == 15 || mailDto.getNotificationCode() == 18) {
			mailCPI(mailDto.getNotificationCode(), mailDto,
					mailDto.getWorkflowTaskDetails().getGmdmApprover_RecipientUsers());
		}
		if (mailDto.getNotificationCode() == 4 || mailDto.getNotificationCode() == 11
				|| mailDto.getNotificationCode() == 17 || mailDto.getNotificationCode() == 19) {
			mailCPI(mailDto.getNotificationCode(), mailDto,
					mailDto.getWorkflowTaskDetails().getFinanceApprover_RecipientUsers());
		}
		if (mailDto.getNotificationCode() == 7) {
			mailCPI(mailDto.getNotificationCode(), mailDto,
					mailDto.getWorkflowTaskDetails().getGisApprover_RecipientUsers());
		}
		if (mailDto.getNotificationCode() == 9 || mailDto.getNotificationCode() == 21) {
			mailCPI(mailDto.getNotificationCode(), mailDto,
					mailDto.getWorkflowTaskDetails().getSourcingApprover_RecipientUsers());
		}
		if (mailDto.getNotificationCode() == 10) {
			mailCPI(mailDto.getNotificationCode(), mailDto,
					mailDto.getWorkflowTaskDetails().getCategoryLeadApprover_RecipientUsers());
		}
		if (mailDto.getNotificationCode() == 14 || mailDto.getNotificationCode() == 16) {
			mailCPI(mailDto.getNotificationCode(), mailDto,
					mailDto.getWorkflowTaskDetails().getRegionalSourcingApprover_RecipientUsers());
		}
		if (mailDto.getNotificationCode() == 20 || mailDto.getNotificationCode() == 22) {
			mailCPI(mailDto.getNotificationCode(), mailDto,
					mailDto.getWorkflowTaskDetails().getQualityApprover_RecipientUsers());
		}
		if (mailDto.getNotificationCode() == 24) {
			mailCPIForRejection(mailDto.getNotificationCode(), mailDto,
					mailDto.getWorkflowTaskDetails().getRequestor());
		}

		return responseMessage;
	}

	private void mailCPIForRejection(int notificationCode, MailDto mailDto, String mailReceivers)
			throws UnirestException, ClientProtocolException, IOException {
		if (!HelperClass.isEmpty(mailDto.getBpCreationFromWorkflowRequest().getRequestId())) {
			MailRequestDto mailRequestDto = new MailRequestDto();
			ServiceResponse responseMessage = new ServiceResponse<>();
			JsonNode ruleResponse = getDataFromEmailRules(notificationCode);
			if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
				String subject = "";
				if (profile.toUpperCase(Locale.ROOT).equalsIgnoreCase("PROD")) {
					subject = ruleResponse.get("data").get("result").get(0).get("VM_EMAIL_NOTIFICATION_CODE_ACTION")
							.get(0).get("VM_EMAIL_SUBJECT").asText();
				} else {
					subject = profile.toUpperCase(Locale.ROOT) + ": " + ruleResponse.get("data").get("result").get(0)
							.get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_SUBJECT").asText();
				}
				subject = subject.replace("<processType>",
						mailDto.getBpCreationFromWorkflowRequest().getBpRequestType());
				System.out.println(ruleResponse.get("data").get("result").get(0)
						.get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_BODY").asText());
				String body = ruleResponse.get("data").get("result").get(0).get("VM_EMAIL_NOTIFICATION_CODE_ACTION")
						.get(0).get("VM_EMAIL_BODY").asText();
				body = body.replace("<requestId>", mailDto.getBpCreationFromWorkflowRequest().getRequestId());
				mailRequestDto.setSubject(subject);
				mailRequestDto.setBodyMessage(body);
				String toMailReceivers = bpUserDetailsService.getUserMail(mailReceivers);
				mailRequestDto.setEmailTo(toMailReceivers);
				sendMailThroughCPI(mailRequestDto);
//	            responseMessage=mailService.sendMail(mailRequestDto);
			} else {
				responseMessage.setMessage("Sending Mail Failed!!");
				responseMessage.setStatus(AppConstants.FAIL_MESSAGE_MAIL);
				responseMessage.setError(null);
				logger.error("No Response received from the rules");
			}
			mailRequestDto.setRequestId("1004");
		}

	}

	public ServiceResponse sendMailForTask(MailDto mailDto, String userEmail)
			throws UnirestException, ClientProtocolException, IOException {
		MailRequestDto mailRequestDto = new MailRequestDto();
		ServiceResponse responseMessage = new ServiceResponse<>();
		System.out.println("****");
		System.out.println(mailDto.toString());
		System.out.println("****");
		if (mailDto.getTaskDescription().contains("Requestor")) {
			System.out.println("**In Requestor**");
			mailCPIForTask(2, mailDto, userEmail.substring(1, userEmail.length() - 1));
		} else {
			mailCPIForTask(1, mailDto, userEmail.substring(1, userEmail.length() - 1));
		}

		return responseMessage;
	}

	private void mailCPIForTask(int notificationCode, MailDto mailDto, String mailReceivers)
			throws UnirestException, ClientProtocolException, IOException {
		HashMap<String, String> hashMap = getDestinationDetails();
		if (!HelperClass.isEmpty(mailDto.getRequestId())) {
			MailRequestDto mailRequestDto = new MailRequestDto();
			ServiceResponse responseMessage = new ServiceResponse<>();
			JsonNode ruleResponse = getDataFromEmailRules(notificationCode);
			if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {

				mailRequestDto.setRequestId(mailDto.getRequestId() == null ? "" : mailDto.getRequestId());
				mailRequestDto
						.setTaskDescription(mailDto.getTaskDescription() == null ? "" : mailDto.getTaskDescription());
				System.out.println("***");
				System.out.println(mailRequestDto.toString());
				System.out.println("***");
				System.out.println(ruleResponse.get("data").get("result").get(0)
						.get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_SUBJECT").asText());
				String subject = "";
				if (profile.toUpperCase(Locale.ROOT).equalsIgnoreCase("PROD")) {
					subject = ruleResponse.get("data").get("result").get(0).get("VM_EMAIL_NOTIFICATION_CODE_ACTION")
							.get(0).get("VM_EMAIL_SUBJECT").asText();
				} else {
					subject = profile.toUpperCase(Locale.ROOT) + ": " + ruleResponse.get("data").get("result").get(0)
							.get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_SUBJECT").asText();
				}
//	            String subject=profile.toUpperCase(Locale.ROOT)+": "+ruleResponse.get("data").get("result").get(0).get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_SUBJECT").asText();

				String link = "<a href=\"" + hashMap.get("iwmInboxLink") + "\">" + "here</a>";
				if (notificationCode == 1) {
					String processName = "";
					if (mailDto.getProcessType().equalsIgnoreCase("extend")) {
						processName = "Extension";
					} else if (mailDto.getProcessType().equalsIgnoreCase("create")) {
						processName = "Creation";
					} else {
						processName = "Change";
					}

					// Changes Done : Author - Dheeraj Kumar ( Adding Company Code,Vendor Number ,
					// country name )
					subject = subject
							.replace("<Request Id>", mailDto.getRequestId() == null ? "" : mailDto.getRequestId())
							.replace("<vendorAccountgroup>",
									mailDto.getVendorAccountGroup() == null ? "" : mailDto.getVendorAccountGroup())
							.replace("<Business Partner name>",
									mailDto.getBusinessPartnerName() == null ? "" : mailDto.getBusinessPartnerName())
							.replace("<processType>", processName == null ? "" : processName)
							.replace("<task-name>",
									mailDto.getTaskDescription() == null ? "" : mailDto.getTaskDescription());
				} else {
					subject = subject
							.replace("<Request Id>", mailDto.getRequestId() == null ? "" : mailDto.getRequestId())
							.replace("<vendorAccountgroup>",
									mailDto.getVendorAccountGroup() == null ? "" : mailDto.getVendorAccountGroup())
							.replace("<Business Partner name>",
									mailDto.getBusinessPartnerName() == null ? "" : mailDto.getBusinessPartnerName())
							.replace("<task-name>",
									mailDto.getTaskDescription() == null ? "" : mailDto.getTaskDescription());
				}

				System.out.println(ruleResponse.get("data").get("result").get(0)
						.get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_BODY").asText());
				String body = ruleResponse.get("data").get("result").get(0).get("VM_EMAIL_NOTIFICATION_CODE_ACTION")
						.get(0).get("VM_EMAIL_BODY").asText();
				body = body
						.replace("<task-name>",
								mailDto.getTaskDescription() == null ? "" : mailDto.getTaskDescription())
						.replace("<companyCode>", mailDto.getCompanyCode() == null ? "" : mailDto.getCompanyCode())
						.replace("<businessPartnerNumber>",
								mailDto.getBusinessPartnerNumber() == null ? "" : mailDto.getBusinessPartnerNumber())
						.replace("<countryName>", mailDto.getCountryName() == null ? "" : mailDto.getCountryName())
						.replace("<Business Partner name>",
								mailDto.getBusinessPartnerName() == null ? "" : mailDto.getBusinessPartnerName())
						.replace("<purchasingOrg>",
								mailDto.getPurchasingOrg() == null ? "" : mailDto.getPurchasingOrg())
						.replace("|", "<br>").replace("\'", "").replace("<Inbox Link>", link);

				mailRequestDto.setSubject(subject);
				mailRequestDto.setBodyMessage(body);

				String toMailReceivers = bpUserDetailsService.getUserMail(mailReceivers);
				mailRequestDto.setEmailTo(toMailReceivers);
				sendMailThroughCPI(mailRequestDto);
			} else {
				responseMessage.setMessage("Sending Mail Failed!!");
				responseMessage.setStatus(AppConstants.FAIL_MESSAGE_MAIL);
				responseMessage.setError(null);
				logger.error("No Response received from the rules");
			}
			mailRequestDto.setRequestId("1004");
		}

	}

}
