package com.incture.bupa.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.client.ClientProtocolException;
//import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.incture.bupa.constants.AppConstants;
import com.incture.bupa.dto.BPBankInformationDto;
import com.incture.bupa.dto.BPBusinessPartnerOrderingAddressDto;
import com.incture.bupa.dto.BPBusinessPartnerRemittanceAddressDto;
import com.incture.bupa.dto.BPCompanyCodeInfoDto;
import com.incture.bupa.dto.BPContactInformationDto;
import com.incture.bupa.dto.BPCreationFromWorkflowRequest;
import com.incture.bupa.dto.BPEmailDto;
import com.incture.bupa.dto.BPFaxInfoDto;
import com.incture.bupa.dto.BPMobilePhoneDto;
import com.incture.bupa.dto.BPPurchaseOrgAdditionalDataDto;
import com.incture.bupa.dto.BPPurchasingOrgDetailDto;
import com.incture.bupa.dto.BPRequestGeneralDataDto;
import com.incture.bupa.dto.BPTelephoneDto;
import com.incture.bupa.dto.BPVendorClassificationAttributeDto;
import com.incture.bupa.dto.BPVendorClassificationEntityDto;
import com.incture.bupa.dto.BPWithholdingTaxDto;
import com.incture.bupa.dto.BusinessPartnerResponse;
import com.incture.bupa.dto.CPIBankDto;
import com.incture.bupa.dto.CPIClassificationDto;
import com.incture.bupa.dto.CPIClassificationItemDto;
import com.incture.bupa.dto.CPICompanyDataDto;
import com.incture.bupa.dto.CPIContactDto;
import com.incture.bupa.dto.CPIContactEmailDto;
import com.incture.bupa.dto.CPIContactPhoneDto;
import com.incture.bupa.dto.CPIDunningDataDto;
import com.incture.bupa.dto.CPIEmailDto;
import com.incture.bupa.dto.CPIFaxDto;
import com.incture.bupa.dto.CPIInvoicePartyDto;
import com.incture.bupa.dto.CPIOrderingAddressDto;
import com.incture.bupa.dto.CPIPhoneDto;
import com.incture.bupa.dto.CPIPlantDto;
import com.incture.bupa.dto.CPIPurchaseOrgDataDto;
import com.incture.bupa.dto.CPIVendorDetailsDto;
import com.incture.bupa.dto.CPIwTaxDto;
import com.incture.bupa.dto.ServiceResponse;
import com.incture.bupa.repository.BPDetailsRepository;
import com.incture.bupa.utils.DestinationUtil;
import com.incture.bupa.utils.HelperClass;
import com.incture.bupa.utils.MailRequestDto;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
 

@Service
public class CPIService {
	private static final Logger log = LoggerFactory.getLogger(CPIService.class);

	@Autowired
	WebClient webClient;
	@Autowired
	BPDetailService bpDetailService;

	@Autowired
	EmailNotificationService emailNotificationService;

	@Autowired
	TestService testService;

	@Autowired
	private BPDetailsRepository bpVendorDetailsRepository;

	@Autowired
	private DestinationUtil destinationUtil;
	
	@Autowired
	private AuditLogService auditLogService;
	 
	int draftStatusId = 1;

	int successfulEmailNotificationCode = 23;

	@Value("${spring.profiles.active}")
	private String profile;

//	public String getAccessToken() {
//		JSONObject authenticationResponseObject = WebClient.builder()
//				.filter(basicAuthentication(ApplicationConstants.CPI_CLIENT_ID, ApplicationConstants.CPI_CLIENT_SECRET))
//				.build().post()
//				.uri(uriBuilder -> uriBuilder.scheme("https").host(ApplicationConstants.CPI_HOST)
//						.path(ApplicationConstants.CPI_PATH).queryParam("grant_type", "client_credentials").build())
//				.retrieve().bodyToMono(JSONObject.class).block();
//		System.out.println(authenticationResponseObject.toString());
//		return authenticationResponseObject.get("access_token").toString();
//	}

	private static String uri;

	private static String tokenUrl;

	private static String clientId;

	private static String clientSecret;

//	public String accessToken() throws JsonMappingException, JsonProcessingException {
//		uri = ApplicationConstants.CPI_URI;
//		tokenUrl = ApplicationConstants.CPI_HOST;
//		clientId = ApplicationConstants.CPI_CLIENT_ID;
//		clientSecret = ApplicationConstants.CPI_CLIENT_SECRET;
//
//		String url = "https://" + tokenUrl + "/oauth/token?grant_type=client_credentials";
//		RestTemplate template = new RestTemplate();
//		HttpHeaders headers = new HttpHeaders();
//		com.nimbusds.jose.util.Base64 encode = com.nimbusds.jose.util.Base64.encode(clientId + ":" + clientSecret);
//		headers.add("Authorization", "Basic " + encode.toString());
//		HttpEntity<String> entity = new HttpEntity<>(headers);
//		ResponseEntity<String> response = template.postForEntity(url, entity, String.class);
//
//		return new ObjectMapper().readTree(response.getBody()).get("access_token").asText();
//	}

	public String getAccessToken(String clientid, String clientsecret, String tokenUrl) throws JsonMappingException, JsonProcessingException {

		RestTemplate template = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		com.nimbusds.jose.util.Base64 encode = com.nimbusds.jose.util.Base64.encode(clientid + ":" + clientsecret);
		headers.add("Authorization", "Basic " + encode.toString());
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = template.postForEntity(tokenUrl+"?grant_type=client_credentials", entity, String.class);

 System.out.println("Access Token For Change Vendor " + response.toString());
		return new ObjectMapper().readTree(response.getBody()).get("access_token").asText();
 
	}

	public BusinessPartnerResponse createVendorInfo(BPCreationFromWorkflowRequest bpCreationFromWorkflowRequest)
			throws UnirestException, ClientProtocolException, IOException {

		/* Getting details from hana db */

		BPRequestGeneralDataDto bpRequestGeneralDataDto = bpDetailService
				.getBPDetailsByRequestId(bpCreationFromWorkflowRequest.getRequestId());

		/* Making payload for ecc posting */

		CPIVendorDetailsDto detailsDto = convert(bpRequestGeneralDataDto, bpCreationFromWorkflowRequest);

		/* Making destination call for posting */

		String accessToken, url = null;
		try {
			String destDetails = destinationUtil.readMdgDestination("mdg-vm-cpi", null, null);

			org.json.JSONObject resObj = new org.json.JSONObject(destDetails);
			log.info("Json object from destination :" + resObj);
			log.info("Client id: " + resObj.optJSONObject("destinationConfiguration").optString("clientId"));
			log.info("clientSecret : " + resObj.optJSONObject("destinationConfiguration").optString("clientSecret"));
			log.info("tokenServiceURL: "
					+ resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));
			log.info("url: " + resObj.optJSONObject("destinationConfiguration").optString("URL"));
			accessToken = getAccessToken(resObj.optJSONObject("destinationConfiguration").optString("clientId"),
					resObj.optJSONObject("destinationConfiguration").optString("clientSecret"),
					resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));

			if (profile.toUpperCase(Locale.ROOT).equalsIgnoreCase("PROD")) {
				url = resObj.optJSONObject("destinationConfiguration").optString("URL") + "/http/Vendor_Creation";
			} else {

				url = resObj.optJSONObject("destinationConfiguration").optString("URL")
						+ "/http/Viatris/CP_HanaDb/BusinessPatrner/Creation";
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		BusinessPartnerResponse result = new BusinessPartnerResponse();
		String body = new ObjectMapper().writeValueAsString(detailsDto);
 

		System.out.println("******************************************");
		System.out.println(body);
		System.out.println("******************************************");

 
		HttpResponse<String> response = Unirest.post(url).header("authorization", "Bearer " + accessToken)
				.header("Content-Type", "application/json").header("Accept", "application/json").body(body).asString();
		int status = response.getStatus();
		System.out
				.println(status + "************************************************************************************"
						+ response + "  " + response.getBody());
		JsonObject object = new JsonParser().parse(response.getBody()).getAsJsonObject();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(response.getBody());
		JsonNode errorDetailsNode = rootNode.path("error").path("innererror").path("errordetails").path("errordetail");

		/* Checking error in validation or syndication */

		System.out.println(errorDetailsNode);
		System.out.println(errorDetailsNode.asText());
		if (rootNode.has("error")) {
			if ((!errorDetailsNode.isArray() || errorDetailsNode.size() == 0)) {
				System.out.println("inside empty");
				JsonNode message = rootNode.path("error").path("message").path("$");
				ObjectMapper mapper = new ObjectMapper();
				ObjectNode errorDetails = mapper.createObjectNode();
				ArrayNode errorDetailArray = JsonNodeFactory.instance.arrayNode();
				ObjectNode messageObject = mapper.createObjectNode();
				messageObject.put("message", message.asText());
				errorDetailArray.add(messageObject);
				errorDetails.set("errordetail", errorDetailArray);
				result.setMessage(errorDetails);
				if(bpRequestGeneralDataDto.getStatusId()<=1) {
					bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), draftStatusId);
					}
					else {
						bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), bpRequestGeneralDataDto.getStatusId());
					}
				return result;
			}

			else if (errorDetailsNode.isArray() && errorDetailsNode.size() != 0) {
				System.out.println("inside non empty");
//				JsonNode rootNode = objectMapper.readTree(response.getBody());
				JsonNode msgerrorDetailsNode = rootNode.path("error").path("innererror").path("errordetails");
				result.setMessage(msgerrorDetailsNode);
				if(bpRequestGeneralDataDto.getStatusId()<=1) {
					bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), draftStatusId);
					}
					else {
						bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), bpRequestGeneralDataDto.getStatusId());
					}
				return result;
			}
		}

		/* Successful validation & Mail to Requestor */

		String crNumber = object.getAsJsonObject("GeneralDataSet").getAsJsonObject("GeneralData").get("Vendor")
				.getAsString();

		result.setCrNumber(crNumber);
		int completedStatusId = 3;
		try {
			bpVendorDetailsRepository.updateVendorNo(bpCreationFromWorkflowRequest.getRequestId(), crNumber);
 
			}
			catch(Exception e){
				throw new RuntimeException("Failed to update status", e);
			}
			if (!bpCreationFromWorkflowRequest.isValidate()) {
				try {
				bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), completedStatusId);
				}
				catch(Exception e){
					throw new RuntimeException("Failed to update status", e);
				}
 
			MailRequestDto mailRequestDto = new MailRequestDto();
			ServiceResponse responseMessage = new ServiceResponse<>();
			
			 
			
			mailRequestDto.setEmailTo(HelperClass.isEmpty(bpRequestGeneralDataDto.getRequestorEmail()) == true
					? "Vaibhav.Anand@viatris.com"
					: bpCreationFromWorkflowRequest.getRequestorEmail());
			JsonNode ruleResponse = emailNotificationService.getDataFromEmailRules(successfulEmailNotificationCode);
			if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
				String subject = "";
				if (profile.toUpperCase(Locale.ROOT).equalsIgnoreCase("PROD")) {
					subject = ruleResponse.get("data").get("result").get(0).get("VM_EMAIL_NOTIFICATION_CODE_ACTION")
							.get(0).get("VM_EMAIL_SUBJECT").asText();
				} else {
					subject = profile.toUpperCase(Locale.ROOT) + ": " + ruleResponse.get("data").get("result").get(0)
							.get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_SUBJECT").asText();
				}

				// Change Done : - Author Dheeraj (Added Request Id)
				subject = subject.replace("<processType>", "Created").replace("<Request Id>",
						bpCreationFromWorkflowRequest.getRequestId());
				
				String emailBody = ruleResponse.get("data").get("result").get(0)
						.get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_BODY").asText();

				String countryName="";
				if(bpCreationFromWorkflowRequest.getCountryName() != null&&!bpCreationFromWorkflowRequest.getCountryName().equalsIgnoreCase("null")) {
					countryName=bpCreationFromWorkflowRequest.getCountryName();
				}
				else {
					countryName="";
				}
				
				emailBody = emailBody.replace("<processType>", "creation").replace("<crNumber>", crNumber)
						.replace("<Business Partner name>", bpCreationFromWorkflowRequest.getBusinessPartnerName() != null ? bpCreationFromWorkflowRequest.getBusinessPartnerName() : "")
						.replace("<countryName>",countryName)
						.replace("<companyCode>", bpCreationFromWorkflowRequest.getCompanyCode() != null ? bpCreationFromWorkflowRequest.getCompanyCode() :"")
						.replace("<purchasingOrg>", bpCreationFromWorkflowRequest.getPurchasingOrg() != null ? bpCreationFromWorkflowRequest.getPurchasingOrg():"")
						.replace("|", "<br>").replace("\'", "");

				mailRequestDto.setSubject(subject);
				mailRequestDto.setBodyMessage(emailBody);
				emailNotificationService.sendMailThroughCPI(mailRequestDto);
//		            responseMessage=mailService.sendMail(mailRequestDto);
			} else {
				responseMessage.setMessage("Sending Mail Failed!!");
				responseMessage.setStatus(AppConstants.FAIL_MESSAGE_MAIL);
				responseMessage.setError(null);
				log.error("No Response received from the rules");
			}
		}
		result.setMessage(objectMapper.readTree("{\"key\": \"Validated Successfully!!\"}").get("key"));
		return result;
	}

//	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public CPIVendorDetailsDto convert(BPRequestGeneralDataDto bpRequestGeneralDataDto,
			BPCreationFromWorkflowRequest bpCreationFromWorkflowRequest) {

		CPIVendorDetailsDto object = new CPIVendorDetailsDto();
		if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0))) {
			object.setType("");
			object.setRequestId(
					bpRequestGeneralDataDto.getRequestId() == null ? "" : bpRequestGeneralDataDto.getRequestId());
			if (bpCreationFromWorkflowRequest.isValidate()) {
				object.setValidation("X");
			} else {
				object.setValidation("");
			}
			object.setChangeIndObject("I");
			object.setVendor("");
			object.setSystemId(bpRequestGeneralDataDto.getSystemId());
			object.setTrainstation("");
			object.setLocationno1(bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo1() == null ? "0000000"
					: bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo1());
			object.setLocationno2(bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo2() == null ? "00000"
					: bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo2());
//		    object.setLocationno1(bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo1() == null ? "" : bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo1());
//		    object.setLocationno2(bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo2() == null ? "" : bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo2());

			object.setAuthorization(bpRequestGeneralDataDto.getBpControlData().get(0).getAuthorization() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getAuthorization());
			object.setIndustry(bpRequestGeneralDataDto.getBpControlData().get(0).getIndustry() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getIndustry());
			object.setCheckdigit(bpRequestGeneralDataDto.getBpControlData().get(0).getCheckDigit() == null ? "0"
					: bpRequestGeneralDataDto.getBpControlData().get(0).getCheckDigit());
//		    object.setCheckdigit(bpRequestGeneralDataDto.getBpControlData().get(0).getCheckDigit() == null ? "" : bpRequestGeneralDataDto.getBpControlData().get(0).getCheckDigit());
			object.setDMEIndicator(
					bpRequestGeneralDataDto.getDmeIndicator() == null ? "" : bpRequestGeneralDataDto.getDmeIndicator());
			if(!HelperClass.isEmpty(bpRequestGeneralDataDto.getInstructionKey())&&!bpRequestGeneralDataDto.getInstructionKey().contains("_")) {
			object.setInstructionkey(bpRequestGeneralDataDto.getInstructionKey());
			}
			else if(!HelperClass.isEmpty(bpRequestGeneralDataDto.getInstructionKey())&&bpRequestGeneralDataDto.getInstructionKey().contains("_")) {
				String[] stringarray = bpRequestGeneralDataDto.getInstructionKey().split("_");
				object.setInstructionkey(stringarray[0]);
			}
			else if(HelperClass.isEmpty(bpRequestGeneralDataDto.getInstructionKey())){
				object.setInstructionkey("");
			}
			object.setISRNumber(
					bpRequestGeneralDataDto.getIsrNumber() == null ? "" : bpRequestGeneralDataDto.getIsrNumber());
			object.setCorporateGroup(bpRequestGeneralDataDto.getBpControlData().get(0).getCorporateGroup() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getCorporateGroup());
			object.setAccountgroup(bpRequestGeneralDataDto.getBupaAccountGrp() == null ? ""
					: bpRequestGeneralDataDto.getBupaAccountGrp());
//		    object.setCustomer(bpRequestGeneralDataDto.getBpControlData().get(0).getCustomer() == null ? "" : bpRequestGeneralDataDto.getBpControlData().get(0).getCustomer());
			object.setCustomer("");
			object.setAlternatpayee(bpRequestGeneralDataDto.getAlternativePayee() == null ? ""
					: bpRequestGeneralDataDto.getAlternativePayee());
			object.setDeletionflag(false);
			object.setPostingBlock(false);
			object.setPurchblock(false);
			object.setTaxNumber1(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo1() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo1());
			object.setTaxNumber2(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo2() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo2());
			object.setEqualizatntax("");
			object.setLiableforVAT(false);
			object.setPayeeindoc(false);
			object.setTradingPartner(bpRequestGeneralDataDto.getBpControlData().get(0).getTradingPartner() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTradingPartner());
			object.setFiscaladdress(bpRequestGeneralDataDto.getBpControlData().get(0).getFiscalAddress() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getFiscalAddress());
			object.setVATRegNo(bpRequestGeneralDataDto.getBpControlData().get(0).getVatRegNo() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getVatRegNo());
			object.setNaturalperson(
					bpRequestGeneralDataDto.getNaturalPer() == null ? "" : bpRequestGeneralDataDto.getNaturalPer());
			object.setBlockfunction("");
			object.setAddress("");
			object.setPlaceofbirth(bpRequestGeneralDataDto.getBpControlData().get(0).getPlaceOfBirth() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getPlaceOfBirth());
			object.setBirthdate("");
			String output = null;
			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getDob())) {
//				SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");

				try {
					Date date = inputFormat.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getDob());

					output = outputFormat.format(date);

					System.out.println("Input: " + bpRequestGeneralDataDto.getBpControlData().get(0).getDob());
					System.out.println("Output: " + output);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			object.setBirthdate(output == null ? "" : output);
//		    object.setBirthdate("19970510");
			object.setSex(bpRequestGeneralDataDto.getBpControlData().get(0).getSex() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getSex());
			object.setCredinfono(bpRequestGeneralDataDto.getCreditInformationNumber() == null ? ""
					: bpRequestGeneralDataDto.getCreditInformationNumber());
//		    object.setLastextreview(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview() == null ? null : bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview());
//		    object.setLastextreview(null);
			long lastExterReview = 0;
			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview())) {
				try {
					Date setLastextreviewDate = sdf
							.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview());
					lastExterReview = setLastextreviewDate.getTime();
					System.out.println("Timestamp in milliseconds: " + lastExterReview);
				} catch (ParseException e) {
					System.err.println("Error parsing the date string: " + e.getMessage());
				}
				object.setLastextreview("/Date(" + lastExterReview + ")/");
			} else {
				object.setLastextreview(null);
			}
			object.setActualQMsys(bpRequestGeneralDataDto.getBpControlData().get(0).getActualQnSys() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getActualQnSys());
			object.setRefacctgroup("");
			object.setPlant("");
			object.setVSRrelevant(true);
			object.setPlantrelevant(true);
			object.setFactorycalend("");
			object.setSCAC(bpRequestGeneralDataDto.getBpControlData().get(0).getScac() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getScac());
			object.setCarfreightgrp(bpRequestGeneralDataDto.getBpControlData().get(0).getCarFreughtGrp() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getCarFreughtGrp());
			object.setServAgntProcGrp(
					bpRequestGeneralDataDto.getBpControlData().get(0).getServAgntProcGrp() == null ? ""
							: bpRequestGeneralDataDto.getBpControlData().get(0).getServAgntProcGrp());
			object.setTaxtype(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxType() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxType());
			object.setTaxnumbertype(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNoType() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNoType());
//		    object.setTaxtype(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxType() == null ? "" : bpRequestGeneralDataDto.getBpControlData().get(0).getTaxType());
//		    object.setTaxnumbertype(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNoType() == null ? "" : bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNoType());

			object.setSocialIns(bpRequestGeneralDataDto.getBpControlData().get(0).getSocInsurance() == null ? false
					: bpRequestGeneralDataDto.getBpControlData().get(0).getSocInsurance());
			object.setSocInsCode(bpRequestGeneralDataDto.getBpControlData().get(0).getSocInsCode() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getSocInsCode());
			object.setTaxNumber3(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo3() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo3());
			object.setTaxNumber4(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo4() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo4());
			object.setTaxsplit(false);
			object.setTaxbase(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxBase() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxBase());
			object.setProfession(bpRequestGeneralDataDto.getBpControlData().get(0).getProfession() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getProfession());
			object.setStatgrpagent("");
			object.setExternalmanuf(bpRequestGeneralDataDto.getBpControlData().get(0).getExternalManuf() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getExternalManuf());
			object.setDeletionblock(false);
			object.setRepsName(bpRequestGeneralDataDto.getBpControlData().get(0).getRepsName() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getRepsName());
			object.setTypeofBusiness(bpRequestGeneralDataDto.getBpControlData().get(0).getTypeOfBusiness() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTypeOfBusiness());
			object.setTypeofIndustry(bpRequestGeneralDataDto.getBpControlData().get(0).getTypeOfIndustr() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTypeOfIndustr());
//		    object.setQMsystemto(null);
			long qmSystemTo = 0;
			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getQmSystemTo())) {
				try {
					Date qmSystemToDate = sdf.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getQmSystemTo());
					qmSystemTo = qmSystemToDate.getTime();
					System.out.println("Timestamp in milliseconds: " + qmSystemTo);
				} catch (ParseException e) {
					System.err.println("Error parsing the date string: " + e.getMessage());
				}
				object.setQMsystemto("/Date(" + qmSystemTo + ")/");
			} else {
				object.setQMsystemto(null);
			}
			object.setPODrelevant(bpRequestGeneralDataDto.getBpControlData().get(0).getPodRelevant() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getPodRelevant());
			object.setTaxoffice(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxOffice() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxOffice());
			object.setTaxNumber(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNumber() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNumber());
			object.setTaxNumber5(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo5() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo5());
			object.setPurposeCompleteFlag("");
			object.setAddressVersion("");
			object.setFrom("/Date(253402214400000)/");
			object.setTo("/Date(253402214400000)/");
			object.setTitle(bpRequestGeneralDataDto.getTitle() == null ? "" : bpRequestGeneralDataDto.getTitle());
			object.setName(bpRequestGeneralDataDto.getName1() == null ? "" : bpRequestGeneralDataDto.getName1());
			object.setName2(bpRequestGeneralDataDto.getName2() == null ? "" : bpRequestGeneralDataDto.getName2());
			object.setName3(bpRequestGeneralDataDto.getName3() == null ? "" : bpRequestGeneralDataDto.getName3());
			object.setName4(bpRequestGeneralDataDto.getName4() == null ? "" : bpRequestGeneralDataDto.getName4());
			object.setConvname("");
			object.setCo(bpRequestGeneralDataDto.getBpAddressInfo().getCo() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getCo());
			object.setCity(bpRequestGeneralDataDto.getBpAddressInfo().getCity() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getCity());
			object.setDistrict(bpRequestGeneralDataDto.getBpAddressInfo().getDistrict() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getDistrict());
			object.setCityNo("");
			object.setDistrictNo("");
			object.setCheckStatus("");
			object.setRegStrGrp(bpRequestGeneralDataDto.getBpAddressInfo().getRegStructGrp() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getRegStructGrp());
			object.setPostalCode(bpRequestGeneralDataDto.getBpAddressInfo().getPostalCode() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getPostalCode());
			object.setPOBoxPostCde(
					bpRequestGeneralDataDto.getPoPostalCode() == null ? "" : bpRequestGeneralDataDto.getPoPostalCode());
			object.setCompanyPostCd(bpRequestGeneralDataDto.getPoCompanyPostalCode() == null ? ""
					: bpRequestGeneralDataDto.getPoCompanyPostalCode());
			object.setPostalCodeExt("");
			object.setPostalCodeExt2("");
			object.setPostalCodeExt3("");
			object.setPOBox(bpRequestGeneralDataDto.getPoBox() == null ? "" : bpRequestGeneralDataDto.getPoBox());
			object.setPOBoxwono(false);
			object.setPOBoxCity("");
			object.setPOCitNo("");
			object.setPORegion("");
			object.setPOboxcountry("");
			object.setISOcode("");
			object.setDeliveryDist("");
			object.setTransportzone(bpRequestGeneralDataDto.getBpAddressInfo().getTransportZone() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getTransportZone());
			object.setStreet(bpRequestGeneralDataDto.getBpAddressInfo().getStreet() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStreet());
			object.setStreetCode("");
			object.setStreetAbbrev("");
			object.setHouseNumber(bpRequestGeneralDataDto.getBpAddressInfo().getHouseNo() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getHouseNo());
			object.setSupplement(bpRequestGeneralDataDto.getBpAddressInfo().getSuppl() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getSuppl());
			object.setNumberRange("");
			object.setStreet2(bpRequestGeneralDataDto.getBpAddressInfo().getStreet2() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStreet2());
			object.setStreet3(bpRequestGeneralDataDto.getBpAddressInfo().getStreet3() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStreet3());
			object.setStreet4(bpRequestGeneralDataDto.getBpAddressInfo().getStreet4() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStreet4());
			object.setStreet5(bpRequestGeneralDataDto.getBpAddressInfo().getStreet5() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStreet5());
			object.setBuildingCode(bpRequestGeneralDataDto.getBpAddressInfo().getBuildingCode() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getBuildingCode());
			object.setFloor(bpRequestGeneralDataDto.getBpAddressInfo().getFloor() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getFloor());
			object.setRoomNumber(bpRequestGeneralDataDto.getBpAddressInfo().getRoom() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getRoom());
			object.setCountry(bpRequestGeneralDataDto.getBpAddressInfo().getCountry() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getCountry());
			object.setCountryISO(bpRequestGeneralDataDto.getBpAddressInfo().getCountry() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getCountry());
			object.setLanguage(bpRequestGeneralDataDto.getBpAddressInfo().getLanguage() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getLanguage());
			object.setLangISO(bpRequestGeneralDataDto.getBpAddressInfo().getLanguage() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getLanguage());
			object.setRegion(bpRequestGeneralDataDto.getBpAddressInfo().getRegion() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getRegion());
			object.setSearchTerm1(
					bpRequestGeneralDataDto.getSearchTerm1() == null ? "" : bpRequestGeneralDataDto.getSearchTerm1());
			object.setSearchTerm2(
					bpRequestGeneralDataDto.getSearchTerm2() == null ? "" : bpRequestGeneralDataDto.getSearchTerm2());
			object.setDataline("");
			object.setTelebox("");
			object.setTimezone(bpRequestGeneralDataDto.getBpAddressInfo().getTimeZone() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getTimeZone());
			object.setTaxJurisdictn(bpRequestGeneralDataDto.getBpAddressInfo().getTaxJurisdiction() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getTaxJurisdiction());
			object.setAddressID("");
			object.setCreationlang("EN");
			object.setLangCRISO("EN");
			object.setCommMethod(bpRequestGeneralDataDto.getBpAddressInfo().getStandardCommMethod() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStandardCommMethod());
			object.setAddressgroup("");
			object.setDifferentCity(bpRequestGeneralDataDto.getBpAddressInfo().getDifferentCity() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getDifferentCity());
			object.setCityCode("");
			object.setUndeliverable(bpRequestGeneralDataDto.getBpAddressInfo().getUndeliverable() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getUndeliverable());
			object.setUndeliverable1("");
			object.setPOBoxLobby("");
			object.setDelvryServType("");
			object.setDeliveryServiceNo("");
			object.setCountycode("");
			object.setCounty("");
			object.setTownshipcode("");
			object.setTownship("");
			object.setPAN("");

			// Setting To Address Data

			object.setToAddressData(new ArrayList<>());

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {

				List<CPICompanyDataDto> cpiCompanyDataDto = new ArrayList<>();
				for (BPCompanyCodeInfoDto companyCodeDto : bpRequestGeneralDataDto.getBpCompanyCodeInfo()) {
					if (!HelperClass.isEmpty(companyCodeDto.getCompanyCode()))
						cpiCompanyDataDto
								.add(convert(companyCodeDto, bpRequestGeneralDataDto, bpCreationFromWorkflowRequest));
				}
				object.setToCompanyData(cpiCompanyDataDto);
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {
				object.setToCompanyData(new ArrayList<>());
			}

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpPurchasingOrgDetail())) {

				List<CPIPurchaseOrgDataDto> cpiPurchaseOrgDataDto = new ArrayList<>();
				for (BPPurchasingOrgDetailDto purchasingOrgDetailDto : bpRequestGeneralDataDto
						.getBpPurchasingOrgDetail()) {
					if (!HelperClass.isEmpty(purchasingOrgDetailDto.getPurchasingOrg()))
						cpiPurchaseOrgDataDto.add(convert(purchasingOrgDetailDto, bpRequestGeneralDataDto));
				}
				object.setToPurchaseOrgData(cpiPurchaseOrgDataDto);
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpPurchasingOrgDetail())) {
				object.setToPurchaseOrgData(new ArrayList<>());
			}

			// Setting To Classification

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpVendorClassificationEntity())) {
				object.setToClassification(bpRequestGeneralDataDto.getBpVendorClassificationEntity().stream()
						.map(vendorClassificationEntityDto -> convert(vendorClassificationEntityDto))
						.collect(Collectors.toList()));
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpVendorClassificationEntity())) {
				object.setToClassification(new ArrayList<>());
			}

			// Setting To Email

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpEmail())) {

				List<CPIEmailDto> cpiEmailDto = new ArrayList<>();
				for (BPEmailDto emailDto : bpRequestGeneralDataDto.getBpCommunication().getBpEmail()) {
					if (!HelperClass.isEmpty(emailDto.getEmailAddress()))
						cpiEmailDto.add(convert(emailDto));
				}
				object.setToEmail(cpiEmailDto);
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpEmail())) {
				object.setToEmail(new ArrayList<>());
			}

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpTelephone())) {

				List<CPIPhoneDto> cpiPhoneDto = new ArrayList<>();
				for (BPTelephoneDto telephoneDto : bpRequestGeneralDataDto.getBpCommunication().getBpTelephone()) {
					if (!HelperClass.isEmpty(telephoneDto.getTelephone()))
						cpiPhoneDto.add(convert(telephoneDto));
				}
				object.setToPhone(cpiPhoneDto);
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpTelephone())) {
				object.setToPhone(new ArrayList<>());
			}

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpMobilePhone())) {

				List<CPIPhoneDto> cpiMobilePhoneDto = new ArrayList<>();
				for (BPMobilePhoneDto mobilePhoneDto : bpRequestGeneralDataDto.getBpCommunication()
						.getBpMobilePhone()) {
					if (!HelperClass.isEmpty(mobilePhoneDto.getMobilePhone()))
						cpiMobilePhoneDto.add(convert(mobilePhoneDto));
				}
				object.getToPhone().addAll(cpiMobilePhoneDto);
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpMobilePhone())) {
				object.getToPhone().addAll(new ArrayList<>());
			}
			// Setting To Fax

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpFaxInfo())) {

				List<CPIFaxDto> cpiFaxDto = new ArrayList<>();
				for (BPFaxInfoDto faxInfoDto : bpRequestGeneralDataDto.getBpCommunication().getBpFaxInfo()) {
					if (!HelperClass.isEmpty(faxInfoDto.getFax()))
						cpiFaxDto.add(convert(faxInfoDto));
				}
				object.setToFax(cpiFaxDto);
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpFaxInfo())) {
				object.setToFax(new ArrayList<>());
			}
			// Setting To Bank

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation())
					&& !bpRequestGeneralDataDto.getSkipBankValidation()) {
				List<CPIBankDto> cpiBankDto = new ArrayList<>();
				for (BPBankInformationDto bankInformationDto : bpRequestGeneralDataDto.getBpBankInformation()) {
					if (!bankInformationDto.getIsDeleted()) {
						cpiBankDto.add(convert(bankInformationDto));
					}
				}
				object.setToBank(cpiBankDto);
			} else if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation())
					&& bpRequestGeneralDataDto.getSkipBankValidation()) {
				object.setToBank(new ArrayList<>());
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation())) {
				object.setToBank(new ArrayList<>());
			}
			// Setting To Contact Info

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpContactInformation())) {

				List<CPIContactDto> cpiContatcDto = new ArrayList<>();
				for (BPContactInformationDto contactInformationDto : bpRequestGeneralDataDto
						.getBpContactInformation()) {
					if (!HelperClass.isEmpty(contactInformationDto.getFirstName()))
						cpiContatcDto.add(convert(contactInformationDto));
				}
				object.setToContact(cpiContatcDto);
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpContactInformation())) {
				object.setToContact(new ArrayList<>());
			}

			// Setting To Return Messages

//		    object.setToReturnMessages(new ArrayList<>());
//		    object.setToTaxData(new ArrayList<>());
			return object;
		} else {
			return object;
		}
	}

	public CPICompanyDataDto convert(BPCompanyCodeInfoDto companyCodeDto) {
		CPICompanyDataDto object = new CPICompanyDataDto();
		object.setChangeIndObject("I");
		object.setVendor("");
		object.setCompanyCode(companyCodeDto.getCompanyCode() == null ? "" : companyCodeDto.getCompanyCode());
		object.setCocodepostblock(false);
		object.setCocdedeletionflag(false);
		object.setSortkey(companyCodeDto.getBpAccountingInformation().getSortKey() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getSortKey());
		object.setReconaccount(
				companyCodeDto.getBpAccountingInformation().getReconcilliationAccountInGeneralLedger() == null ? ""
						: companyCodeDto.getBpAccountingInformation().getReconcilliationAccountInGeneralLedger());
		object.setAuthorization(companyCodeDto.getBpAccountingInformation().getAuthorization() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getAuthorization());
		object.setInterestindic(companyCodeDto.getBpAccountingInformation().getInterestInd() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getInterestInd());
		object.setPaymentmethods(companyCodeDto.getBpPaymentTransaction().getPaymentMethods() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPaymentMethods());
		object.setClrgwithcust(false);
		object.setPaymentblock(companyCodeDto.getBpPaymentTransaction().getPaymentBlock() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPaymentBlock());
		object.setPaytTerms(companyCodeDto.getBpPaymentTransaction().getPaymentTerms() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPaymentTerms());
		object.setAcctvendor("");
		object.setClerkatvendor(companyCodeDto.getBpCorrespondance().getClerkAtVendor() == null ? ""
				: companyCodeDto.getBpCorrespondance().getClerkAtVendor());
		object.setAccountmemo(companyCodeDto.getBpCorrespondance().getAccountMemo() == null ? ""
				: companyCodeDto.getBpCorrespondance().getAccountMemo());
		object.setPlanninggroup("");
		object.setAcctgclerk(companyCodeDto.getBpCorrespondance().getAccountingClerk() == null ? ""
				: companyCodeDto.getBpCorrespondance().getAccountingClerk());
		object.setHeadoffice(companyCodeDto.getBpAccountingInformation().getHeadOffice() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getHeadOffice());
		object.setAlternatpayee(companyCodeDto.getBpPaymentTransaction().getAlternatePayee() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getAlternatePayee());
//		object.setLastkeydate(null);
		long lastKey = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getLastKeyDate())) {
			try {
				Date lastKeyDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getLastKeyDate());
				lastKey = lastKeyDate.getTime();
				System.out.println("Timestamp in milliseconds: " + lastKey);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setLastkeydate("/Date(" + lastKey + ")/");
		} else {
			object.setLastkeydate(null);
		}
		object.setIntcalcfreq("00");
//		object.setIntcalcfreq("");
//		object.setLastintcalc(null);
		long lastintcalc = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getLastInterestRun())) {
			try {
				Date intcalcDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getLastKeyDate());
				lastintcalc = intcalcDate.getTime();
				System.out.println("Timestamp in milliseconds: " + lastintcalc);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setLastintcalc("/Date(" + lastintcalc + ")/");
		} else {
			object.setLastintcalc(null);
		}
		object.setLocalprocess(companyCodeDto.getBpCorrespondance().getLocalProcess() == null ? false
				: companyCodeDto.getBpCorrespondance().getLocalProcess());
		object.setBexchlimit(companyCodeDto.getBpPaymentTransaction().getBExchLimit() == null ? "0.000"
				: companyCodeDto.getBpPaymentTransaction().getBExchLimit());
		object.setChkcashngtime(companyCodeDto.getBpPaymentTransaction().getChkCashingTime() == null ? "0"
				: companyCodeDto.getBpPaymentTransaction().getChkCashingTime());
		object.setChkdoubleinv(companyCodeDto.getBpPaymentTransaction().getChkDoubleInv() == null ? true
				: companyCodeDto.getBpPaymentTransaction().getChkDoubleInv());
		object.setTolerancegroup(companyCodeDto.getBpPaymentTransaction().getPayDataToleranceGroup() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPayDataToleranceGroup());
		object.setHouseBank(companyCodeDto.getBpPaymentTransaction().getHouseBank() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getHouseBank());
		object.setIndividualpmnt(companyCodeDto.getBpPaymentTransaction().getIndividualPermit() == null ? false
				: companyCodeDto.getBpPaymentTransaction().getIndividualPermit());
		object.setPmtmethsupl(companyCodeDto.getBpPaymentTransaction().getPmtmethsupl() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPmtmethsupl());
		object.setExemptionno("");
		long validUntil = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getValidUntil())) {
			try {
				Date validUntilDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getValidUntil());
				validUntil = validUntilDate.getTime();
				System.out.println("Timestamp in milliseconds: " + validUntil);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setValiduntil("/Date(" + validUntil + ")/");
		} else {
			object.setValiduntil(null);
		}
		object.setWTaxCode(companyCodeDto.getBpAccountingInformation().getWtaxCode() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getWtaxCode());
		object.setSubsind("");
		object.setMaineconomicact("0000");
		object.setMinorityindic(companyCodeDto.getBpAccountingInformation().getMinorityIndicator() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getMinorityIndicator());
		object.setPrevacctno(companyCodeDto.getBpAccountingInformation().getPrevAcctNo() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getPrevAcctNo());
		object.setGroupingkey1(companyCodeDto.getBpPaymentTransaction().getGroupingKey() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getGroupingKey());
		object.setGroupingkey2("");
		object.setPmtmethsupl("");
		object.setRecipienttype("");
		object.setExmptauthority(companyCodeDto.getBpAccountingInformation().getExemptionAuthority() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getExemptionAuthority());
		object.setCountryForWT("");
		object.setPmtadvbyEDI(companyCodeDto.getBpPaymentTransaction().getPmtAdvByEDI() == null ? false
				: companyCodeDto.getBpPaymentTransaction().getPmtAdvByEDI());
		object.setReleasegroup(companyCodeDto.getBpAccountingInformation().getReleaseGroup() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getReleaseGroup());
		object.setClerksfax(companyCodeDto.getBpCorrespondance().getClerkFax() == null ? ""
				: companyCodeDto.getBpCorrespondance().getClerkFax());
		object.setClrksinternet(companyCodeDto.getBpCorrespondance().getClerkInternet() == null ? ""
				: companyCodeDto.getBpCorrespondance().getClerkInternet());
		object.setCrmemoterms("");
		object.setActivityCode("");
		object.setDistrType("");
		object.setAcctstatement("");
		long timestampOfExemptFromDate = 253402214400000L;

		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getCertificationDate())) {
			try {
				Date exemptFromDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getCertificationDate());
				timestampOfExemptFromDate = exemptFromDate.getTime();
				System.out.println("Timestamp in milliseconds: " + timestampOfExemptFromDate);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setCertifictnDate("/Date(" + timestampOfExemptFromDate + ")/");
		} else if (HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getCertificationDate())) {
			object.setCertifictnDate(null);
		} else {
			object.setCertifictnDate(null);
		}
		object.setTolerancegrp(
				companyCodeDto.getBpPaymentTransaction().getInvoiceVerificationToleranceGroup() == null ? ""
						: companyCodeDto.getBpPaymentTransaction().getInvoiceVerificationToleranceGroup());
		object.setPersonnelNo(companyCodeDto.getBpAccountingInformation().getPersonnelNumber() == null ? "00000000"
				: companyCodeDto.getBpAccountingInformation().getPersonnelNumber());
		object.setCoCddelblock(false);
		object.setActclktelno(companyCodeDto.getBpCorrespondance().getActingClerksTelephone() == null ? ""
				: companyCodeDto.getBpCorrespondance().getActingClerksTelephone());
		object.setPrepaymentRelevant(companyCodeDto.getBpPaymentTransaction().getPrePayment() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPrePayment());
		object.setAssignmTestGroup("");
		object.setPurposeCompleteFlag("");
		List<CPIDunningDataDto> cpiDunningDataDto = new ArrayList<>();
		if (!HelperClass.isEmpty(companyCodeDto.getBpCorrespondance().getDunnProcedure())) {
			CPIDunningDataDto dunningDataObject = new CPIDunningDataDto();
			dunningDataObject.setChangeIndObject("I");
			dunningDataObject.setVendor("");
			dunningDataObject
					.setCompanyCode(companyCodeDto.getCompanyCode() == null ? "" : companyCodeDto.getCompanyCode());
			dunningDataObject.setDunningArea("");
			dunningDataObject.setDunnProcedure(companyCodeDto.getBpCorrespondance().getDunnProcedure() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunnProcedure());
			dunningDataObject.setDunnBlock(companyCodeDto.getBpCorrespondance().getDunningBlock() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunningBlock());

			long lastDunned = 0;
			if (!HelperClass.isEmpty(companyCodeDto.getBpCorrespondance().getLastDunned())) {
				try {
					Date lastDunnedDate = sdf.parse(companyCodeDto.getBpCorrespondance().getLastDunned());
					lastDunned = lastDunnedDate.getTime();
					System.out.println("Timestamp in milliseconds: " + lastDunned);
				} catch (ParseException e) {
					System.err.println("Error parsing the date string: " + e.getMessage());
				}
				dunningDataObject.setLastDunned("/Date(" + lastDunned + ")/");
			} else {
				dunningDataObject.setLastDunned(null);
			}
			dunningDataObject.setDunningLevel(companyCodeDto.getBpCorrespondance().getDunningLevel() == null ? "0"
					: companyCodeDto.getBpCorrespondance().getDunningLevel().toString());

			dunningDataObject.setDunnrecipient(companyCodeDto.getBpCorrespondance().getDunnRecepient() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunnRecepient());
//			dunningDataObject.setLegdunnproc(null);
			long legDunnProc = 0;
			if (!HelperClass.isEmpty(companyCodeDto.getBpCorrespondance().getLegalDunnProc())) {
				try {
					Date legDunnProcDate = sdf.parse(companyCodeDto.getBpCorrespondance().getLegalDunnProc());
					legDunnProc = legDunnProcDate.getTime();
					System.out.println("Timestamp in milliseconds: " + legDunnProc);
				} catch (ParseException e) {
					System.err.println("Error parsing the date string: " + e.getMessage());
				}
				dunningDataObject.setLegdunnproc("/Date(" + legDunnProc + ")/");
			} else {
				dunningDataObject.setLegdunnproc(null);
			}
			dunningDataObject.setDunningclerk(companyCodeDto.getBpCorrespondance().getDunningClerk() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunningClerk());

			cpiDunningDataDto.add(dunningDataObject);

			object.setToDunningData(cpiDunningDataDto);
		} else {
			object.setToDunningData(new ArrayList<>());
		}
		object.setToWtax(new ArrayList<>());
		if (!HelperClass.isEmpty(companyCodeDto.getBpWithholdingTax())) {
			object.setToWtax(companyCodeDto.getBpWithholdingTax().stream()
					.map(wTaxDto -> convert(wTaxDto, companyCodeDto)).collect(Collectors.toList()));
		} else {
			object.setToWtax(new ArrayList<>());
		}
		return object;
	}

	public CPICompanyDataDto convert(BPCompanyCodeInfoDto companyCodeDto,
			BPRequestGeneralDataDto bpRequestGeneralDataDto,
			BPCreationFromWorkflowRequest bpCreationFromWorkflowRequest) {
		CPICompanyDataDto object = new CPICompanyDataDto();
		object.setChangeIndObject("I");
		object.setVendor("");
		object.setCompanyCode(companyCodeDto.getCompanyCode() == null ? "" : companyCodeDto.getCompanyCode());
		object.setCocodepostblock(false);
		object.setCocdedeletionflag(false);
		object.setSortkey(companyCodeDto.getBpAccountingInformation().getSortKey() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getSortKey());
		object.setReconaccount(
				companyCodeDto.getBpAccountingInformation().getReconcilliationAccountInGeneralLedger() == null ? ""
						: companyCodeDto.getBpAccountingInformation().getReconcilliationAccountInGeneralLedger());
		object.setAuthorization(companyCodeDto.getBpAccountingInformation().getAuthorization() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getAuthorization());
		object.setInterestindic(companyCodeDto.getBpAccountingInformation().getInterestInd() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getInterestInd());
		object.setPaymentmethods(companyCodeDto.getBpPaymentTransaction().getPaymentMethods() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPaymentMethods());
		object.setClrgwithcust(false);
		object.setPaymentblock(companyCodeDto.getBpPaymentTransaction().getPaymentBlock() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPaymentBlock());
		object.setPaytTerms(companyCodeDto.getBpPaymentTransaction().getPaymentTerms() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPaymentTerms());
		object.setAcctvendor("");
		object.setClerkatvendor(companyCodeDto.getBpCorrespondance().getClerkAtVendor() == null ? ""
				: companyCodeDto.getBpCorrespondance().getClerkAtVendor());
		object.setAccountmemo(companyCodeDto.getBpCorrespondance().getAccountMemo() == null ? ""
				: companyCodeDto.getBpCorrespondance().getAccountMemo());
		object.setPlanninggroup("");
		object.setAcctgclerk(companyCodeDto.getBpCorrespondance().getAccountingClerk() == null ? ""
				: companyCodeDto.getBpCorrespondance().getAccountingClerk());
		object.setHeadoffice(companyCodeDto.getBpAccountingInformation().getHeadOffice() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getHeadOffice());
		object.setAlternatpayee(companyCodeDto.getBpPaymentTransaction().getAlternatePayee() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getAlternatePayee());
//		object.setLastkeydate(null);
		long lastKey = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getLastKeyDate())) {
			try {
				Date lastKeyDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getLastKeyDate());
				lastKey = lastKeyDate.getTime();
				System.out.println("Timestamp in milliseconds: " + lastKey);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setLastkeydate("/Date(" + lastKey + ")/");
		} else {
			object.setLastkeydate(null);
		}
		object.setIntcalcfreq("00");
		long lastintcalc = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getLastInterestRun())) {
			try {
				Date intcalcDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getLastInterestRun());
				lastintcalc = intcalcDate.getTime();
				System.out.println("Timestamp in milliseconds: " + lastintcalc);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setLastintcalc("/Date(" + lastintcalc + ")/");
		} else {
			object.setLastintcalc(null);
		}
		object.setLocalprocess(companyCodeDto.getBpCorrespondance().getLocalProcess() == null ? false
				: companyCodeDto.getBpCorrespondance().getLocalProcess());
		object.setBexchlimit(companyCodeDto.getBpPaymentTransaction().getBExchLimit() == null ? "0.000"
				: companyCodeDto.getBpPaymentTransaction().getBExchLimit());
		object.setChkcashngtime(companyCodeDto.getBpPaymentTransaction().getChkCashingTime() == null ? "0"
				: companyCodeDto.getBpPaymentTransaction().getChkCashingTime());
		object.setChkdoubleinv(companyCodeDto.getBpPaymentTransaction().getChkDoubleInv() == null ? true
				: companyCodeDto.getBpPaymentTransaction().getChkDoubleInv());
		object.setTolerancegroup(companyCodeDto.getBpPaymentTransaction().getPayDataToleranceGroup() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPayDataToleranceGroup());
		object.setHouseBank(companyCodeDto.getBpPaymentTransaction().getHouseBank() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getHouseBank());
		object.setIndividualpmnt(companyCodeDto.getBpPaymentTransaction().getIndividualPermit() == null ? false
				: companyCodeDto.getBpPaymentTransaction().getIndividualPermit());
		object.setPmtmethsupl(companyCodeDto.getBpPaymentTransaction().getPmtmethsupl() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPmtmethsupl());
//		object.setExemptionno("");
		object.setExemptionno(companyCodeDto.getBpAccountingInformation().getExemptionNumber() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getExemptionNumber());
//		object.setValiduntil(null);
		long validUntil = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getValidUntil())) {
			try {
				Date validUntilDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getValidUntil());
				validUntil = validUntilDate.getTime();
				System.out.println("Timestamp in milliseconds: " + validUntil);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setValiduntil("/Date(" + validUntil + ")/");
		} else {
			object.setValiduntil(null);
		}
		object.setWTaxCode(companyCodeDto.getBpAccountingInformation().getWtaxCode() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getWtaxCode());
		object.setSubsind("");
		object.setMaineconomicact("0000");
//		object.setMaineconomicact("");
		object.setMinorityindic(companyCodeDto.getBpAccountingInformation().getMinorityIndicator() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getMinorityIndicator());
		object.setPrevacctno(companyCodeDto.getBpAccountingInformation().getPrevAcctNo() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getPrevAcctNo());
		object.setGroupingkey1(companyCodeDto.getBpPaymentTransaction().getGroupingKey() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getGroupingKey());
		object.setGroupingkey2("");
		object.setPmtmethsupl("");
//		object.setRecipienttype("");
		object.setRecipienttype(companyCodeDto.getBpAccountingInformation().getRecipientType() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getRecipientType());
		object.setExmptauthority(companyCodeDto.getBpAccountingInformation().getExemptionAuthority() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getExemptionAuthority());
		object.setCountryForWT(companyCodeDto.getWhTaxCountry() == null ? "" : companyCodeDto.getWhTaxCountry());
		object.setPmtadvbyEDI(companyCodeDto.getBpPaymentTransaction().getPmtAdvByEDI() == null ? false
				: companyCodeDto.getBpPaymentTransaction().getPmtAdvByEDI());
		object.setReleasegroup(companyCodeDto.getBpAccountingInformation().getReleaseGroup() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getReleaseGroup());
		object.setClerksfax(companyCodeDto.getBpCorrespondance().getClerkFax() == null ? ""
				: companyCodeDto.getBpCorrespondance().getClerkFax());
		object.setClrksinternet(companyCodeDto.getBpCorrespondance().getClerkInternet() == null ? ""
				: companyCodeDto.getBpCorrespondance().getClerkInternet());
		object.setCrmemoterms("");
		object.setActivityCode("");
		object.setDistrType("");
		object.setAcctstatement("");
		long timestampOfExemptFromDate = 0;
//		long timestampOfExemptToDate = 0;
//
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getCertificationDate())) {
			try {
				Date exemptFromDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getCertificationDate());
				timestampOfExemptFromDate = exemptFromDate.getTime();
				System.out.println("Timestamp in milliseconds: " + timestampOfExemptFromDate);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setCertifictnDate("/Date(" + timestampOfExemptFromDate + ")/");
		} else {
			object.setCertifictnDate(null);
		}
		object.setTolerancegrp(
				companyCodeDto.getBpPaymentTransaction().getInvoiceVerificationToleranceGroup() == null ? ""
						: companyCodeDto.getBpPaymentTransaction().getInvoiceVerificationToleranceGroup());
		object.setPersonnelNo(companyCodeDto.getBpAccountingInformation().getPersonnelNumber() == null ? "00000000"
				: companyCodeDto.getBpAccountingInformation().getPersonnelNumber());
//		object.setPersonnelNo(companyCodeDto.getBpAccountingInformation().getPersonnelNumber() == null ? "" : companyCodeDto.getBpAccountingInformation().getPersonnelNumber());
		object.setCoCddelblock(false);
		object.setActclktelno(companyCodeDto.getBpCorrespondance().getActingClerksTelephone() == null ? ""
				: companyCodeDto.getBpCorrespondance().getActingClerksTelephone());
		object.setPrepaymentRelevant(companyCodeDto.getBpPaymentTransaction().getPrePayment() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPrePayment());
		object.setAssignmTestGroup("");
		object.setPurposeCompleteFlag("");
		object.setBranchCode(companyCodeDto.getBranchCode() == null ? "" : companyCodeDto.getBranchCode());
		object.setBranchCodeDescription(
				companyCodeDto.getBranchCodeDescription() == null ? "" : companyCodeDto.getBranchCodeDescription());
		List<CPIDunningDataDto> cpiDunningDataDto = new ArrayList<>();
		if (!HelperClass.isEmpty(companyCodeDto.getBpCorrespondance().getDunnProcedure())) {
			CPIDunningDataDto dunningDataObject = new CPIDunningDataDto();
			dunningDataObject.setChangeIndObject("I");
			dunningDataObject.setVendor("");
			dunningDataObject
					.setCompanyCode(companyCodeDto.getCompanyCode() == null ? "" : companyCodeDto.getCompanyCode());
			dunningDataObject.setDunningArea("");
			dunningDataObject.setDunnProcedure(companyCodeDto.getBpCorrespondance().getDunnProcedure() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunnProcedure());
			dunningDataObject.setDunnBlock(companyCodeDto.getBpCorrespondance().getDunningBlock() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunningBlock());
//			dunningDataObject.setLastDunned(null);
			long lastDunned = 0;
			if (!HelperClass.isEmpty(companyCodeDto.getBpCorrespondance().getLastDunned())) {
				try {
					Date lastDunnedDate = sdf.parse(companyCodeDto.getBpCorrespondance().getLastDunned());
					lastDunned = lastDunnedDate.getTime();
					System.out.println("Timestamp in milliseconds: " + lastDunned);
				} catch (ParseException e) {
					System.err.println("Error parsing the date string: " + e.getMessage());
				}
				dunningDataObject.setLastDunned("/Date(" + lastDunned + ")/");
			} else {
				dunningDataObject.setLastDunned(null);
			}
			dunningDataObject.setDunningLevel(companyCodeDto.getBpCorrespondance().getDunningLevel() == null ? "0"
					: companyCodeDto.getBpCorrespondance().getDunningLevel().toString());
//			dunningDataObject.setDunningLevel(companyCodeDto.getBpCorrespondance().getDunningLevel() == null ? ""
//					: companyCodeDto.getBpCorrespondance().getDunningLevel().toString());
			dunningDataObject.setDunnrecipient(companyCodeDto.getBpCorrespondance().getDunnRecepient() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunnRecepient());
//			dunningDataObject.setLegdunnproc(null);
			long legDunnProc = 0;
			if (!HelperClass.isEmpty(companyCodeDto.getBpCorrespondance().getLegalDunnProc())) {
				try {
					Date legDunnProcDate = sdf.parse(companyCodeDto.getBpCorrespondance().getLegalDunnProc());
					legDunnProc = legDunnProcDate.getTime();
					System.out.println("Timestamp in milliseconds: " + legDunnProc);
				} catch (ParseException e) {
					System.err.println("Error parsing the date string: " + e.getMessage());
				}
				dunningDataObject.setLegdunnproc("/Date(" + legDunnProc + ")/");
			} else {
				dunningDataObject.setLegdunnproc(null);
			}
			dunningDataObject.setDunningclerk(companyCodeDto.getBpCorrespondance().getDunningClerk() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunningClerk());

			cpiDunningDataDto.add(dunningDataObject);

			object.setToDunningData(cpiDunningDataDto);
		} else {
			object.setToDunningData(new ArrayList<>());
		}
		if (!HelperClass.isEmpty(companyCodeDto.getBpWithholdingTax())) {

			List<CPIwTaxDto> cpiwTaxDto = new ArrayList<>();
			for (BPWithholdingTaxDto withholdingTaxDto : companyCodeDto.getBpWithholdingTax()) {
				if (!HelperClass.isEmpty(withholdingTaxDto.getWithholdingTaxType()))
					cpiwTaxDto.add(convert(withholdingTaxDto, companyCodeDto));
			}
			object.setToWtax(cpiwTaxDto);
		} else if (HelperClass.isEmpty(companyCodeDto.getBpWithholdingTax())) {
			object.setToWtax(new ArrayList<>());
		}
		return object;
	}

	public CPIDunningDataDto convertDunningData() {
		CPIDunningDataDto object = new CPIDunningDataDto();
		object.setChangeIndObject("");
		object.setVendor("");
		object.setCompanyCode("");
		object.setDunningArea("");
		object.setDunnProcedure("");
		object.setDunnBlock("");
		object.setLastDunned("");
		object.setDunningLevel("");
		object.setDunnrecipient("");
		object.setLegdunnproc("");
		object.setDunningclerk("");
		return object;
	}

	public CPIwTaxDto convert(BPWithholdingTaxDto withholdingTaxDto, BPCompanyCodeInfoDto companyCodeDto) {
		CPIwTaxDto object = new CPIwTaxDto();
		object.setChangeIndObject("I");
		object.setVendor("");
		object.setCompanyCode(companyCodeDto.getCompanyCode() == null ? "" : companyCodeDto.getCompanyCode());
		object.setCountry(companyCodeDto.getWhTaxCountry() == null ? "" : companyCodeDto.getWhTaxCountry());
		object.setWithhldtaxtype(
				withholdingTaxDto.getWithholdingTaxType() == null ? "" : withholdingTaxDto.getWithholdingTaxType());
		object.setSubjecttowtx(withholdingTaxDto.getLiable() == null ? false : withholdingTaxDto.getLiable());
		object.setRecipienttype(
				withholdingTaxDto.getRecipientType() == null ? "" : withholdingTaxDto.getRecipientType());
		object.setWtaxnumber(withholdingTaxDto.getWTaxId() == null ? "" : withholdingTaxDto.getWTaxId());
		object.setWtaxcode(
				withholdingTaxDto.getWithholdingTaxCode() == null ? "00" : withholdingTaxDto.getWithholdingTaxCode());
//		object.setWtaxcode(withholdingTaxDto.getWithholdingTaxCode() == null ? "" : withholdingTaxDto.getWithholdingTaxCode());

		object.setExemptionnumber(withholdingTaxDto.getExemptionNo() == null ? "" : withholdingTaxDto.getExemptionNo());
		object.setExemptionrate(
				withholdingTaxDto.getExemPercentage() == null ? "0.00" : withholdingTaxDto.getExemPercentage());
//		object.setExemptionrate(withholdingTaxDto.getExemPercentage() == null ? "" : withholdingTaxDto.getExemPercentage());
		long timestampOfExemptFromDate = 0;
		long timestampOfExemptToDate = 0;
		if (!HelperClass.isEmpty(withholdingTaxDto.getExemptFrom())) {
			try {
				Date exemptFromDate = sdf.parse(withholdingTaxDto.getExemptFrom());

				timestampOfExemptFromDate = exemptFromDate.getTime();

				System.out.println("Timestamp in milliseconds: " + timestampOfExemptFromDate);

			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setExemptfrom("/Date(" + timestampOfExemptFromDate + ")/");
		} else {
			object.setExemptfrom(null);
		}
		if (!HelperClass.isEmpty(withholdingTaxDto.getExemptTo())) {
			try {

				Date exemptToDate = sdf.parse(withholdingTaxDto.getExemptTo());

				timestampOfExemptToDate = exemptToDate.getTime();

				System.out.println("Timestamp in milliseconds: " + timestampOfExemptToDate);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setExemptTo("/Date(" + timestampOfExemptToDate + ")/");
		} else {
			object.setExemptTo(null);
		}

		object.setExemptionreas(withholdingTaxDto.getExemResn() == null ? "" : withholdingTaxDto.getExemResn());

		return object;
	}

	private CPIContactDto convert(BPContactInformationDto bpContactDto) {
		CPIContactDto contactDto = new CPIContactDto();
		contactDto.setChangeIndObject("I");
		contactDto.setVendor("");
		contactDto.setContactPerson("");
//		contactDto.setContactPerson(bpContactDto.getContactFunction());

		contactDto.setDepartment(bpContactDto.getDepartment() == null ? "" : bpContactDto.getDepartment());
//		contactDto.setDepartment("0001");
		contactDto.setHighLevelPerson("0000000000");
//		contactDto.setHighLevelPerson("");
		contactDto.setFunction(bpContactDto.getContactFunction() == null ? "" : bpContactDto.getContactFunction());
//		contactDto.setFunction("");
		contactDto.setAuthority("");
		contactDto.setVIP("1");
		contactDto.setGender("");
		contactDto.setRepresentno("0000000000");
//		contactDto.setRepresentno("");
		contactDto.setCallfrequency("");
		contactDto.setBuyinghabits("");
		contactDto.setNotes("");
		contactDto.setMaritalStat("0");
//		contactDto.setMaritalStat("");
		contactDto.setTitle(bpContactDto.getFormOfAddress() == null ? "" : bpContactDto.getFormOfAddress());
		contactDto.setLastname(bpContactDto.getLastName() == null ? "" : bpContactDto.getLastName());
		contactDto.setFirstname(bpContactDto.getFirstName() == null ? "" : bpContactDto.getFirstName());
		contactDto.setNameatBirth("");
		contactDto.setFamilynameSecond("");
		contactDto.setCompletename("");
		contactDto.setAcademicTitle("");
		contactDto.setAcadtitlesecond("");
		contactDto.setPrefix("");
		contactDto.setPrefixSecond("");
		contactDto.setNameSupplement("");
		contactDto.setNickname("");
		contactDto.setFormatname("");
		contactDto.setFormatcountry("");

		if (!HelperClass.isEmpty(bpContactDto.getTelephone())) {
			List<CPIContactPhoneDto> cpiContactPhoneDtos = new ArrayList<>();
			cpiContactPhoneDtos.add(convertContactPhone(bpContactDto));
			contactDto.setToContactPhone(cpiContactPhoneDtos);
		} else {
			contactDto.setToContactPhone(new ArrayList<>());
		}

		if (!HelperClass.isEmpty(bpContactDto.getUserEmail())) {
			List<CPIContactEmailDto> cpiContactEmailDtos = new ArrayList<>();
			cpiContactEmailDtos.add(convertContactEmail(bpContactDto));
			contactDto.setToContactEmail(cpiContactEmailDtos);
		} else {
			contactDto.setToContactEmail(new ArrayList<>());
		}
		return contactDto;
	}

	public CPIContactPhoneDto convertContactPhone(BPContactInformationDto bpContactDto) {
		CPIContactPhoneDto contactPhoneDto = new CPIContactPhoneDto();
		contactPhoneDto.setChangeIndObject("I");
		contactPhoneDto.setVendor("");
		contactPhoneDto.setAddrnumber("");
		contactPhoneDto.setCountry(bpContactDto.getTelephoneCode() == null ? "" : bpContactDto.getTelephoneCode());
		contactPhoneDto.setStdNo(true);
		contactPhoneDto.setTelephone(bpContactDto.getTelephone() == null ? "" : bpContactDto.getTelephone());
		contactPhoneDto.setExtension("");
		contactPhoneDto.setTelNo("");
		contactPhoneDto.setCallerNo(bpContactDto.getTelephone() == null ? "" : bpContactDto.getTelephone());
		contactPhoneDto.setStdRecip("");
		contactPhoneDto.setR3User("1");
		return contactPhoneDto;
	}

	public CPIContactEmailDto convertContactEmail(BPContactInformationDto bpContactDto) {
		CPIContactEmailDto contactEmailDto = new CPIContactEmailDto();
		contactEmailDto.setChangeIndObject("I");
		contactEmailDto.setVendor("");
		contactEmailDto.setAddrnumber("");
		contactEmailDto.setStdndardNo(true);
		contactEmailDto.setEMail(bpContactDto.getUserEmail() == null ? "" : bpContactDto.getUserEmail());
		contactEmailDto.setEmailSrch("");
		return contactEmailDto;
	}

	public CPIClassificationDto convert(BPVendorClassificationEntityDto classificationEntityDto) {
		CPIClassificationDto object = new CPIClassificationDto();
		object.setChangeIndObject("I");
		object.setVendor("");
		object.setClassnum(classificationEntityDto.getClassnum() == null ? "" : classificationEntityDto.getClassnum());
//        object.setClassnum("SBA_SMALL_BUS_ADM");
		object.setClasstype("010");
//        object.setClasstype("");
		object.setObject("");
		object.setObjecttable("");
		object.setKeydate(null);
		object.setDescription(
				classificationEntityDto.getDescription() == null ? "" : classificationEntityDto.getDescription());
		object.setStatus("");
		object.setChangenumber("");
		object.setStdClass("");
		object.setFlag(false);
		object.setObjectGuid("000000000000000000");
//        object.setObjectGuid("");
		if (!HelperClass.isEmpty(classificationEntityDto.getBpVendorClassificationAttribute())) {
			object.setToClassificationItem(classificationEntityDto.getBpVendorClassificationAttribute().stream()
					.map(vendorClassificationAttributeDto -> convert(vendorClassificationAttributeDto))
					.collect(Collectors.toList()));
		} else if (HelperClass.isEmpty(classificationEntityDto.getBpVendorClassificationAttribute())) {
			object.setToClassificationItem(new ArrayList<>());
		}
		return object;

	}

	public CPIClassificationItemDto convert(BPVendorClassificationAttributeDto classificationAttributeDto) {
		CPIClassificationItemDto object = new CPIClassificationItemDto();
		object.setChangeIndObject("I");
		object.setVendor("");
//        object.setCharact("CERTIFICATION_TYPE");
		object.setCharact(
				classificationAttributeDto.getCharact() == null ? "" : classificationAttributeDto.getCharact());
		object.setValuChar("");
//        object.setValueChar("STATE");
		object.setInherited("");
		object.setInstance("");
		object.setValueNeutral(classificationAttributeDto.getValueNeutral() == null ? ""
				: classificationAttributeDto.getValueNeutral());
//        object.setCharactDescr("Certification Type");
		object.setCharactDescr(classificationAttributeDto.getCharactDescr() == null ? ""
				: classificationAttributeDto.getCharactDescr());
		object.setValueCharLong("");
		object.setValueNeutralLong("");
		return object;
	}

	public CPIPurchaseOrgDataDto convert(BPPurchasingOrgDetailDto bpPurchasingOrgDetailDto,
			BPRequestGeneralDataDto bpRequestGeneralDataDto) {
		CPIPurchaseOrgDataDto object = new CPIPurchaseOrgDataDto();
		object.setChangeIndObject("I");
		object.setVendor("");
		object.setAllvendor("");
		object.setPurchasingOrg(
				bpPurchasingOrgDetailDto.getPurchasingOrg() == null ? "" : bpPurchasingOrgDetailDto.getPurchasingOrg());
//		object.setPurchasingOrg("1234");

		object.setPurblockPOrg(false);
		object.setDelflagPOrg(false);
		object.setABCindicator(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAbcIndicator() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAbcIndicator());
		object.setOrdercurrency(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getOrderCurrency() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getOrderCurrency());
		object.setSalesperson(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSalesPerson() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSalesPerson());
		object.setTelephone(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTelephone() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTelephone());
		object.setMinimumvalue(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue() == null ? "0.00"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue());
//		object.setMinimumvalue(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue()==null?"":bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue());
		object.setPaytTerms(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTermsOfPayment() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTermsOfPayment());
		object.setIncoterms(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms());
		object.setIncoterms2(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms2() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms2());
		object.setGRBasedIV(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getGrBasedInvVerify() == null ? true
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getGrBasedInvVerify());
		object.setAcknowlReqd(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAcknowledgementReqd() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAcknowledgementReqd());
		object.setSchemaGrpVndr(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSchemaGroupVendor() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSchemaGroupVendor());
		object.setAutomaticPO(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutomaticPurchaseOrder() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutomaticPurchaseOrder());
		object.setModeOfTrBorder(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getModeOfTransportBorder() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getModeOfTransportBorder());
		object.setCustomsoffice("");
		object.setPrDateCat("");
		object.setPurchGroup(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPurchasingGroup() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPurchasingGroup());
		object.setSubseqsett(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubsequentSettlement() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubsequentSettlement());
		object.setBvolcompag(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getBVolComp() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getBVolComp());
		object.setERS(false);
		object.setPlDelivTime(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime() == null ? "0"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime());
//		object.setPlDelivTime(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime()==null?"":bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime());
		object.setPlanningcal("");
		object.setPlanningcycle("");
		object.setPOentryvend("");
		object.setPricemkgvnd(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPriceMarkingAgreed() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPriceMarkingAgreed());
		object.setRackjobbing("");
		object.setSSindexactive(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubSeqSettIndex() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubSeqSettIndex());
		object.setPricedetermin(false);
		object.setQualiffDKd("");
		object.setDocumentIndex(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getDocIndexActive() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getDocIndexActive());
		object.setSortcriterion(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSortCriterion() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSortCriterion());
		object.setConfControl(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getConfirmationControl() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getConfirmationControl());
		object.setRndingProfile(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getRoundingProfile() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getRoundingProfile());
		object.setUoMGroup("");
		object.setVenServLevl(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getServLevel() == null ? "0.0"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getServLevel());
//		object.setVenServLevl(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getServLevel()==null?"":bpPurchasingOrgDetailDto.getBpPurchaseOrg().getServLevel());
		object.setLBprofile("");
		object.setAutGRSetRet(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutoEvalGRSetMtRet() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutoEvalGRSetMtRet());
		object.setAccwvendor(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAccWithVendor() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAccWithVendor());
		object.setPROACTcontrolprof(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getProActControlProf() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getProActControlProf());
		object.setAgencybusiness(
				bpPurchasingOrgDetailDto.getBpPurchaseOrg().getRelevantForAgencyBusiness() == null ? false
						: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getRelevantForAgencyBusiness());
		object.setRevaluation(false);
		object.setShippingCond(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getShippingConditions() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getShippingConditions());
		object.setSrvBasedInvVer(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSrvBasedInvVar() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSrvBasedInvVar());
		if (!HelperClass.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData())) {
			List<CPIPlantDto> cpiPlantDto = new ArrayList<>();
			for (BPPurchaseOrgAdditionalDataDto purchaseOrgAdditionalDto : bpPurchasingOrgDetailDto
					.getBpPurchaseOrgAdditionalData()) {
				if (!HelperClass.isEmpty(purchaseOrgAdditionalDto.getPlant()))
					cpiPlantDto.add(convert(purchaseOrgAdditionalDto, bpPurchasingOrgDetailDto.getPurchasingOrg()));
			}
			object.setToPlant(cpiPlantDto);
		} else if (HelperClass.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData())) {
			object.setToPlant(new ArrayList<>());
		}

		if (bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			List<CPIOrderingAddressDto> cpiOrderingAddressDto = new ArrayList<>();
			cpiOrderingAddressDto.add(
					convert(bpPurchasingOrgDetailDto.getBpBusinessPartnerOrderingAddress(), bpRequestGeneralDataDto));
			object.setToOderingAddress(cpiOrderingAddressDto);
		} else if (!bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			object.setToOderingAddress(new ArrayList<>());
		}

		if (bpPurchasingOrgDetailDto.getRemittanceAddressCheck()) {
			List<CPIInvoicePartyDto> cpiInvoicePartyDto = new ArrayList<>();
			cpiInvoicePartyDto.add(
					convert(bpPurchasingOrgDetailDto.getBpBusinessPartnerRemittanceAddress(), bpRequestGeneralDataDto));
			object.setToInvoiceParty(cpiInvoicePartyDto);
		} else if (!bpPurchasingOrgDetailDto.getRemittanceAddressCheck()) {
			object.setToInvoiceParty(new ArrayList<>());
		}
		return object;
	}

	private CPIOrderingAddressDto convert(BPBusinessPartnerOrderingAddressDto bpBusinessPartnerOrderingAddressDto,
			BPRequestGeneralDataDto bpRequestGeneralDataDto) {
		CPIOrderingAddressDto cpiOrderingAddressDto = new CPIOrderingAddressDto();
		cpiOrderingAddressDto.setType("Order");
		cpiOrderingAddressDto.setValidation("");
		cpiOrderingAddressDto.setChangeIndObject("I");
		cpiOrderingAddressDto.setVendor("");
		cpiOrderingAddressDto.setTrainstation("");
		cpiOrderingAddressDto.setLocationno1("");
		cpiOrderingAddressDto.setLocationno2("");
		cpiOrderingAddressDto.setAuthorization("");
		cpiOrderingAddressDto.setIndustry("");
		cpiOrderingAddressDto.setCheckdigit("");
		cpiOrderingAddressDto.setDMEIndicator("");
		cpiOrderingAddressDto.setInstructionkey("");
		cpiOrderingAddressDto.setISRNumber("");
		cpiOrderingAddressDto.setCorporateGroup("");
		cpiOrderingAddressDto.setAccountgroup("0006");
		cpiOrderingAddressDto.setCustomer("");
		cpiOrderingAddressDto.setAlternatpayee("");
		cpiOrderingAddressDto.setDeletionflag(false);
		cpiOrderingAddressDto.setPostingBlock(false);
		cpiOrderingAddressDto.setPurchblock(false);
		cpiOrderingAddressDto.setTaxNumber1("");
		cpiOrderingAddressDto.setTaxNumber2("");
		cpiOrderingAddressDto.setEqualizatntax("");
		cpiOrderingAddressDto.setLiableforVAT(false);
		cpiOrderingAddressDto.setPayeeindoc(false);
		cpiOrderingAddressDto.setTradingPartner("");
		cpiOrderingAddressDto.setFiscaladdress("");
		cpiOrderingAddressDto.setVATRegNo("");
		cpiOrderingAddressDto.setNaturalperson("");
		cpiOrderingAddressDto.setBlockfunction("");
		cpiOrderingAddressDto.setAddress("");
		cpiOrderingAddressDto.setPlaceofbirth("");
		cpiOrderingAddressDto.setBirthdate("");
		cpiOrderingAddressDto.setSex("");
		cpiOrderingAddressDto.setCredinfono("");
		cpiOrderingAddressDto.setLastextreview("");
		cpiOrderingAddressDto.setActualQMsys("");
		cpiOrderingAddressDto.setRefacctgroup("");
		cpiOrderingAddressDto.setPlant("");
		cpiOrderingAddressDto.setPlant("");
		cpiOrderingAddressDto.setFactorycalend("");
		cpiOrderingAddressDto.setSCAC("");
		cpiOrderingAddressDto.setCarfreightgrp("");
		cpiOrderingAddressDto.setServAgntProcGrp("");
		cpiOrderingAddressDto.setTaxtype("");
		cpiOrderingAddressDto.setTaxnumbertype("");
		cpiOrderingAddressDto.setSocialIns(false);
		cpiOrderingAddressDto.setSocInsCode("");
		cpiOrderingAddressDto.setTaxNumber3("");
		cpiOrderingAddressDto.setTaxNumber4("");
		cpiOrderingAddressDto.setTaxsplit(false);
		cpiOrderingAddressDto.setTaxbase("");
		cpiOrderingAddressDto.setProfession("");
		cpiOrderingAddressDto.setStatgrpagent("");
		cpiOrderingAddressDto.setExternalmanuf("");
		cpiOrderingAddressDto.setDeletionblock(false);
		cpiOrderingAddressDto.setRepsName("");
		cpiOrderingAddressDto.setTypeofBusiness("");
		cpiOrderingAddressDto.setTypeofIndustry("");
		cpiOrderingAddressDto.setPODrelevant("");
		cpiOrderingAddressDto.setTaxoffice("");
		cpiOrderingAddressDto.setTaxNumber("");
		cpiOrderingAddressDto.setTaxNumber5("");
		cpiOrderingAddressDto.setPurposeCompleteFlag("");
		cpiOrderingAddressDto.setAddressVersion("");
		cpiOrderingAddressDto.setFrom("/Date(253402214400000)/");
		cpiOrderingAddressDto.setTo("/Date(253402214400000)/");
		cpiOrderingAddressDto.setTitle(bpBusinessPartnerOrderingAddressDto.getTitle() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getTitle());
		cpiOrderingAddressDto.setName(bpBusinessPartnerOrderingAddressDto.getName1() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getName1());
		cpiOrderingAddressDto.setName2(bpBusinessPartnerOrderingAddressDto.getName2() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getName2());
		cpiOrderingAddressDto.setName3(bpBusinessPartnerOrderingAddressDto.getName3() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getName3());
		cpiOrderingAddressDto.setName4(bpBusinessPartnerOrderingAddressDto.getName4() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getName4());
		cpiOrderingAddressDto.setConvname("");
		cpiOrderingAddressDto.setCo(
				bpBusinessPartnerOrderingAddressDto.getCo() == null ? "" : bpBusinessPartnerOrderingAddressDto.getCo());
		cpiOrderingAddressDto.setCity(bpBusinessPartnerOrderingAddressDto.getCity() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getCity());
		cpiOrderingAddressDto.setDistrict(bpBusinessPartnerOrderingAddressDto.getDistrict() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getDistrict());
		cpiOrderingAddressDto.setCityNo("");
		cpiOrderingAddressDto.setCheckStatus("");
		cpiOrderingAddressDto.setRegStrGrp(bpBusinessPartnerOrderingAddressDto.getRegStructGrp() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getRegStructGrp());
		cpiOrderingAddressDto.setPostalCode(bpBusinessPartnerOrderingAddressDto.getPostalCode() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getPostalCode());
		cpiOrderingAddressDto.setPOBoxPostCde("");
		cpiOrderingAddressDto.setCompanyPostCd("");
		cpiOrderingAddressDto.setPostalCodeExt("");
		cpiOrderingAddressDto.setPostalCodeExt2("");
		cpiOrderingAddressDto.setPostalCodeExt3("");
		cpiOrderingAddressDto.setPOBox("");
		cpiOrderingAddressDto.setPOBoxwono(false);
		cpiOrderingAddressDto.setPOBoxCity("");
		cpiOrderingAddressDto.setPOCitNo("");
		cpiOrderingAddressDto.setPORegion("");
		cpiOrderingAddressDto.setPOboxcountry("");
		cpiOrderingAddressDto.setISOcode("");
		cpiOrderingAddressDto.setDeliveryDist("");
		cpiOrderingAddressDto.setTransportzone(bpBusinessPartnerOrderingAddressDto.getTransportZone() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getTransportZone());
		cpiOrderingAddressDto.setStreet(bpBusinessPartnerOrderingAddressDto.getStreet() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStreet());
		cpiOrderingAddressDto.setStreetCode("");
		cpiOrderingAddressDto.setStreetAbbrev("");
		cpiOrderingAddressDto.setHouseNumber(bpBusinessPartnerOrderingAddressDto.getHouseNo() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getHouseNo());
		cpiOrderingAddressDto.setSupplement(bpBusinessPartnerOrderingAddressDto.getSuppl() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getSuppl());
		cpiOrderingAddressDto.setNumberRange("");
		cpiOrderingAddressDto.setStreet2(bpBusinessPartnerOrderingAddressDto.getStreet2() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStreet2());
		cpiOrderingAddressDto.setStreet3(bpBusinessPartnerOrderingAddressDto.getStreet3() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStreet3());
		cpiOrderingAddressDto.setStreet4(bpBusinessPartnerOrderingAddressDto.getStreet4() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStreet4());
		cpiOrderingAddressDto.setStreet5(bpBusinessPartnerOrderingAddressDto.getStreet5() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStreet5());
		cpiOrderingAddressDto.setBuildingCode(bpBusinessPartnerOrderingAddressDto.getBuildingCode() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getBuildingCode());
		cpiOrderingAddressDto.setFloor(bpBusinessPartnerOrderingAddressDto.getFloor() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getFloor());
		cpiOrderingAddressDto.setRoomNumber(bpBusinessPartnerOrderingAddressDto.getRoom() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getRoom());
		cpiOrderingAddressDto.setCountry(bpBusinessPartnerOrderingAddressDto.getCountry() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getCountry());
		cpiOrderingAddressDto.setCountryISO("");
		cpiOrderingAddressDto.setLanguage(bpBusinessPartnerOrderingAddressDto.getLanguage() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getLanguage());
		cpiOrderingAddressDto.setLangISO("");
		cpiOrderingAddressDto.setRegion(bpBusinessPartnerOrderingAddressDto.getRegion() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getRegion());
		cpiOrderingAddressDto.setSearchTerm1(
				bpRequestGeneralDataDto.getSearchTerm1() == null ? "NA" : bpRequestGeneralDataDto.getSearchTerm1());
		cpiOrderingAddressDto.setSearchTerm2("");
		cpiOrderingAddressDto.setDataline("");
		cpiOrderingAddressDto.setTelebox("");
		cpiOrderingAddressDto.setTimezone(bpBusinessPartnerOrderingAddressDto.getTimeZone() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getTimeZone());
		cpiOrderingAddressDto.setTaxJurisdictn(bpBusinessPartnerOrderingAddressDto.getTaxJurisdiction() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getTaxJurisdiction());
		cpiOrderingAddressDto.setAddressID("");
		cpiOrderingAddressDto.setCreationlang("");
		cpiOrderingAddressDto.setLangCRISO("");
		cpiOrderingAddressDto.setCommMethod(bpBusinessPartnerOrderingAddressDto.getStandardCommMethod() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStandardCommMethod());
		cpiOrderingAddressDto.setAddressgroup("");
		cpiOrderingAddressDto.setDifferentCity(bpBusinessPartnerOrderingAddressDto.getDifferentCity() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getDifferentCity());
		cpiOrderingAddressDto.setCityCode("");
		cpiOrderingAddressDto.setUndeliverable(bpBusinessPartnerOrderingAddressDto.getUndeliverable() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getUndeliverable());
		cpiOrderingAddressDto.setUndeliverable1("");
		cpiOrderingAddressDto.setPOBoxLobby("");
		cpiOrderingAddressDto.setDelvryServType("");
		cpiOrderingAddressDto.setDeliveryServiceNo("");
		cpiOrderingAddressDto.setCountycode("");
		cpiOrderingAddressDto.setCounty("");
		cpiOrderingAddressDto.setTownshipcode("");
		cpiOrderingAddressDto.setTownship("");
		cpiOrderingAddressDto.setPAN("");
		return cpiOrderingAddressDto;
	}

	private CPIInvoicePartyDto convert(BPBusinessPartnerRemittanceAddressDto bpBusinessPartnerRemittanceAddressDto,
			BPRequestGeneralDataDto bpRequestGeneralDataDto) {
		CPIInvoicePartyDto cpiInvoicePartyDto = new CPIInvoicePartyDto();
		cpiInvoicePartyDto.setType("Invoice");
		cpiInvoicePartyDto.setValidation("");
		cpiInvoicePartyDto.setChangeIndObject("I");
		cpiInvoicePartyDto.setVendor("");
		cpiInvoicePartyDto.setTrainstation("");
		cpiInvoicePartyDto.setLocationno1("");
		cpiInvoicePartyDto.setLocationno2("");
		cpiInvoicePartyDto.setAuthorization("");
		cpiInvoicePartyDto.setIndustry("");
		cpiInvoicePartyDto.setCheckdigit("");
		cpiInvoicePartyDto.setDMEIndicator("");
		cpiInvoicePartyDto.setInstructionkey("");
		cpiInvoicePartyDto.setISRNumber("");
		cpiInvoicePartyDto.setCorporateGroup("");
		cpiInvoicePartyDto.setAccountgroup("0004");
		cpiInvoicePartyDto.setCustomer("");
		cpiInvoicePartyDto.setAlternatpayee("");
		cpiInvoicePartyDto.setDeletionflag(false);
		cpiInvoicePartyDto.setPostingBlock(false);
		cpiInvoicePartyDto.setPurchblock(false);
		cpiInvoicePartyDto.setTaxNumber1("");
		cpiInvoicePartyDto.setTaxNumber2("");
		cpiInvoicePartyDto.setEqualizatntax("");
		cpiInvoicePartyDto.setLiableforVAT(false);
		cpiInvoicePartyDto.setPayeeindoc(false);
		cpiInvoicePartyDto.setTradingPartner("");
		cpiInvoicePartyDto.setFiscaladdress("");
		cpiInvoicePartyDto.setVATRegNo("");
		cpiInvoicePartyDto.setNaturalperson("");
		cpiInvoicePartyDto.setBlockfunction("");
		cpiInvoicePartyDto.setAddress("");
		cpiInvoicePartyDto.setPlaceofbirth("");
		cpiInvoicePartyDto.setBirthdate("");
		cpiInvoicePartyDto.setSex("");
		cpiInvoicePartyDto.setCredinfono("");
		cpiInvoicePartyDto.setLastextreview("");
		cpiInvoicePartyDto.setActualQMsys("");
		cpiInvoicePartyDto.setRefacctgroup("");
		cpiInvoicePartyDto.setPlant("");
		cpiInvoicePartyDto.setPlant("");
		cpiInvoicePartyDto.setFactorycalend("");
		cpiInvoicePartyDto.setSCAC("");
		cpiInvoicePartyDto.setCarfreightgrp("");
		cpiInvoicePartyDto.setServAgntProcGrp("");
		cpiInvoicePartyDto.setTaxtype("");
		cpiInvoicePartyDto.setTaxnumbertype("");
		cpiInvoicePartyDto.setSocialIns(false);
		cpiInvoicePartyDto.setSocInsCode("");
		cpiInvoicePartyDto.setTaxNumber3("");
		cpiInvoicePartyDto.setTaxNumber4("");
		cpiInvoicePartyDto.setTaxsplit(false);
		cpiInvoicePartyDto.setTaxbase("");
		cpiInvoicePartyDto.setProfession("");
		cpiInvoicePartyDto.setStatgrpagent("");
		cpiInvoicePartyDto.setExternalmanuf("");
		cpiInvoicePartyDto.setDeletionblock(false);
		cpiInvoicePartyDto.setRepsName("");
		cpiInvoicePartyDto.setTypeofBusiness("");
		cpiInvoicePartyDto.setTypeofIndustry("");
		cpiInvoicePartyDto.setPODrelevant("");
		cpiInvoicePartyDto.setTaxoffice("");
		cpiInvoicePartyDto.setTaxNumber("");
		cpiInvoicePartyDto.setTaxNumber5("");
		cpiInvoicePartyDto.setPurposeCompleteFlag("");
		cpiInvoicePartyDto.setAddressVersion("");
		cpiInvoicePartyDto.setFrom("/Date(253402214400000)/");
		cpiInvoicePartyDto.setTo("/Date(253402214400000)/");
		cpiInvoicePartyDto.setTitle(bpBusinessPartnerRemittanceAddressDto.getTitle() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getTitle());
		cpiInvoicePartyDto.setName(bpBusinessPartnerRemittanceAddressDto.getName1() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getName1());
		cpiInvoicePartyDto.setName2(bpBusinessPartnerRemittanceAddressDto.getName2() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getName2());
		cpiInvoicePartyDto.setName3(bpBusinessPartnerRemittanceAddressDto.getName3() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getName3());
		cpiInvoicePartyDto.setName4(bpBusinessPartnerRemittanceAddressDto.getName4() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getName4());
		cpiInvoicePartyDto.setConvname("");
		cpiInvoicePartyDto.setCo(bpBusinessPartnerRemittanceAddressDto.getCo() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getCo());
		cpiInvoicePartyDto.setCity(bpBusinessPartnerRemittanceAddressDto.getCity() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getCity());
		cpiInvoicePartyDto.setDistrict(bpBusinessPartnerRemittanceAddressDto.getDistrict() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getDistrict());
		cpiInvoicePartyDto.setCityNo("");
		cpiInvoicePartyDto.setCheckStatus("");
		cpiInvoicePartyDto.setRegStrGrp(bpBusinessPartnerRemittanceAddressDto.getRegStructGrp() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getRegStructGrp());
		cpiInvoicePartyDto.setPostalCode(bpBusinessPartnerRemittanceAddressDto.getPostalCode() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getPostalCode());
		cpiInvoicePartyDto.setPOBoxPostCde("");
		cpiInvoicePartyDto.setCompanyPostCd("");
		cpiInvoicePartyDto.setPostalCodeExt("");
		cpiInvoicePartyDto.setPostalCodeExt2("");
		cpiInvoicePartyDto.setPostalCodeExt3("");
		cpiInvoicePartyDto.setPOBox("");
		cpiInvoicePartyDto.setPOBoxwono(false);
		cpiInvoicePartyDto.setPOBoxCity("");
		cpiInvoicePartyDto.setPOCitNo("");
		cpiInvoicePartyDto.setPORegion("");
		cpiInvoicePartyDto.setPOboxcountry("");
		cpiInvoicePartyDto.setISOcode("");
		cpiInvoicePartyDto.setDeliveryDist("");
		cpiInvoicePartyDto.setTransportzone(bpBusinessPartnerRemittanceAddressDto.getTransportZone() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getTransportZone());
		cpiInvoicePartyDto.setStreet(bpBusinessPartnerRemittanceAddressDto.getStreet() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStreet());
		cpiInvoicePartyDto.setStreetCode("");
		cpiInvoicePartyDto.setStreetAbbrev("");
		cpiInvoicePartyDto.setHouseNumber(bpBusinessPartnerRemittanceAddressDto.getHouseNo() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getHouseNo());
		cpiInvoicePartyDto.setSupplement(bpBusinessPartnerRemittanceAddressDto.getSuppl() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getSuppl());
		cpiInvoicePartyDto.setNumberRange("");
		cpiInvoicePartyDto.setStreet2(bpBusinessPartnerRemittanceAddressDto.getStreet2() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStreet2());
		cpiInvoicePartyDto.setStreet3(bpBusinessPartnerRemittanceAddressDto.getStreet3() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStreet3());
		cpiInvoicePartyDto.setStreet4(bpBusinessPartnerRemittanceAddressDto.getStreet4() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStreet4());
		cpiInvoicePartyDto.setStreet5(bpBusinessPartnerRemittanceAddressDto.getStreet5() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStreet5());
		cpiInvoicePartyDto.setBuildingCode(bpBusinessPartnerRemittanceAddressDto.getBuildingCode() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getBuildingCode());
		cpiInvoicePartyDto.setFloor(bpBusinessPartnerRemittanceAddressDto.getFloor() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getFloor());
		cpiInvoicePartyDto.setRoomNumber(bpBusinessPartnerRemittanceAddressDto.getRoom() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getRoom());
		cpiInvoicePartyDto.setCountry(bpBusinessPartnerRemittanceAddressDto.getCountry() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getCountry());
		cpiInvoicePartyDto.setCountryISO("");
		cpiInvoicePartyDto.setLanguage(bpBusinessPartnerRemittanceAddressDto.getLanguage() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getLanguage());
		cpiInvoicePartyDto.setLangISO("");
		cpiInvoicePartyDto.setRegion(bpBusinessPartnerRemittanceAddressDto.getRegion() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getRegion());
		cpiInvoicePartyDto.setSearchTerm1(
				bpRequestGeneralDataDto.getSearchTerm1() == null ? "NA" : bpRequestGeneralDataDto.getSearchTerm1());
		cpiInvoicePartyDto.setSearchTerm2("");
		cpiInvoicePartyDto.setDataline("");
		cpiInvoicePartyDto.setTelebox("");
		cpiInvoicePartyDto.setTimezone(bpBusinessPartnerRemittanceAddressDto.getTimeZone() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getTimeZone());
		cpiInvoicePartyDto.setTaxJurisdictn(bpBusinessPartnerRemittanceAddressDto.getTaxJurisdiction() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getTaxJurisdiction());
		cpiInvoicePartyDto.setAddressID("");
		cpiInvoicePartyDto.setCreationlang("");
		cpiInvoicePartyDto.setLangCRISO("");
		cpiInvoicePartyDto.setCommMethod(bpBusinessPartnerRemittanceAddressDto.getStandardCommMethod() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStandardCommMethod());
		cpiInvoicePartyDto.setAddressgroup("");
		cpiInvoicePartyDto.setDifferentCity(bpBusinessPartnerRemittanceAddressDto.getDifferentCity() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getDifferentCity());
		cpiInvoicePartyDto.setCityCode("");
		cpiInvoicePartyDto.setUndeliverable(bpBusinessPartnerRemittanceAddressDto.getUndeliverable() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getUndeliverable());
		cpiInvoicePartyDto.setUndeliverable1("");
		cpiInvoicePartyDto.setPOBoxLobby("");
		cpiInvoicePartyDto.setDelvryServType("");
		cpiInvoicePartyDto.setDeliveryServiceNo("");
		cpiInvoicePartyDto.setCountycode("");
		cpiInvoicePartyDto.setCounty("");
		cpiInvoicePartyDto.setTownshipcode("");
		cpiInvoicePartyDto.setTownship("");
		cpiInvoicePartyDto.setPAN("");
		return cpiInvoicePartyDto;
	}

	private CPIPhoneDto convert(BPTelephoneDto telephoneDto) {
		CPIPhoneDto cpiPhoneDto = new CPIPhoneDto();

		cpiPhoneDto.setChangeIndObject("I");
		cpiPhoneDto.setVendor("");
		cpiPhoneDto.setCountry(telephoneDto.getCountry() == null ? "" : telephoneDto.getCountry());
		cpiPhoneDto.setISOcode(telephoneDto.getCountry() == null ? "" : telephoneDto.getCountry());
		cpiPhoneDto.setStandardNo(telephoneDto.isStandardNumber() == true ? true : telephoneDto.isStandardNumber());
		cpiPhoneDto.setTelephone(telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone());
		cpiPhoneDto.setExtension(telephoneDto.getExtension() == null ? "" : telephoneDto.getExtension());
		cpiPhoneDto.setTelephoneno((telephoneDto.getExtension() == null ? "" : telephoneDto.getExtension())
				+ (telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone()));
		cpiPhoneDto.setCallernumber(telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone());
		cpiPhoneDto.setSMSEnab("");
		cpiPhoneDto.setMobilephone("1");
		cpiPhoneDto.setHomeaddress(true);
		cpiPhoneDto.setSequenceNumber("1");
		cpiPhoneDto.setError(false);
		cpiPhoneDto.setDonotuse(telephoneDto.isDoNotUse());
		cpiPhoneDto.setValidFrom("");
		cpiPhoneDto.setValidTo("");
		;

		return cpiPhoneDto;

	}

	private CPIPhoneDto convert(BPMobilePhoneDto telephoneDto) {
		CPIPhoneDto cpiPhoneDto = new CPIPhoneDto();

		cpiPhoneDto.setChangeIndObject("I");
		cpiPhoneDto.setVendor("");
		cpiPhoneDto.setCountry(telephoneDto.getCountry() == null ? "" : telephoneDto.getCountry());
		cpiPhoneDto.setISOcode(telephoneDto.getCountry() == null ? "" : telephoneDto.getCountry());
		cpiPhoneDto.setStandardNo(telephoneDto.isStandardNumber() == true ? true : telephoneDto.isStandardNumber());
		cpiPhoneDto.setTelephone(telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone());
		cpiPhoneDto.setExtension(telephoneDto.getExtension() == null ? "" : telephoneDto.getExtension());
		cpiPhoneDto.setTelephoneno((telephoneDto.getExtension() == null ? "" : telephoneDto.getExtension())
				+ (telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone()));
		cpiPhoneDto.setCallernumber(telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone());
		cpiPhoneDto.setSMSEnab("");
		cpiPhoneDto.setMobilephone("3");
		cpiPhoneDto.setHomeaddress(true);
		cpiPhoneDto.setSequenceNumber("001");
		cpiPhoneDto.setError(false);
		cpiPhoneDto.setDonotuse(telephoneDto.isDoNotUse());
		cpiPhoneDto.setValidFrom("");
		cpiPhoneDto.setValidTo("");
		return cpiPhoneDto;

	}

	private CPIEmailDto convert(BPEmailDto bpEmailDto) {
		CPIEmailDto cpiEmailDto = new CPIEmailDto();
		cpiEmailDto.setChangeIndObject("I");
		cpiEmailDto.setVendor("");
		cpiEmailDto.setStandardNo(bpEmailDto.getStandardNumber() == null ? true : bpEmailDto.getStandardNumber());
		cpiEmailDto.setEMailAddress(bpEmailDto.getEmailAddress() == null ? "" : bpEmailDto.getEmailAddress());
//		cpiEmailDto.setEMailAddress("vaibhav.anand@incture.com");
		cpiEmailDto.setEMailAddressSearch("");
		cpiEmailDto.setStdrecipient(false);
		cpiEmailDto.setSAPConnection(false);
		cpiEmailDto.setCoding("");
		cpiEmailDto.setTNEF(false);
		cpiEmailDto.setHomeaddress(true);
		cpiEmailDto.setSequenceNumber("001");
		cpiEmailDto.setError(false);
		cpiEmailDto.setDonotuse(HelperClass.isEmpty(bpEmailDto.isDoNotUse()) == true ? false : bpEmailDto.isDoNotUse());
		cpiEmailDto.setValidFrom("");
		cpiEmailDto.setValidTo("");
		return cpiEmailDto;
	}

	private CPIBankDto convert(BPBankInformationDto bpBankInformationDto) {
		CPIBankDto cpiBankDto = new CPIBankDto();
		cpiBankDto.setChangeIndObject("I");
		cpiBankDto.setVendor("");
//		cpiBankDto.setBanknumber(bpBankInformationDto.getBankCountry() == null ? "" : bpBankInformationDto.getBankCountry());
//		cpiBankDto.setBankCountry(bpBankInformationDto.getBankKey() == null ? "" : bpBankInformationDto.getBankKey());
		cpiBankDto.setBanknumber(bpBankInformationDto.getBankKey() == null ? "" : bpBankInformationDto.getBankKey());
		cpiBankDto.setBankCountry(
				bpBankInformationDto.getBankCountry() == null ? "" : bpBankInformationDto.getBankCountry());
		cpiBankDto.setBankAccount(
				bpBankInformationDto.getBankAccountNo() == null ? "" : bpBankInformationDto.getBankAccountNo());
		cpiBankDto.setControlkey(
				bpBankInformationDto.getControlKey() == null ? "" : bpBankInformationDto.getControlKey());
		cpiBankDto.setPartBankType(bpBankInformationDto.getBankT() == null ? "" : bpBankInformationDto.getBankT());
		cpiBankDto.setCollectauthor(bpBankInformationDto.getDebitAuthorization() == null ? false
				: bpBankInformationDto.getDebitAuthorization());
		cpiBankDto.setReference(
				bpBankInformationDto.getReferenceDetails() == null ? "" : bpBankInformationDto.getReferenceDetails());
		cpiBankDto.setAccountholder(
				bpBankInformationDto.getAccHolderName() == null ? "" : bpBankInformationDto.getAccHolderName());
		cpiBankDto.setIBAN(bpBankInformationDto.getIban() == null ? "" : bpBankInformationDto.getIban());
		cpiBankDto.setIBANvalidfrom(null);
		cpiBankDto.setSwiftCode(bpBankInformationDto.getSwift() == null ? "" : bpBankInformationDto.getSwift());

		return cpiBankDto;
	}

	private CPIFaxDto convert(BPFaxInfoDto faxDto) {
		 
		CPIFaxDto cpiFaxDto = new CPIFaxDto();
		cpiFaxDto.setChangeIndObject("I");
		cpiFaxDto.setVendor("");
		cpiFaxDto.setCountry(faxDto.getCountry() == null ? "" : faxDto.getCountry());
		cpiFaxDto.setISOcode(faxDto.getCountry() == null ? "" : faxDto.getCountry());
		cpiFaxDto.setStandardNo(false);
		cpiFaxDto.setFax(faxDto.getFax() == null ? "" : faxDto.getFax());
		cpiFaxDto.setExtension(faxDto.getExtension() == null ? "" : faxDto.getExtension());
		cpiFaxDto.setFaxnumber((faxDto.getExtension() == null ? "" : faxDto.getExtension())
				+ (faxDto.getFax() == null ? "" : faxDto.getFax()));
		cpiFaxDto.setSendernumber(faxDto.getFax() == null ? "" : faxDto.getFax());
		cpiFaxDto.setFaxgroup("");
		cpiFaxDto.setStdrecipient(false);
		cpiFaxDto.setSAPConnection(false);
		cpiFaxDto.setHomeaddress(true);
		cpiFaxDto.setSequenceNumber("001");
		cpiFaxDto.setError(false);
		cpiFaxDto.setDonotuse(false);
		cpiFaxDto.setValidFrom("");
		cpiFaxDto.setValidTo("");
		return cpiFaxDto;
	}

	private CPIPlantDto plantConvert(BPPurchasingOrgDetailDto bpPurchasingOrgDetailDto) {
		CPIPlantDto cpiPlantDto = new CPIPlantDto();

		cpiPlantDto.setChangeIndObject("I");
		cpiPlantDto.setVendor("");
		cpiPlantDto.setVendorSubrange("");
		cpiPlantDto.setPlant(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getPlant() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getPlant());
		cpiPlantDto.setPurblockPOrg(false);
		cpiPlantDto.setDelflagPOrg(false);
		cpiPlantDto.setABCindicator(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAbcIndicator() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAbcIndicator());
		cpiPlantDto.setOrdercurrency(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getOrderCurrency() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getOrderCurrency());
		cpiPlantDto.setSalesperson(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSalesPerson() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSalesPerson());
		cpiPlantDto.setMinimumvalue(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue() == null ? "0.00"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue());
		cpiPlantDto.setPaytTerms(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTermsOfPayment() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTermsOfPayment());
		cpiPlantDto.setIncoterms(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms());
		cpiPlantDto.setIncoterms2("Free On Board");
		cpiPlantDto.setGRBasedIV(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getGrBasedInvVerify() == null ? true
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getGrBasedInvVerify());
		cpiPlantDto.setAcknowlReqd(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAcknowledgementReqd() == null ? true
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAcknowledgementReqd());
		cpiPlantDto.setSchemaGrpVndr(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSchemaGroupVendor() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSchemaGroupVendor());
		cpiPlantDto.setAutomaticPO(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getAutomaticPurchaseOrder() == null
						? false
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getAutomaticPurchaseOrder());
		cpiPlantDto
				.setModeOfTrBorder(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getModeOfTransportBorder() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getModeOfTransportBorder());
		cpiPlantDto.setCustomsoffice("");
		cpiPlantDto.setPrDateCat("");
		cpiPlantDto.setPurchGroup(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPurchasingGroup() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPurchasingGroup());
		cpiPlantDto.setSubseqsett(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubsequentSettlement() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubsequentSettlement());
		cpiPlantDto.setBvolcompag(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getBVolComp() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getBVolComp());
		cpiPlantDto.setERS(false);
		cpiPlantDto.setPlDelivTime(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime() == null ? "0"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime());
		cpiPlantDto.setPlanningcal("");
		cpiPlantDto.setPlanningcycle(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getPlanningCycle() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getPlanningCycle());
		cpiPlantDto.setPOentryvend("");
		cpiPlantDto.setPricemkgvnd(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPriceMarkingAgreed() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPriceMarkingAgreed());
		cpiPlantDto.setRackjobbing("");
		cpiPlantDto.setMRPController(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getMrpController() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getMrpController());
		cpiPlantDto.setConfControl(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getConfirmationControl() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getConfirmationControl());
		cpiPlantDto.setRndingProfile(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getRoundingProfile() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getRoundingProfile());
		cpiPlantDto.setUoMGroup(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getUnitofMeasureGroup() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getUnitofMeasureGroup());
		cpiPlantDto.setLBprofile("");
		cpiPlantDto.setAutGRSetRet(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutoEvalGRSetMtRet() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutoEvalGRSetMtRet());
		cpiPlantDto.setPROACTcontrolprof(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getProActControlProf() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getProActControlProf());
		cpiPlantDto.setRevaluation(false);
		cpiPlantDto.setSrvBasedInvVer(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSrvBasedInvVar() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSrvBasedInvVar());

		return cpiPlantDto;
	}

	private CPIPlantDto convert(BPPurchaseOrgAdditionalDataDto bpPurchaseOrgAdditionalDataDto, String purchasingOrg) {
		CPIPlantDto cpiPlantDto = new CPIPlantDto();

		cpiPlantDto.setChangeIndObject("I");
		cpiPlantDto.setVendor("");
		cpiPlantDto.setVendorSubrange("");
		cpiPlantDto.setPurchasingOrg(purchasingOrg == null ? "" : purchasingOrg);
		cpiPlantDto.setPlant(
				bpPurchaseOrgAdditionalDataDto.getPlant() == null ? "" : bpPurchaseOrgAdditionalDataDto.getPlant());
		cpiPlantDto.setPurblockPOrg(false);
		cpiPlantDto.setDelflagPOrg(false);
		cpiPlantDto.setABCindicator(bpPurchaseOrgAdditionalDataDto.getAbcIndicator() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getAbcIndicator());
		cpiPlantDto.setOrdercurrency(bpPurchaseOrgAdditionalDataDto.getOrderCurrency() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getOrderCurrency());
		cpiPlantDto.setSalesperson("");
		cpiPlantDto.setMinimumvalue(bpPurchaseOrgAdditionalDataDto.getMinOrderValue() == null ? "0.00"
				: bpPurchaseOrgAdditionalDataDto.getMinOrderValue());
//		cpiPlantDto.setMinimumvalue(bpPurchaseOrgAdditionalDataDto.getMinOrderValue()==null?"":bpPurchaseOrgAdditionalDataDto.getMinOrderValue());
		cpiPlantDto.setPaytTerms(bpPurchaseOrgAdditionalDataDto.getTermsOfPayment() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getTermsOfPayment());
		cpiPlantDto.setIncoterms(bpPurchaseOrgAdditionalDataDto.getIncoTerms() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getIncoTerms());
		cpiPlantDto.setIncoterms2(bpPurchaseOrgAdditionalDataDto.getIncoTerms2() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getIncoTerms2());
		cpiPlantDto.setGRBasedIV(bpPurchaseOrgAdditionalDataDto.getGrBasedInvVerify() == null ? true
				: bpPurchaseOrgAdditionalDataDto.getGrBasedInvVerify());
		cpiPlantDto.setAcknowlReqd(bpPurchaseOrgAdditionalDataDto.getAcknowledgementReqd() == null ? true
				: bpPurchaseOrgAdditionalDataDto.getAcknowledgementReqd());
		cpiPlantDto.setSchemaGrpVndr("");
		cpiPlantDto.setAutomaticPO(bpPurchaseOrgAdditionalDataDto.getAutomaticPurchaseOrder() == null ? false
				: bpPurchaseOrgAdditionalDataDto.getAutomaticPurchaseOrder());
		cpiPlantDto.setModeOfTrBorder("");
		cpiPlantDto.setCustomsoffice("");
		cpiPlantDto.setPrDateCat("");
		cpiPlantDto.setPurchGroup(bpPurchaseOrgAdditionalDataDto.getPurchasingGroup() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getPurchasingGroup());
		cpiPlantDto.setSubseqsett(false);
		cpiPlantDto.setBvolcompag(false);
		cpiPlantDto.setERS(false);
		cpiPlantDto.setPlDelivTime(bpPurchaseOrgAdditionalDataDto.getPlanneddelivtime() == null ? "0"
				: bpPurchaseOrgAdditionalDataDto.getPlanneddelivtime());
//		cpiPlantDto.setPlDelivTime(bpPurchaseOrgAdditionalDataDto.getPlanneddelivtime()==null?"":bpPurchaseOrgAdditionalDataDto.getPlanneddelivtime());
		cpiPlantDto.setPlanningcal("");
		cpiPlantDto.setPlanningcycle(bpPurchaseOrgAdditionalDataDto.getPlanningCycle() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getPlanningCycle());
		cpiPlantDto.setPOentryvend("");
		cpiPlantDto.setPricemkgvnd("");
		cpiPlantDto.setRackjobbing("");
		cpiPlantDto.setMRPController(bpPurchaseOrgAdditionalDataDto.getMrpController() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getMrpController());
		cpiPlantDto.setConfControl(bpPurchaseOrgAdditionalDataDto.getConfirmationControl() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getConfirmationControl());
		cpiPlantDto.setRndingProfile(bpPurchaseOrgAdditionalDataDto.getRoundingProfile() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getRoundingProfile());
		cpiPlantDto.setUoMGroup(bpPurchaseOrgAdditionalDataDto.getUnitofMeasureGroup() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getUnitofMeasureGroup());
		cpiPlantDto.setLBprofile("");
		cpiPlantDto.setAutGRSetRet(bpPurchaseOrgAdditionalDataDto.getAutoEvalGRSetMtRet() == null ? false
				: bpPurchaseOrgAdditionalDataDto.getAutoEvalGRSetMtRet());
		cpiPlantDto.setPROACTcontrolprof(bpPurchaseOrgAdditionalDataDto.getProActControlProf() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getProActControlProf());
		cpiPlantDto.setRevaluation(false);
		cpiPlantDto.setSrvBasedInvVer(bpPurchaseOrgAdditionalDataDto.getSrvBasedInvVar() == null ? false
				: bpPurchaseOrgAdditionalDataDto.getSrvBasedInvVar());

		return cpiPlantDto;
	}

	public BusinessPartnerResponse changeVendorInfo(BPCreationFromWorkflowRequest bpCreationFromWorkflowRequest)
			throws UnirestException, ClientProtocolException, IOException {

		BPRequestGeneralDataDto bpRequestGeneralDataDto = bpDetailService
				.getBPDetailsByRequestId(bpCreationFromWorkflowRequest.getRequestId());

		CPIVendorDetailsDto detailsDto = convertChange1(bpRequestGeneralDataDto,
				bpCreationFromWorkflowRequest.isValidate());

		String accessToken, url = null;
		try {
			String destDetails = destinationUtil.readMdgDestination("mdg-vm-cpi", null, null);

			org.json.JSONObject resObj = new org.json.JSONObject(destDetails);
			log.info("Json object from destination :" + resObj);
			log.info("Client id: " + resObj.optJSONObject("destinationConfiguration").optString("clientId"));
			log.info("clientSecret : " + resObj.optJSONObject("destinationConfiguration").optString("clientSecret"));
			log.info("tokenServiceURL: "
					+ resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));
			log.info("url: " + resObj.optJSONObject("destinationConfiguration").optString("URL"));
			accessToken = getAccessToken(resObj.optJSONObject("destinationConfiguration").optString("clientId"),
					resObj.optJSONObject("destinationConfiguration").optString("clientSecret"),
					resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));

//	            url=resObj.optJSONObject("destinationConfiguration").optString("URL")+"/http/Vendor_Change";
			if (profile.toUpperCase(Locale.ROOT).equalsIgnoreCase("PROD")) {
				url = resObj.optJSONObject("destinationConfiguration").optString("URL") + "/http/Vendor_Change";
			} else {

				url = resObj.optJSONObject("destinationConfiguration").optString("URL")
//					+ "/http/Viatris";
						+ "/http/Viatris/CP_HanaDb/BusinessPatrner/Change";
			}

//	            https://viatris-its-dev-092tl30u.it-cpi013-rt.cfapps.us21.hana.ondemand.com/http/Vendor_Create
//	            https://viatris-its-dev-092tl30u.it-cpi013-rt.cfapps.us21.hana.ondemand.com

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		BusinessPartnerResponse result = new BusinessPartnerResponse();
		String body = new ObjectMapper().writeValueAsString(detailsDto);
		System.out.println("**%% ODATA" + body);
		String urlValue = "https://inccpidev.it-cpi001-rt.cfapps.eu10.hana.ondemand.com/http/Vendor_CreateSap";
		System.out.println("*******");
//        System.out.println(accessToken());
		System.out.println(accessToken);

		System.out.println("*******");
		HttpResponse<String> response = Unirest.post(url).header("authorization", "Bearer " + accessToken)
				.header("Content-Type", "application/json").header("Accept", "application/json").body(body).asString();
		int status = response.getStatus();
		System.out
				.println(status + "************************************************************************************"
						+ response + "  " + response.getBody());
		JsonObject object = new JsonParser().parse(response.getBody()).getAsJsonObject();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(response.getBody());
		JsonNode errorDetailsNode = rootNode.path("error").path("innererror").path("errordetails").path("errordetail");

		System.out.println(errorDetailsNode);
		System.out.println(errorDetailsNode.asText());
		if (rootNode.has("error")) {
			if ((!errorDetailsNode.isArray() || errorDetailsNode.size() == 0)) {
				System.out.println("inside empty");
				JsonNode message = rootNode.path("error").path("message").path("$");
				ObjectMapper mapper = new ObjectMapper();
				ObjectNode errorDetails = mapper.createObjectNode();
				ArrayNode errorDetailArray = JsonNodeFactory.instance.arrayNode();
				ObjectNode messageObject = mapper.createObjectNode();
				messageObject.put("message", message.asText());
				errorDetailArray.add(messageObject);
				errorDetails.set("errordetail", errorDetailArray);
				result.setMessage(errorDetails);
				if(bpRequestGeneralDataDto.getStatusId()<=1) {
					bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), draftStatusId);
					}
					else {
						bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), bpRequestGeneralDataDto.getStatusId());
					}
				return result;
			}

			else if (errorDetailsNode.isArray() && errorDetailsNode.size() != 0) {
				System.out.println("inside non empty");
//			JsonNode rootNode = objectMapper.readTree(response.getBody());
				JsonNode msgerrorDetailsNode = rootNode.path("error").path("innererror").path("errordetails");
				result.setMessage(msgerrorDetailsNode);
				if(bpRequestGeneralDataDto.getStatusId()<=1) {
					bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), draftStatusId);
					}
					else {
						bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), bpRequestGeneralDataDto.getStatusId());
					}
				return result;
			}
		}
		String crNumber = object.getAsJsonObject("GeneralDataSet").getAsJsonObject("GeneralData").get("Vendor")
				.getAsString();
		System.out.println("************************************************************************************"
				+ crNumber + "@@@@@" + status);
		int completedStatusId = 3;
		result.setCrNumber(crNumber);
		try {
			bpVendorDetailsRepository.updateVendorNo(bpCreationFromWorkflowRequest.getRequestId(), crNumber);
 
			}
			catch(Exception e){
				throw new RuntimeException("Failed to update status", e);
			}
			if (!bpCreationFromWorkflowRequest.isValidate()) {
				try {
				bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), completedStatusId);
				}
				catch(Exception e){
					throw new RuntimeException("Failed to update status", e);
				}
 
			MailRequestDto mailRequestDto = new MailRequestDto();
			ServiceResponse responseMessage = new ServiceResponse<>();
			mailRequestDto.setEmailTo(HelperClass.isEmpty(bpCreationFromWorkflowRequest.getRequestorEmail()) == true
					? "Vaibhav.Anand@viatris.com"
					: bpCreationFromWorkflowRequest.getRequestorEmail());
			JsonNode ruleResponse = emailNotificationService.getDataFromEmailRules(successfulEmailNotificationCode);
			if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
				String subject = "";
				if (profile.toUpperCase(Locale.ROOT).equalsIgnoreCase("PROD")) {
					subject = ruleResponse.get("data").get("result").get(0).get("VM_EMAIL_NOTIFICATION_CODE_ACTION")
							.get(0).get("VM_EMAIL_SUBJECT").asText();
				} else {
					subject = profile.toUpperCase(Locale.ROOT) + ": " + ruleResponse.get("data").get("result").get(0)
							.get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_SUBJECT").asText();
				}
				
				//Change Done :  Author - Dheeraj Kumar(Added Request Id) For Sprint - 3
				subject = subject.replace("<processType>", "Changed").replace("<Request Id>",
						bpCreationFromWorkflowRequest.getRequestId());
				
				String emailBody = ruleResponse.get("data").get("result").get(0)
						.get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_BODY").asText();
				
 				
				Object bPDetailsByRequestId = bpDetailService
						.getBPDetailsByRequestId(bpCreationFromWorkflowRequest.getRequestId());
				
 
				ObjectMapper objectMapperForUI = new ObjectMapper();
				String bPDetailsByRequestIdJson = objectMapperForUI.writeValueAsString(bPDetailsByRequestId);
				
 				
				JSONObject jsonObject = new JSONObject(bPDetailsByRequestIdJson);
		        //JSONObject data = jsonObject.getJSONObject("data");
		 
		        
		      
		        JSONArray companyCodeInfoArray = jsonObject.getJSONArray("bpCompanyCodeInfo");
		        List<String> companyCodes = new ArrayList<>();
		        for (int i = 0; i < companyCodeInfoArray.length(); i++) {
		            JSONObject companyCodeInfo = companyCodeInfoArray.getJSONObject(i);
		            companyCodes.add(companyCodeInfo.getString("companyCode"));
		        }
 		 
		     
		        JSONArray purchasingOrgDetailArray = jsonObject.getJSONArray("bpPurchasingOrgDetail");
		        List<String> purchasingOrgs = new ArrayList<>();
		        for (int i = 0; i < purchasingOrgDetailArray.length(); i++) {
		            JSONObject purchasingOrgDetail = purchasingOrgDetailArray.getJSONObject(i);
		            purchasingOrgs.add(purchasingOrgDetail.getString("purchasingOrg"));
		        }
		        
 		 
		       
		        String companyCodesStr = String.join(", ", companyCodes);
		        String purchasingOrgsStr = String.join(", ", purchasingOrgs);
		        
		       String companyCodeChar[] = companyCodesStr.split(",");
		       String purchasingOrgChar[] = purchasingOrgsStr.split(",");
		      
		         
		        
		        //********************************************
		         
		        
		        
		        Object auditLogInfo = auditLogService.getAuditLogInfoByRequestId(
		        		bpCreationFromWorkflowRequest.getRequestId());
 				 
				String auditLogInfoJson = null;
				auditLogInfoJson = objectMapper.writeValueAsString(auditLogInfo);
 				
				
				JSONArray dataList = new JSONArray(auditLogInfoJson);

				List<String> pathList = new ArrayList<>();

				for (int i = 0; i < dataList.length(); i++) {
					JSONObject dataObject = dataList.getJSONObject(i);
					String path = dataObject.getString("path");

					pathList.add(path);
				}

				List<Integer> companyCodeList = new ArrayList<>();
				List<Integer> purchasingOrgList = new ArrayList<>();

//				Pattern pattern = Pattern.compile("\\.(\\d+)\\.");
				 Pattern companycodepattern = Pattern.compile("bpCompanyCodeInfo\\.(\\d+)\\.");
				 Pattern purOrgpattern = Pattern.compile("bpPurchasingOrgDetail\\.(\\d+)\\.");

				for (String path : pathList) {
					Matcher matcher = companycodepattern.matcher(path);
					while (matcher.find()) {
						int index = Integer.parseInt(matcher.group(1));
						if (path.contains("bpCompanyCodeInfo")) {
							companyCodeList.add(index);

						}  else {
							System.out.println("Skipping path " );
						}
					}
				}
				
				
				for (String path : pathList) {
					Matcher matcher = purOrgpattern.matcher(path);
					while (matcher.find()) {
						int index = Integer.parseInt(matcher.group(1));
						if (path.contains("bpPurchasingOrgDetail")) {
							purchasingOrgList.add(index);

						}  else {
							System.out.println("Skipping path " );
						}
					}
				}



 

				List<String> companyCoderesult = new ArrayList<>();
				Set<Integer> companyCodeSet = new HashSet<>();

				for (int index : companyCodeList) {
					companyCodeSet.add(index);
				}

				for (int index : companyCodeSet) {
					companyCoderesult.add(companyCodeChar[index]);
				}

				String companyCodeConcatResult = String.join(",", companyCoderesult);
				
 
				List<String> purchaseOrgResult = new ArrayList<>();
				Set<Integer> purchaseOrgSet = new HashSet<>();

				for (int index : purchasingOrgList) {
					purchaseOrgSet.add(index);
				}

				for (int index : purchaseOrgSet) {
					purchaseOrgResult.add(purchasingOrgChar[index]);
				}

				String purchaseOrgConcatResult = String.join(",", purchaseOrgResult);

 			 
			
			
				String countryName="";
				if(bpCreationFromWorkflowRequest.getCountryName() != null&&!bpCreationFromWorkflowRequest.getCountryName().equalsIgnoreCase("null")) {
					countryName=bpCreationFromWorkflowRequest.getCountryName();
				}
				else {
					countryName="";
				}
				
				emailBody = emailBody.replace("<processType>", "change").replace("<crNumber>", crNumber)
						.replace("<Business Partner name>", bpCreationFromWorkflowRequest.getBusinessPartnerName() != null ? bpCreationFromWorkflowRequest.getBusinessPartnerName() : "")
						.replace("<countryName>", countryName)
						.replace("<companyCode>", companyCodeConcatResult != null ? companyCodeConcatResult : "")
						.replace("<purchasingOrg>", purchaseOrgConcatResult != null ? purchaseOrgConcatResult : "")
						.replace("|", "<br>").replace("\'", "");

				mailRequestDto.setSubject(subject);
				mailRequestDto.setBodyMessage(emailBody);
				emailNotificationService.sendMailThroughCPI(mailRequestDto);
//	            responseMessage=mailService.sendMail(mailRequestDto);
			} else {
				responseMessage.setMessage("Sending Mail Failed!!");
				responseMessage.setStatus(AppConstants.FAIL_MESSAGE_MAIL);
				responseMessage.setError(null);
				log.error("No Response received from the rules");
			}
		}
		result.setMessage(objectMapper.readTree("{\"key\": \"Validated Successfully!!\"}").get("key"));
		return result;
	}

	public BusinessPartnerResponse changeVendorInfo1(BPCreationFromWorkflowRequest bpCreationFromWorkflowRequest)
			throws UnirestException, ClientProtocolException, IOException {
		BPRequestGeneralDataDto bpRequestGeneralDataDto = bpDetailService
				.getBPDetailsByRequestId(bpCreationFromWorkflowRequest.getRequestId());
		CPIVendorDetailsDto detailsDto = convertChange1(bpRequestGeneralDataDto,
				bpCreationFromWorkflowRequest.isValidate());

		String accessToken, url = null;
		try {
			String destDetails = destinationUtil.readMdgDestination("mdg-vm-cpi", null, null);

			org.json.JSONObject resObj = new org.json.JSONObject(destDetails);
			log.info("Json object from destination :" + resObj);
			log.info("Client id: " + resObj.optJSONObject("destinationConfiguration").optString("clientId"));
			log.info("clientSecret : " + resObj.optJSONObject("destinationConfiguration").optString("clientSecret"));
			log.info("tokenServiceURL: "
					+ resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));
			log.info("url: " + resObj.optJSONObject("destinationConfiguration").optString("URL"));
			accessToken = getAccessToken(resObj.optJSONObject("destinationConfiguration").optString("clientId"),
					resObj.optJSONObject("destinationConfiguration").optString("clientSecret"),
					resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));

//	            url=resObj.optJSONObject("destinationConfiguration").optString("URL")+"/http/Vendor_Change";
			url = resObj.optJSONObject("destinationConfiguration").optString("URL")
					+ "/http/Viatris/CP_HanaDb/BusinessPatrner/Change";

//	            https://viatris-its-dev-092tl30u.it-cpi013-rt.cfapps.us21.hana.ondemand.com/http/Vendor_Create
//	            https://viatris-its-dev-092tl30u.it-cpi013-rt.cfapps.us21.hana.ondemand.com

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		BusinessPartnerResponse result = new BusinessPartnerResponse();
		String body = new ObjectMapper().writeValueAsString(detailsDto);
		System.out.println("**%%" + body);
		String urlValue = "https://inccpidev.it-cpi001-rt.cfapps.eu10.hana.ondemand.com/http/Vendor_CreateSap";
		System.out.println("*******");
//        System.out.println(accessToken());
		System.out.println(accessToken);

		System.out.println("*******");
		HttpResponse<String> response = Unirest.post(url).header("authorization", "Bearer " + accessToken)
				.header("Content-Type", "application/json").header("Accept", "application/json").body(body).asString();
		int status = response.getStatus();
		System.out
				.println(status + "************************************************************************************"
						+ response + "  " + response.getBody());
		JsonObject object = new JsonParser().parse(response.getBody()).getAsJsonObject();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(response.getBody());
		JsonNode errorDetailsNode = rootNode.path("error").path("innererror").path("errordetails").path("errordetail");

		System.out.println(errorDetailsNode);
		System.out.println(errorDetailsNode.asText());
		if (rootNode.has("error")) {
			if ((!errorDetailsNode.isArray() || errorDetailsNode.size() == 0)) {
				System.out.println("inside empty");
				JsonNode message = rootNode.path("error").path("message").path("$");
				ObjectMapper mapper = new ObjectMapper();
				ObjectNode errorDetails = mapper.createObjectNode();
				ArrayNode errorDetailArray = JsonNodeFactory.instance.arrayNode();
				ObjectNode messageObject = mapper.createObjectNode();
				messageObject.put("message", message.asText());
				errorDetailArray.add(messageObject);
				errorDetails.set("errordetail", errorDetailArray);
				result.setMessage(errorDetails);
				bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), draftStatusId);
				return result;
			}

			else if (errorDetailsNode.isArray() && errorDetailsNode.size() != 0) {
				System.out.println("inside non empty");
//			JsonNode rootNode = objectMapper.readTree(response.getBody());
				JsonNode msgerrorDetailsNode = rootNode.path("error").path("innererror").path("errordetails");
				result.setMessage(msgerrorDetailsNode);
				bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), draftStatusId);
				return result;
			}
		}
		String crNumber = object.getAsJsonObject("GeneralDataSet").getAsJsonObject("GeneralData").get("Vendor")
				.getAsString();
		System.out.println("************************************************************************************"
				+ crNumber + "@@@@@" + status);
		int completedStatusId = 3;
		result.setCrNumber(crNumber);
		bpVendorDetailsRepository.updateVendorNo(bpCreationFromWorkflowRequest.getRequestId(), crNumber);
		if (!bpCreationFromWorkflowRequest.isValidate()) {
			bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), completedStatusId);
			MailRequestDto mailRequestDto = new MailRequestDto();
			ServiceResponse responseMessage = new ServiceResponse<>();
			mailRequestDto.setEmailTo(HelperClass.isEmpty(bpRequestGeneralDataDto.getRequestorEmail()) == true
					? "Vaibhav.Anand@viatris.com"
					: bpCreationFromWorkflowRequest.getRequestorEmail());
			JsonNode ruleResponse = emailNotificationService.getDataFromEmailRules(successfulEmailNotificationCode);
			if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
				String subject = profile.toUpperCase(Locale.ROOT) + ": " + ruleResponse.get("data").get("result").get(0)
						.get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_SUBJECT").asText();
				subject = subject.replace("<processType>", "Changed");
				String emailBody = ruleResponse.get("data").get("result").get(0)
						.get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_BODY").asText();
				emailBody = emailBody.replace("<processType>", "change").replace("<crNumber>", crNumber);
				mailRequestDto.setSubject(subject);
				mailRequestDto.setBodyMessage(emailBody);
				emailNotificationService.sendMailThroughCPI(mailRequestDto);
//	            responseMessage=mailService.sendMail(mailRequestDto);
			} else {
				responseMessage.setMessage("Sending Mail Failed!!");
				responseMessage.setStatus(AppConstants.FAIL_MESSAGE_MAIL);
				responseMessage.setError(null);
				log.error("No Response received from the rules");
			}
		}
		result.setMessage(objectMapper.readTree("{\"key\": \"Validated Successfully!!\"}").get("key"));
		return result;
	}

	public CPIVendorDetailsDto convertChange(BPRequestGeneralDataDto bpRequestGeneralDataDto, boolean validate) {

//		String responseStr = testService.getVendorDataFromOdata(bpRequestGeneralDataDto.getRequestId());
//		if (responseStr.contains("200")) {
//			responseStr = responseStr.replace("<200 OK OK,", "").replace(",[]>", "");
//			System.out.println("****");
//			System.out.println(responseStr);
//			System.out.println("****");
//			org.json.JSONObject jsonObject = new org.json.JSONObject(responseStr);
//			org.json.JSONObject myResponse = jsonObject.getJSONObject("d");
//			org.json.JSONObject result = (org.json.JSONObject) ((JSONArray) myResponse.get("results")).get(0);
//			org.json.JSONObject toItem = (org.json.JSONObject) ((JSONArray) result.getJSONObject("ToItem")
//					.get("results")).get(0);
//		}

		CPIVendorDetailsDto object = new CPIVendorDetailsDto();
		if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0))) {
			object.setType("");
			if (validate) {
				object.setValidation("X");
			} else {
				object.setValidation("");
			}
			object.setChangeIndObject("U");
			object.setVendor(bpRequestGeneralDataDto.getBupaNo());
			object.setSystemId(bpRequestGeneralDataDto.getSystemId());
			object.setTrainstation("");
			object.setLocationno1(bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo1() == null ? "0000000"
					: bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo1());
			object.setLocationno2(bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo2() == null ? "00000"
					: bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo2());
			object.setAuthorization(bpRequestGeneralDataDto.getBpControlData().get(0).getAuthorization() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getAuthorization());
			object.setIndustry(bpRequestGeneralDataDto.getBpControlData().get(0).getIndustry() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getIndustry());
			object.setCheckdigit(bpRequestGeneralDataDto.getBpControlData().get(0).getCheckDigit() == null ? "0"
					: bpRequestGeneralDataDto.getBpControlData().get(0).getCheckDigit());
			object.setDMEIndicator(
					bpRequestGeneralDataDto.getDmeIndicator() == null ? "" : bpRequestGeneralDataDto.getDmeIndicator());
			object.setInstructionkey(bpRequestGeneralDataDto.getInstructionKey() == null ? ""
					: bpRequestGeneralDataDto.getInstructionKey());
			object.setISRNumber(
					bpRequestGeneralDataDto.getIsrNumber() == null ? "" : bpRequestGeneralDataDto.getIsrNumber());
			object.setCorporateGroup(bpRequestGeneralDataDto.getBpControlData().get(0).getCorporateGroup() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getCorporateGroup());
			object.setAccountgroup(bpRequestGeneralDataDto.getBupaAccountGrp() == null ? ""
					: bpRequestGeneralDataDto.getBupaAccountGrp());
//			    object.setCustomer(bpRequestGeneralDataDto.getBpControlData().get(0).getCustomer() == null ? "" : bpRequestGeneralDataDto.getBpControlData().get(0).getCustomer());
			object.setCustomer("");
			object.setAlternatpayee(bpRequestGeneralDataDto.getAlternativePayee() == null ? ""
					: bpRequestGeneralDataDto.getAlternativePayee());
			object.setDeletionflag(false);
			object.setPostingBlock(false);
			object.setPurchblock(false);
			object.setTaxNumber1(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo1() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo1());
			object.setTaxNumber2(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo2() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo2());
			object.setEqualizatntax("");
			object.setLiableforVAT(false);
			object.setPayeeindoc(false);
			object.setTradingPartner(bpRequestGeneralDataDto.getBpControlData().get(0).getTradingPartner() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTradingPartner());
			object.setFiscaladdress(bpRequestGeneralDataDto.getBpControlData().get(0).getFiscalAddress() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getFiscalAddress());
			object.setVATRegNo(bpRequestGeneralDataDto.getBpControlData().get(0).getVatRegNo() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getVatRegNo());
			object.setNaturalperson(
					bpRequestGeneralDataDto.getNaturalPer() == null ? "" : bpRequestGeneralDataDto.getNaturalPer());
			object.setBlockfunction("");
			object.setAddress("");
			object.setPlaceofbirth(bpRequestGeneralDataDto.getBpControlData().get(0).getPlaceOfBirth() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getPlaceOfBirth());
			String output = null;
			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getDob())) {
//				SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");

				try {
					Date date = inputFormat.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getDob());

					output = outputFormat.format(date);

					System.out.println("Input: " + bpRequestGeneralDataDto.getBpControlData().get(0).getDob());
					System.out.println("Output: " + output);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			object.setBirthdate(output == null ? "" : output);
			object.setSex(bpRequestGeneralDataDto.getBpControlData().get(0).getSex() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getSex());
			object.setCredinfono(bpRequestGeneralDataDto.getCreditInformationNumber() == null ? ""
					: bpRequestGeneralDataDto.getCreditInformationNumber());
//			    object.setLastextreview(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview() == null ? null : bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview());
//			    object.setLastextreview(null);
			long lastExtReview = 0;
			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview())) {
				try {
					Date lastExtReviewDate = sdf
							.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview());
					lastExtReview = lastExtReviewDate.getTime();
					System.out.println("Timestamp in milliseconds: " + lastExtReview);
				} catch (ParseException e) {
					System.err.println("Error parsing the date string: " + e.getMessage());
				}
				object.setLastextreview("/Date(" + lastExtReview + ")/");
			} else {
				object.setLastextreview(null);
			}
			object.setActualQMsys(bpRequestGeneralDataDto.getBpControlData().get(0).getActualQnSys() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getActualQnSys());
			object.setRefacctgroup("");
			object.setPlant("");
			object.setVSRrelevant(true);
			object.setPlantrelevant(true);
			object.setFactorycalend("");
			object.setSCAC(bpRequestGeneralDataDto.getBpControlData().get(0).getScac() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getScac());
			object.setCarfreightgrp(bpRequestGeneralDataDto.getBpControlData().get(0).getCarFreughtGrp() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getCarFreughtGrp());
			object.setServAgntProcGrp(
					bpRequestGeneralDataDto.getBpControlData().get(0).getServAgntProcGrp() == null ? ""
							: bpRequestGeneralDataDto.getBpControlData().get(0).getServAgntProcGrp());
			object.setTaxtype(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxType() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxType());
			object.setTaxnumbertype(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNoType() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNoType());
			object.setSocialIns(false);
			object.setSocInsCode(bpRequestGeneralDataDto.getBpControlData().get(0).getSocInsCode() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getSocInsCode());
			object.setTaxNumber3(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo3() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo3());
			object.setTaxNumber4(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo4() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo4());
			object.setTaxsplit(false);
			object.setTaxbase(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxBase() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxBase());
			object.setProfession(bpRequestGeneralDataDto.getBpControlData().get(0).getProfession() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getProfession());
			object.setStatgrpagent("");
			object.setExternalmanuf(bpRequestGeneralDataDto.getBpControlData().get(0).getExternalManuf() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getExternalManuf());
			object.setDeletionblock(false);
			object.setRepsName(bpRequestGeneralDataDto.getBpControlData().get(0).getRepsName() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getRepsName());
			object.setTypeofBusiness(bpRequestGeneralDataDto.getBpControlData().get(0).getTypeOfBusiness() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTypeOfBusiness());
			object.setTypeofIndustry(bpRequestGeneralDataDto.getBpControlData().get(0).getTypeOfIndustr() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTypeOfIndustr());
			long qmSystemTo = 0;
			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getQmSystemTo())) {
				try {
					Date qmSystemToDate = sdf.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getQmSystemTo());
					qmSystemTo = qmSystemToDate.getTime();
					System.out.println("Timestamp in milliseconds: " + qmSystemTo);
				} catch (ParseException e) {
					System.err.println("Error parsing the date string: " + e.getMessage());
				}
				object.setQMsystemto("/Date(" + qmSystemTo + ")/");
			} else {
				object.setQMsystemto(null);
			}
			object.setPODrelevant(bpRequestGeneralDataDto.getBpControlData().get(0).getPodRelevant() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getPodRelevant());
			object.setTaxoffice(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxOffice() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxOffice());
			object.setTaxNumber(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNumber() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNumber());
			object.setTaxNumber5(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo5() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo5());
			object.setPurposeCompleteFlag("");
			object.setAddressVersion("");
			object.setFrom("/Date(253402214400000)/");
			object.setTo("/Date(253402214400000)/");
			object.setTitle(bpRequestGeneralDataDto.getTitle());
			object.setName(bpRequestGeneralDataDto.getName1() == null ? "" : bpRequestGeneralDataDto.getName1());
			object.setName2(bpRequestGeneralDataDto.getName2() == null ? "" : bpRequestGeneralDataDto.getName2());
			object.setName3(bpRequestGeneralDataDto.getName3() == null ? "" : bpRequestGeneralDataDto.getName3());
			object.setName4(bpRequestGeneralDataDto.getName4() == null ? "" : bpRequestGeneralDataDto.getName4());
			object.setConvname("");
			object.setCo(bpRequestGeneralDataDto.getBpAddressInfo().getCo() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getCo());
			object.setCity(bpRequestGeneralDataDto.getBpAddressInfo().getCity() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getCity());
			object.setDistrict(bpRequestGeneralDataDto.getBpAddressInfo().getDistrict() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getDistrict());
			object.setCityNo("");
			object.setDistrictNo("");
			object.setCheckStatus("");
			object.setRegStrGrp(bpRequestGeneralDataDto.getBpAddressInfo().getRegStructGrp() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getRegStructGrp());
			object.setPostalCode(bpRequestGeneralDataDto.getBpAddressInfo().getPostalCode() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getPostalCode());
			object.setPOBoxPostCde(
					bpRequestGeneralDataDto.getPoPostalCode() == null ? "" : bpRequestGeneralDataDto.getPoPostalCode());
			object.setCompanyPostCd(bpRequestGeneralDataDto.getPoCompanyPostalCode() == null ? ""
					: bpRequestGeneralDataDto.getPoCompanyPostalCode());
			object.setPostalCodeExt("");
			object.setPostalCodeExt2("");
			object.setPostalCodeExt3("");
			object.setPOBox(bpRequestGeneralDataDto.getPoBox() == null ? "" : bpRequestGeneralDataDto.getPoBox());
			object.setPOBoxwono(false);
			object.setPOBoxCity("");
			object.setPOCitNo("");
			object.setPORegion("");
			object.setPOboxcountry("");
			object.setISOcode("");
			object.setDeliveryDist("");
			object.setTransportzone(bpRequestGeneralDataDto.getBpAddressInfo().getTransportZone() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getTransportZone());
			object.setStreet(bpRequestGeneralDataDto.getBpAddressInfo().getStreet() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStreet());
			object.setStreetCode("");
			object.setStreetAbbrev("");
			object.setHouseNumber(bpRequestGeneralDataDto.getBpAddressInfo().getHouseNo() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getHouseNo());
			object.setSupplement(bpRequestGeneralDataDto.getBpAddressInfo().getSuppl() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getSuppl());
			object.setNumberRange("");
			object.setStreet2(bpRequestGeneralDataDto.getBpAddressInfo().getStreet2() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStreet2());
			object.setStreet3(bpRequestGeneralDataDto.getBpAddressInfo().getStreet3() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStreet3());
			object.setStreet4(bpRequestGeneralDataDto.getBpAddressInfo().getStreet4() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStreet4());
			object.setStreet5(bpRequestGeneralDataDto.getBpAddressInfo().getStreet5() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStreet5());
			object.setBuildingCode(bpRequestGeneralDataDto.getBpAddressInfo().getBuildingCode() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getBuildingCode());
			object.setFloor(bpRequestGeneralDataDto.getBpAddressInfo().getFloor() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getFloor());
			object.setRoomNumber(bpRequestGeneralDataDto.getBpAddressInfo().getRoom() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getRoom());
			object.setCountry(bpRequestGeneralDataDto.getBpAddressInfo().getCountry() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getCountry());
			object.setCountryISO(bpRequestGeneralDataDto.getBpAddressInfo().getCountry() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getCountry());
			object.setLanguage(bpRequestGeneralDataDto.getBpAddressInfo().getLanguage() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getLanguage());
			object.setLangISO(bpRequestGeneralDataDto.getBpAddressInfo().getLanguage() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getLanguage());
			object.setRegion(bpRequestGeneralDataDto.getBpAddressInfo().getRegion() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getRegion());
			object.setSearchTerm1(
					bpRequestGeneralDataDto.getSearchTerm1() == null ? "" : bpRequestGeneralDataDto.getSearchTerm1());
			object.setSearchTerm2(
					bpRequestGeneralDataDto.getSearchTerm2() == null ? "" : bpRequestGeneralDataDto.getSearchTerm2());
			object.setDataline("");
			object.setTelebox("");
			object.setTimezone(bpRequestGeneralDataDto.getBpAddressInfo().getTimeZone() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getTimeZone());
			object.setTaxJurisdictn(bpRequestGeneralDataDto.getBpAddressInfo().getTaxJurisdiction() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getTaxJurisdiction());
			object.setAddressID("");
			object.setCreationlang("EN");
			object.setLangCRISO("EN");
			object.setCommMethod(bpRequestGeneralDataDto.getBpAddressInfo().getStandardCommMethod() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStandardCommMethod());
			object.setAddressgroup("");
			object.setDifferentCity(bpRequestGeneralDataDto.getBpAddressInfo().getDifferentCity() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getDifferentCity());
			object.setCityCode("");
			object.setUndeliverable(bpRequestGeneralDataDto.getBpAddressInfo().getUndeliverable() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getUndeliverable());
			object.setUndeliverable1("");
			object.setPOBoxLobby("");
			object.setDelvryServType("");
			object.setDeliveryServiceNo("");
			object.setCountycode("");
			object.setCounty("");
			object.setTownshipcode("");
			object.setTownship("");
			object.setPAN("");

			// Setting To Address Data

			object.setToAddressData(new ArrayList<>());

			// Setting To Company Data

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {
				object.setToCompanyData(bpRequestGeneralDataDto.getBpCompanyCodeInfo().stream()
						.map(companyCodeDto -> convertChange(companyCodeDto)).collect(Collectors.toList()));
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {
				object.setToCompanyData(new ArrayList<>());
			}

			// Setting To Purchase Org Data

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpPurchasingOrgDetail())) {
				object.setToPurchaseOrgData(bpRequestGeneralDataDto.getBpPurchasingOrgDetail().stream()
						.map(purchasingOrgDto -> convertChange(purchasingOrgDto)).collect(Collectors.toList()));
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpPurchasingOrgDetail())) {
				object.setToPurchaseOrgData(new ArrayList<>());
			}

			// Setting To Classification

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpVendorClassificationEntity())) {
				object.setToClassification(bpRequestGeneralDataDto.getBpVendorClassificationEntity().stream()
						.map(vendorClassificationEntityDto -> changeConvert(vendorClassificationEntityDto))
						.collect(Collectors.toList()));
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpVendorClassificationEntity())) {
				object.setToClassification(new ArrayList<>());
			}

			// Setting To Email

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpEmail())) {
				object.setToEmail(bpRequestGeneralDataDto.getBpCommunication().getBpEmail().stream()
						.map(emailDto -> changeConvert(emailDto)).collect(Collectors.toList()));
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpEmail())) {
				object.setToEmail(new ArrayList<>());
			}

			// Setting To Phone

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpTelephone())) {
				object.setToPhone(bpRequestGeneralDataDto.getBpCommunication().getBpTelephone().stream()
						.map(telephoneDto -> changeConvert(telephoneDto)).collect(Collectors.toList()));
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpTelephone())) {
				object.setToPhone(new ArrayList<>());
			}
			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpMobilePhone())) {
				object.getToPhone().addAll(bpRequestGeneralDataDto.getBpCommunication().getBpMobilePhone().stream()
						.map(mobileDto -> changeConvert(mobileDto)).collect(Collectors.toList()));
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpMobilePhone())) {
				object.getToPhone().addAll(new ArrayList<>());
			}

			// Setting To Fax

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpFaxInfo())) {
				object.setToFax(bpRequestGeneralDataDto.getBpCommunication().getBpFaxInfo().stream()
						.map(faxDto -> changeConvert(faxDto)).collect(Collectors.toList()));
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpFaxInfo())) {
				object.setToFax(new ArrayList<>());
			}
			// Setting To Bank

//			    if ((!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation()))&&(!bpRequestGeneralDataDto.getSkipBankValidation())) {
//			        object.setToBank(bpRequestGeneralDataDto.getBpBankInformation().stream()
//			                .map(bankDto -> changeConvert(bankDto)).collect(Collectors.toList()));
//			    }
			if ((!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation()))
					&& (!bpRequestGeneralDataDto.getSkipBankValidation())) {
				List<CPIBankDto> cpiBankDto = new ArrayList<>();
				for (BPBankInformationDto bankInformationDto : bpRequestGeneralDataDto.getBpBankInformation()) {
					if (bankInformationDto.getIsNew()) {
						cpiBankDto.add(changeConvertNew(bankInformationDto));
					} else {
						cpiBankDto.add(changeConvert(bankInformationDto));
					}
				}
				object.setToBank(cpiBankDto);
			} else if ((!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation()))
					&& (bpRequestGeneralDataDto.getSkipBankValidation())) {
				object.setToBank(new ArrayList<>());
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation())) {
				object.setToBank(new ArrayList<>());
			}
			// Setting To Contact Info

//			    if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpContactInformation())) {
//			        object.setToContact(bpRequestGeneralDataDto.getBpContactInformation().stream()
//			                .map(contactDto -> convertChange(contactDto,bpRequestGeneralDataDto.getBupaNo())).collect(Collectors.toList()));
//			    }
//				else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpContactInformation())) {
//					object.setToContact(new ArrayList<>());
//				}

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpContactInformation())) {
				List<CPIContactDto> cpiContactDto = new ArrayList<>();
				for (BPContactInformationDto contactInformationDto : bpRequestGeneralDataDto
						.getBpContactInformation()) {
					if (contactInformationDto.getIsNew()) {
						cpiContactDto.add(convertChangeNew(contactInformationDto, bpRequestGeneralDataDto.getBupaNo()));
					} else {
						cpiContactDto.add(convertChange(contactInformationDto, bpRequestGeneralDataDto.getBupaNo()));
					}
				}
				object.setToContact(cpiContactDto);
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpContactInformation())) {
				object.setToContact(new ArrayList<>());
			}

			// Setting To Return Messages

//			    object.setToReturnMessages(new ArrayList<>());
//			    object.setToTaxData(new ArrayList<>());
			return object;
		} else {
			return object;
		}
	}

	public CPIVendorDetailsDto convertChange1(BPRequestGeneralDataDto bpRequestGeneralDataDto, boolean validate) {

		String responseStr = testService.getVendorDataFromOdata(bpRequestGeneralDataDto.getBupaNo());
		responseStr = responseStr.replace("<200 OK OK,", "").replace(",[]>", "");
		System.out.println("****");
		System.out.println(responseStr);
		System.out.println("****");
		org.json.JSONObject jsonObject = new org.json.JSONObject(responseStr);
		org.json.JSONObject myResponse = jsonObject.getJSONObject("d");
		org.json.JSONObject result = (org.json.JSONObject) ((JSONArray) myResponse.get("results")).get(0);
		JSONArray toBank = ((JSONArray) result.getJSONObject("ToBank").get("results"));
		JSONArray toCompanyData = ((JSONArray) result.getJSONObject("ToCompanyData").get("results"));
		JSONArray toEmailData = ((JSONArray) result.getJSONObject("ToEmail").get("results"));
		JSONArray toPhoneData = ((JSONArray) result.getJSONObject("ToPhone").get("results"));
		JSONArray toFaxData = ((JSONArray) result.getJSONObject("ToFax").get("results"));
		JSONArray toContactData = ((JSONArray) result.getJSONObject("ToContact").get("results"));
		int toBankSize = toBank.length();
		int toCompanyDataSize = toCompanyData.length();
		int toEmailDataSize = toEmailData.length();
		int toPhoneDataSize = toPhoneData.length();
		int toFaxDataSize = toFaxData.length();
		int toContactDataSize = toContactData.length();

		CPIVendorDetailsDto object = new CPIVendorDetailsDto();
		if (bpRequestGeneralDataDto.getSubProcessType().equalsIgnoreCase("Generic")) {
			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0))) {
				object.setType("");
				object.setRequestId(
						bpRequestGeneralDataDto.getRequestId() == null ? "" : bpRequestGeneralDataDto.getRequestId());
				if (validate) {
					object.setValidation("X");
				} else {
					object.setValidation("");
				}
				object.setChangeIndObject("U");
				object.setVendor(bpRequestGeneralDataDto.getBupaNo());
				object.setSystemId(bpRequestGeneralDataDto.getSystemId());
				object.setTrainstation("");
				object.setLocationno1(
						bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo1() == null ? "0000000"
								: bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo1());
				object.setLocationno2(
						bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo2() == null ? "00000"
								: bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo2());
				object.setAuthorization(
						bpRequestGeneralDataDto.getBpControlData().get(0).getAuthorization() == null ? ""
								: bpRequestGeneralDataDto.getBpControlData().get(0).getAuthorization());
				object.setIndustry(bpRequestGeneralDataDto.getBpControlData().get(0).getIndustry() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getIndustry());
				object.setCheckdigit(bpRequestGeneralDataDto.getBpControlData().get(0).getCheckDigit() == null ? "0"
						: bpRequestGeneralDataDto.getBpControlData().get(0).getCheckDigit());
				object.setDMEIndicator(bpRequestGeneralDataDto.getDmeIndicator() == null ? ""
						: bpRequestGeneralDataDto.getDmeIndicator());
				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getInstructionKey())
						&& !bpRequestGeneralDataDto.getInstructionKey().contains("_")) {
					object.setInstructionkey(bpRequestGeneralDataDto.getInstructionKey());
				} else if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getInstructionKey())
						&& bpRequestGeneralDataDto.getInstructionKey().contains("_")) {
					String[] stringarray = bpRequestGeneralDataDto.getInstructionKey().split("_");
					object.setInstructionkey(stringarray[0]);
				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getInstructionKey())) {
					object.setInstructionkey("");
				}
				object.setISRNumber(
						bpRequestGeneralDataDto.getIsrNumber() == null ? "" : bpRequestGeneralDataDto.getIsrNumber());
				object.setCorporateGroup(
						bpRequestGeneralDataDto.getBpControlData().get(0).getCorporateGroup() == null ? ""
								: bpRequestGeneralDataDto.getBpControlData().get(0).getCorporateGroup());
				object.setAccountgroup(bpRequestGeneralDataDto.getBupaAccountGrp() == null ? ""
						: bpRequestGeneralDataDto.getBupaAccountGrp());
//			    object.setCustomer(bpRequestGeneralDataDto.getBpControlData().get(0).getCustomer() == null ? "" : bpRequestGeneralDataDto.getBpControlData().get(0).getCustomer());
				object.setCustomer("");
				object.setAlternatpayee(bpRequestGeneralDataDto.getAlternativePayee() == null ? ""
						: bpRequestGeneralDataDto.getAlternativePayee());
				object.setDeletionflag(false);
				object.setPostingBlock(false);
				object.setPurchblock(false);
				object.setTaxNumber1(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo1() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo1());
				object.setTaxNumber2(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo2() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo2());
				object.setEqualizatntax("");
				object.setLiableforVAT(false);
				object.setPayeeindoc(false);
				object.setTradingPartner(
						bpRequestGeneralDataDto.getBpControlData().get(0).getTradingPartner() == null ? ""
								: bpRequestGeneralDataDto.getBpControlData().get(0).getTradingPartner());
				object.setFiscaladdress(
						bpRequestGeneralDataDto.getBpControlData().get(0).getFiscalAddress() == null ? ""
								: bpRequestGeneralDataDto.getBpControlData().get(0).getFiscalAddress());
				object.setVATRegNo(bpRequestGeneralDataDto.getBpControlData().get(0).getVatRegNo() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getVatRegNo());
				object.setNaturalperson(
						bpRequestGeneralDataDto.getNaturalPer() == null ? "" : bpRequestGeneralDataDto.getNaturalPer());
				object.setBlockfunction("");
				object.setAddress("");
				object.setPlaceofbirth(bpRequestGeneralDataDto.getBpControlData().get(0).getPlaceOfBirth() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getPlaceOfBirth());
				String output = null;
				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getDob())) {
//				SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");

					try {
						Date date = inputFormat.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getDob());

						output = outputFormat.format(date);

						System.out.println("Input: " + bpRequestGeneralDataDto.getBpControlData().get(0).getDob());
						System.out.println("Output: " + output);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				object.setBirthdate(output == null ? "" : output);
				object.setSex(bpRequestGeneralDataDto.getBpControlData().get(0).getSex() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getSex());
				object.setCredinfono(bpRequestGeneralDataDto.getCreditInformationNumber() == null ? ""
						: bpRequestGeneralDataDto.getCreditInformationNumber());
//			    object.setLastextreview(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview() == null ? null : bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview());
//			    object.setLastextreview(null);
				long lastExtReview = 0;
				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview())) {
					try {
						Date lastExtReviewDate = sdf
								.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview());
						lastExtReview = lastExtReviewDate.getTime();
						System.out.println("Timestamp in milliseconds: " + lastExtReview);
					} catch (ParseException e) {
						System.err.println("Error parsing the date string: " + e.getMessage());
					}
					object.setLastextreview("/Date(" + lastExtReview + ")/");
				} else {
					object.setLastextreview(null);
				}
				object.setActualQMsys(bpRequestGeneralDataDto.getBpControlData().get(0).getActualQnSys() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getActualQnSys());
				object.setRefacctgroup("");
				object.setPlant("");
				object.setVSRrelevant(true);
				object.setPlantrelevant(true);
				object.setFactorycalend("");
				object.setSCAC(bpRequestGeneralDataDto.getBpControlData().get(0).getScac() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getScac());
				object.setCarfreightgrp(
						bpRequestGeneralDataDto.getBpControlData().get(0).getCarFreughtGrp() == null ? ""
								: bpRequestGeneralDataDto.getBpControlData().get(0).getCarFreughtGrp());
				object.setServAgntProcGrp(
						bpRequestGeneralDataDto.getBpControlData().get(0).getServAgntProcGrp() == null ? ""
								: bpRequestGeneralDataDto.getBpControlData().get(0).getServAgntProcGrp());
				object.setTaxtype(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxType() == null ? "01"
						: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxType());
				object.setTaxnumbertype(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNoType() == null ? "01"
						: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNoType());
				object.setSocialIns(false);
				object.setSocInsCode(bpRequestGeneralDataDto.getBpControlData().get(0).getSocInsCode() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getSocInsCode());
				object.setTaxNumber3(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo3() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo3());
				object.setTaxNumber4(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo4() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo4());
				object.setTaxsplit(false);
				object.setTaxbase(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxBase() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxBase());
				object.setProfession(bpRequestGeneralDataDto.getBpControlData().get(0).getProfession() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getProfession());
				object.setStatgrpagent("");
				object.setExternalmanuf(
						bpRequestGeneralDataDto.getBpControlData().get(0).getExternalManuf() == null ? ""
								: bpRequestGeneralDataDto.getBpControlData().get(0).getExternalManuf());
				object.setDeletionblock(false);
				object.setRepsName(bpRequestGeneralDataDto.getBpControlData().get(0).getRepsName() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getRepsName());
				object.setTypeofBusiness(
						bpRequestGeneralDataDto.getBpControlData().get(0).getTypeOfBusiness() == null ? ""
								: bpRequestGeneralDataDto.getBpControlData().get(0).getTypeOfBusiness());
				object.setTypeofIndustry(
						bpRequestGeneralDataDto.getBpControlData().get(0).getTypeOfIndustr() == null ? ""
								: bpRequestGeneralDataDto.getBpControlData().get(0).getTypeOfIndustr());
				long qmSystemTo = 0;
				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getQmSystemTo())) {
					try {
						Date qmSystemToDate = sdf
								.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getQmSystemTo());
						qmSystemTo = qmSystemToDate.getTime();
						System.out.println("Timestamp in milliseconds: " + qmSystemTo);
					} catch (ParseException e) {
						System.err.println("Error parsing the date string: " + e.getMessage());
					}
					object.setQMsystemto("/Date(" + qmSystemTo + ")/");
				} else {
					object.setQMsystemto(null);
				}
				object.setPODrelevant(bpRequestGeneralDataDto.getBpControlData().get(0).getPodRelevant() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getPodRelevant());
				object.setTaxoffice(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxOffice() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxOffice());
				object.setTaxNumber(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNumber() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNumber());
				object.setTaxNumber5(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo5() == null ? ""
						: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo5());
				object.setPurposeCompleteFlag("");
				object.setAddressVersion("");
				object.setFrom("/Date(253402214400000)/");
				object.setTo("/Date(253402214400000)/");
				object.setTitle(bpRequestGeneralDataDto.getTitle());
				object.setName(bpRequestGeneralDataDto.getName1() == null ? "" : bpRequestGeneralDataDto.getName1());
				object.setName2(bpRequestGeneralDataDto.getName2() == null ? "" : bpRequestGeneralDataDto.getName2());
				object.setName3(bpRequestGeneralDataDto.getName3() == null ? "" : bpRequestGeneralDataDto.getName3());
				object.setName4(bpRequestGeneralDataDto.getName4() == null ? "" : bpRequestGeneralDataDto.getName4());
				object.setConvname("");
				object.setCo(bpRequestGeneralDataDto.getBpAddressInfo().getCo() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getCo());
				object.setCity(bpRequestGeneralDataDto.getBpAddressInfo().getCity() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getCity());
				object.setDistrict(bpRequestGeneralDataDto.getBpAddressInfo().getDistrict() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getDistrict());
				object.setCityNo("");
				object.setDistrictNo("");
				object.setCheckStatus("");
				object.setRegStrGrp(bpRequestGeneralDataDto.getBpAddressInfo().getRegStructGrp() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getRegStructGrp());
				object.setPostalCode(bpRequestGeneralDataDto.getBpAddressInfo().getPostalCode() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getPostalCode());
				object.setPOBoxPostCde(bpRequestGeneralDataDto.getPoPostalCode() == null ? ""
						: bpRequestGeneralDataDto.getPoPostalCode());
				object.setCompanyPostCd(bpRequestGeneralDataDto.getPoCompanyPostalCode() == null ? ""
						: bpRequestGeneralDataDto.getPoCompanyPostalCode());
				object.setPostalCodeExt("");
				object.setPostalCodeExt2("");
				object.setPostalCodeExt3("");
				object.setPOBox(bpRequestGeneralDataDto.getPoBox() == null ? "" : bpRequestGeneralDataDto.getPoBox());
				object.setPOBoxwono(false);
				object.setPOBoxCity("");
				object.setPOCitNo("");
				object.setPORegion("");
				object.setPOboxcountry("");
				object.setISOcode("");
				object.setDeliveryDist("");
				object.setTransportzone(bpRequestGeneralDataDto.getBpAddressInfo().getTransportZone() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getTransportZone());
				object.setStreet(bpRequestGeneralDataDto.getBpAddressInfo().getStreet() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getStreet());
				object.setStreetCode("");
				object.setStreetAbbrev("");
				object.setHouseNumber(bpRequestGeneralDataDto.getBpAddressInfo().getHouseNo() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getHouseNo());
				object.setSupplement(bpRequestGeneralDataDto.getBpAddressInfo().getSuppl() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getSuppl());
				object.setNumberRange("");
				object.setStreet2(bpRequestGeneralDataDto.getBpAddressInfo().getStreet2() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getStreet2());
				object.setStreet3(bpRequestGeneralDataDto.getBpAddressInfo().getStreet3() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getStreet3());
				object.setStreet4(bpRequestGeneralDataDto.getBpAddressInfo().getStreet4() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getStreet4());
				object.setStreet5(bpRequestGeneralDataDto.getBpAddressInfo().getStreet5() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getStreet5());
				object.setBuildingCode(bpRequestGeneralDataDto.getBpAddressInfo().getBuildingCode() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getBuildingCode());
				object.setFloor(bpRequestGeneralDataDto.getBpAddressInfo().getFloor() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getFloor());
				object.setRoomNumber(bpRequestGeneralDataDto.getBpAddressInfo().getRoom() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getRoom());
				object.setCountry(bpRequestGeneralDataDto.getBpAddressInfo().getCountry() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getCountry());
				object.setCountryISO(bpRequestGeneralDataDto.getBpAddressInfo().getCountry() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getCountry());
				object.setLanguage(bpRequestGeneralDataDto.getBpAddressInfo().getLanguage() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getLanguage());
				object.setLangISO(bpRequestGeneralDataDto.getBpAddressInfo().getLanguage() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getLanguage());
				object.setRegion(bpRequestGeneralDataDto.getBpAddressInfo().getRegion() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getRegion());
				object.setSearchTerm1(bpRequestGeneralDataDto.getSearchTerm1() == null ? ""
						: bpRequestGeneralDataDto.getSearchTerm1());
				object.setSearchTerm2(bpRequestGeneralDataDto.getSearchTerm2() == null ? ""
						: bpRequestGeneralDataDto.getSearchTerm2());
				object.setDataline("");
				object.setTelebox("");
				object.setTimezone(bpRequestGeneralDataDto.getBpAddressInfo().getTimeZone() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getTimeZone());
				object.setTaxJurisdictn(bpRequestGeneralDataDto.getBpAddressInfo().getTaxJurisdiction() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getTaxJurisdiction());
				object.setAddressID("");
				object.setCreationlang("EN");
				object.setLangCRISO("EN");
				object.setCommMethod(bpRequestGeneralDataDto.getBpAddressInfo().getStandardCommMethod() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getStandardCommMethod());
				object.setAddressgroup("");
				object.setDifferentCity(bpRequestGeneralDataDto.getBpAddressInfo().getDifferentCity() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getDifferentCity());
				object.setCityCode("");
				object.setUndeliverable(bpRequestGeneralDataDto.getBpAddressInfo().getUndeliverable() == null ? ""
						: bpRequestGeneralDataDto.getBpAddressInfo().getUndeliverable());
				object.setUndeliverable1("");
				object.setPOBoxLobby("");
				object.setDelvryServType("");
				object.setDeliveryServiceNo("");
				object.setCountycode("");
				object.setCounty("");
				object.setTownshipcode("");
				object.setTownship("");
				object.setPAN("");

				// Setting To Address Data

				object.setToAddressData(new ArrayList<>());

				// Setting To Company Data

//			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {
//				object.setToCompanyData(bpRequestGeneralDataDto.getBpCompanyCodeInfo().stream()
//						.map(companyCodeDto -> convertChange1(companyCodeDto)).collect(Collectors.toList()));
//			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {
//				object.setToCompanyData(new ArrayList<>());
//			}
				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {
					List<CPICompanyDataDto> cpiCompanyDataDto = new ArrayList<>();
					int i = 0;
					for (BPCompanyCodeInfoDto bpCompanyCodeInfoDto : bpRequestGeneralDataDto.getBpCompanyCodeInfo()) {
						if (!HelperClass.isEmpty(bpCompanyCodeInfoDto.getCompanyCode())) {
							org.json.JSONArray odataCompanyData = result.getJSONObject("ToCompanyData")
									.getJSONArray("results");
							if (!odataCompanyData.isEmpty()) {
								org.json.JSONObject toCompanyData1 = (org.json.JSONObject) ((JSONArray) result
										.getJSONObject("ToCompanyData").get("results")).get(i);
								cpiCompanyDataDto.add(convertChange1(bpRequestGeneralDataDto.getBupaNo(),
										bpCompanyCodeInfoDto, toCompanyData1.getString("PaytTerms")));
								i++;
							}
						}
					}
					object.setToCompanyData(cpiCompanyDataDto);
				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {
					object.setToCompanyData(new ArrayList<>());
				}

				// Setting To Purchase Org Data

//			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpPurchasingOrgDetail())) {
//				object.setToPurchaseOrgData(bpRequestGeneralDataDto.getBpPurchasingOrgDetail().stream()
//						.map(purchasingOrgDto -> convertChange(purchasingOrgDto)).collect(Collectors.toList()));
//			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpPurchasingOrgDetail())) {
//				object.setToPurchaseOrgData(new ArrayList<>());
//			}

				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpPurchasingOrgDetail())) {
					List<CPIPurchaseOrgDataDto> cpiPurchaseOrgDataDto = new ArrayList<>();
					int i = 0;
					for (BPPurchasingOrgDetailDto bpPurchasingOrgDetailDto : bpRequestGeneralDataDto
							.getBpPurchasingOrgDetail()) {
						if (!HelperClass.isEmpty(bpPurchasingOrgDetailDto.getPurchasingOrg())) {
							org.json.JSONArray odataPurchaseData = result.getJSONObject("ToPurchaseOrgData")
									.getJSONArray("results");
							if (!odataPurchaseData.isEmpty()) {
								org.json.JSONObject toPurchaseOrg1 = (org.json.JSONObject) ((JSONArray) result
										.getJSONObject("ToPurchaseOrgData").get("results")).get(i);
								org.json.JSONArray toPlantResults = toPurchaseOrg1.getJSONObject("ToPlant")
										.getJSONArray("results");
								cpiPurchaseOrgDataDto
										.add(convertChange1(bpRequestGeneralDataDto, bpPurchasingOrgDetailDto,
												toPlantResults, toPurchaseOrg1.getString("PaytTerms")));
								i++;
							}
						}
					}
					object.setToPurchaseOrgData(cpiPurchaseOrgDataDto);
				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {
					object.setToPurchaseOrgData(new ArrayList<>());
				}

				// Setting To Classification

				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpVendorClassificationEntity())) {
					object.setToClassification(bpRequestGeneralDataDto.getBpVendorClassificationEntity().stream()
							.map(vendorClassificationEntityDto -> changeConvert(vendorClassificationEntityDto))
							.collect(Collectors.toList()));
				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpVendorClassificationEntity())) {
					object.setToClassification(new ArrayList<>());
				}

				// Setting To Email

				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpEmail())) {
					object.setToEmail(bpRequestGeneralDataDto.getBpCommunication().getBpEmail().stream()
							.map(emailDto -> changeConvert(emailDto)).collect(Collectors.toList()));
				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpEmail())) {
					object.setToEmail(new ArrayList<>());
				}

				// Setting To Phone
//
				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpTelephone())) {
					object.setToPhone(bpRequestGeneralDataDto.getBpCommunication().getBpTelephone().stream()
							.map(telephoneDto -> changeConvert(telephoneDto)).collect(Collectors.toList()));
				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpTelephone())) {
					object.setToPhone(new ArrayList<>());
				}

//			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpTelephone())) {
//				List<CPIPhoneDto> cpiTelePhoneDto = new ArrayList<>();
//				for (BPTelephoneDto telePhoneDto : bpRequestGeneralDataDto.getBpCommunication().getBpTelephone()) {
//					if (telePhoneDto.getIsNew()&&!HelperClass.isEmpty(telePhoneDto.getTelephone())) {
//						cpiTelePhoneDto.add(changeNewConvert(telePhoneDto));
//					} else if(!HelperClass.isEmpty(telePhoneDto.getTelephone())){
//						cpiTelePhoneDto.add(changeConvert(telePhoneDto));
//					}
//				}
//				object.setToPhone(cpiTelePhoneDto);
//			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpTelephone())) {
//				object.setToPhone(new ArrayList<>());
//			}

				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpMobilePhone())) {
					object.getToPhone().addAll(bpRequestGeneralDataDto.getBpCommunication().getBpMobilePhone().stream()
							.map(mobileDto -> changeConvert(mobileDto)).collect(Collectors.toList()));
				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpMobilePhone())) {
					object.getToPhone().addAll(new ArrayList<>());
				}

//			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpMobilePhone())) {
//				List<CPIPhoneDto> cpiMobilePhoneDto = new ArrayList<>();
//				for (BPMobilePhoneDto mobilePhoneDto : bpRequestGeneralDataDto.getBpCommunication().getBpMobilePhone()) {
//					if (mobilePhoneDto.getIsNew()&&!HelperClass.isEmpty(mobilePhoneDto.getMobilePhone())) {
//						cpiMobilePhoneDto.add(changeNewConvert(mobilePhoneDto));
//					} else if(!HelperClass.isEmpty(mobilePhoneDto.getMobilePhone())){
//						cpiMobilePhoneDto.add(changeConvert(mobilePhoneDto));
//					}
//				}
//				object.getToPhone().addAll(cpiMobilePhoneDto);
//			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpMobilePhone())) {
//				object.getToPhone().addAll(new ArrayList<>());
//			}

				// Setting To Fax

				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpFaxInfo())) {
					object.setToFax(bpRequestGeneralDataDto.getBpCommunication().getBpFaxInfo().stream()
							.map(faxDto -> changeConvert(faxDto)).collect(Collectors.toList()));
				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpFaxInfo())) {
					object.setToFax(new ArrayList<>());
				}
				// Setting To Bank

//			    if ((!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation()))&&(!bpRequestGeneralDataDto.getSkipBankValidation())) {
//			        object.setToBank(bpRequestGeneralDataDto.getBpBankInformation().stream()
//			                .map(bankDto -> changeConvert(bankDto)).collect(Collectors.toList()));
//			    }
				List<CPIBankDto> cpiBankDtoList = new ArrayList<>();
				if (!bpRequestGeneralDataDto.getSkipBankValidation()) {

					for (int i = 0; i < toBankSize; i++) {
						CPIBankDto cpiBankDto = new CPIBankDto();
						org.json.JSONObject toBank1 = (org.json.JSONObject) ((JSONArray) result.getJSONObject("ToBank")
								.get("results")).get(i);
						cpiBankDto.setChangeIndObject("U");
						cpiBankDto.setVendor(toBank1.getString("Vendor"));
						cpiBankDto.setBankCountry(toBank1.getString("BankCountry"));
						cpiBankDto.setBanknumber(toBank1.getString("Banknumber"));
						cpiBankDto.setBankAccount(toBank1.getString("BankAccount"));
						cpiBankDto.setControlkey(toBank1.getString("Controlkey"));
						cpiBankDto.setPartBankType(toBank1.getString("PartBankType"));
						cpiBankDto.setCollectauthor(toBank1.getBoolean("Collectauthor"));
						cpiBankDto.setReference(toBank1.getString("Reference"));
						cpiBankDto.setAccountholder(toBank1.getString("Accountholder"));
						cpiBankDto.setIBAN(toBank1.getString("IBAN"));
//					if (toBank1.has("IBANvalidfrom") && !toBank1.isNull("IBANvalidfrom")) {
//				        cpiBankDto.setIBANvalidfrom(toBank1.getString("IBANvalidfrom"));
//				    } else {

						cpiBankDto.setIBANvalidfrom(null);
//				    }
						cpiBankDto.setSwiftCode(toBank1.getString("SwiftCode"));
						cpiBankDtoList.add(cpiBankDto);
					}
				}
				if (bpRequestGeneralDataDto.getSkipBankValidation()) {
					object.setToBank(new ArrayList<>());
				}
//			if ((!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation()))
//					&& (!bpRequestGeneralDataDto.getSkipBankValidation())) {
//				List<CPIBankDto> cpiBankDto = new ArrayList<>();
//				for (BPBankInformationDto bankInformationDto : bpRequestGeneralDataDto.getBpBankInformation()) {
//					if (bankInformationDto.getIsNew()) {
//						cpiBankDto.add(changeConvertNew(bankInformationDto));
//					} else {
//						
//						cpiBankDto.add(changeConvert(bankInformationDto));
//					}
//				}
				object.setToBank(cpiBankDtoList);
//			} else if ((!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation()))
//					&& (bpRequestGeneralDataDto.getSkipBankValidation())) {
//				object.setToBank(new ArrayList<>());
//			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation())) {
//				object.setToBank(new ArrayList<>());
//			}
				// Setting To Contact Info

//			    if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpContactInformation())) {
//			        object.setToContact(bpRequestGeneralDataDto.getBpContactInformation().stream()
//			                .map(contactDto -> convertChange(contactDto,bpRequestGeneralDataDto.getBupaNo())).collect(Collectors.toList()));
//			    }
//				else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpContactInformation())) {
//					object.setToContact(new ArrayList<>());
//				}

				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpContactInformation())) {
					List<CPIContactDto> cpiContactDto = new ArrayList<>();
					for (BPContactInformationDto contactInformationDto : bpRequestGeneralDataDto
							.getBpContactInformation()) {
						if (contactInformationDto.getIsNew()) {
							cpiContactDto
									.add(convertChangeNew(contactInformationDto, bpRequestGeneralDataDto.getBupaNo()));
						} else {
							cpiContactDto
									.add(convertChange(contactInformationDto, bpRequestGeneralDataDto.getBupaNo()));
						}
					}
					object.setToContact(cpiContactDto);
				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpContactInformation())) {
					object.setToContact(new ArrayList<>());
				}

				// Setting To Return Messages

//			    object.setToReturnMessages(new ArrayList<>());
//			    object.setToTaxData(new ArrayList<>());
				return object;
			} else {
				return object;
			}
		}
		if (bpRequestGeneralDataDto.getSubProcessType().equalsIgnoreCase("Bank")) {
			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0))) {
				object.setType(result.has("Type") ? result.getString("Type") : "");
				object.setRequestId(
						bpRequestGeneralDataDto.getRequestId() == null ? "" : bpRequestGeneralDataDto.getRequestId());
				if (validate) {
					object.setValidation("X");
				} else {
					object.setValidation("");
				}
				object.setChangeIndObject("U");
				object.setVendor(result.has("Vendor") ? result.getString("Vendor") : "");
				object.setSystemId(bpRequestGeneralDataDto.getSystemId());
				object.setTrainstation(result.has("Trainstation") ? result.getString("Trainstation") : "");
				object.setLocationno1(result.has("Locationno1") ? result.getString("Locationno1") : "");
				object.setLocationno2(result.has("Locationno2") ? result.getString("Locationno2") : "");
				object.setAuthorization(result.has("Authorization") ? result.getString("Authorization") : "");
				object.setIndustry(result.has("Industry") ? result.getString("Industry") : "");
				object.setCheckdigit(result.has("Industry") ? result.getString("Checkdigit") : "");
				object.setDMEIndicator(result.has("DMEIndicator") ? result.getString("DMEIndicator") : "");
				object.setInstructionkey(result.has("Instructionkey") ? result.getString("Instructionkey") : "");
				object.setISRNumber(result.has("ISRNumber") ? result.getString("ISRNumber") : "");
				object.setCorporateGroup(result.has("CorporateGroup") ? result.getString("CorporateGroup") : "");
				object.setAccountgroup(result.has("Accountgroup") ? result.getString("Accountgroup") : "");
//				    object.setCustomer(bpRequestGeneralDataDto.getBpControlData().get(0).getCustomer() == null ? "" : bpRequestGeneralDataDto.getBpControlData().get(0).getCustomer());
				object.setCustomer(result.has("Customer") ? result.getString("Customer") : "");
				object.setAlternatpayee(result.has("Alternatpayee") ? result.getString("Alternatpayee") : "");
				object.setDeletionflag(result.has("Deletionflag") ? result.getBoolean("Deletionflag") : false);
				object.setPostingBlock(result.has("PostingBlock") ? result.getBoolean("PostingBlock") : false);
				object.setPurchblock(result.has("Purchblock") ? result.getBoolean("Purchblock") : false);
				object.setTaxNumber1(result.has("TaxNumber1") ? result.getString("TaxNumber1") : "");
				object.setTaxNumber2(result.has("TaxNumber2") ? result.getString("TaxNumber2") : "");
				object.setEqualizatntax(result.has("Equalizatntax") ? result.getString("Equalizatntax") : "");
				object.setLiableforVAT(result.has("LiableforVAT") ? result.getBoolean("LiableforVAT") : false);
				object.setPayeeindoc(result.has("Payeeindoc") ? result.getBoolean("Payeeindoc") : false);
				object.setTradingPartner(result.has("TradingPartner") ? result.getString("TradingPartner") : "");
				object.setFiscaladdress(result.has("Fiscaladdress") ? result.getString("Fiscaladdress") : "");
				object.setVATRegNo(result.has("VATRegNo") ? result.getString("VATRegNo") : "");
				object.setNaturalperson(result.has("Naturalperson") ? result.getString("Naturalperson") : "");
				object.setBlockfunction(result.has("Blockfunction") ? result.getString("Blockfunction") : "");
				object.setAddress(result.has("Address") ? result.getString("Address") : "");
				object.setPlaceofbirth(result.has("Placeofbirth") ? result.getString("Placeofbirth") : "");
				String output = null;
//				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getDob())) {
////					SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");
//
//					try {
//						Date date = inputFormat.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getDob());
//
//						output = outputFormat.format(date);
//
//						System.out.println("Input: " + bpRequestGeneralDataDto.getBpControlData().get(0).getDob());
//						System.out.println("Output: " + output);
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//				}
				object.setBirthdate(result.has("Birthdate") ? result.getString("Birthdate") : "");
				object.setSex(result.has("Sex") ? result.getString("Sex") : "");
				object.setCredinfono(result.has("Credinfono") ? result.getString("Credinfono") : "");
//				    object.setLastextreview(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview() == null ? null : bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview());
//				    object.setLastextreview(null);
				long lastExtReview = 0;
//				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview())) {
//					try {
//						Date lastExtReviewDate = sdf
//								.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview());
//						lastExtReview = lastExtReviewDate.getTime();
//						System.out.println("Timestamp in milliseconds: " + lastExtReview);
//					} catch (ParseException e) {
//						System.err.println("Error parsing the date string: " + e.getMessage());
//					}
//					object.setLastextreview("/Date(" + lastExtReview + ")/");
//				} else {
//					object.setLastextreview(null);
//				}
				if (result.has("Lastextreview") && !result.isNull("Lastextreview")) {
					object.setLastextreview(result.getString("Lastextreview"));
				} else {

					object.setLastextreview(null);
				}

				object.setActualQMsys(result.has("ActualQMsys") ? result.getString("ActualQMsys") : "");
				object.setRefacctgroup(result.has("Refacctgroup") ? result.getString("Refacctgroup") : "");
				object.setPlant(result.has("Plant") ? result.getString("Plant") : "");
				object.setVSRrelevant(result.has("VSRrelevant") ? result.getBoolean("VSRrelevant") : false);
				object.setPlantrelevant(result.has("Plantrelevant") ? result.getBoolean("Plantrelevant") : false);
				object.setFactorycalend(result.has("Factorycalend") ? result.getString("Factorycalend") : "");
				object.setSCAC(result.has("SCAC") ? result.getString("SCAC") : "");
				object.setCarfreightgrp(result.has("Carfreightgrp") ? result.getString("Carfreightgrp") : "");
				object.setServAgntProcGrp(result.has("ServAgntProcGrp") ? result.getString("ServAgntProcGrp") : "");
				object.setTaxtype(result.has("TaxType") ? result.getString("TaxType") : "01");
				object.setTaxnumbertype(result.has("Taxnumbertype") ? result.getString("Taxnumbertype") : "01");
				object.setSocialIns(result.has("SocialIns") ? result.getBoolean("SocialIns") : false);
				object.setSocInsCode(result.has("SocInsCode") ? result.getString("SocInsCode") : "");
				object.setTaxNumber3(result.has("TaxNumber3") ? result.getString("TaxNumber3") : "");
				object.setTaxNumber4(result.has("TaxNumber4") ? result.getString("TaxNumber4") : "");
				object.setTaxsplit(result.has("Taxsplit") ? result.getBoolean("Taxsplit") : false);
				object.setTaxbase(result.has("Taxbase") ? result.getString("Taxbase") : "0");
				object.setProfession(result.has("Profession") ? result.getString("Profession") : "");
				object.setExternalmanuf(result.has("Externalmanuf") ? result.getString("Externalmanuf") : "");
				object.setStatgrpagent("");
				object.setDeletionblock(result.has("Deletionblock") ? result.getBoolean("Deletionblock") : false);
				object.setRepsName(result.has("RepsName") ? result.getString("RepsName") : "");
				object.setTypeofBusiness(result.has("TypeOfBusiness") ? result.getString("TypeOfBusiness") : "");
				object.setTypeofIndustry(result.has("TypeofIndustry") ? result.getString("TypeofIndustry") : "");
				long qmSystemTo = 0;
//				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getQmSystemTo())) {
//					try {
//						Date qmSystemToDate = sdf.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getQmSystemTo());
//						qmSystemTo = qmSystemToDate.getTime();
//						System.out.println("Timestamp in milliseconds: " + qmSystemTo);
//					} catch (ParseException e) {
//						System.err.println("Error parsing the date string: " + e.getMessage());
//					}
//					object.setQMsystemto("/Date(" + qmSystemTo + ")/");
//				} else {
//					object.setQMsystemto(null);
//				}
				if (result.has("QMsystemto") && !result.isNull("QMsystemto")) {
					object.setQMsystemto(result.getString("QMsystemto"));
				} else {

					object.setQMsystemto(null);
				}
				object.setPODrelevant(result.has("PODrelevant") ? result.getString("PODrelevant") : "");
				object.setTaxoffice(result.has("Taxoffice") ? result.getString("Taxoffice") : "");
				object.setTaxNumber(result.has("TaxNumber") ? result.getString("TaxNumber") : "");
				object.setTaxNumber5(result.has("TaxNumber5") ? result.getString("TaxNumber5") : "");
				object.setPurposeCompleteFlag(
						result.has("PurposeCompleteFlag") ? result.getString("PurposeCompleteFlag") : "");
				object.setAddressVersion(result.has("AddressVersion") ? result.getString("AddressVersion") : "");
				object.setFrom(result.has("From") ? result.getString("From") : "");
				object.setTo(result.has("To") ? result.getString("To") : "");
				object.setTitle(result.has("Title") ? result.getString("Title") : "");
				object.setName(result.has("Name") ? result.getString("Name") : "");
				object.setName2(result.has("Name2") ? result.getString("Name2") : "");
				object.setName3(result.has("Name3") ? result.getString("Name3") : "");
				object.setName4(result.has("Name4") ? result.getString("Name4") : "");
				object.setConvname(result.has("Convname") ? result.getString("Convname") : "");
				object.setCo(result.has("co") ? result.getString("co") : "");
				object.setCity(result.has("City") ? result.getString("City") : "");
				object.setDistrict(result.has("District") ? result.getString("District") : "");
				object.setCityNo(result.has("CityNo") ? result.getString("CityNo") : "");
				object.setDistrictNo(result.has("DistrictNo") ? result.getString("DistrictNo") : "");
				object.setCheckStatus(result.has("CheckStatus") ? result.getString("CheckStatus") : "");
				object.setRegStrGrp(result.has("RegStrGrp") ? result.getString("RegStrGrp") : "");
				object.setPostalCode(result.has("PostalCode") ? result.getString("PostalCode") : "");
				object.setPOBoxPostCde(result.has("POBoxPostCde") ? result.getString("POBoxPostCde") : "");
				object.setCompanyPostCd(result.has("CompanyPostCd") ? result.getString("CompanyPostCd") : "");
				object.setPostalCodeExt(result.has("PostalCodeExt") ? result.getString("PostalCodeExt") : "");
				object.setPostalCodeExt2(result.has("PostalCodeExt2") ? result.getString("PostalCodeExt2") : "");
				object.setPostalCodeExt3(result.has("PostalCodeExt3") ? result.getString("PostalCodeExt3") : "");
				object.setPOBox(result.has("POBox") ? result.getString("POBox") : "");
				object.setPOBoxwono(result.has("POBoxwono") ? result.getBoolean("POBoxwono") : false);
				object.setPOBoxCity(result.has("POBoxCity") ? result.getString("POBoxCity") : "");
				object.setPOCitNo(result.has("POCitNo") ? result.getString("POCitNo") : "");
				object.setPORegion(result.has("PORegion") ? result.getString("PORegion") : "");
				object.setPOboxcountry(result.has("POboxcountry") ? result.getString("POboxcountry") : "");
				object.setISOcode(result.has("ISOcode") ? result.getString("ISOcode") : "");
				object.setDeliveryDist(result.has("DeliveryDist") ? result.getString("DeliveryDist") : "");
				object.setTransportzone(result.has("Transportzone") ? result.getString("Transportzone") : "");
				object.setStreet(result.has("Street") ? result.getString("Street") : "");

				object.setStreetCode(result.has("StreetCode") ? result.getString("StreetCode") : "");
				object.setStreetAbbrev(result.has("StreetAbbrev") ? result.getString("StreetAbbrev") : "");
				object.setHouseNumber(result.has("HouseNumber") ? result.getString("HouseNumber") : "");
				object.setSupplement(result.has("Supplement") ? result.getString("Supplement") : "");
				object.setNumberRange(result.has("NumberRange") ? result.getString("NumberRange") : "");
				object.setStreet2(result.has("Street2") ? result.getString("Street2") : "");
				object.setStreet3(result.has("Street3") ? result.getString("Street3") : "");
				object.setStreet4(result.has("Street4") ? result.getString("Street4") : "");
				object.setStreet5(result.has("Street5") ? result.getString("Street5") : "");
				object.setBuildingCode(result.has("BuildingCode") ? result.getString("BuildingCode") : "");
				object.setFloor(result.has("Floor") ? result.getString("Floor") : "");
				object.setRoomNumber(result.has("RoomNumber") ? result.getString("RoomNumber") : "");
				object.setCountry(result.has("Country") ? result.getString("Country") : "");
				object.setCountryISO(result.has("CountryISO") ? result.getString("CountryISO") : "");
				object.setLanguage(result.has("Language") ? result.getString("Language") : "");
				object.setLangISO(result.has("LangISO") ? result.getString("LangISO") : "");
				object.setRegion(result.has("Region") ? result.getString("Region") : "");
				object.setSearchTerm1(result.has("SearchTerm1") ? result.getString("SearchTerm1") : "");
				object.setSearchTerm2(result.has("SearchTerm2") ? result.getString("SearchTerm2") : "");
				object.setDataline(result.has("Dataline") ? result.getString("Dataline") : "");
				object.setTelebox(result.has("Telebox") ? result.getString("Telebox") : "");
				object.setTimezone(result.has("Timezone") ? result.getString("Timezone") : "");
				object.setTaxJurisdictn(result.has("TaxJurisdictn") ? result.getString("TaxJurisdictn") : "");

				object.setAddressID(result.has("AddressID") ? result.getString("AddressID") : "");
				object.setCreationlang(result.has("Creationlang") ? result.getString("Creationlang") : "");
				object.setLangCRISO(result.has("LangCRISO") ? result.getString("LangCRISO") : "");
				object.setCommMethod(result.has("CommMethod") ? result.getString("CommMethod") : "");
				object.setAddressgroup(result.has("Addressgroup") ? result.getString("Addressgroup") : "");
				object.setDifferentCity(result.has("DifferentCity") ? result.getString("DifferentCity") : "");
				object.setCityCode(result.has("CityCode") ? result.getString("CityCode") : "");
				object.setUndeliverable(result.has("Undeliverable") ? result.getString("Undeliverable") : "");
				object.setUndeliverable1(result.has("Undeliverable1") ? result.getString("Undeliverable1") : "");
				object.setPOBoxLobby(result.has("POBoxLobby") ? result.getString("POBoxLobby") : "");
				object.setDelvryServType(result.has("DelvryServType") ? result.getString("DelvryServType") : "");
				object.setDeliveryServiceNo(
						result.has("DeliveryServiceNo") ? result.getString("DeliveryServiceNo") : "");
				object.setCountycode(result.has("Countycode") ? result.getString("Countycode") : "");
				object.setCounty(result.has("County") ? result.getString("County") : "");
				object.setTownshipcode(result.has("Townshipcode") ? result.getString("Townshipcode") : "");
				object.setTownship(result.has("Township") ? result.getString("Township") : "");
				object.setPAN(result.has("PAN") ? result.getString("PAN") : "");

				// Setting To Address Data

				object.setToAddressData(new ArrayList<>());

				// Setting To Company Data

//				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {
//					object.setToCompanyData(bpRequestGeneralDataDto.getBpCompanyCodeInfo().stream()
//							.map(companyCodeDto -> convertChange1(companyCodeDto)).collect(Collectors.toList()));
//				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {
//					object.setToCompanyData(new ArrayList<>());
//				}
				if (!toCompanyData.isEmpty()) {
					List<CPICompanyDataDto> cpiCompanyDataDto = new ArrayList<>();
					for (int i = 0; i < toCompanyDataSize; i++) {
						org.json.JSONObject toCompanyData1 = (org.json.JSONObject) ((JSONArray) result
								.getJSONObject("ToCompanyData").get("results")).get(i);
						cpiCompanyDataDto.add(convertChange2(null, toCompanyData1));
					}
					object.setToCompanyData(cpiCompanyDataDto);
				} else if (toCompanyData.isEmpty()) {
					object.setToCompanyData(new ArrayList<>());
				}

				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpPurchasingOrgDetail())) {
					List<CPIPurchaseOrgDataDto> cpiPurchaseOrgDataDto = new ArrayList<>();
					int i = 0;
					for (BPPurchasingOrgDetailDto bpPurchasingOrgDetailDto : bpRequestGeneralDataDto
							.getBpPurchasingOrgDetail()) {
						if (!HelperClass.isEmpty(bpPurchasingOrgDetailDto.getPurchasingOrg())) {
							org.json.JSONObject toCompanyData1 = (org.json.JSONObject) ((JSONArray) result
									.getJSONObject("ToPurchaseOrgData").get("results")).get(i);
							org.json.JSONArray toPlantResults = toCompanyData1.getJSONObject("ToPlant")
									.getJSONArray("results");

							cpiPurchaseOrgDataDto.add(convertChange2(bpRequestGeneralDataDto, bpPurchasingOrgDetailDto,
									toCompanyData1, toPlantResults, toCompanyData1.getString("PaytTerms")));
							i++;
						}
					}
					object.setToPurchaseOrgData(cpiPurchaseOrgDataDto);
				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {
					object.setToPurchaseOrgData(new ArrayList<>());
				}

				// Setting To Classification

				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpVendorClassificationEntity())) {
					object.setToClassification(bpRequestGeneralDataDto.getBpVendorClassificationEntity().stream()
							.map(vendorClassificationEntityDto -> changeConvert(vendorClassificationEntityDto))
							.collect(Collectors.toList()));
				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpVendorClassificationEntity())) {
					object.setToClassification(new ArrayList<>());
				}

				// Setting To Email

				if (!toEmailData.isEmpty()) {
					List<CPIEmailDto> cpiEmailDto = new ArrayList<>();
					for (int i = 0; i < toEmailDataSize; i++) {
						org.json.JSONObject toEmailData1 = (org.json.JSONObject) ((JSONArray) result
								.getJSONObject("ToEmail").get("results")).get(i);
						cpiEmailDto.add(changeConvert2(toEmailData1));
					}
					object.setToEmail(cpiEmailDto);
				} else if (toEmailData.isEmpty()) {
					object.setToEmail(new ArrayList<>());
				}

				// Setting To Phone

				if (!toPhoneData.isEmpty()) {
					List<CPIPhoneDto> cpiPhoneDto = new ArrayList<>();
					for (int i = 0; i < toPhoneDataSize; i++) {
						org.json.JSONObject toPhoneData1 = (org.json.JSONObject) ((JSONArray) result
								.getJSONObject("ToPhone").get("results")).get(i);
						cpiPhoneDto.add(changeConvertEmail2(toPhoneData1));
					}
					object.setToPhone(cpiPhoneDto);
				} else if (toPhoneData.isEmpty()) {
					object.setToPhone(new ArrayList<>());
				}

				// Setting To Fax
				if (!toFaxData.isEmpty()) {
					List<CPIFaxDto> cpiFaxDto = new ArrayList<>();
					for (int i = 0; i < toFaxDataSize; i++) {
						org.json.JSONObject toFaxData1 = (org.json.JSONObject) ((JSONArray) result
								.getJSONObject("ToFax").get("results")).get(i);
						cpiFaxDto.add(changeConvertFax2(toFaxData1));
					}
					object.setToFax(cpiFaxDto);
				} else if (toFaxData.isEmpty()) {
					object.setToFax(new ArrayList<>());
				}
				// Setting To Bank

//				    if ((!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation()))&&(!bpRequestGeneralDataDto.getSkipBankValidation())) {
//				        object.setToBank(bpRequestGeneralDataDto.getBpBankInformation().stream()
//				                .map(bankDto -> changeConvert(bankDto)).collect(Collectors.toList()));
//				    }
//				List<CPIBankDto> cpiBankDtoList = new ArrayList<>();
//				if(!bpRequestGeneralDataDto.getSkipBankValidation()) {
//					
//					
//					for(int i=0;i<toBankSize;i++) {
//						CPIBankDto cpiBankDto=new CPIBankDto();
//						org.json.JSONObject toBank1 = (org.json.JSONObject) ((JSONArray) result.getJSONObject("ToBank")
//								.get("results")).get(i);
//						cpiBankDto.setChangeIndObject("U");
//						cpiBankDto.setVendor(toBank1.getString("Vendor"));
//						cpiBankDto.setBankCountry(toBank1.getString("BankCountry"));
//						cpiBankDto.setBanknumber(toBank1.getString("Banknumber"));
//						cpiBankDto.setBankAccount(toBank1.getString("BankAccount"));
//						cpiBankDto.setControlkey(toBank1.getString("Controlkey"));
//						cpiBankDto.setPartBankType(toBank1.getString("PartBankType"));
//						cpiBankDto.setCollectauthor(toBank1.getBoolean("Collectauthor"));
//						cpiBankDto.setReference(toBank1.getString("Reference"));
//						cpiBankDto.setAccountholder(toBank1.getString("Accountholder"));
//						cpiBankDto.setIBAN(toBank1.getString("IBAN"));
//						if (toBank1.has("IBANvalidfrom") && !toBank1.isNull("IBANvalidfrom")) {
//					        cpiBankDto.setIBANvalidfrom(toBank1.getString("IBANvalidfrom"));
//					    } else {
//					      
//					        cpiBankDto.setIBANvalidfrom(null);
//					    }
//						cpiBankDto.setSwiftCode(toBank1.getString("SwiftCode"));
//						cpiBankDtoList.add(cpiBankDto);
//						}
//				}
				if ((!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation()))
						&& (!bpRequestGeneralDataDto.getSkipBankValidation())) {
					List<CPIBankDto> cpiBankDto = new ArrayList<>();
					for (BPBankInformationDto bankInformationDto : bpRequestGeneralDataDto.getBpBankInformation()) {
						if (bankInformationDto.getIsNew() && !bankInformationDto.getIsDeleted()) {
							cpiBankDto.add(changeConvertNew(bankInformationDto));
						} else if (!bankInformationDto.getIsNew() && bankInformationDto.getIsDeleted() && !validate) {
							cpiBankDto.add(deleteBank(bankInformationDto));
						} else if (!bankInformationDto.getIsNew() && !bankInformationDto.getIsDeleted()) {
							cpiBankDto.add(changeConvert(bankInformationDto));
						}
					}
					cpiBankDto.sort(Comparator.comparing((CPIBankDto b) -> (String) b.getChangeIndObject()));
					object.setToBank(cpiBankDto);
				} else if ((!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation()))
						&& (bpRequestGeneralDataDto.getSkipBankValidation())) {
					object.setToBank(new ArrayList<>());
				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation())) {
					object.setToBank(new ArrayList<>());
				}

				if (!toContactData.isEmpty()) {
					List<CPIContactDto> cpiContactDto = new ArrayList<>();
					for (int i = 0; i < toContactDataSize; i++) {
						org.json.JSONObject toContactData1 = (org.json.JSONObject) ((JSONArray) result
								.getJSONObject("ToContact").get("results")).get(i);
						cpiContactDto.add(convertChangeContact2(toContactData1));
					}
					object.setToContact(cpiContactDto);
				} else if (toFaxData.isEmpty()) {
					object.setToContact(new ArrayList<>());
				}

				// Setting To Return Messages

//				    object.setToReturnMessages(new ArrayList<>());
//				    object.setToTaxData(new ArrayList<>());
				return object;
			} else {
				return object;
			}
		}
		if (bpRequestGeneralDataDto.getSubProcessType().equalsIgnoreCase("PaymentTerms")) {
			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0))) {
				object.setType(result.getString("Type"));
				object.setRequestId(
						bpRequestGeneralDataDto.getRequestId() == null ? "" : bpRequestGeneralDataDto.getRequestId());
				if (validate) {
					object.setValidation("X");
				} else {
					object.setValidation("");
				}
				object.setChangeIndObject("U");
				object.setVendor(result.getString("Vendor"));
				object.setSystemId(bpRequestGeneralDataDto.getSystemId());
				object.setTrainstation(result.getString("Trainstation"));
				object.setLocationno1(result.getString("Locationno1"));
				object.setLocationno2(result.getString("Locationno2"));
				object.setAuthorization(result.getString("Authorization"));
				object.setIndustry(result.getString("Industry"));
				object.setCheckdigit(result.getString("Checkdigit"));
				object.setDMEIndicator(result.getString("DMEIndicator"));
				object.setInstructionkey(result.getString("Instructionkey"));
				object.setISRNumber(result.getString("ISRNumber"));
				object.setCorporateGroup(result.getString("CorporateGroup"));
				object.setAccountgroup(result.getString("Accountgroup"));
//				    object.setCustomer(bpRequestGeneralDataDto.getBpControlData().get(0).getCustomer() == null ? "" : bpRequestGeneralDataDto.getBpControlData().get(0).getCustomer());
				object.setCustomer(result.getString("Customer"));
				object.setAlternatpayee(result.getString("Alternatpayee"));
				object.setDeletionflag(result.getBoolean("Deletionflag"));
				object.setPostingBlock(result.getBoolean("PostingBlock"));
				object.setPurchblock(result.getBoolean("Purchblock"));
				object.setTaxNumber1(result.getString("TaxNumber1"));
				object.setTaxNumber2(result.getString("TaxNumber2"));
				object.setEqualizatntax(result.getString("Equalizatntax"));
				object.setLiableforVAT(result.getBoolean("LiableforVAT"));
				object.setPayeeindoc(result.getBoolean("Payeeindoc"));
				object.setTradingPartner(result.getString("TradingPartner"));
				object.setFiscaladdress(result.getString("Fiscaladdress"));
				object.setVATRegNo(result.getString("VATRegNo"));
				object.setNaturalperson(
						bpRequestGeneralDataDto.getNaturalPer() == null ? "" : bpRequestGeneralDataDto.getNaturalPer());
				object.setBlockfunction("");
				object.setAddress("");
				object.setPlaceofbirth(result.has("Placeofbirth") ? result.getString("Placeofbirth") : "");
				String output = null;
//				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getDob())) {
////					SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");
//
//					try {
//						Date date = inputFormat.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getDob());
//
//						output = outputFormat.format(date);
//
//						System.out.println("Input: " + bpRequestGeneralDataDto.getBpControlData().get(0).getDob());
//						System.out.println("Output: " + output);
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//				}
				object.setBirthdate(result.has("Birthdate") ? result.getString("Birthdate") : "");
				object.setSex(result.has("Sex") ? result.getString("Sex") : "");
				object.setCredinfono(result.has("Credinfono") ? result.getString("Credinfono") : "");
//				    object.setLastextreview(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview() == null ? null : bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview());
//				    object.setLastextreview(null);
				long lastExtReview = 0;
//				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview())) {
//					try {
//						Date lastExtReviewDate = sdf
//								.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview());
//						lastExtReview = lastExtReviewDate.getTime();
//						System.out.println("Timestamp in milliseconds: " + lastExtReview);
//					} catch (ParseException e) {
//						System.err.println("Error parsing the date string: " + e.getMessage());
//					}
//					object.setLastextreview("/Date(" + lastExtReview + ")/");
//				} else {
//					object.setLastextreview(null);
//				}
				if (result.has("Lastextreview") && !result.isNull("Lastextreview")) {
					object.setLastextreview(result.getString("Lastextreview"));
				} else {

					object.setLastextreview(null);
				}

				object.setActualQMsys(result.has("ActualQMsys") ? result.getString("ActualQMsys") : "");
				object.setRefacctgroup(result.has("Refacctgroup") ? result.getString("Refacctgroup") : "");
				object.setPlant(result.has("Plant") ? result.getString("Plant") : "");
				object.setVSRrelevant(result.has("VSRrelevant") ? result.getBoolean("VSRrelevant") : false);
				object.setPlantrelevant(result.has("Plantrelevant") ? result.getBoolean("Plantrelevant") : false);
				object.setFactorycalend(result.has("Factorycalend") ? result.getString("Factorycalend") : "");
				object.setSCAC(result.has("SCAC") ? result.getString("SCAC") : "");
				object.setCarfreightgrp(result.has("Carfreightgrp") ? result.getString("Carfreightgrp") : "");
				object.setServAgntProcGrp(result.has("ServAgntProcGrp") ? result.getString("ServAgntProcGrp") : "");
				object.setTaxtype(result.has("TaxType") ? result.getString("TaxType") : "01");
				object.setTaxnumbertype(result.has("Taxnumbertype") ? result.getString("Taxnumbertype") : "01");
				object.setSocialIns(result.has("SocialIns") ? result.getBoolean("SocialIns") : false);
				object.setSocInsCode(result.has("SocInsCode") ? result.getString("SocInsCode") : "");
				object.setTaxNumber3(result.has("TaxNumber3") ? result.getString("TaxNumber3") : "");
				object.setTaxNumber4(result.has("TaxNumber4") ? result.getString("TaxNumber4") : "");
				object.setTaxsplit(result.has("Taxsplit") ? result.getBoolean("Taxsplit") : false);
				object.setTaxbase(result.has("Taxbase") ? result.getString("Taxbase") : "0");
				object.setProfession(result.has("Profession") ? result.getString("Profession") : "");
				object.setExternalmanuf(result.has("Externalmanuf") ? result.getString("Externalmanuf") : "");
				object.setStatgrpagent("");
				object.setDeletionblock(result.has("Deletionblock") ? result.getBoolean("Deletionblock") : false);
				object.setRepsName(result.has("RepsName") ? result.getString("RepsName") : "");
				object.setTypeofBusiness(result.has("TypeOfBusiness") ? result.getString("TypeOfBusiness") : "");
				object.setTypeofIndustry(result.has("TypeofIndustry") ? result.getString("TypeofIndustry") : "");
				long qmSystemTo = 0;
//				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getQmSystemTo())) {
//					try {
//						Date qmSystemToDate = sdf.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getQmSystemTo());
//						qmSystemTo = qmSystemToDate.getTime();
//						System.out.println("Timestamp in milliseconds: " + qmSystemTo);
//					} catch (ParseException e) {
//						System.err.println("Error parsing the date string: " + e.getMessage());
//					}
//					object.setQMsystemto("/Date(" + qmSystemTo + ")/");
//				} else {
//					object.setQMsystemto(null);
//				}
				if (result.has("QMsystemto") && !result.isNull("QMsystemto")) {
					object.setQMsystemto(result.getString("QMsystemto"));
				} else {

					object.setQMsystemto(null);
				}
				object.setPODrelevant(result.has("PODrelevant") ? result.getString("PODrelevant") : "");
				object.setTaxoffice(result.has("Taxoffice") ? result.getString("Taxoffice") : "");
				object.setTaxNumber(result.has("TaxNumber") ? result.getString("TaxNumber") : "");
				object.setTaxNumber5(result.has("TaxNumber5") ? result.getString("TaxNumber5") : "");
				object.setPurposeCompleteFlag(
						result.has("PurposeCompleteFlag") ? result.getString("PurposeCompleteFlag") : "");
				object.setAddressVersion(result.has("AddressVersion") ? result.getString("AddressVersion") : "");
				object.setFrom(result.has("From") ? result.getString("From") : "");
				object.setTo(result.has("To") ? result.getString("To") : "");
				object.setTitle(result.has("Title") ? result.getString("Title") : "");
				object.setName(result.has("Name") ? result.getString("Name") : "");
				object.setName2(result.has("Name2") ? result.getString("Name2") : "");
				object.setName3(result.has("Name3") ? result.getString("Name3") : "");
				object.setName4(result.has("Name4") ? result.getString("Name4") : "");
				object.setConvname(result.has("Convname") ? result.getString("Convname") : "");
				object.setCo(result.has("co") ? result.getString("co") : "");
				object.setCity(result.has("City") ? result.getString("City") : "");
				object.setDistrict(result.has("District") ? result.getString("District") : "");
				object.setCityNo(result.has("CityNo") ? result.getString("CityNo") : "");
				object.setDistrictNo(result.has("DistrictNo") ? result.getString("DistrictNo") : "");
				object.setCheckStatus(result.has("CheckStatus") ? result.getString("CheckStatus") : "");
				object.setRegStrGrp(result.has("RegStrGrp") ? result.getString("RegStrGrp") : "");
				object.setPostalCode(result.has("PostalCode") ? result.getString("PostalCode") : "");
				object.setPOBoxPostCde(result.has("POBoxPostCde") ? result.getString("POBoxPostCde") : "");
				object.setCompanyPostCd(result.has("CompanyPostCd") ? result.getString("CompanyPostCd") : "");
				object.setPostalCodeExt(result.has("PostalCodeExt") ? result.getString("PostalCodeExt") : "");
				object.setPostalCodeExt2(result.has("PostalCodeExt2") ? result.getString("PostalCodeExt2") : "");
				object.setPostalCodeExt3(result.has("PostalCodeExt3") ? result.getString("PostalCodeExt3") : "");
				object.setPOBox(result.has("POBox") ? result.getString("POBox") : "");
				object.setPOBoxwono(result.has("POBoxwono") ? result.getBoolean("POBoxwono") : false);
				object.setPOBoxCity(result.has("POBoxCity") ? result.getString("POBoxCity") : "");
				object.setPOCitNo(result.has("POCitNo") ? result.getString("POCitNo") : "");
				object.setPORegion(result.has("PORegion") ? result.getString("PORegion") : "");
				object.setPOboxcountry(result.has("POboxcountry") ? result.getString("POboxcountry") : "");
				object.setISOcode(result.has("ISOcode") ? result.getString("ISOcode") : "");
				object.setDeliveryDist(result.has("DeliveryDist") ? result.getString("DeliveryDist") : "");
				object.setTransportzone(result.has("Transportzone") ? result.getString("Transportzone") : "");
				object.setStreet(result.has("Street") ? result.getString("Street") : "");

				object.setStreetCode(result.has("StreetCode") ? result.getString("StreetCode") : "");
				object.setStreetAbbrev(result.has("StreetAbbrev") ? result.getString("StreetAbbrev") : "");
				object.setHouseNumber(result.has("HouseNumber") ? result.getString("HouseNumber") : "");
				object.setSupplement(result.has("Supplement") ? result.getString("Supplement") : "");
				object.setNumberRange(result.has("NumberRange") ? result.getString("NumberRange") : "");
				object.setStreet2(result.has("Street2") ? result.getString("Street2") : "");
				object.setStreet3(result.has("Street3") ? result.getString("Street3") : "");
				object.setStreet4(result.has("Street4") ? result.getString("Street4") : "");
				object.setStreet5(result.has("Street5") ? result.getString("Street5") : "");
				object.setBuildingCode(result.has("BuildingCode") ? result.getString("BuildingCode") : "");
				object.setFloor(result.has("Floor") ? result.getString("Floor") : "");
				object.setRoomNumber(result.has("RoomNumber") ? result.getString("RoomNumber") : "");
				object.setCountry(result.has("Country") ? result.getString("Country") : "");
				object.setCountryISO(result.has("CountryISO") ? result.getString("CountryISO") : "");
				object.setLanguage(result.has("Language") ? result.getString("Language") : "");
				object.setLangISO(result.has("LangISO") ? result.getString("LangISO") : "");
				object.setRegion(result.has("Region") ? result.getString("Region") : "");
				object.setSearchTerm1(result.has("SearchTerm1") ? result.getString("SearchTerm1") : "");
				object.setSearchTerm2(result.has("SearchTerm2") ? result.getString("SearchTerm2") : "");
				object.setDataline(result.has("Dataline") ? result.getString("Dataline") : "");
				object.setTelebox(result.has("Telebox") ? result.getString("Telebox") : "");
				object.setTimezone(result.has("Timezone") ? result.getString("Timezone") : "");
				object.setTaxJurisdictn(result.has("TaxJurisdictn") ? result.getString("TaxJurisdictn") : "");

				object.setAddressID(result.has("AddressID") ? result.getString("AddressID") : "");
				object.setCreationlang(result.has("Creationlang") ? result.getString("Creationlang") : "");
				object.setLangCRISO(result.has("LangCRISO") ? result.getString("LangCRISO") : "");
				object.setCommMethod(result.has("CommMethod") ? result.getString("CommMethod") : "");
				object.setAddressgroup(result.has("Addressgroup") ? result.getString("Addressgroup") : "");
				object.setDifferentCity(result.has("DifferentCity") ? result.getString("DifferentCity") : "");
				object.setCityCode(result.has("CityCode") ? result.getString("CityCode") : "");
				object.setUndeliverable(result.has("Undeliverable") ? result.getString("Undeliverable") : "");
				object.setUndeliverable1(result.has("Undeliverable1") ? result.getString("Undeliverable1") : "");
				object.setPOBoxLobby(result.has("POBoxLobby") ? result.getString("POBoxLobby") : "");
				object.setDelvryServType(result.has("DelvryServType") ? result.getString("DelvryServType") : "");
				object.setDeliveryServiceNo(
						result.has("DeliveryServiceNo") ? result.getString("DeliveryServiceNo") : "");
				object.setCountycode(result.has("Countycode") ? result.getString("Countycode") : "");
				object.setCounty(result.has("County") ? result.getString("County") : "");
				object.setTownshipcode(result.has("Townshipcode") ? result.getString("Townshipcode") : "");
				object.setTownship(result.has("Township") ? result.getString("Township") : "");
				object.setPAN(result.has("PAN") ? result.getString("PAN") : "");

				// Setting To Address Data

				object.setToAddressData(new ArrayList<>());

				// Setting To Company Data

//				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {
//					object.setToCompanyData(bpRequestGeneralDataDto.getBpCompanyCodeInfo().stream()
//							.map(companyCodeDto -> convertChange1(companyCodeDto)).collect(Collectors.toList()));
//				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {
//					object.setToCompanyData(new ArrayList<>());
//				}
				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {
					List<CPICompanyDataDto> cpiCompanyDataDto = new ArrayList<>();
					int i = 0;
					for (BPCompanyCodeInfoDto bpCompanyCodeInfoDto : bpRequestGeneralDataDto.getBpCompanyCodeInfo()) {
						if (!HelperClass.isEmpty(bpCompanyCodeInfoDto.getCompanyCode())) {
							org.json.JSONObject toCompanyData1 = (org.json.JSONObject) ((JSONArray) result
									.getJSONObject("ToCompanyData").get("results")).get(i);
							cpiCompanyDataDto.add(convertChange2(bpCompanyCodeInfoDto, toCompanyData1));
							i++;
						}
					}
					object.setToCompanyData(cpiCompanyDataDto);
				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {
					object.setToCompanyData(new ArrayList<>());
				}

				// Setting To Purchase Org Data

				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpPurchasingOrgDetail())) {
					List<CPIPurchaseOrgDataDto> cpiPurchaseOrgDataDto = new ArrayList<>();
					int i = 0;
					for (BPPurchasingOrgDetailDto bpPurchasingOrgDetailDto : bpRequestGeneralDataDto
							.getBpPurchasingOrgDetail()) {
						if (!HelperClass.isEmpty(bpPurchasingOrgDetailDto.getPurchasingOrg())) {
							org.json.JSONObject toCompanyData1 = (org.json.JSONObject) ((JSONArray) result
									.getJSONObject("ToPurchaseOrgData").get("results")).get(i);
							org.json.JSONArray toPlantResults = toCompanyData1.getJSONObject("ToPlant")
									.getJSONArray("results");
							cpiPurchaseOrgDataDto.add(convertChange2(bpRequestGeneralDataDto, bpPurchasingOrgDetailDto,
									toCompanyData1, toPlantResults, null));
							i++;
						}
					}
					object.setToPurchaseOrgData(cpiPurchaseOrgDataDto);
				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())) {
					object.setToPurchaseOrgData(new ArrayList<>());
				}

				// Setting To Classification

				if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpVendorClassificationEntity())) {
					object.setToClassification(bpRequestGeneralDataDto.getBpVendorClassificationEntity().stream()
							.map(vendorClassificationEntityDto -> changeConvert(vendorClassificationEntityDto))
							.collect(Collectors.toList()));
				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpVendorClassificationEntity())) {
					object.setToClassification(new ArrayList<>());
				}

// Setting To Email

				if (!toEmailData.isEmpty()) {
					List<CPIEmailDto> cpiEmailDto = new ArrayList<>();
					for (int i = 0; i < toEmailDataSize; i++) {
						org.json.JSONObject toEmailData1 = (org.json.JSONObject) ((JSONArray) result
								.getJSONObject("ToEmail").get("results")).get(i);
						cpiEmailDto.add(changeConvert2(toEmailData1));
					}
					object.setToEmail(cpiEmailDto);
				} else if (toEmailData.isEmpty()) {
					object.setToEmail(new ArrayList<>());
				}

				// Setting To Phone

				if (!toPhoneData.isEmpty()) {
					List<CPIPhoneDto> cpiPhoneDto = new ArrayList<>();
					for (int i = 0; i < toPhoneDataSize; i++) {
						org.json.JSONObject toPhoneData1 = (org.json.JSONObject) ((JSONArray) result
								.getJSONObject("ToPhone").get("results")).get(i);
						cpiPhoneDto.add(changeConvertEmail2(toPhoneData1));
					}
					object.setToPhone(cpiPhoneDto);
				} else if (toPhoneData.isEmpty()) {
					object.setToPhone(new ArrayList<>());
				}

				// Setting To Fax
				if (!toFaxData.isEmpty()) {
					List<CPIFaxDto> cpiFaxDto = new ArrayList<>();
					for (int i = 0; i < toFaxDataSize; i++) {
						org.json.JSONObject toFaxData1 = (org.json.JSONObject) ((JSONArray) result
								.getJSONObject("ToFax").get("results")).get(i);
						cpiFaxDto.add(changeConvertFax2(toFaxData1));
					}
					object.setToFax(cpiFaxDto);
				} else if (toFaxData.isEmpty()) {
					object.setToFax(new ArrayList<>());
				}
				// Setting To Bank

//				    if ((!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation()))&&(!bpRequestGeneralDataDto.getSkipBankValidation())) {
//				        object.setToBank(bpRequestGeneralDataDto.getBpBankInformation().stream()
//				                .map(bankDto -> changeConvert(bankDto)).collect(Collectors.toList()));
//				    }
				List<CPIBankDto> cpiBankDtoList = new ArrayList<>();
				if (!bpRequestGeneralDataDto.getSkipBankValidation()) {

					for (int i = 0; i < toBankSize; i++) {
						CPIBankDto cpiBankDto = new CPIBankDto();
						org.json.JSONObject toBank1 = (org.json.JSONObject) ((JSONArray) result.getJSONObject("ToBank")
								.get("results")).get(i);
						cpiBankDto.setChangeIndObject("U");
						cpiBankDto.setVendor(toBank1.getString("Vendor"));
						cpiBankDto.setBankCountry(toBank1.getString("BankCountry"));
						cpiBankDto.setBanknumber(toBank1.getString("Banknumber"));
						cpiBankDto.setBankAccount(toBank1.getString("BankAccount"));
						cpiBankDto.setControlkey(toBank1.getString("Controlkey"));
						cpiBankDto.setPartBankType(toBank1.getString("PartBankType"));
						cpiBankDto.setCollectauthor(toBank1.getBoolean("Collectauthor"));
						cpiBankDto.setReference(toBank1.getString("Reference"));
						cpiBankDto.setAccountholder(toBank1.getString("Accountholder"));
						cpiBankDto.setIBAN(toBank1.getString("IBAN"));
//						if (toBank1.has("IBANvalidfrom") && !toBank1.isNull("IBANvalidfrom")) {
//					        cpiBankDto.setIBANvalidfrom(toBank1.getString("IBANvalidfrom"));
//					    } else {
//					      
						cpiBankDto.setIBANvalidfrom(null);
//					    }
						cpiBankDto.setSwiftCode(toBank1.getString("SwiftCode"));
						cpiBankDtoList.add(cpiBankDto);
					}
				}
//				if ((!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation()))
//						&& (!bpRequestGeneralDataDto.getSkipBankValidation())) {
//					List<CPIBankDto> cpiBankDto = new ArrayList<>();
//					for (BPBankInformationDto bankInformationDto : bpRequestGeneralDataDto.getBpBankInformation()) {
//						if (bankInformationDto.getIsNew()) {
//							cpiBankDto.add(changeConvertNew(bankInformationDto));
//						} else {
//							
//							cpiBankDto.add(changeConvert(bankInformationDto));
//						}
//					}
				object.setToBank(cpiBankDtoList);
//				} else if ((!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation()))
//						&& (bpRequestGeneralDataDto.getSkipBankValidation())) {
//					object.setToBank(new ArrayList<>());
//				} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation())) {
//					object.setToBank(new ArrayList<>());
//				}
				// Setting To Contact Info

//				    if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpContactInformation())) {
//				        object.setToContact(bpRequestGeneralDataDto.getBpContactInformation().stream()
//				                .map(contactDto -> convertChange(contactDto,bpRequestGeneralDataDto.getBupaNo())).collect(Collectors.toList()));
//				    }
//					else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpContactInformation())) {
//						object.setToContact(new ArrayList<>());
//					}

				if (!toContactData.isEmpty()) {
					List<CPIContactDto> cpiContactDto = new ArrayList<>();
					for (int i = 0; i < toContactDataSize; i++) {
						org.json.JSONObject toContactData1 = (org.json.JSONObject) ((JSONArray) result
								.getJSONObject("ToContact").get("results")).get(i);
						cpiContactDto.add(convertChangeContact2(toContactData1));
					}
					object.setToContact(cpiContactDto);
				} else if (toFaxData.isEmpty()) {
					object.setToContact(new ArrayList<>());
				}

				// Setting To Return Messages

//				    object.setToReturnMessages(new ArrayList<>());
//				    object.setToTaxData(new ArrayList<>());
				return object;
			} else {
				return object;
			}
		}
		return null;
	}

	private List<CPIBankDto> deleteBank(JSONArray toBank, ArrayList<BPBankInformationDto> bpBankInformation) {
		List<CPIBankDto> cpiBankDtoList = new ArrayList<>();
		for (int i = 0; i < toBank.length(); i++) {
			CPIBankDto cpiBankDto = new CPIBankDto();
//			org.json.JSONObject toBank1 = (org.json.JSONObject) ((JSONArray) result.getJSONObject("ToBank")
//					.get("results")).get(i);
			org.json.JSONObject toBank1 = toBank.getJSONObject(i);
			cpiBankDto.setChangeIndObject("D");
			cpiBankDto.setVendor(toBank1.getString("Vendor"));
			cpiBankDto.setBankCountry(toBank1.getString("BankCountry"));
			cpiBankDto.setBanknumber(toBank1.getString("Banknumber"));
			cpiBankDto.setBankAccount(toBank1.getString("BankAccount"));
			cpiBankDto.setControlkey(toBank1.getString("Controlkey"));
			cpiBankDto.setPartBankType(toBank1.getString("PartBankType"));
			cpiBankDto.setCollectauthor(toBank1.getBoolean("Collectauthor"));
			cpiBankDto.setReference(toBank1.getString("Reference"));
			cpiBankDto.setAccountholder(toBank1.getString("Accountholder"));
			cpiBankDto.setIBAN(toBank1.getString("IBAN"));
//			if (toBank1.has("IBANvalidfrom") && !toBank1.isNull("IBANvalidfrom")) {
//		        cpiBankDto.setIBANvalidfrom(toBank1.getString("IBANvalidfrom"));
//		    } else {

			cpiBankDto.setIBANvalidfrom(null);
//		    }
			cpiBankDto.setSwiftCode(toBank1.getString("SwiftCode"));
			cpiBankDtoList.add(cpiBankDto);
		}
		System.out.println("%%%%%%");
		System.out.println(cpiBankDtoList);
		System.out.println("%%%%%%");
		return cpiBankDtoList;
	}

	public CPICompanyDataDto convertChange1(String bupaNo, BPCompanyCodeInfoDto companyCodeDto, String payTerms) {
		CPICompanyDataDto object = new CPICompanyDataDto();
		object.setChangeIndObject("U");
		object.setVendor(bupaNo == null ? "" : bupaNo);
		object.setCompanyCode(companyCodeDto.getCompanyCode() == null ? "" : companyCodeDto.getCompanyCode());
		object.setCocodepostblock(false);
		object.setCocdedeletionflag(false);
		object.setSortkey(companyCodeDto.getBpAccountingInformation().getSortKey() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getSortKey());
		object.setReconaccount(
				companyCodeDto.getBpAccountingInformation().getReconcilliationAccountInGeneralLedger() == null ? ""
						: companyCodeDto.getBpAccountingInformation().getReconcilliationAccountInGeneralLedger());
		object.setAuthorization(companyCodeDto.getBpAccountingInformation().getAuthorization() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getAuthorization());
		object.setInterestindic(companyCodeDto.getBpAccountingInformation().getInterestInd() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getInterestInd());
		object.setPaymentmethods(companyCodeDto.getBpPaymentTransaction().getPaymentMethods() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPaymentMethods());
		object.setClrgwithcust(false);
		object.setPaymentblock(companyCodeDto.getBpPaymentTransaction().getPaymentBlock() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPaymentBlock());
		object.setPaytTerms(payTerms == null ? "" : payTerms);
		object.setAcctvendor("");
		object.setClerkatvendor(companyCodeDto.getBpCorrespondance().getClerkAtVendor() == null ? ""
				: companyCodeDto.getBpCorrespondance().getClerkAtVendor());
		object.setAccountmemo(companyCodeDto.getBpCorrespondance().getAccountMemo() == null ? ""
				: companyCodeDto.getBpCorrespondance().getAccountMemo());
		object.setPlanninggroup("");
		object.setAcctgclerk(companyCodeDto.getBpCorrespondance().getAccountingClerk() == null ? ""
				: companyCodeDto.getBpCorrespondance().getAccountingClerk());
		object.setHeadoffice(companyCodeDto.getBpAccountingInformation().getHeadOffice() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getHeadOffice());
		object.setAlternatpayee(companyCodeDto.getBpPaymentTransaction().getAlternatePayee() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getAlternatePayee());
		long lastKey = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getLastKeyDate())) {
			try {
				Date lastKeyDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getLastKeyDate());
				lastKey = lastKeyDate.getTime();
				System.out.println("Timestamp in milliseconds: " + lastKey);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setLastkeydate("/Date(" + lastKey + ")/");
		} else {
			object.setLastkeydate(null);
		}
		object.setIntcalcfreq("00");
		long lastintcalc = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getLastInterestRun())) {
			try {
				Date intcalcDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getLastInterestRun());
				lastintcalc = intcalcDate.getTime();
				System.out.println("Timestamp in milliseconds: " + lastintcalc);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setLastintcalc("/Date(" + lastintcalc + ")/");
		} else {
			object.setLastintcalc(null);
		}
		object.setLocalprocess(companyCodeDto.getBpCorrespondance().getLocalProcess() == null ? false
				: companyCodeDto.getBpCorrespondance().getLocalProcess());
		object.setBexchlimit(companyCodeDto.getBpPaymentTransaction().getBExchLimit() == null ? "0.000"
				: companyCodeDto.getBpPaymentTransaction().getBExchLimit());
		object.setChkcashngtime(companyCodeDto.getBpPaymentTransaction().getChkCashingTime() == null ? "0"
				: companyCodeDto.getBpPaymentTransaction().getChkCashingTime());
		object.setChkdoubleinv(companyCodeDto.getBpPaymentTransaction().getChkDoubleInv() == null ? true
				: companyCodeDto.getBpPaymentTransaction().getChkDoubleInv());
		object.setTolerancegroup(companyCodeDto.getBpPaymentTransaction().getPayDataToleranceGroup() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPayDataToleranceGroup());
		object.setHouseBank(companyCodeDto.getBpPaymentTransaction().getHouseBank() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getHouseBank());
		object.setIndividualpmnt(companyCodeDto.getBpPaymentTransaction().getIndividualPermit() == null ? false
				: companyCodeDto.getBpPaymentTransaction().getIndividualPermit());
		object.setPmtmethsupl(companyCodeDto.getBpPaymentTransaction().getPmtmethsupl() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPmtmethsupl());
		object.setExemptionno(companyCodeDto.getBpAccountingInformation().getExemptionNumber() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getExemptionNumber());
		long validUntil = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getValidUntil())) {
			try {
				Date validUntilDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getValidUntil());
				validUntil = validUntilDate.getTime();
				System.out.println("Timestamp in milliseconds: " + validUntil);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setValiduntil("/Date(" + validUntil + ")/");
		} else {
			object.setValiduntil(null);
		}
		object.setWTaxCode(companyCodeDto.getBpAccountingInformation().getWtaxCode() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getWtaxCode());
		object.setSubsind("");
		object.setMaineconomicact("0000");
		object.setMinorityindic(companyCodeDto.getBpAccountingInformation().getMinorityIndicator() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getMinorityIndicator());
		object.setPrevacctno(companyCodeDto.getBpAccountingInformation().getPrevAcctNo() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getPrevAcctNo());
		object.setGroupingkey1(companyCodeDto.getBpPaymentTransaction().getGroupingKey() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getGroupingKey());
		object.setGroupingkey2("");
		object.setPmtmethsupl("");
		object.setRecipienttype(companyCodeDto.getBpAccountingInformation().getRecipientType() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getRecipientType());
		object.setExmptauthority(companyCodeDto.getBpAccountingInformation().getExemptionAuthority() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getExemptionAuthority());
		object.setCountryForWT(companyCodeDto.getWhTaxCountry());
		object.setPmtadvbyEDI(companyCodeDto.getBpPaymentTransaction().getPmtAdvByEDI() == null ? false
				: companyCodeDto.getBpPaymentTransaction().getPmtAdvByEDI());
		object.setReleasegroup(companyCodeDto.getBpAccountingInformation().getReleaseGroup() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getReleaseGroup());
		object.setClerksfax(companyCodeDto.getBpCorrespondance().getClerkFax() == null ? ""
				: companyCodeDto.getBpCorrespondance().getClerkFax());
		object.setClrksinternet(companyCodeDto.getBpCorrespondance().getClerkInternet() == null ? ""
				: companyCodeDto.getBpCorrespondance().getClerkInternet());
		object.setCrmemoterms("");
		object.setActivityCode("");
		object.setDistrType("");
		object.setAcctstatement("");
		long timestampOfExemptFromDate = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getCertificationDate())) {
			try {
				Date exemptFromDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getCertificationDate());
				timestampOfExemptFromDate = exemptFromDate.getTime();
				System.out.println("Timestamp in milliseconds: " + timestampOfExemptFromDate);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setCertifictnDate("/Date(" + timestampOfExemptFromDate + ")/");
		} else {
			object.setCertifictnDate(null);
		}
		object.setTolerancegrp(
				companyCodeDto.getBpPaymentTransaction().getInvoiceVerificationToleranceGroup() == null ? ""
						: companyCodeDto.getBpPaymentTransaction().getInvoiceVerificationToleranceGroup());
		object.setPersonnelNo(companyCodeDto.getBpAccountingInformation().getPersonnelNumber() == null ? "00000000"
				: companyCodeDto.getBpAccountingInformation().getPersonnelNumber());
		object.setCoCddelblock(false);
		object.setActclktelno(companyCodeDto.getBpCorrespondance().getActingClerksTelephone() == null ? ""
				: companyCodeDto.getBpCorrespondance().getActingClerksTelephone());
		object.setPrepaymentRelevant(companyCodeDto.getBpPaymentTransaction().getPrePayment() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPrePayment());
		object.setAssignmTestGroup("");
		object.setPurposeCompleteFlag("");
		object.setBranchCode(companyCodeDto.getBranchCode() == null ? "" : companyCodeDto.getBranchCode());
		object.setBranchCodeDescription(
				companyCodeDto.getBranchCodeDescription() == null ? "" : companyCodeDto.getBranchCodeDescription());

		List<CPIDunningDataDto> cpiDunningDataDto = new ArrayList<>();
		if (!HelperClass.isEmpty(companyCodeDto.getBpCorrespondance().getDunnProcedure())) {
			CPIDunningDataDto dunningDataObject = new CPIDunningDataDto();
			dunningDataObject.setChangeIndObject("U");
			dunningDataObject.setVendor(bupaNo == null ? "" : bupaNo);
			dunningDataObject
					.setCompanyCode(companyCodeDto.getCompanyCode() == null ? "" : companyCodeDto.getCompanyCode());
			dunningDataObject.setDunningArea("");
			dunningDataObject.setDunnProcedure(companyCodeDto.getBpCorrespondance().getDunnProcedure() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunnProcedure());
			dunningDataObject.setDunnBlock(companyCodeDto.getBpCorrespondance().getDunningBlock() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunningBlock());
			long lastDunned = 0;
			if (!HelperClass.isEmpty(companyCodeDto.getBpCorrespondance().getLastDunned())) {
				try {
					Date lastDunnedDate = sdf.parse(companyCodeDto.getBpCorrespondance().getLastDunned());
					lastDunned = lastDunnedDate.getTime();
					System.out.println("Timestamp in milliseconds: " + lastDunned);
				} catch (ParseException e) {
					System.err.println("Error parsing the date string: " + e.getMessage());
				}
				dunningDataObject.setLastDunned("/Date(" + lastDunned + ")/");
			} else {
				dunningDataObject.setLastDunned(null);
			}
			dunningDataObject.setDunningLevel(companyCodeDto.getBpCorrespondance().getDunningLevel() == null ? "0"
					: companyCodeDto.getBpCorrespondance().getDunningLevel().toString());
			dunningDataObject.setDunnrecipient(companyCodeDto.getBpCorrespondance().getDunnRecepient() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunnRecepient());
			dunningDataObject.setLegdunnproc(null);
			dunningDataObject.setDunningclerk(companyCodeDto.getBpCorrespondance().getDunningClerk() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunningClerk());

			cpiDunningDataDto.add(dunningDataObject);

			object.setToDunningData(cpiDunningDataDto);
		} else {
			object.setToDunningData(new ArrayList<>());
		}
//		object.setToWtax(new ArrayList<>());
//			if (!HelperClass.isEmpty(companyCodeDto.getBpWithholdingTax())) {
//				object.setToWtax(companyCodeDto.getBpWithholdingTax().stream()
//						.map(wTaxDto -> convertChange(wTaxDto,companyCodeDto)).collect(Collectors.toList()));
//			}
//			else {
//				object.setToWtax(new ArrayList<>());
//				}
		if (!HelperClass.isEmpty(companyCodeDto.getBpWithholdingTax())) {
			List<CPIwTaxDto> cpiwTaxDto = new ArrayList<>();
			for (BPWithholdingTaxDto withholdingTaxDto : companyCodeDto.getBpWithholdingTax()) {
				if (withholdingTaxDto.getIsNew() && !HelperClass.isEmpty(withholdingTaxDto.getWithholdingTaxType())) {
					cpiwTaxDto.add(convertChangeNew(withholdingTaxDto, companyCodeDto, true));
				} else if (!withholdingTaxDto.getIsNew()
						&& !HelperClass.isEmpty(withholdingTaxDto.getWithholdingTaxType())) {
					cpiwTaxDto.add(convertChangeOld(bupaNo, withholdingTaxDto, companyCodeDto, false));
				}
			}
			object.setToWtax(cpiwTaxDto);
		} else if (HelperClass.isEmpty(companyCodeDto.getBpWithholdingTax())) {
			object.setToWtax(new ArrayList<>());
		}
		return object;
	}

	public CPICompanyDataDto convertChange2(BPCompanyCodeInfoDto bpCompanyCodeInfoDto, org.json.JSONObject obj) {
		CPICompanyDataDto object = new CPICompanyDataDto();
		object.setChangeIndObject("U");
		object.setVendor(obj.has("Vendor") ? obj.getString("Vendor") : "");
		object.setCompanyCode(obj.has("CompanyCode") ? obj.getString("CompanyCode") : "");
		object.setCocodepostblock(obj.has("Cocodepostblock") ? obj.getBoolean("Cocodepostblock") : false);
		object.setCocdedeletionflag(obj.has("Cocdedeletionflag") ? obj.getBoolean("Cocdedeletionflag") : false);
		object.setSortkey(obj.has("Sortkey") ? obj.getString("Sortkey") : "");
		object.setReconaccount(obj.has("Reconaccount") ? obj.getString("Reconaccount") : "");
		object.setAuthorization(obj.has("Authorization") ? obj.getString("Authorization") : "");
		object.setInterestindic(obj.has("Interestindic") ? obj.getString("Interestindic") : "");
		object.setPaymentmethods(obj.has("Paymentmethods") ? obj.getString("Paymentmethods") : "");
		object.setClrgwithcust(obj.has("Clrgwithcust") ? obj.getBoolean("Clrgwithcust") : false);
		object.setPaymentblock(obj.has("Paymentblock") ? obj.getString("Paymentblock") : "");
		if (bpCompanyCodeInfoDto != null) {
			object.setPaytTerms(bpCompanyCodeInfoDto.getBpPaymentTransaction().getPaymentTerms() == null ? ""
					: bpCompanyCodeInfoDto.getBpPaymentTransaction().getPaymentTerms());
		} else {
			object.setPaytTerms(obj.has("PaytTerms") ? obj.getString("PaytTerms") : "");
		}
		object.setAcctvendor(obj.has("Acctvendor") ? obj.getString("Acctvendor") : "");
		object.setClerkatvendor(obj.has("Clerkatvendor") ? obj.getString("Clerkatvendor") : "");
		object.setAccountmemo(obj.has("Accountmemo") ? obj.getString("Accountmemo") : "");
		object.setPlanninggroup(obj.has("Planninggroup") ? obj.getString("Planninggroup") : "");
		object.setAcctgclerk(obj.has("Acctgclerk") ? obj.getString("Acctgclerk") : "");
		object.setHeadoffice(obj.has("Headoffice") ? obj.getString("Headoffice") : "");
		object.setAlternatpayee(obj.has("Alternatpayee") ? obj.getString("Alternatpayee") : "");

		long lastKey = 0;
//		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getLastKeyDate())) {
//			try {
//				Date lastKeyDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getLastKeyDate());
//				lastKey = lastKeyDate.getTime();
//				System.out.println("Timestamp in milliseconds: " + lastKey);
//			} catch (ParseException e) {
//				System.err.println("Error parsing the date string: " + e.getMessage());
//			}
//			object.setLastkeydate("/Date(" + lastKey + ")/");
//		} else {
//			object.setLastkeydate(null);
//		}
		if (obj.has("Lastkeydate") && !obj.isNull("Lastkeydate")) {
			object.setLastkeydate(obj.getString("Lastkeydate"));
		} else {

			object.setLastkeydate(null);
		}
		object.setIntcalcfreq(obj.has("Intcalcfreq") ? obj.getString("Intcalcfreq") : "00");
		long lastintcalc = 0;
//		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getLastInterestRun())) {
//			try {
//				Date intcalcDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getLastKeyDate());
//				lastintcalc = intcalcDate.getTime();
//				System.out.println("Timestamp in milliseconds: " + lastintcalc);
//			} catch (ParseException e) {
//				System.err.println("Error parsing the date string: " + e.getMessage());
//			}
//			object.setLastintcalc("/Date(" + lastintcalc + ")/");
//		} else {
//			object.setLastintcalc(null);
//		}
		if (obj.has("Lastintcalc") && !obj.isNull("Lastintcalc")) {
			object.setLastintcalc(obj.getString("Lastintcalc"));
		} else {

			object.setLastintcalc(null);
		}
		object.setLocalprocess(obj.has("Localprocess") ? obj.getBoolean("Localprocess") : false);
		object.setBexchlimit(obj.has("Bexchlimit") ? obj.getString("Bexchlimit") : "0.000");
		object.setChkcashngtime(obj.has("Chkcashngtime") ? obj.getString("Chkcashngtime") : "0");

		object.setChkdoubleinv(obj.has("Chkdoubleinv") ? obj.getBoolean("Chkdoubleinv") : false);
		object.setTolerancegroup(obj.has("Tolerancegroup") ? obj.getString("Tolerancegroup") : "");
		object.setHouseBank(obj.has("HouseBank") ? obj.getString("HouseBank") : "");
		object.setIndividualpmnt(obj.has("Individualpmnt") ? obj.getBoolean("Individualpmnt") : false);
		object.setPmtmethsupl(obj.has("Pmtmethsupl") ? obj.getString("Pmtmethsupl") : "");

		object.setExemptionno(obj.has("Exemptionno") ? obj.getString("Exemptionno") : "");
		long validUntil = 0;
//		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getValidUntil())) {
//			try {
//				Date validUntilDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getValidUntil());
//				validUntil = validUntilDate.getTime();
//				System.out.println("Timestamp in milliseconds: " + validUntil);
//			} catch (ParseException e) {
//				System.err.println("Error parsing the date string: " + e.getMessage());
//			}
//			object.setValiduntil("/Date(" + validUntil + ")/");
//		} else {
//			object.setValiduntil(null);
//		}

		if (obj.has("Validuntil") && !obj.isNull("Validuntil")) {
			object.setValiduntil(obj.getString("Validuntil"));
		} else {

			object.setValiduntil(null);
		}
		object.setWTaxCode(obj.has("WTaxCode") ? obj.getString("WTaxCode") : "");
		object.setSubsind(obj.has("Subsind") ? obj.getString("Subsind") : "");
		object.setMaineconomicact(obj.has("maineconomicact") ? obj.getString("maineconomicact") : "");
		object.setMinorityindic(obj.has("Minorityindic") ? obj.getString("Minorityindic") : "");
		object.setPrevacctno(obj.has("Prevacctno") ? obj.getString("Prevacctno") : "");
		object.setGroupingkey1(obj.has("Groupingkey1") ? obj.getString("Groupingkey1") : "");

		object.setGroupingkey2(obj.has("Groupingkey2") ? obj.getString("Groupingkey2") : "");
		object.setRecipienttype(obj.has("Recipienttype") ? obj.getString("Recipienttype") : "");
		object.setExmptauthority(obj.has("Exmptauthority") ? obj.getString("Exmptauthority") : "");
		object.setCountryForWT(obj.has("CountryForWT") ? obj.getString("CountryForWT") : "");
		object.setPmtadvbyEDI(obj.has("PmtadvbyEDI") ? obj.getBoolean("PmtadvbyEDI") : false);
		object.setReleasegroup(obj.has("Releasegroup") ? obj.getString("Releasegroup") : "");
		object.setClerksfax(obj.has("Clerksfax") ? obj.getString("Clerksfax") : "");
		object.setClrksinternet(obj.has("Clrksinternet") ? obj.getString("Clrksinternet") : "");
		object.setCrmemoterms(obj.has("Crmemoterms") ? obj.getString("Crmemoterms") : "");
		object.setActivityCode(obj.has("ActivityCode") ? obj.getString("ActivityCode") : "");
		object.setDistrType(obj.has("DistrType") ? obj.getString("DistrType") : "");
		object.setAcctstatement(obj.has("Acctstatement") ? obj.getString("Acctstatement") : "");

		long timestampOfExemptFromDate = 0;
//		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getCertificationDate())) {
//			try {
//				Date exemptFromDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getCertificationDate());
//				timestampOfExemptFromDate = exemptFromDate.getTime();
//				System.out.println("Timestamp in milliseconds: " + timestampOfExemptFromDate);
//			} catch (ParseException e) {
//				System.err.println("Error parsing the date string: " + e.getMessage());
//			}
//			object.setCertifictnDate("/Date(" + timestampOfExemptFromDate + ")/");
//		} else {
//			object.setCertifictnDate(null);
//		}

		if (obj.has("CertifictnDate") && !obj.isNull("CertifictnDate")) {
			object.setCertifictnDate(obj.getString("CertifictnDate"));
		} else {

			object.setCertifictnDate(null);
		}
		object.setTolerancegrp(obj.has("Tolerancegrp") ? obj.getString("Tolerancegrp") : "");
		object.setPersonnelNo(obj.has("PersonnelNo") ? obj.getString("PersonnelNo") : "00000000");
		object.setCoCddelblock(obj.has("CoCddelblock") ? obj.getBoolean("CoCddelblock") : false);
		object.setActclktelno(obj.has("Actclktelno") ? obj.getString("Actclktelno") : "");
		object.setPrepaymentRelevant(obj.has("PrepaymentRelevant") ? obj.getString("PrepaymentRelevant") : "");
		object.setAssignmTestGroup(obj.has("AssignmTestGroup") ? obj.getString("AssignmTestGroup") : "");
		object.setPurposeCompleteFlag(obj.has("PurposeCompleteFlag") ? obj.getString("PurposeCompleteFlag") : "");

		List<CPIDunningDataDto> cpiDunningDataDto = new ArrayList<>();
//		if (!HelperClass.isEmpty(companyCodeDto.getBpCorrespondance().getDunnProcedure())) {
//			CPIDunningDataDto dunningDataObject = new CPIDunningDataDto();
//			dunningDataObject.setChangeIndObject("U");
//			dunningDataObject.setVendor("");
//			dunningDataObject
//					.setCompanyCode(companyCodeDto.getCompanyCode() == null ? "" : companyCodeDto.getCompanyCode());
//			dunningDataObject.setDunningArea("");
//			dunningDataObject.setDunnProcedure(companyCodeDto.getBpCorrespondance().getDunnProcedure() == null ? ""
//					: companyCodeDto.getBpCorrespondance().getDunnProcedure());
//			dunningDataObject.setDunnBlock(companyCodeDto.getBpCorrespondance().getDunningBlock() == null ? ""
//					: companyCodeDto.getBpCorrespondance().getDunningBlock());
//			long lastDunned = 0;
//			if (!HelperClass.isEmpty(companyCodeDto.getBpCorrespondance().getLastDunned())) {
//				try {
//					Date lastDunnedDate = sdf.parse(companyCodeDto.getBpCorrespondance().getLastDunned());
//					lastDunned = lastDunnedDate.getTime();
//					System.out.println("Timestamp in milliseconds: " + lastDunned);
//				} catch (ParseException e) {
//					System.err.println("Error parsing the date string: " + e.getMessage());
//				}
//				dunningDataObject.setLastDunned("/Date(" + lastDunned + ")/");
//			} else {
//				dunningDataObject.setLastDunned(null);
//			}
//			dunningDataObject.setDunningLevel(companyCodeDto.getBpCorrespondance().getDunningLevel() == null ? "0"
//					: companyCodeDto.getBpCorrespondance().getDunningLevel().toString());
//			dunningDataObject.setDunnrecipient(companyCodeDto.getBpCorrespondance().getDunnRecepient() == null ? ""
//					: companyCodeDto.getBpCorrespondance().getDunnRecepient());
//			dunningDataObject.setLegdunnproc(null);
//			dunningDataObject.setDunningclerk(companyCodeDto.getBpCorrespondance().getDunningClerk() == null ? ""
//					: companyCodeDto.getBpCorrespondance().getDunningClerk());
//
//			cpiDunningDataDto.add(dunningDataObject);
//
//			object.setToDunningData(cpiDunningDataDto);
//		} else {
		object.setToDunningData(new ArrayList<>());
//		}
		object.setToWtax(new ArrayList<>());
//			if (!HelperClass.isEmpty(companyCodeDto.getBpWithholdingTax())) {
//				object.setToWtax(companyCodeDto.getBpWithholdingTax().stream()
//						.map(wTaxDto -> convertChange(wTaxDto,companyCodeDto)).collect(Collectors.toList()));
//			}
//			else {
//				object.setToWtax(new ArrayList<>());
//				}
		return object;
	}

	public CPICompanyDataDto convertChange(BPCompanyCodeInfoDto companyCodeDto) {
		CPICompanyDataDto object = new CPICompanyDataDto();
		object.setChangeIndObject("U");
		object.setVendor("");
		object.setCompanyCode(companyCodeDto.getCompanyCode() == null ? "" : companyCodeDto.getCompanyCode());
		object.setCocodepostblock(false);
		object.setCocdedeletionflag(false);
		object.setSortkey(companyCodeDto.getBpAccountingInformation().getSortKey() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getSortKey());
		object.setReconaccount(
				companyCodeDto.getBpAccountingInformation().getReconcilliationAccountInGeneralLedger() == null ? ""
						: companyCodeDto.getBpAccountingInformation().getReconcilliationAccountInGeneralLedger());
		object.setAuthorization(companyCodeDto.getBpAccountingInformation().getAuthorization() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getAuthorization());
		object.setInterestindic(companyCodeDto.getBpAccountingInformation().getInterestInd() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getInterestInd());
		object.setPaymentmethods(companyCodeDto.getBpPaymentTransaction().getPaymentMethods() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPaymentMethods());
		object.setClrgwithcust(false);
		object.setPaymentblock(companyCodeDto.getBpPaymentTransaction().getPaymentBlock() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPaymentBlock());
		object.setPaytTerms(companyCodeDto.getBpPaymentTransaction().getPaymentTerms() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPaymentTerms());
		object.setAcctvendor("");
		object.setClerkatvendor(companyCodeDto.getBpCorrespondance().getClerkAtVendor() == null ? ""
				: companyCodeDto.getBpCorrespondance().getClerkAtVendor());
		object.setAccountmemo(companyCodeDto.getBpCorrespondance().getAccountMemo() == null ? ""
				: companyCodeDto.getBpCorrespondance().getAccountMemo());
		object.setPlanninggroup("");
		object.setAcctgclerk(companyCodeDto.getBpCorrespondance().getAccountingClerk() == null ? ""
				: companyCodeDto.getBpCorrespondance().getAccountingClerk());
		object.setHeadoffice(companyCodeDto.getBpAccountingInformation().getHeadOffice() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getHeadOffice());
		object.setAlternatpayee(companyCodeDto.getBpPaymentTransaction().getAlternatePayee() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getAlternatePayee());
		long lastKey = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getLastKeyDate())) {
			try {
				Date lastKeyDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getLastKeyDate());
				lastKey = lastKeyDate.getTime();
				System.out.println("Timestamp in milliseconds: " + lastKey);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setLastkeydate("/Date(" + lastKey + ")/");
		} else {
			object.setLastkeydate(null);
		}
		object.setIntcalcfreq("00");
		long lastintcalc = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getLastInterestRun())) {
			try {
				Date intcalcDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getLastKeyDate());
				lastintcalc = intcalcDate.getTime();
				System.out.println("Timestamp in milliseconds: " + lastintcalc);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setLastintcalc("/Date(" + lastintcalc + ")/");
		} else {
			object.setLastintcalc(null);
		}
		object.setLocalprocess(companyCodeDto.getBpCorrespondance().getLocalProcess() == null ? false
				: companyCodeDto.getBpCorrespondance().getLocalProcess());
		object.setBexchlimit(companyCodeDto.getBpPaymentTransaction().getBExchLimit() == null ? "0.000"
				: companyCodeDto.getBpPaymentTransaction().getBExchLimit());
		object.setChkcashngtime(companyCodeDto.getBpPaymentTransaction().getChkCashingTime() == null ? "0"
				: companyCodeDto.getBpPaymentTransaction().getChkCashingTime());
		object.setChkdoubleinv(companyCodeDto.getBpPaymentTransaction().getChkDoubleInv() == null ? true
				: companyCodeDto.getBpPaymentTransaction().getChkDoubleInv());
		object.setTolerancegroup(companyCodeDto.getBpPaymentTransaction().getPayDataToleranceGroup() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPayDataToleranceGroup());
		object.setHouseBank(companyCodeDto.getBpPaymentTransaction().getHouseBank() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getHouseBank());
		object.setIndividualpmnt(companyCodeDto.getBpPaymentTransaction().getIndividualPermit() == null ? false
				: companyCodeDto.getBpPaymentTransaction().getIndividualPermit());
		object.setPmtmethsupl(companyCodeDto.getBpPaymentTransaction().getPmtmethsupl() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPmtmethsupl());
		object.setExemptionno("");
		long validUntil = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getValidUntil())) {
			try {
				Date validUntilDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getValidUntil());
				validUntil = validUntilDate.getTime();
				System.out.println("Timestamp in milliseconds: " + validUntil);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setValiduntil("/Date(" + validUntil + ")/");
		} else {
			object.setValiduntil(null);
		}
		object.setWTaxCode(companyCodeDto.getBpAccountingInformation().getWtaxCode() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getWtaxCode());
		object.setSubsind("");
		object.setMaineconomicact("0000");
		object.setMinorityindic(companyCodeDto.getBpAccountingInformation().getMinorityIndicator() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getMinorityIndicator());
		object.setPrevacctno(companyCodeDto.getBpAccountingInformation().getPrevAcctNo() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getPrevAcctNo());
		object.setGroupingkey1(companyCodeDto.getBpPaymentTransaction().getGroupingKey() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getGroupingKey());
		object.setGroupingkey2("");
		object.setPmtmethsupl("");
		object.setRecipienttype("");
		object.setExmptauthority(companyCodeDto.getBpAccountingInformation().getExemptionAuthority() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getExemptionAuthority());
		object.setCountryForWT("");
		object.setPmtadvbyEDI(companyCodeDto.getBpPaymentTransaction().getPmtAdvByEDI() == null ? false
				: companyCodeDto.getBpPaymentTransaction().getPmtAdvByEDI());
		object.setReleasegroup(companyCodeDto.getBpAccountingInformation().getReleaseGroup() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getReleaseGroup());
		object.setClerksfax(companyCodeDto.getBpCorrespondance().getClerkFax() == null ? ""
				: companyCodeDto.getBpCorrespondance().getClerkFax());
		object.setClrksinternet(companyCodeDto.getBpCorrespondance().getClerkInternet() == null ? ""
				: companyCodeDto.getBpCorrespondance().getClerkInternet());
		object.setCrmemoterms("");
		object.setActivityCode("");
		object.setDistrType("");
		object.setAcctstatement("");
		long timestampOfExemptFromDate = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getCertificationDate())) {
			try {
				Date exemptFromDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getCertificationDate());
				timestampOfExemptFromDate = exemptFromDate.getTime();
				System.out.println("Timestamp in milliseconds: " + timestampOfExemptFromDate);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setCertifictnDate("/Date(" + timestampOfExemptFromDate + ")/");
		} else {
			object.setCertifictnDate(null);
		}
		object.setTolerancegrp(
				companyCodeDto.getBpPaymentTransaction().getInvoiceVerificationToleranceGroup() == null ? ""
						: companyCodeDto.getBpPaymentTransaction().getInvoiceVerificationToleranceGroup());
		object.setPersonnelNo(companyCodeDto.getBpAccountingInformation().getPersonnelNumber() == null ? "00000000"
				: companyCodeDto.getBpAccountingInformation().getPersonnelNumber());
		object.setCoCddelblock(false);
		object.setActclktelno(companyCodeDto.getBpCorrespondance().getActingClerksTelephone() == null ? ""
				: companyCodeDto.getBpCorrespondance().getActingClerksTelephone());
		object.setPrepaymentRelevant(companyCodeDto.getBpPaymentTransaction().getPrePayment() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPrePayment());
		object.setAssignmTestGroup("");
		object.setPurposeCompleteFlag("");

		List<CPIDunningDataDto> cpiDunningDataDto = new ArrayList<>();
		if (!HelperClass.isEmpty(companyCodeDto.getBpCorrespondance().getDunnProcedure())) {
			CPIDunningDataDto dunningDataObject = new CPIDunningDataDto();
			dunningDataObject.setChangeIndObject("U");
			dunningDataObject.setVendor("");
			dunningDataObject
					.setCompanyCode(companyCodeDto.getCompanyCode() == null ? "" : companyCodeDto.getCompanyCode());
			dunningDataObject.setDunningArea("");
			dunningDataObject.setDunnProcedure(companyCodeDto.getBpCorrespondance().getDunnProcedure() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunnProcedure());
			dunningDataObject.setDunnBlock(companyCodeDto.getBpCorrespondance().getDunningBlock() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunningBlock());
			long lastDunned = 0;
			if (!HelperClass.isEmpty(companyCodeDto.getBpCorrespondance().getLastDunned())) {
				try {
					Date lastDunnedDate = sdf.parse(companyCodeDto.getBpCorrespondance().getLastDunned());
					lastDunned = lastDunnedDate.getTime();
					System.out.println("Timestamp in milliseconds: " + lastDunned);
				} catch (ParseException e) {
					System.err.println("Error parsing the date string: " + e.getMessage());
				}
				dunningDataObject.setLastDunned("/Date(" + lastDunned + ")/");
			} else {
				dunningDataObject.setLastDunned(null);
			}
			dunningDataObject.setDunningLevel(companyCodeDto.getBpCorrespondance().getDunningLevel() == null ? "0"
					: companyCodeDto.getBpCorrespondance().getDunningLevel().toString());
			dunningDataObject.setDunnrecipient(companyCodeDto.getBpCorrespondance().getDunnRecepient() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunnRecepient());
			dunningDataObject.setLegdunnproc(null);
			dunningDataObject.setDunningclerk(companyCodeDto.getBpCorrespondance().getDunningClerk() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunningClerk());

			cpiDunningDataDto.add(dunningDataObject);

			object.setToDunningData(cpiDunningDataDto);
		} else {
			object.setToDunningData(new ArrayList<>());
		}
		object.setToWtax(new ArrayList<>());
//			if (!HelperClass.isEmpty(companyCodeDto.getBpWithholdingTax())) {
//				object.setToWtax(companyCodeDto.getBpWithholdingTax().stream()
//						.map(wTaxDto -> convertChange(wTaxDto,companyCodeDto)).collect(Collectors.toList()));
//			}
//			else {
//				object.setToWtax(new ArrayList<>());
//				}
		return object;
	}

	public CPIDunningDataDto convertDunningData1() {
		CPIDunningDataDto object = new CPIDunningDataDto();
		object.setChangeIndObject("");
		object.setVendor("");
		object.setCompanyCode("");
		object.setDunningArea("");
		object.setDunnProcedure("");
		object.setDunnBlock("");
		object.setLastDunned("");
		object.setDunningLevel("");
		object.setDunnrecipient("");
		object.setLegdunnproc("");
		object.setDunningclerk("");
		return object;
	}

	public CPIwTaxDto convertChangeNew(BPWithholdingTaxDto withholdingTaxDto, BPCompanyCodeInfoDto companyCodeDto,
			boolean newCheck) {
		CPIwTaxDto object = new CPIwTaxDto();
		object.setChangeIndObject("I");

		object.setVendor("");
		object.setCompanyCode(companyCodeDto.getCompanyCode() == null ? "" : companyCodeDto.getCompanyCode());
		object.setCountry(companyCodeDto.getWhTaxCountry() == null ? "" : companyCodeDto.getWhTaxCountry());
		object.setWithhldtaxtype(
				withholdingTaxDto.getWithholdingTaxType() == null ? "" : withholdingTaxDto.getWithholdingTaxType());
		object.setSubjecttowtx(withholdingTaxDto.getLiable() == null ? false : withholdingTaxDto.getLiable());
		object.setRecipienttype(
				withholdingTaxDto.getRecipientType() == null ? "" : withholdingTaxDto.getRecipientType());
		object.setWtaxnumber(withholdingTaxDto.getWTaxId() == null ? "" : withholdingTaxDto.getWTaxId());
		object.setWtaxcode(
				withholdingTaxDto.getWithholdingTaxCode() == null ? "00" : withholdingTaxDto.getWithholdingTaxCode());

		object.setExemptionnumber(withholdingTaxDto.getExemptionNo() == null ? "" : withholdingTaxDto.getExemptionNo());
		object.setExemptionrate(
				withholdingTaxDto.getExemPercentage() == null ? "0.00" : withholdingTaxDto.getExemPercentage());
		long timestampOfExemptFromDate = 0;
		long timestampOfExemptToDate = 0;
		if (!HelperClass.isEmpty(withholdingTaxDto.getExemptFrom())) {
			try {
				Date exemptFromDate = sdf.parse(withholdingTaxDto.getExemptFrom());

				timestampOfExemptFromDate = exemptFromDate.getTime();

				System.out.println("Timestamp in milliseconds: " + timestampOfExemptFromDate);

			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setExemptfrom("/Date(" + timestampOfExemptFromDate + ")/");
		} else {
			object.setExemptfrom(null);
		}
		if (!HelperClass.isEmpty(withholdingTaxDto.getExemptTo())) {
			try {

				Date exemptToDate = sdf.parse(withholdingTaxDto.getExemptTo());

				timestampOfExemptToDate = exemptToDate.getTime();

				System.out.println("Timestamp in milliseconds: " + timestampOfExemptToDate);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setExemptTo("/Date(" + timestampOfExemptToDate + ")/");
		} else {
			object.setExemptTo(null);
		}
		object.setExemptionreas(withholdingTaxDto.getExemResn() == null ? "" : withholdingTaxDto.getExemResn());

		return object;
	}

	public CPIwTaxDto convertChangeOld(String bupaNo, BPWithholdingTaxDto withholdingTaxDto,
			BPCompanyCodeInfoDto companyCodeDto, boolean newCheck) {
		CPIwTaxDto object = new CPIwTaxDto();
		object.setChangeIndObject("U");
		object.setVendor(bupaNo == null ? "" : bupaNo);
		object.setCompanyCode(companyCodeDto.getCompanyCode() == null ? "" : companyCodeDto.getCompanyCode());
		object.setCountry(companyCodeDto.getWhTaxCountry() == null ? "" : companyCodeDto.getWhTaxCountry());
		object.setWithhldtaxtype(
				withholdingTaxDto.getWithholdingTaxType() == null ? "" : withholdingTaxDto.getWithholdingTaxType());
		object.setSubjecttowtx(withholdingTaxDto.getLiable() == null ? false : withholdingTaxDto.getLiable());
		object.setRecipienttype(
				withholdingTaxDto.getRecipientType() == null ? "" : withholdingTaxDto.getRecipientType());
		object.setWtaxnumber(withholdingTaxDto.getWTaxId() == null ? "" : withholdingTaxDto.getWTaxId());
		object.setWtaxcode(
				withholdingTaxDto.getWithholdingTaxCode() == null ? "00" : withholdingTaxDto.getWithholdingTaxCode());

		object.setExemptionnumber(withholdingTaxDto.getExemptionNo() == null ? "" : withholdingTaxDto.getExemptionNo());
		object.setExemptionrate(
				withholdingTaxDto.getExemPercentage() == null ? "0.00" : withholdingTaxDto.getExemPercentage());
		long timestampOfExemptFromDate = 0;
		long timestampOfExemptToDate = 0;
		if (!HelperClass.isEmpty(withholdingTaxDto.getExemptFrom())) {
			try {
				Date exemptFromDate = sdf.parse(withholdingTaxDto.getExemptFrom());

				timestampOfExemptFromDate = exemptFromDate.getTime();

				System.out.println("Timestamp in milliseconds: " + timestampOfExemptFromDate);

			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setExemptfrom("/Date(" + timestampOfExemptFromDate + ")/");
		} else {
			object.setExemptfrom(null);
		}
		if (!HelperClass.isEmpty(withholdingTaxDto.getExemptTo())) {
			try {

				Date exemptToDate = sdf.parse(withholdingTaxDto.getExemptTo());

				timestampOfExemptToDate = exemptToDate.getTime();

				System.out.println("Timestamp in milliseconds: " + timestampOfExemptToDate);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setExemptTo("/Date(" + timestampOfExemptToDate + ")/");
		} else {
			object.setExemptTo(null);
		}
		object.setExemptionreas(withholdingTaxDto.getExemResn() == null ? "" : withholdingTaxDto.getExemResn());

		return object;
	}

	private CPIContactDto convertChange(BPContactInformationDto bpContactDto, String bupaNo) {
		CPIContactDto contactDto = new CPIContactDto();
		contactDto.setChangeIndObject("U");
		contactDto.setVendor(bupaNo == null ? "" : bupaNo);
//		contactDto.setContactPerson("");
		contactDto.setContactPerson(bpContactDto.getContactPerson()==null?"":bpContactDto.getContactPerson());

		contactDto.setDepartment(bpContactDto.getDepartment() == null ? "" : bpContactDto.getDepartment());
//			contactDto.setDepartment("0001");
		contactDto.setHighLevelPerson("0000000000");
		contactDto.setFunction(bpContactDto.getContactFunction() == null ? "" : bpContactDto.getContactFunction());
//			contactDto.setFunction("");
		contactDto.setAuthority("");
		contactDto.setVIP("1");
		contactDto.setGender("");
		contactDto.setRepresentno("0000000000");
		contactDto.setCallfrequency("");
		contactDto.setBuyinghabits("");
		contactDto.setNotes("");
		contactDto.setMaritalStat("0");
		contactDto.setTitle(bpContactDto.getFormOfAddress() == null ? "" : bpContactDto.getFormOfAddress());
		contactDto.setLastname(bpContactDto.getLastName() == null ? "" : bpContactDto.getLastName());
		contactDto.setFirstname(bpContactDto.getFirstName() == null ? "" : bpContactDto.getFirstName());
		contactDto.setNameatBirth("");
		contactDto.setFamilynameSecond("");
		contactDto.setCompletename("");
		contactDto.setAcademicTitle("");
		contactDto.setAcadtitlesecond("");
		contactDto.setPrefix("");
		contactDto.setPrefixSecond("");
		contactDto.setNameSupplement("");
		contactDto.setNickname("");
		contactDto.setFormatname("");
		contactDto.setFormatcountry("");

//		if (!HelperClass.isEmpty(bpContactDto.getTelephone())) {
			List<CPIContactPhoneDto> cpiContactPhoneDtos = new ArrayList<>();
			cpiContactPhoneDtos.add(changeContactPhone(bpContactDto));
			contactDto.setToContactPhone(cpiContactPhoneDtos);
//		} else {
//			contactDto.setToContactPhone(new ArrayList<>());
//		}

//		if (!HelperClass.isEmpty(bpContactDto.getUserEmail())) {
			List<CPIContactEmailDto> cpiContactEmailDtos = new ArrayList<>();
			cpiContactEmailDtos.add(changeContactEmail(bpContactDto));
			contactDto.setToContactEmail(cpiContactEmailDtos);
//		} else {
//			contactDto.setToContactEmail(new ArrayList<>());
//		}
		return contactDto;
	}

	public CPIContactPhoneDto changeContactPhone(BPContactInformationDto bpContactDto) {
		CPIContactPhoneDto contactPhoneDto = new CPIContactPhoneDto();
		contactPhoneDto.setChangeIndObject("U");
		contactPhoneDto.setVendor("");
		contactPhoneDto.setAddrnumber("");
		if (!HelperClass.isEmpty(bpContactDto.getTelephoneCode())) {
		contactPhoneDto.setCountry(bpContactDto.getTelephoneCode());
		}
		else {
			contactPhoneDto.setCountry("");
		}
		contactPhoneDto.setStdNo(true);
		if (!HelperClass.isEmpty(bpContactDto.getTelephone())) {
		contactPhoneDto.setTelephone( bpContactDto.getTelephone());
		}
		else {
			contactPhoneDto.setTelephone("");
		}
		contactPhoneDto.setExtension("");
		contactPhoneDto.setTelNo("");
		if (!HelperClass.isEmpty(bpContactDto.getTelephone())) {
		contactPhoneDto.setCallerNo(bpContactDto.getTelephone());
		}
		else {
			contactPhoneDto.setCallerNo(bpContactDto.getTelephone());
		}
		contactPhoneDto.setStdRecip("");
		contactPhoneDto.setR3User("1");
		return contactPhoneDto;
	}

	public CPIContactEmailDto changeContactEmail(BPContactInformationDto bpContactDto) {
		CPIContactEmailDto contactEmailDto = new CPIContactEmailDto();
		contactEmailDto.setChangeIndObject("U");
		contactEmailDto.setVendor("");
		contactEmailDto.setAddrnumber("");
		contactEmailDto.setStdndardNo(true);
		if (!HelperClass.isEmpty(bpContactDto.getUserEmail())) {
		contactEmailDto.setEMail( bpContactDto.getUserEmail());
		}
		else {
			contactEmailDto.setEMail("");
		}
		contactEmailDto.setEmailSrch("");
		return contactEmailDto;
	}

	private CPIContactDto convertChangeContact2(org.json.JSONObject obj) {
		CPIContactDto contactDto = new CPIContactDto();
		contactDto.setChangeIndObject("U");
		contactDto.setVendor(obj.has("Vendor") ? obj.getString("Vendor") : "");
		contactDto.setContactPerson(obj.has("ContactPerson") ? obj.getString("ContactPerson") : "");

		contactDto.setDepartment(obj.has("Department") ? obj.getString("Department") : "");
		contactDto.setHighLevelPerson(obj.has("HighLevelPerson") ? obj.getString("HighLevelPerson") : "");
		contactDto.setFunction(obj.has("Function") ? obj.getString("Function") : "");
		contactDto.setAuthority(obj.has("Authority") ? obj.getString("Authority") : "");
		contactDto.setVIP(obj.has("VIP") ? obj.getString("VIP") : "");
		contactDto.setGender(obj.has("Gender") ? obj.getString("Gender") : "");
		contactDto.setRepresentno(obj.has("Representno") ? obj.getString("Representno") : "");
		contactDto.setCallfrequency(obj.has("Callfrequency") ? obj.getString("Callfrequency") : "");
		contactDto.setBuyinghabits(obj.has("Buyinghabits") ? obj.getString("Buyinghabits") : "");
		contactDto.setNotes(obj.has("Notes") ? obj.getString("Notes") : "");
		contactDto.setMaritalStat(obj.has("MaritalStat") ? obj.getString("MaritalStat") : "");
		contactDto.setTitle(obj.has("Title") ? obj.getString("Title") : "");
		contactDto.setLastname(obj.has("Lastname") ? obj.getString("Lastname") : "");
		contactDto.setFirstname(obj.has("Firstname") ? obj.getString("Firstname") : "");
		contactDto.setNameatBirth(obj.has("NameatBirth") ? obj.getString("NameatBirth") : "");
		contactDto.setFamilynameSecond(obj.has("familynameSecond") ? obj.getString("familynameSecond") : "");
		contactDto.setCompletename(obj.has("Completename") ? obj.getString("Completename") : "");
		contactDto.setAcademicTitle(obj.has("AcademicTitle") ? obj.getString("AcademicTitle") : "");
		contactDto.setAcadtitlesecond(obj.has("Acadtitlesecond") ? obj.getString("Acadtitlesecond") : "");
		contactDto.setPrefix(obj.has("Prefix") ? obj.getString("Prefix") : "");
		contactDto.setPrefixSecond(obj.has("prefixSecond") ? obj.getString("prefixSecond") : "");
		contactDto.setNameSupplement(obj.has("NameSupplement") ? obj.getString("NameSupplement") : "");
		contactDto.setNickname(obj.has("Nickname") ? obj.getString("Nickname") : "");
		contactDto.setFormatname(obj.has("Formatname") ? obj.getString("Formatname") : "");
		contactDto.setFormatcountry(obj.has("Formatcountry") ? obj.getString("Formatcountry") : "");
		return contactDto;
	}

	private CPIContactDto convertChangeNew(BPContactInformationDto bpContactDto, String bupaNo) {
		CPIContactDto contactDto = new CPIContactDto();
		contactDto.setChangeIndObject("I");
		contactDto.setVendor(bupaNo == null ? "" : bupaNo);
//		contactDto.setContactPerson("");
		contactDto.setContactPerson(bpContactDto.getContactPerson()==null?"":bpContactDto.getContactPerson());

		contactDto.setDepartment(bpContactDto.getDepartment() == null ? "" : bpContactDto.getDepartment());
//			contactDto.setDepartment("0001");
		contactDto.setHighLevelPerson("0000000000");
		contactDto.setFunction(bpContactDto.getContactFunction() == null ? "" : bpContactDto.getContactFunction());
//			contactDto.setFunction("");
		contactDto.setAuthority("");
		contactDto.setVIP("1");
		contactDto.setGender("");
		contactDto.setRepresentno("0000000000");
		contactDto.setCallfrequency("");
		contactDto.setBuyinghabits("");
		contactDto.setNotes("");
		contactDto.setMaritalStat("0");
		contactDto.setTitle(bpContactDto.getFormOfAddress() == null ? "" : bpContactDto.getFormOfAddress());
		contactDto.setLastname(bpContactDto.getLastName() == null ? "" : bpContactDto.getLastName());
		contactDto.setFirstname(bpContactDto.getFirstName() == null ? "" : bpContactDto.getFirstName());
		contactDto.setNameatBirth("");
		contactDto.setFamilynameSecond("");
		contactDto.setCompletename("");
		contactDto.setAcademicTitle("");
		contactDto.setAcadtitlesecond("");
		contactDto.setPrefix("");
		contactDto.setPrefixSecond("");
		contactDto.setNameSupplement("");
		contactDto.setNickname("");
		contactDto.setFormatname("");
		contactDto.setFormatcountry("");
		if(!HelperClass.isEmpty(bpContactDto.getTelephone())) {
			List<CPIContactPhoneDto> cpiContactPhoneDtos = new ArrayList<>();
			cpiContactPhoneDtos.add(convertContactPhone(bpContactDto));
			contactDto.setToContactPhone(cpiContactPhoneDtos);
		} else {
			contactDto.setToContactPhone(new ArrayList<>());
		}

		if(!HelperClass.isEmpty(bpContactDto.getUserEmail())) {
			List<CPIContactEmailDto> cpiContactEmailDtos = new ArrayList<>();
			cpiContactEmailDtos.add(convertContactEmail(bpContactDto));
			contactDto.setToContactEmail(cpiContactEmailDtos);
		} else {
			contactDto.setToContactEmail(new ArrayList<>());
		}
		return contactDto;
	}

	public CPIClassificationDto changeConvert(BPVendorClassificationEntityDto classificationEntityDto) {
		CPIClassificationDto object = new CPIClassificationDto();
		object.setChangeIndObject("U");
		object.setVendor("");
		object.setClassnum(classificationEntityDto.getClassnum() == null ? "" : classificationEntityDto.getClassnum());
//	        object.setClassnum("SBA_SMALL_BUS_ADM");
		object.setClasstype("010");
		object.setObject("");
		object.setObjecttable("");
		object.setKeydate(null);
		object.setDescription(
				classificationEntityDto.getDescription() == null ? "" : classificationEntityDto.getDescription());
		object.setStatus("");
		object.setChangenumber("");
		object.setStdClass("");
		object.setFlag(false);
		object.setObjectGuid("000000000000000000");
		if (!HelperClass.isEmpty(classificationEntityDto.getBpVendorClassificationAttribute())) {
			object.setToClassificationItem(classificationEntityDto.getBpVendorClassificationAttribute().stream()
					.map(vendorClassificationAttributeDto -> changeConvert(vendorClassificationAttributeDto))
					.collect(Collectors.toList()));
		} else if (HelperClass.isEmpty(classificationEntityDto.getBpVendorClassificationAttribute())) {
			object.setToClassificationItem(new ArrayList<>());
		}
		return object;

	}

	public CPIClassificationItemDto changeConvert(BPVendorClassificationAttributeDto classificationAttributeDto) {
		CPIClassificationItemDto object = new CPIClassificationItemDto();
		object.setChangeIndObject("U");
		object.setVendor("");
//	        object.setCharact("CERTIFICATION_TYPE");
		object.setCharact(
				classificationAttributeDto.getCharact() == null ? "" : classificationAttributeDto.getCharact());
		object.setValuChar("");
//	        object.setValueChar("STATE");
		object.setInherited("");
		object.setInstance("");
		object.setValueNeutral(classificationAttributeDto.getValueNeutral() == null ? ""
				: classificationAttributeDto.getValueNeutral());
//	        object.setCharactDescr("Certification Type");
		object.setCharactDescr(classificationAttributeDto.getCharactDescr() == null ? ""
				: classificationAttributeDto.getCharactDescr());
		object.setValueCharLong("");
		object.setValueNeutralLong("");
		return object;
	}

	public CPIPurchaseOrgDataDto convertChange(BPPurchasingOrgDetailDto bpPurchasingOrgDetailDto) {
		CPIPurchaseOrgDataDto object = new CPIPurchaseOrgDataDto();
		object.setChangeIndObject("U");
		object.setVendor("");
		object.setAllvendor("");
		object.setPurchasingOrg(
				bpPurchasingOrgDetailDto.getPurchasingOrg() == null ? "" : bpPurchasingOrgDetailDto.getPurchasingOrg());
//			object.setPurchasingOrg("1234");

		object.setPurblockPOrg(false);
		object.setDelflagPOrg(false);
		object.setABCindicator("");
		object.setOrdercurrency(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getOrderCurrency() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getOrderCurrency());
		object.setSalesperson(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSalesPerson() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSalesPerson());
		object.setTelephone(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTelephone() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTelephone());
		object.setMinimumvalue(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue() == null ? "0.00"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue());
		object.setPaytTerms(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTermsOfPayment() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTermsOfPayment());
		object.setIncoterms(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms());
		object.setIncoterms2(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms2() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms2());
		object.setGRBasedIV(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getGrBasedInvVerify() == null ? true
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getGrBasedInvVerify());
		object.setAcknowlReqd(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAcknowledgementReqd() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAcknowledgementReqd());
		object.setSchemaGrpVndr(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSchemaGroupVendor() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSchemaGroupVendor());
		object.setAutomaticPO(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutomaticPurchaseOrder() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutomaticPurchaseOrder());
		object.setModeOfTrBorder(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getModeOfTransportBorder() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getModeOfTransportBorder());
		object.setCustomsoffice("");
		object.setPrDateCat("");
		object.setPurchGroup(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPurchasingGroup() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPurchasingGroup());
		object.setSubseqsett(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubsequentSettlement() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubsequentSettlement());
		object.setBvolcompag(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getBVolComp() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getBVolComp());
		object.setERS(false);
		object.setPlDelivTime(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime() == null ? "0"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime());
		object.setPlanningcal("");
		object.setPlanningcycle("");
		object.setPOentryvend("");
		object.setPricemkgvnd(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPriceMarkingAgreed() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPriceMarkingAgreed());
		object.setRackjobbing("");
		object.setSSindexactive(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubSeqSettIndex() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubSeqSettIndex());
		object.setPricedetermin(false);
		object.setQualiffDKd("");
		object.setDocumentIndex(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getDocIndexActive() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getDocIndexActive());
		object.setSortcriterion(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSortCriterion() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSortCriterion());
		object.setConfControl(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getConfirmationControl() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getConfirmationControl());
		object.setRndingProfile("");
		object.setUoMGroup("");
		object.setVenServLevl(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getServLevel() == null ? "0.0"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getServLevel());
		object.setLBprofile("");
		object.setAutGRSetRet(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutoEvalGRSetMtRet() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutoEvalGRSetMtRet());
		object.setAccwvendor(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAccWithVendor() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAccWithVendor());
		object.setPROACTcontrolprof(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getProActControlProf() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getProActControlProf());
		object.setAgencybusiness(false);
		object.setRevaluation(false);
		object.setShippingCond(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getShippingConditions() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getShippingConditions());
		object.setSrvBasedInvVer(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSrvBasedInvVar() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSrvBasedInvVar());
		if (!HelperClass.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData())) {
			List<CPIPlantDto> cpiPlantDto = new ArrayList<>();
			for (BPPurchaseOrgAdditionalDataDto purchaseOrgAdditionalDto : bpPurchasingOrgDetailDto
					.getBpPurchaseOrgAdditionalData()) {
				if (!HelperClass.isEmpty(purchaseOrgAdditionalDto.getPlant()))
					cpiPlantDto
							.add(convertChange(purchaseOrgAdditionalDto, bpPurchasingOrgDetailDto.getPurchasingOrg()));
			}
			object.setToPlant(cpiPlantDto);
		} else if (HelperClass.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData())) {
			object.setToPlant(new ArrayList<>());
		}
		if (bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			List<CPIOrderingAddressDto> cpiOrderingAddressDto = new ArrayList<>();
			cpiOrderingAddressDto.add(convert(bpPurchasingOrgDetailDto.getBpBusinessPartnerOrderingAddress(), null));
			object.setToOderingAddress(cpiOrderingAddressDto);
		} else if (!bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			object.setToOderingAddress(new ArrayList<>());
		}

		if (bpPurchasingOrgDetailDto.getRemittanceAddressCheck()) {
			List<CPIInvoicePartyDto> cpiInvoicePartyDto = new ArrayList<>();
			cpiInvoicePartyDto.add(convert(bpPurchasingOrgDetailDto.getBpBusinessPartnerRemittanceAddress(), null));
			object.setToInvoiceParty(cpiInvoicePartyDto);
		} else if (!bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			object.setToInvoiceParty(new ArrayList<>());
		}
		return object;
	}

	public CPIPurchaseOrgDataDto convertChange1(BPRequestGeneralDataDto bpRequestGeneralDataDto,
			BPPurchasingOrgDetailDto bpPurchasingOrgDetailDto, JSONArray toPlantResults, String payTerms) {
		CPIPurchaseOrgDataDto object = new CPIPurchaseOrgDataDto();
		object.setChangeIndObject("U");
		object.setVendor(bpRequestGeneralDataDto.getBupaNo() == null ? "" : bpRequestGeneralDataDto.getBupaNo());
		object.setAllvendor("");
		object.setPurchasingOrg(
				bpPurchasingOrgDetailDto.getPurchasingOrg() == null ? "" : bpPurchasingOrgDetailDto.getPurchasingOrg());
//			object.setPurchasingOrg("1234");

		object.setPurblockPOrg(false);
		object.setDelflagPOrg(false);
		object.setABCindicator(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAbcIndicator() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAbcIndicator());
		object.setOrdercurrency(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getOrderCurrency() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getOrderCurrency());
		object.setSalesperson(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSalesPerson() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSalesPerson());
		object.setTelephone(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTelephone() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTelephone());
		object.setMinimumvalue(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue() == null ? "0.00"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue());
		object.setPaytTerms(payTerms == null ? "" : payTerms);
		object.setIncoterms(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms());
		object.setIncoterms2(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms2() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms2());
		object.setGRBasedIV(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getGrBasedInvVerify() == null ? true
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getGrBasedInvVerify());
		object.setAcknowlReqd(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAcknowledgementReqd() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAcknowledgementReqd());
		object.setSchemaGrpVndr(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSchemaGroupVendor() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSchemaGroupVendor());
		object.setAutomaticPO(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutomaticPurchaseOrder() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutomaticPurchaseOrder());
		object.setModeOfTrBorder(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getModeOfTransportBorder() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getModeOfTransportBorder());
		object.setCustomsoffice("");
		object.setPrDateCat("");
		object.setPurchGroup(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPurchasingGroup() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPurchasingGroup());
		object.setSubseqsett(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubsequentSettlement() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubsequentSettlement());
		object.setBvolcompag(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getBVolComp() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getBVolComp());
		object.setERS(false);
		object.setPlDelivTime(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime() == null ? "0"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime());
		object.setPlanningcal("");
		object.setPlanningcycle("");
		object.setPOentryvend("");
		object.setPricemkgvnd(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPriceMarkingAgreed() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPriceMarkingAgreed());
		object.setRackjobbing("");
		object.setSSindexactive(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubSeqSettIndex() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubSeqSettIndex());
		object.setPricedetermin(false);
		object.setQualiffDKd("");
		object.setDocumentIndex(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getDocIndexActive() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getDocIndexActive());
		object.setSortcriterion(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSortCriterion() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSortCriterion());
		object.setConfControl(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getConfirmationControl() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getConfirmationControl());
		object.setRndingProfile(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getRoundingProfile() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getRoundingProfile());
		object.setUoMGroup("");
		object.setVenServLevl(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getServLevel() == null ? "0.0"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getServLevel());
		object.setLBprofile("");
		object.setAutGRSetRet(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutoEvalGRSetMtRet() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutoEvalGRSetMtRet());
		object.setAccwvendor(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAccWithVendor() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAccWithVendor());
		object.setPROACTcontrolprof(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getProActControlProf() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getProActControlProf());
		object.setAgencybusiness(
				bpPurchasingOrgDetailDto.getBpPurchaseOrg().getRelevantForAgencyBusiness() == null ? false
						: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getRelevantForAgencyBusiness());
		object.setRevaluation(false);
		object.setShippingCond(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getShippingConditions() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getShippingConditions());
		object.setSrvBasedInvVer(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSrvBasedInvVar() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSrvBasedInvVar());
		int i = 0;
//		if (!HelperClass.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData())) {
//			List<CPIPlantDto> cpiPlantDto = new ArrayList<>();
//			for (BPPurchaseOrgAdditionalDataDto purchaseOrgAdditionalDto : bpPurchasingOrgDetailDto
//					.getBpPurchaseOrgAdditionalData()) {
//				if (!HelperClass.isEmpty(purchaseOrgAdditionalDto.getPlant())) {
//					cpiPlantDto
//							.add(convertChange(purchaseOrgAdditionalDto, bpPurchasingOrgDetailDto.getPurchasingOrg()));
//				}
//			}
//			object.setToPlant(cpiPlantDto);
//		} else if (HelperClass.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData())) {
//			object.setToPlant(new ArrayList<>());
//		}
		if ((!HelperClass.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData()))
				&& (!toPlantResults.isEmpty())) {
			List<CPIPlantDto> cpiPlantDto = new ArrayList<>();
			for (BPPurchaseOrgAdditionalDataDto purchaseOrgAdditionalDto : bpPurchasingOrgDetailDto
					.getBpPurchaseOrgAdditionalData()) {
				if (!HelperClass.isEmpty(purchaseOrgAdditionalDto.getPlant())) {
					org.json.JSONObject toPlant = toPlantResults.getJSONObject(i);
					cpiPlantDto.add(convertChange1(bpRequestGeneralDataDto, purchaseOrgAdditionalDto,
							toPlant.getString("PaytTerms"), bpPurchasingOrgDetailDto.getPurchasingOrg()));
					i++;
				}
			}
			object.setToPlant(cpiPlantDto);
		} else if (HelperClass.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData())) {
			object.setToPlant(new ArrayList<>());
		}
		if (bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			List<CPIOrderingAddressDto> cpiOrderingAddressDto = new ArrayList<>();
			cpiOrderingAddressDto.add(
					convert(bpPurchasingOrgDetailDto.getBpBusinessPartnerOrderingAddress(), bpRequestGeneralDataDto));
			object.setToOderingAddress(cpiOrderingAddressDto);
		} else if (!bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			object.setToOderingAddress(new ArrayList<>());
		}

		if (bpPurchasingOrgDetailDto.getRemittanceAddressCheck()) {
			List<CPIInvoicePartyDto> cpiInvoicePartyDto = new ArrayList<>();
			cpiInvoicePartyDto.add(
					convert(bpPurchasingOrgDetailDto.getBpBusinessPartnerRemittanceAddress(), bpRequestGeneralDataDto));
			object.setToInvoiceParty(cpiInvoicePartyDto);
		} else if (!bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			object.setToInvoiceParty(new ArrayList<>());
		}
		return object;
	}

	public CPIPurchaseOrgDataDto convertChange2(BPRequestGeneralDataDto bpRequestGeneralDataDto,
			BPPurchasingOrgDetailDto bpPurchasingOrgDetailDto, org.json.JSONObject obj, JSONArray toPlantResults,
			String payTerms) {
		CPIPurchaseOrgDataDto object = new CPIPurchaseOrgDataDto();
		object.setChangeIndObject("U");
		object.setVendor(obj.has("Vendor") ? obj.getString("Vendor") : "");
		object.setAllvendor(obj.has("Allvendor") ? obj.getString("Allvendor") : "");
		object.setPurchasingOrg(obj.has("PurchasingOrg") ? obj.getString("PurchasingOrg") : "");
//			object.setPurchasingOrg("1234");

		object.setPurblockPOrg(obj.has("PurblockPOrg") ? obj.getBoolean("PurblockPOrg") : false);
		object.setDelflagPOrg(obj.has("DelflagPOrg") ? obj.getBoolean("DelflagPOrg") : false);
		object.setABCindicator(obj.has("ABCindicator") ? obj.getString("ABCindicator") : "");
		object.setOrdercurrency(obj.has("Ordercurrency") ? obj.getString("Ordercurrency") : "");
		object.setSalesperson(obj.has("Salesperson") ? obj.getString("Salesperson") : "");
		object.setTelephone(obj.has("Telephone") ? obj.getString("Telephone") : "");
		object.setMinimumvalue(obj.has("Minimumvalue") ? obj.getString("Minimumvalue") : "0.00");
		if (HelperClass.isEmpty(payTerms)) {
			object.setPaytTerms(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTermsOfPayment() == null ? ""
					: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTermsOfPayment());
		} else {
			object.setPaytTerms(payTerms);
		}
		object.setIncoterms(obj.has("Incoterms") ? obj.getString("Incoterms") : "");
		object.setIncoterms2(obj.has("Incoterms2") ? obj.getString("Incoterms2") : "");

		object.setGRBasedIV(obj.has("GRBasedIV") ? obj.getBoolean("GRBasedIV") : true);
		object.setAcknowlReqd(obj.has("AcknowlReqd") ? obj.getBoolean("AcknowlReqd") : false);
		object.setSchemaGrpVndr(obj.has("SchemaGrpVndr") ? obj.getString("SchemaGrpVndr") : "");
		object.setAutomaticPO(obj.has("AutomaticPO") ? obj.getBoolean("AutomaticPO") : false);
		object.setModeOfTrBorder(obj.has("ModeOfTrBorder") ? obj.getString("ModeOfTrBorder") : "");

		object.setCustomsoffice(obj.has("Customsoffice") ? obj.getString("Customsoffice") : "");
		object.setPrDateCat(obj.has("PrDateCat") ? obj.getString("PrDateCat") : "");
		object.setPurchGroup(obj.has("PurchGroup") ? obj.getString("PurchGroup") : "");
		object.setSubseqsett(obj.has("Subseqsett") ? obj.getBoolean("Subseqsett") : false);
		object.setBvolcompag(obj.has("Bvolcompag") ? obj.getBoolean("Bvolcompag") : false);
		object.setERS(obj.has("ERS") ? obj.getBoolean("ERS") : false);
		object.setPlDelivTime(obj.has("PlannedDelivTime") ? obj.getString("PlannedDelivTime") : "0");

		object.setPlanningcal(obj.has("Planningcal") ? obj.getString("Planningcal") : "");
		object.setPlanningcycle(obj.has("Planningcycle") ? obj.getString("Planningcycle") : "");
		object.setPOentryvend(obj.has("POentryvend") ? obj.getString("POentryvend") : "");
		object.setPricemkgvnd(obj.has("Pricemkgvnd") ? obj.getString("Pricemkgvnd") : "");
		object.setRackjobbing(obj.has("Rackjobbing") ? obj.getString("Rackjobbing") : "");
		object.setSSindexactive(obj.has("SSindexactive") ? obj.getBoolean("SSindexactive") : false);
		object.setPricedetermin(obj.has("Pricedetermin") ? obj.getBoolean("Pricedetermin") : false);
		object.setQualiffDKd(obj.has("QualiffDKd") ? obj.getString("QualiffDKd") : "");
		object.setDocumentIndex(obj.has("DocumentIndex") ? obj.getBoolean("DocumentIndex") : false);
		object.setSortcriterion(obj.has("Sortcriterion") ? obj.getString("Sortcriterion") : "");
		object.setConfControl(obj.has("ConfControl") ? obj.getString("ConfControl") : "");
		object.setRndingProfile(obj.has("RndingProfile") ? obj.getString("RndingProfile") : "");
		object.setUoMGroup(obj.has("UoMGroup") ? obj.getString("UoMGroup") : "");
		object.setVenServLevl(obj.has("VenServLevl") ? obj.getString("VenServLevl") : "0.0");
		object.setLBprofile(obj.has("LBprofile") ? obj.getString("LBprofile") : "");
		object.setAutGRSetRet(obj.has("AutGRSetRet") ? obj.getBoolean("AutGRSetRet") : false);
		object.setAccwvendor(obj.has("Accwvendor") ? obj.getString("Accwvendor") : "");
		object.setPROACTcontrolprof(obj.has("PROACTcontrolprof") ? obj.getString("PROACTcontrolprof") : "");
		object.setAgencybusiness(obj.has("Agencybusiness") ? obj.getBoolean("Agencybusiness") : false);
		object.setRevaluation(obj.has("Revaluation") ? obj.getBoolean("Revaluation") : false);
		object.setShippingCond(obj.has("ShippingCond") ? obj.getString("ShippingCond") : "");
		object.setSrvBasedInvVer(obj.has("SrvBasedInvVer") ? obj.getBoolean("SrvBasedInvVer") : false);

		int i = 0;
//		if (!HelperClass.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData())) {
//			List<CPIPlantDto> cpiPlantDto = new ArrayList<>();
//			for (BPPurchaseOrgAdditionalDataDto purchaseOrgAdditionalDto : bpPurchasingOrgDetailDto
//					.getBpPurchaseOrgAdditionalData()) {
//				if (!HelperClass.isEmpty(purchaseOrgAdditionalDto.getPlant())) {
//					cpiPlantDto
//							.add(convertChange(purchaseOrgAdditionalDto, bpPurchasingOrgDetailDto.getPurchasingOrg()));
//				}
//			}
//			object.setToPlant(cpiPlantDto);
//		} else if (HelperClass.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData())) {
//			object.setToPlant(new ArrayList<>());
//		}
		if ((!HelperClass.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData()))
				&& (!toPlantResults.isEmpty())) {
			List<CPIPlantDto> cpiPlantDto = new ArrayList<>();
			for (BPPurchaseOrgAdditionalDataDto purchaseOrgAdditionalDto : bpPurchasingOrgDetailDto
					.getBpPurchaseOrgAdditionalData()) {
				if (!HelperClass.isEmpty(purchaseOrgAdditionalDto.getPlant())) {
					org.json.JSONObject toPlant = toPlantResults.getJSONObject(i);
					cpiPlantDto.add(convertChange2(purchaseOrgAdditionalDto, toPlant, null,
							bpPurchasingOrgDetailDto.getPurchasingOrg()));
					i++;
				}
			}
			object.setToPlant(cpiPlantDto);
		} else if (HelperClass.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData())) {
			object.setToPlant(new ArrayList<>());
		}
		if (bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			List<CPIOrderingAddressDto> cpiOrderingAddressDto = new ArrayList<>();
			cpiOrderingAddressDto.add(
					convert(bpPurchasingOrgDetailDto.getBpBusinessPartnerOrderingAddress(), bpRequestGeneralDataDto));
			object.setToOderingAddress(cpiOrderingAddressDto);
		} else if (!bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			object.setToOderingAddress(new ArrayList<>());
		}

		if (bpPurchasingOrgDetailDto.getRemittanceAddressCheck()) {
			List<CPIInvoicePartyDto> cpiInvoicePartyDto = new ArrayList<>();
			cpiInvoicePartyDto.add(
					convert(bpPurchasingOrgDetailDto.getBpBusinessPartnerRemittanceAddress(), bpRequestGeneralDataDto));
			object.setToInvoiceParty(cpiInvoicePartyDto);
		} else if (!bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			object.setToInvoiceParty(new ArrayList<>());
		}
		return object;
	}
//		

	private CPIPhoneDto changeConvert(BPTelephoneDto telephoneDto) {
		CPIPhoneDto cpiPhoneDto = new CPIPhoneDto();

		cpiPhoneDto.setChangeIndObject("U");
		cpiPhoneDto.setVendor("");
		cpiPhoneDto.setCountry(telephoneDto.getCountry() == null ? "" : telephoneDto.getCountry());
		cpiPhoneDto.setISOcode(telephoneDto.getCountry() == null ? "" : telephoneDto.getCountry());
		cpiPhoneDto.setStandardNo(telephoneDto.isStandardNumber() == true ? true : telephoneDto.isStandardNumber());
		cpiPhoneDto.setTelephone(telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone());
		cpiPhoneDto.setExtension(telephoneDto.getExtension() == null ? "" : telephoneDto.getExtension());
		cpiPhoneDto.setTelephoneno((telephoneDto.getExtension() == null ? "" : telephoneDto.getExtension())
				+ (telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone()));
		cpiPhoneDto.setCallernumber(telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone());
		cpiPhoneDto.setSMSEnab("");
		cpiPhoneDto.setMobilephone("1");
		cpiPhoneDto.setHomeaddress(true);
		cpiPhoneDto.setSequenceNumber("1");
		cpiPhoneDto.setError(false);
		cpiPhoneDto.setDonotuse(telephoneDto.isDoNotUse());
		cpiPhoneDto.setValidFrom("");
		cpiPhoneDto.setValidTo("");

		return cpiPhoneDto;

	}

	private CPIPhoneDto changeNewConvert(BPTelephoneDto telephoneDto) {
		CPIPhoneDto cpiPhoneDto = new CPIPhoneDto();

		cpiPhoneDto.setChangeIndObject("I");
		cpiPhoneDto.setVendor("");
		cpiPhoneDto.setCountry(telephoneDto.getCountry() == null ? "" : telephoneDto.getCountry());
		cpiPhoneDto.setISOcode(telephoneDto.getCountry() == null ? "" : telephoneDto.getCountry());
		cpiPhoneDto.setStandardNo(telephoneDto.isStandardNumber() == true ? true : telephoneDto.isStandardNumber());
		cpiPhoneDto.setTelephone(telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone());
		cpiPhoneDto.setExtension(telephoneDto.getExtension() == null ? "" : telephoneDto.getExtension());
		cpiPhoneDto.setTelephoneno((telephoneDto.getExtension() == null ? "" : telephoneDto.getExtension())
				+ (telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone()));
		cpiPhoneDto.setCallernumber(telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone());
		cpiPhoneDto.setSMSEnab("");
		cpiPhoneDto.setMobilephone("1");
		cpiPhoneDto.setHomeaddress(false);
		cpiPhoneDto.setSequenceNumber("1");
		cpiPhoneDto.setError(false);
		cpiPhoneDto.setDonotuse(telephoneDto.isDoNotUse());
		cpiPhoneDto.setValidFrom("");
		cpiPhoneDto.setValidTo("");

		return cpiPhoneDto;

	}

	private CPIPhoneDto changeConvert(BPMobilePhoneDto telephoneDto) {
		CPIPhoneDto cpiPhoneDto = new CPIPhoneDto();

		cpiPhoneDto.setChangeIndObject("U");
		cpiPhoneDto.setVendor("");
		cpiPhoneDto.setCountry(telephoneDto.getCountry() == null ? "" : telephoneDto.getCountry());
		cpiPhoneDto.setISOcode(telephoneDto.getCountry() == null ? "" : telephoneDto.getCountry());
		cpiPhoneDto.setStandardNo(telephoneDto.isStandardNumber() == true ? true : telephoneDto.isStandardNumber());
		cpiPhoneDto.setTelephone(telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone());
		cpiPhoneDto.setExtension(telephoneDto.getExtension() == null ? "" : telephoneDto.getExtension());
		cpiPhoneDto.setTelephoneno((telephoneDto.getExtension() == null ? "" : telephoneDto.getExtension())
				+ (telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone()));
		cpiPhoneDto.setCallernumber(telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone());
		cpiPhoneDto.setSMSEnab("");
		cpiPhoneDto.setMobilephone("3");
		cpiPhoneDto.setHomeaddress(true);
		cpiPhoneDto.setSequenceNumber("1");
		cpiPhoneDto.setError(false);
		cpiPhoneDto.setDonotuse(telephoneDto.isDoNotUse());
		cpiPhoneDto.setValidFrom("");
		cpiPhoneDto.setValidTo("");

		return cpiPhoneDto;

	}

	private CPIPhoneDto changeNewConvert(BPMobilePhoneDto telephoneDto) {
		CPIPhoneDto cpiPhoneDto = new CPIPhoneDto();

		cpiPhoneDto.setChangeIndObject("I");
		cpiPhoneDto.setVendor("");
		cpiPhoneDto.setCountry(telephoneDto.getCountry() == null ? "" : telephoneDto.getCountry());
		cpiPhoneDto.setISOcode(telephoneDto.getCountry() == null ? "" : telephoneDto.getCountry());
		cpiPhoneDto.setStandardNo(telephoneDto.isStandardNumber() == true ? true : telephoneDto.isStandardNumber());
		cpiPhoneDto.setTelephone(telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone());
		cpiPhoneDto.setExtension(telephoneDto.getExtension() == null ? "" : telephoneDto.getExtension());
		cpiPhoneDto.setTelephoneno((telephoneDto.getExtension() == null ? "" : telephoneDto.getExtension())
				+ (telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone()));
		cpiPhoneDto.setCallernumber(telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone());
		cpiPhoneDto.setSMSEnab("");
		cpiPhoneDto.setMobilephone("3");
		cpiPhoneDto.setHomeaddress(true);
		cpiPhoneDto.setSequenceNumber("1");
		cpiPhoneDto.setError(false);
		cpiPhoneDto.setDonotuse(telephoneDto.isDoNotUse());
		cpiPhoneDto.setValidFrom("");
		cpiPhoneDto.setValidTo("");

		return cpiPhoneDto;

	}

	private CPIPhoneDto changeConvertEmail2(org.json.JSONObject obj) {
		CPIPhoneDto cpiPhoneDto = new CPIPhoneDto();

		cpiPhoneDto.setChangeIndObject("U");
		cpiPhoneDto.setVendor(obj.has("Vendor") ? obj.getString("Vendor") : "");
		cpiPhoneDto.setCountry(obj.has("Country") ? obj.getString("Country") : "");
		cpiPhoneDto.setISOcode(obj.has("ISOcode") ? obj.getString("ISOcode") : "");
		cpiPhoneDto.setStandardNo(obj.has("StandardNo") ? obj.getBoolean("StandardNo") : true);
		cpiPhoneDto.setTelephone(obj.has("Telephone") ? obj.getString("Telephone") : "");
		cpiPhoneDto.setExtension(obj.has("Extension") ? obj.getString("Extension") : "");
		cpiPhoneDto.setTelephoneno((obj.has("Extension") ? obj.getString("Extension") : "")
				+ (obj.has("Telephone") ? obj.getString("Telephone") : ""));
		cpiPhoneDto.setCallernumber(obj.has("Callernumber") ? obj.getString("Callernumber") : "");
		cpiPhoneDto.setSMSEnab(obj.has("SMSEnab") ? obj.getString("SMSEnab") : "");
		cpiPhoneDto.setMobilephone(obj.has("Mobilephone") ? obj.getString("Mobilephone") : "");
		cpiPhoneDto.setHomeaddress(obj.has("Homeaddress") ? obj.getBoolean("Homeaddress") : true);
		cpiPhoneDto.setSequenceNumber(obj.has("SequenceNumber") ? obj.getString("SequenceNumber") : "001");
		cpiPhoneDto.setError(obj.has("Error") ? obj.getBoolean("Error") : false);
		cpiPhoneDto.setDonotuse(obj.has("Donotuse") ? obj.getBoolean("Donotuse") : false);
		cpiPhoneDto.setValidFrom(obj.has("ValidFrom") ? obj.getString("ValidFrom") : "");
		cpiPhoneDto.setValidTo(obj.has("ValidTo") ? obj.getString("ValidTo") : "");
		return cpiPhoneDto;

	}

	private CPIEmailDto changeConvert(BPEmailDto bpEmailDto) {
		CPIEmailDto cpiEmailDto = new CPIEmailDto();
		cpiEmailDto.setChangeIndObject("U");
		cpiEmailDto.setVendor("");
		cpiEmailDto.setStandardNo(bpEmailDto.getStandardNumber() == null ? true : bpEmailDto.getStandardNumber());
		cpiEmailDto.setEMailAddress(bpEmailDto.getEmailAddress() == null ? "" : bpEmailDto.getEmailAddress());
//			cpiEmailDto.setEMailAddress("vaibhav.anand@incture.com");
		cpiEmailDto.setEMailAddressSearch("");
		cpiEmailDto.setStdrecipient(false);
		cpiEmailDto.setSAPConnection(false);
		cpiEmailDto.setCoding("");
		cpiEmailDto.setTNEF(false);
		cpiEmailDto.setHomeaddress(true);
		cpiEmailDto.setSequenceNumber("001");
		cpiEmailDto.setError(false);
		cpiEmailDto.setDonotuse(HelperClass.isEmpty(bpEmailDto.isDoNotUse()) == true ? false : bpEmailDto.isDoNotUse());
		cpiEmailDto.setValidFrom("");
		cpiEmailDto.setValidTo("");
		return cpiEmailDto;
	}

	private CPIEmailDto changeConvert2(org.json.JSONObject obj) {
		CPIEmailDto cpiEmailDto = new CPIEmailDto();
		cpiEmailDto.setChangeIndObject("U");
		cpiEmailDto.setVendor(obj.has("Vendor") ? obj.getString("Vendor") : "");
		cpiEmailDto.setStandardNo(obj.has("StandardNo") ? obj.getBoolean("StandardNo") : true);
		cpiEmailDto.setEMailAddress(obj.has("EMailAddress") ? obj.getString("EMailAddress") : "");
		cpiEmailDto.setEMailAddressSearch(obj.has("EMailAddressSearch") ? obj.getString("EMailAddressSearch") : "");
		cpiEmailDto.setStdrecipient(obj.has("Stdrecipient") ? obj.getBoolean("Stdrecipient") : false);
		cpiEmailDto.setSAPConnection(obj.has("SAPConnection") ? obj.getBoolean("SAPConnection") : false);
		cpiEmailDto.setCoding(obj.has("Coding") ? obj.getString("Coding") : "");
		cpiEmailDto.setTNEF(obj.has("TNEF") ? obj.getBoolean("TNEF") : false);
		cpiEmailDto.setHomeaddress(obj.has("Homeaddress") ? obj.getBoolean("Homeaddress") : true);
		cpiEmailDto.setSequenceNumber(obj.has("SequenceNumber") ? obj.getString("SequenceNumber") : "001");
		cpiEmailDto.setError(obj.has("Error") ? obj.getBoolean("Error") : false);
		cpiEmailDto.setDonotuse(false);
		cpiEmailDto.setValidFrom("");
		cpiEmailDto.setValidTo("");
		return cpiEmailDto;
	}

	private CPIBankDto changeConvert(BPBankInformationDto bpBankInformationDto) {
		CPIBankDto cpiBankDto = new CPIBankDto();
		cpiBankDto.setChangeIndObject("U");
		cpiBankDto.setVendor("");
//			cpiBankDto.setBanknumber(bpBankInformationDto.getBankCountry() == null ? "" : bpBankInformationDto.getBankCountry());
//			cpiBankDto.setBankCountry(bpBankInformationDto.getBankKey() == null ? "" : bpBankInformationDto.getBankKey());
		cpiBankDto.setBanknumber(bpBankInformationDto.getBankKey() == null ? "" : bpBankInformationDto.getBankKey());
		cpiBankDto.setBankCountry(
				bpBankInformationDto.getBankCountry() == null ? "" : bpBankInformationDto.getBankCountry());
		cpiBankDto.setBankAccount(
				bpBankInformationDto.getBankAccountNo() == null ? "" : bpBankInformationDto.getBankAccountNo());
		cpiBankDto.setControlkey(
				bpBankInformationDto.getControlKey() == null ? "" : bpBankInformationDto.getControlKey());
		cpiBankDto.setPartBankType(bpBankInformationDto.getBankT() == null ? "" : bpBankInformationDto.getBankT());
		cpiBankDto.setCollectauthor(bpBankInformationDto.getDebitAuthorization() == null ? false
				: bpBankInformationDto.getDebitAuthorization());
		cpiBankDto.setReference(
				bpBankInformationDto.getReferenceDetails() == null ? "" : bpBankInformationDto.getReferenceDetails());
		cpiBankDto.setAccountholder(
				bpBankInformationDto.getAccHolderName() == null ? "" : bpBankInformationDto.getAccHolderName());
		cpiBankDto.setIBAN(bpBankInformationDto.getIban() == null ? "" : bpBankInformationDto.getIban());
		cpiBankDto.setIBANvalidfrom(null);
		cpiBankDto.setSwiftCode(bpBankInformationDto.getSwift() == null ? "" : bpBankInformationDto.getSwift());

		return cpiBankDto;
	}

	private CPIBankDto deleteBank(BPBankInformationDto bpBankInformationDto) {
		CPIBankDto cpiBankDto = new CPIBankDto();
		cpiBankDto.setChangeIndObject("D");
		cpiBankDto.setVendor("");
//			cpiBankDto.setBanknumber(bpBankInformationDto.getBankCountry() == null ? "" : bpBankInformationDto.getBankCountry());
//			cpiBankDto.setBankCountry(bpBankInformationDto.getBankKey() == null ? "" : bpBankInformationDto.getBankKey());
		cpiBankDto.setBanknumber(bpBankInformationDto.getBankKey() == null ? "" : bpBankInformationDto.getBankKey());
		cpiBankDto.setBankCountry(
				bpBankInformationDto.getBankCountry() == null ? "" : bpBankInformationDto.getBankCountry());
		cpiBankDto.setBankAccount(
				bpBankInformationDto.getBankAccountNo() == null ? "" : bpBankInformationDto.getBankAccountNo());
		cpiBankDto.setControlkey(
				bpBankInformationDto.getControlKey() == null ? "" : bpBankInformationDto.getControlKey());
		cpiBankDto.setPartBankType(bpBankInformationDto.getBankT() == null ? "" : bpBankInformationDto.getBankT());
		cpiBankDto.setCollectauthor(bpBankInformationDto.getDebitAuthorization() == null ? false
				: bpBankInformationDto.getDebitAuthorization());
		cpiBankDto.setReference(
				bpBankInformationDto.getReferenceDetails() == null ? "" : bpBankInformationDto.getReferenceDetails());
		cpiBankDto.setAccountholder(
				bpBankInformationDto.getAccHolderName() == null ? "" : bpBankInformationDto.getAccHolderName());
		cpiBankDto.setIBAN(bpBankInformationDto.getIban() == null ? "" : bpBankInformationDto.getIban());
		cpiBankDto.setIBANvalidfrom(null);
		cpiBankDto.setSwiftCode(bpBankInformationDto.getSwift() == null ? "" : bpBankInformationDto.getSwift());

		return cpiBankDto;
	}

	private CPIBankDto changeConvertNew(BPBankInformationDto bpBankInformationDto) {
		CPIBankDto cpiBankDto = new CPIBankDto();
		cpiBankDto.setChangeIndObject("I");
		cpiBankDto.setVendor("");
//			cpiBankDto.setBanknumber(bpBankInformationDto.getBankCountry() == null ? "" : bpBankInformationDto.getBankCountry());
//			cpiBankDto.setBankCountry(bpBankInformationDto.getBankKey() == null ? "" : bpBankInformationDto.getBankKey());
		cpiBankDto.setBanknumber(bpBankInformationDto.getBankKey() == null ? "" : bpBankInformationDto.getBankKey());
		cpiBankDto.setBankCountry(
				bpBankInformationDto.getBankCountry() == null ? "" : bpBankInformationDto.getBankCountry());
		cpiBankDto.setBankAccount(
				bpBankInformationDto.getBankAccountNo() == null ? "" : bpBankInformationDto.getBankAccountNo());
		cpiBankDto.setControlkey(
				bpBankInformationDto.getControlKey() == null ? "" : bpBankInformationDto.getControlKey());
		cpiBankDto.setPartBankType(bpBankInformationDto.getBankT() == null ? "" : bpBankInformationDto.getBankT());
		cpiBankDto.setCollectauthor(bpBankInformationDto.getDebitAuthorization() == null ? false
				: bpBankInformationDto.getDebitAuthorization());
		cpiBankDto.setReference(
				bpBankInformationDto.getReferenceDetails() == null ? "" : bpBankInformationDto.getReferenceDetails());
		cpiBankDto.setAccountholder(
				bpBankInformationDto.getAccHolderName() == null ? "" : bpBankInformationDto.getAccHolderName());
		cpiBankDto.setIBAN(bpBankInformationDto.getIban() == null ? "" : bpBankInformationDto.getIban());
		cpiBankDto.setIBANvalidfrom(null);
		cpiBankDto.setSwiftCode(bpBankInformationDto.getSwift() == null ? "" : bpBankInformationDto.getSwift());

		return cpiBankDto;
	}

	private CPIFaxDto changeConvert(BPFaxInfoDto faxDto) {
		 
		CPIFaxDto cpiFaxDto = new CPIFaxDto();
		cpiFaxDto.setChangeIndObject("U");
		cpiFaxDto.setVendor("");
		cpiFaxDto.setCountry(faxDto.getCountry() == null ? "" : faxDto.getCountry());
		cpiFaxDto.setISOcode(faxDto.getCountry() == null ? "" : faxDto.getCountry());
		cpiFaxDto.setStandardNo(false);
		cpiFaxDto.setFax(faxDto.getFax() == null ? "" : faxDto.getFax());
		cpiFaxDto.setExtension(faxDto.getExtension() == null ? "" : faxDto.getExtension());
		cpiFaxDto.setFaxnumber((faxDto.getExtension() == null ? "" : faxDto.getExtension())
				+ (faxDto.getFax() == null ? "" : faxDto.getFax()));
		cpiFaxDto.setSendernumber(faxDto.getFax() == null ? "" : faxDto.getFax());
		cpiFaxDto.setFaxgroup("");
		cpiFaxDto.setStdrecipient(false);
		cpiFaxDto.setSAPConnection(false);
		cpiFaxDto.setHomeaddress(true);
		cpiFaxDto.setSequenceNumber("001");
		cpiFaxDto.setError(false);
		cpiFaxDto.setDonotuse(false);
		cpiFaxDto.setValidFrom("");
		cpiFaxDto.setValidTo("");
		return cpiFaxDto;
	}

	private CPIFaxDto changeConvertFax2(org.json.JSONObject obj) {
		 
		CPIFaxDto cpiFaxDto = new CPIFaxDto();
		cpiFaxDto.setChangeIndObject("U");
		cpiFaxDto.setVendor(obj.has("Vendor") ? obj.getString("Vendor") : "");
		cpiFaxDto.setCountry(obj.has("Country") ? obj.getString("Country") : "");
		cpiFaxDto.setISOcode(obj.has("ISOcode") ? obj.getString("Vendor") : "");
		cpiFaxDto.setStandardNo(obj.has("StandardNo") ? obj.getBoolean("StandardNo") : true);
		cpiFaxDto.setFax(obj.has("FAX") ? obj.getString("FAX") : "");
		cpiFaxDto.setExtension(obj.has("Extension") ? obj.getString("Extension") : "");
		cpiFaxDto.setFaxnumber(obj.has("Faxnumber") ? obj.getString("Faxnumber") : "");
		cpiFaxDto.setSendernumber(obj.has("Sendernumber") ? obj.getString("Sendernumber") : "");
		cpiFaxDto.setFaxgroup(obj.has("Faxgroup") ? obj.getString("Faxgroup") : "");
		cpiFaxDto.setStdrecipient(obj.has("Stdrecipient") ? obj.getBoolean("Stdrecipient") : false);
		cpiFaxDto.setSAPConnection(obj.has("SAPConnection") ? obj.getBoolean("SAPConnection") : false);
		cpiFaxDto.setHomeaddress(obj.has("Homeaddress") ? obj.getBoolean("Homeaddress") : true);
		cpiFaxDto.setSequenceNumber(obj.has("SequenceNumber") ? obj.getString("SequenceNumber") : "001");
		cpiFaxDto.setError(obj.has("Error") ? obj.getBoolean("Error") : false);
		cpiFaxDto.setDonotuse(obj.has("Donotuse") ? obj.getBoolean("Donotuse") : false);
		cpiFaxDto.setValidFrom(obj.has("ValidFrom") ? obj.getString("ValidFrom") : "");
		cpiFaxDto.setValidTo(obj.has("ValidTo") ? obj.getString("ValidTo") : "");
		return cpiFaxDto;
	}

	private CPIPlantDto changePlantConvert(BPPurchasingOrgDetailDto bpPurchasingOrgDetailDto) {
		CPIPlantDto cpiPlantDto = new CPIPlantDto();

		cpiPlantDto.setChangeIndObject("U");
		cpiPlantDto.setVendor("");
		cpiPlantDto.setVendorSubrange("");
		cpiPlantDto.setPlant(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getPlant() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getPlant());
		cpiPlantDto.setPurblockPOrg(false);
		cpiPlantDto.setDelflagPOrg(false);
		cpiPlantDto.setABCindicator(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAbcIndicator() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAbcIndicator());
		cpiPlantDto.setOrdercurrency(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getOrderCurrency() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getOrderCurrency());
		cpiPlantDto.setSalesperson(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSalesPerson() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSalesPerson());
		cpiPlantDto.setMinimumvalue(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue() == null ? "0.00"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue());
		cpiPlantDto.setPaytTerms(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTermsOfPayment() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTermsOfPayment());
		cpiPlantDto.setIncoterms(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms());
		cpiPlantDto.setIncoterms2("Free On Board");
		cpiPlantDto.setGRBasedIV(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getGrBasedInvVerify() == null ? true
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getGrBasedInvVerify());
		cpiPlantDto.setAcknowlReqd(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAcknowledgementReqd() == null ? true
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAcknowledgementReqd());
		cpiPlantDto.setSchemaGrpVndr(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSchemaGroupVendor() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSchemaGroupVendor());
		cpiPlantDto.setAutomaticPO(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getAutomaticPurchaseOrder() == null
						? false
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getAutomaticPurchaseOrder());
		cpiPlantDto
				.setModeOfTrBorder(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getModeOfTransportBorder() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getModeOfTransportBorder());
		cpiPlantDto.setCustomsoffice("");
		cpiPlantDto.setPrDateCat("");
		cpiPlantDto.setPurchGroup(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPurchasingGroup() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPurchasingGroup());
		cpiPlantDto.setSubseqsett(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubsequentSettlement() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubsequentSettlement());
		cpiPlantDto.setBvolcompag(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getBVolComp() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getBVolComp());
		cpiPlantDto.setERS(false);
		cpiPlantDto.setPlDelivTime(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime() == null ? "0"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime());
		cpiPlantDto.setPlanningcal("");
		cpiPlantDto.setPlanningcycle(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getPlanningCycle() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getPlanningCycle());
		cpiPlantDto.setPOentryvend("");
		cpiPlantDto.setPricemkgvnd(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPriceMarkingAgreed() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPriceMarkingAgreed());
		cpiPlantDto.setRackjobbing("");
		cpiPlantDto.setMRPController(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getMrpController() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getMrpController());
		cpiPlantDto.setConfControl(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getConfirmationControl() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getConfirmationControl());
		cpiPlantDto.setRndingProfile(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getRoundingProfile() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getRoundingProfile());
		cpiPlantDto.setUoMGroup(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getUnitofMeasureGroup() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getUnitofMeasureGroup());
		cpiPlantDto.setLBprofile("");
		cpiPlantDto.setAutGRSetRet(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutoEvalGRSetMtRet() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutoEvalGRSetMtRet());
		cpiPlantDto.setPROACTcontrolprof(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getProActControlProf() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getProActControlProf());
		cpiPlantDto.setRevaluation(false);
		cpiPlantDto.setSrvBasedInvVer(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSrvBasedInvVar() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSrvBasedInvVar());

		return cpiPlantDto;
	}

	private CPIPlantDto convertChange(BPPurchaseOrgAdditionalDataDto bpPurchaseOrgAdditionalDataDto,
			String purchasingOrg) {
		CPIPlantDto cpiPlantDto = new CPIPlantDto();

		cpiPlantDto.setChangeIndObject("U");
		cpiPlantDto.setVendor("");
		cpiPlantDto.setVendorSubrange("");
		cpiPlantDto.setPurchasingOrg(purchasingOrg == null ? "" : purchasingOrg);
		cpiPlantDto.setPlant(
				bpPurchaseOrgAdditionalDataDto.getPlant() == null ? "" : bpPurchaseOrgAdditionalDataDto.getPlant());
		cpiPlantDto.setPurblockPOrg(false);
		cpiPlantDto.setDelflagPOrg(false);
		cpiPlantDto.setABCindicator(bpPurchaseOrgAdditionalDataDto.getAbcIndicator() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getAbcIndicator());
		cpiPlantDto.setOrdercurrency(bpPurchaseOrgAdditionalDataDto.getOrderCurrency() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getOrderCurrency());
		cpiPlantDto.setSalesperson("");
		cpiPlantDto.setMinimumvalue(bpPurchaseOrgAdditionalDataDto.getMinOrderValue() == null ? "0.00"
				: bpPurchaseOrgAdditionalDataDto.getMinOrderValue());
		cpiPlantDto.setPaytTerms(bpPurchaseOrgAdditionalDataDto.getTermsOfPayment() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getTermsOfPayment());
		cpiPlantDto.setIncoterms(bpPurchaseOrgAdditionalDataDto.getIncoTerms() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getIncoTerms());
		cpiPlantDto.setIncoterms2(bpPurchaseOrgAdditionalDataDto.getIncoTerms2() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getIncoTerms2());
		cpiPlantDto.setGRBasedIV(bpPurchaseOrgAdditionalDataDto.getGrBasedInvVerify() == null ? true
				: bpPurchaseOrgAdditionalDataDto.getGrBasedInvVerify());
		cpiPlantDto.setAcknowlReqd(bpPurchaseOrgAdditionalDataDto.getAcknowledgementReqd() == null ? true
				: bpPurchaseOrgAdditionalDataDto.getAcknowledgementReqd());
		cpiPlantDto.setSchemaGrpVndr("");
		cpiPlantDto.setAutomaticPO(bpPurchaseOrgAdditionalDataDto.getAutomaticPurchaseOrder() == null ? false
				: bpPurchaseOrgAdditionalDataDto.getAutomaticPurchaseOrder());
		cpiPlantDto.setModeOfTrBorder("");
		cpiPlantDto.setCustomsoffice("");
		cpiPlantDto.setPrDateCat("");
		cpiPlantDto.setPurchGroup(bpPurchaseOrgAdditionalDataDto.getPurchasingGroup() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getPurchasingGroup());
		cpiPlantDto.setSubseqsett(false);
		cpiPlantDto.setBvolcompag(false);
		cpiPlantDto.setERS(false);
		cpiPlantDto.setPlDelivTime(bpPurchaseOrgAdditionalDataDto.getPlanneddelivtime() == null ? "0"
				: bpPurchaseOrgAdditionalDataDto.getPlanneddelivtime());
		cpiPlantDto.setPlanningcal("");
		cpiPlantDto.setPlanningcycle(bpPurchaseOrgAdditionalDataDto.getPlanningCycle() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getPlanningCycle());
		cpiPlantDto.setPOentryvend("");
		cpiPlantDto.setPricemkgvnd("");
		cpiPlantDto.setRackjobbing("");
		cpiPlantDto.setMRPController(bpPurchaseOrgAdditionalDataDto.getMrpController() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getMrpController());
		cpiPlantDto.setConfControl(bpPurchaseOrgAdditionalDataDto.getConfirmationControl() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getConfirmationControl());
		cpiPlantDto.setRndingProfile(bpPurchaseOrgAdditionalDataDto.getRoundingProfile() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getRoundingProfile());
		cpiPlantDto.setUoMGroup(bpPurchaseOrgAdditionalDataDto.getUnitofMeasureGroup() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getUnitofMeasureGroup());
		cpiPlantDto.setLBprofile("");
		cpiPlantDto.setAutGRSetRet(bpPurchaseOrgAdditionalDataDto.getAutoEvalGRSetMtRet() == null ? false
				: bpPurchaseOrgAdditionalDataDto.getAutoEvalGRSetMtRet());
		cpiPlantDto.setPROACTcontrolprof(bpPurchaseOrgAdditionalDataDto.getProActControlProf() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getProActControlProf());
		cpiPlantDto.setRevaluation(false);
		cpiPlantDto.setSrvBasedInvVer(bpPurchaseOrgAdditionalDataDto.getSrvBasedInvVar() == null ? false
				: bpPurchaseOrgAdditionalDataDto.getSrvBasedInvVar());

		return cpiPlantDto;
	}

	private CPIPlantDto convertChange1(BPRequestGeneralDataDto bpRequestGeneralDataDto,
			BPPurchaseOrgAdditionalDataDto bpPurchaseOrgAdditionalDataDto, String payTerms, String purchasingOrg) {
		CPIPlantDto cpiPlantDto = new CPIPlantDto();

		cpiPlantDto.setChangeIndObject("U");
		cpiPlantDto.setVendor(bpRequestGeneralDataDto.getBupaNo() == null ? "" : bpRequestGeneralDataDto.getBupaNo());
		cpiPlantDto.setVendorSubrange("");
		cpiPlantDto.setPurchasingOrg(purchasingOrg == null ? "" : purchasingOrg);
		cpiPlantDto.setPlant(
				bpPurchaseOrgAdditionalDataDto.getPlant() == null ? "" : bpPurchaseOrgAdditionalDataDto.getPlant());
		cpiPlantDto.setPurblockPOrg(false);
		cpiPlantDto.setDelflagPOrg(false);
		cpiPlantDto.setABCindicator(bpPurchaseOrgAdditionalDataDto.getAbcIndicator() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getAbcIndicator());
		cpiPlantDto.setOrdercurrency(bpPurchaseOrgAdditionalDataDto.getOrderCurrency() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getOrderCurrency());
		cpiPlantDto.setSalesperson("");
		cpiPlantDto.setMinimumvalue(bpPurchaseOrgAdditionalDataDto.getMinOrderValue() == null ? "0.00"
				: bpPurchaseOrgAdditionalDataDto.getMinOrderValue());
		cpiPlantDto.setPaytTerms(payTerms == null ? "" : payTerms);
		cpiPlantDto.setIncoterms(bpPurchaseOrgAdditionalDataDto.getIncoTerms() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getIncoTerms());
		cpiPlantDto.setIncoterms2(bpPurchaseOrgAdditionalDataDto.getIncoTerms2() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getIncoTerms2());
		cpiPlantDto.setGRBasedIV(bpPurchaseOrgAdditionalDataDto.getGrBasedInvVerify() == null ? true
				: bpPurchaseOrgAdditionalDataDto.getGrBasedInvVerify());
		cpiPlantDto.setAcknowlReqd(bpPurchaseOrgAdditionalDataDto.getAcknowledgementReqd() == null ? true
				: bpPurchaseOrgAdditionalDataDto.getAcknowledgementReqd());
		cpiPlantDto.setSchemaGrpVndr("");
		cpiPlantDto.setAutomaticPO(bpPurchaseOrgAdditionalDataDto.getAutomaticPurchaseOrder() == null ? false
				: bpPurchaseOrgAdditionalDataDto.getAutomaticPurchaseOrder());
		cpiPlantDto.setModeOfTrBorder("");
		cpiPlantDto.setCustomsoffice("");
		cpiPlantDto.setPrDateCat("");
		cpiPlantDto.setPurchGroup(bpPurchaseOrgAdditionalDataDto.getPurchasingGroup() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getPurchasingGroup());
		cpiPlantDto.setSubseqsett(false);
		cpiPlantDto.setBvolcompag(false);
		cpiPlantDto.setERS(false);
		cpiPlantDto.setPlDelivTime(bpPurchaseOrgAdditionalDataDto.getPlanneddelivtime() == null ? "0"
				: bpPurchaseOrgAdditionalDataDto.getPlanneddelivtime());
		cpiPlantDto.setPlanningcal("");
		cpiPlantDto.setPlanningcycle(bpPurchaseOrgAdditionalDataDto.getPlanningCycle() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getPlanningCycle());
		cpiPlantDto.setPOentryvend("");
		cpiPlantDto.setPricemkgvnd("");
		cpiPlantDto.setRackjobbing("");
		cpiPlantDto.setMRPController(bpPurchaseOrgAdditionalDataDto.getMrpController() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getMrpController());
		cpiPlantDto.setConfControl(bpPurchaseOrgAdditionalDataDto.getConfirmationControl() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getConfirmationControl());
		cpiPlantDto.setRndingProfile(bpPurchaseOrgAdditionalDataDto.getRoundingProfile() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getRoundingProfile());
		cpiPlantDto.setUoMGroup(bpPurchaseOrgAdditionalDataDto.getUnitofMeasureGroup() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getUnitofMeasureGroup());
		cpiPlantDto.setLBprofile("");
		cpiPlantDto.setAutGRSetRet(bpPurchaseOrgAdditionalDataDto.getAutoEvalGRSetMtRet() == null ? false
				: bpPurchaseOrgAdditionalDataDto.getAutoEvalGRSetMtRet());
		cpiPlantDto.setPROACTcontrolprof(bpPurchaseOrgAdditionalDataDto.getProActControlProf() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getProActControlProf());
		cpiPlantDto.setRevaluation(false);
		cpiPlantDto.setSrvBasedInvVer(bpPurchaseOrgAdditionalDataDto.getSrvBasedInvVar() == null ? false
				: bpPurchaseOrgAdditionalDataDto.getSrvBasedInvVar());

		return cpiPlantDto;
	}

	private CPIPlantDto convertChange2(BPPurchaseOrgAdditionalDataDto bpPurchaseOrgAdditionalDataDto,
			org.json.JSONObject obj, String payTerms, String purchasingOrg) {
		CPIPlantDto cpiPlantDto = new CPIPlantDto();

		cpiPlantDto.setChangeIndObject("U");
		cpiPlantDto.setVendor(obj.has("Vendor") ? obj.getString("Vendor") : "");
		cpiPlantDto.setVendorSubrange(obj.has("VendorSubrange") ? obj.getString("VendorSubrange") : "");
		cpiPlantDto.setPurchasingOrg(obj.has("PurchasingOrg") ? obj.getString("PurchasingOrg") : "");
		cpiPlantDto.setPlant(obj.has("Plant") ? obj.getString("Plant") : "");
		cpiPlantDto.setPurblockPOrg(obj.has("PurblockPOrg") ? obj.getBoolean("PurblockPOrg") : false);
		cpiPlantDto.setDelflagPOrg(obj.has("DelflagPOrg") ? obj.getBoolean("DelflagPOrg") : false);
		cpiPlantDto.setABCindicator(obj.has("ABCindicator") ? obj.getString("ABCindicator") : "");
		cpiPlantDto.setOrdercurrency(obj.has("Ordercurrency") ? obj.getString("Ordercurrency") : "");
		cpiPlantDto.setSalesperson(obj.has("Salesperson") ? obj.getString("Salesperson") : "");
		cpiPlantDto.setMinimumvalue(obj.has("Minimumvalue") ? obj.getString("Minimumvalue") : "0.00");
		if (!HelperClass.isEmpty(payTerms)) {
			cpiPlantDto.setPaytTerms(payTerms == null ? "" : payTerms);
		} else {
			cpiPlantDto.setPaytTerms(bpPurchaseOrgAdditionalDataDto.getTermsOfPayment() == null ? ""
					: bpPurchaseOrgAdditionalDataDto.getTermsOfPayment());
		}
		cpiPlantDto.setIncoterms(obj.has("Incoterms") ? obj.getString("Incoterms") : "");
		cpiPlantDto.setIncoterms2(obj.has("Incoterms2") ? obj.getString("Incoterms2") : "");
		cpiPlantDto.setGRBasedIV(obj.has("GRBasedIV") ? obj.getBoolean("GRBasedIV") : false);
		cpiPlantDto.setAcknowlReqd(obj.has("AcknowlReqd") ? obj.getBoolean("AcknowlReqd") : false);
		cpiPlantDto.setSchemaGrpVndr(obj.has("SchemaGrpVndr") ? obj.getString("SchemaGrpVndr") : "");
		cpiPlantDto.setAutomaticPO(obj.has("AutomaticPO") ? obj.getBoolean("AutomaticPO") : false);
		cpiPlantDto.setModeOfTrBorder(obj.has("ModeOfTrBorder") ? obj.getString("ModeOfTrBorder") : "");
		cpiPlantDto.setCustomsoffice(obj.has("Customsoffice") ? obj.getString("Customsoffice") : "");
		cpiPlantDto.setPrDateCat(obj.has("PrDateCat") ? obj.getString("PrDateCat") : "");
		cpiPlantDto.setPurchGroup(obj.has("PurchGroup") ? obj.getString("PurchGroup") : "");
		cpiPlantDto.setSubseqsett(obj.has("Subseqsett") ? obj.getBoolean("Subseqsett") : false);
		cpiPlantDto.setBvolcompag(obj.has("Bvolcompag") ? obj.getBoolean("Bvolcompag") : false);
		cpiPlantDto.setERS(obj.has("ERS") ? obj.getBoolean("ERS") : false);
		cpiPlantDto.setPlDelivTime(obj.has("PlDelivTime") ? obj.getString("PlDelivTime") : "0.00");
		cpiPlantDto.setPlanningcal(obj.has("Planningcal") ? obj.getString("Planningcal") : "");
		cpiPlantDto.setPlanningcycle(obj.has("Planningcycle") ? obj.getString("Planningcycle") : "");
		cpiPlantDto.setPOentryvend(obj.has("POentryvend") ? obj.getString("POentryvend") : "");
		cpiPlantDto.setPricemkgvnd(obj.has("Pricemkgvnd") ? obj.getString("Pricemkgvnd") : "");
		cpiPlantDto.setRackjobbing(obj.has("Rackjobbing") ? obj.getString("Rackjobbing") : "");
		cpiPlantDto.setMRPController(obj.has("MRPController") ? obj.getString("MRPController") : "");
		cpiPlantDto.setConfControl(obj.has("ConfControl") ? obj.getString("ConfControl") : "");
		cpiPlantDto.setRndingProfile(obj.has("RndingProfile") ? obj.getString("RndingProfile") : "");
		cpiPlantDto.setUoMGroup(obj.has("UoMGroup") ? obj.getString("UoMGroup") : "");
		cpiPlantDto.setLBprofile(obj.has("LBprofile") ? obj.getString("LBprofile") : "");
		cpiPlantDto.setAutGRSetRet(obj.has("AutGRSetRet") ? obj.getBoolean("AutGRSetRet") : false);
		cpiPlantDto.setPROACTcontrolprof(obj.has("PROACTcontrolprof") ? obj.getString("PROACTcontrolprof") : "");
		cpiPlantDto.setRevaluation(obj.has("Revaluation") ? obj.getBoolean("Revaluation") : false);
		cpiPlantDto.setSrvBasedInvVer(obj.has("SrvBasedInvVer") ? obj.getBoolean("SrvBasedInvVer") : false);

		return cpiPlantDto;
	}

	public BusinessPartnerResponse extendVendorInfo(BPCreationFromWorkflowRequest bpCreationFromWorkflowRequest)
			throws UnirestException, ClientProtocolException, IOException {
//	    	BPRequestGeneralDataDto bpRequestGeneralDataDto=getBPDetailsByRequestId(bpCreationFromWorkflowRequest.getRequestId());
//	    	CPIVendorDetailsDto detailsDto=convert(bpRequestGeneralDataDto);
		BPRequestGeneralDataDto bpRequestGeneralDataDto = bpDetailService
				.getBPDetailsByRequestId(bpCreationFromWorkflowRequest.getRequestId());

		CPIVendorDetailsDto detailsDto = convertExtend(bpRequestGeneralDataDto,
				bpCreationFromWorkflowRequest.isValidate());

		String accessToken, url = null;
		try {
			String destDetails = destinationUtil.readMdgDestination("mdg-vm-cpi", null, null);

			org.json.JSONObject resObj = new org.json.JSONObject(destDetails);
			log.info("Json object from destination :" + resObj);
			log.info("Client id: " + resObj.optJSONObject("destinationConfiguration").optString("clientId"));
			log.info("clientSecret : " + resObj.optJSONObject("destinationConfiguration").optString("clientSecret"));
			log.info("tokenServiceURL: "
					+ resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));
			log.info("url: " + resObj.optJSONObject("destinationConfiguration").optString("URL"));
			accessToken = getAccessToken(resObj.optJSONObject("destinationConfiguration").optString("clientId"),
					resObj.optJSONObject("destinationConfiguration").optString("clientSecret"),
					resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));

//		            url=resObj.optJSONObject("destinationConfiguration").optString("URL")+"/http/Vendor_Extend";
			if (profile.toUpperCase(Locale.ROOT).equalsIgnoreCase("PROD")) {
				url = resObj.optJSONObject("destinationConfiguration").optString("URL") + "/http/Vendor_Extend";
			} else {

				url = resObj.optJSONObject("destinationConfiguration").optString("URL")
						+ "/http/Viatris/CP_HanaDb/BusinessPatrner/Extend";
			}

//		            https://viatris-its-dev-092tl30u.it-cpi013-rt.cfapps.us21.hana.ondemand.com/http/Vendor_Create
//		            https://viatris-its-dev-092tl30u.it-cpi013-rt.cfapps.us21.hana.ondemand.com

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		BusinessPartnerResponse result = new BusinessPartnerResponse();
		String body = new ObjectMapper().writeValueAsString(detailsDto);
		System.out.println("**%% ODATA" + body);
//	        String urlValue = "https://inccpidev.it-cpi001-rt.cfapps.eu10.hana.ondemand.com/http/Vendor_CreateSap";
		System.out.println("*******");
//	        System.out.println(accessToken());
		System.out.println(accessToken);

		System.out.println("*******");
		HttpResponse<String> response = Unirest.post(url).header("authorization", "Bearer " + accessToken)
				.header("Content-Type", "application/json").header("Accept", "application/json").body(body).asString();
		int status = response.getStatus();
		System.out
				.println(status + "************************************************************************************"
						+ response + "  " + response.getBody());
		JsonObject object = new JsonParser().parse(response.getBody()).getAsJsonObject();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(response.getBody());
		JsonNode errorDetailsNode = rootNode.path("error").path("innererror").path("errordetails").path("errordetail");

		System.out.println(errorDetailsNode);
		System.out.println(errorDetailsNode.asText());
		if (rootNode.has("error")) {
			if ((!errorDetailsNode.isArray() || errorDetailsNode.size() == 0)) {
				System.out.println("inside empty");
				JsonNode message = rootNode.path("error").path("message").path("$");
				ObjectMapper mapper = new ObjectMapper();
				ObjectNode errorDetails = mapper.createObjectNode();
				ArrayNode errorDetailArray = JsonNodeFactory.instance.arrayNode();
				ObjectNode messageObject = mapper.createObjectNode();
				messageObject.put("message", message.asText());
				errorDetailArray.add(messageObject);
				errorDetails.set("errordetail", errorDetailArray);
				result.setMessage(errorDetails);
				if(bpRequestGeneralDataDto.getStatusId()<=1) {
					bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), draftStatusId);
					}
					else {
						bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), bpRequestGeneralDataDto.getStatusId());
					}
				return result;
			}

			else if (errorDetailsNode.isArray() && errorDetailsNode.size() != 0) {
				System.out.println("inside non empty");
//				JsonNode rootNode = objectMapper.readTree(response.getBody());
				JsonNode msgerrorDetailsNode = rootNode.path("error").path("innererror").path("errordetails");
				result.setMessage(msgerrorDetailsNode);
				if(bpRequestGeneralDataDto.getStatusId()<=1) {
					bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), draftStatusId);
					}
					else {
						bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), bpRequestGeneralDataDto.getStatusId());
					}
				return result;
			}
		}
		String crNumber = object.getAsJsonObject("GeneralDataSet").getAsJsonObject("GeneralData").get("Vendor")
				.getAsString();
		System.out.println("************************************************************************************"
				+ crNumber + "@@@@@" + status);
		int completedStatusId = 3;
		result.setCrNumber(crNumber);
		try {
			bpVendorDetailsRepository.updateVendorNo(bpCreationFromWorkflowRequest.getRequestId(), crNumber);
 
			}
			catch(Exception e){
				throw new RuntimeException("Failed to update status", e);
			}
			if (!bpCreationFromWorkflowRequest.isValidate()) {
				try {
				bpVendorDetailsRepository.updateStatusId(bpCreationFromWorkflowRequest.getRequestId(), completedStatusId);
				}
				catch(Exception e){
					throw new RuntimeException("Failed to update status", e);
				}
 
			MailRequestDto mailRequestDto = new MailRequestDto();
			ServiceResponse responseMessage = new ServiceResponse<>();
			mailRequestDto.setEmailTo(HelperClass.isEmpty(bpRequestGeneralDataDto.getRequestorEmail()) == true
					? "Vaibhav.Anand@viatris.com"
					: bpCreationFromWorkflowRequest.getRequestorEmail());
			JsonNode ruleResponse = emailNotificationService.getDataFromEmailRules(successfulEmailNotificationCode);
			if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
				String subject = "";
				if (profile.toUpperCase(Locale.ROOT).equalsIgnoreCase("PROD")) {
					subject = ruleResponse.get("data").get("result").get(0).get("VM_EMAIL_NOTIFICATION_CODE_ACTION")
							.get(0).get("VM_EMAIL_SUBJECT").asText();
				} else {
					subject = profile.toUpperCase(Locale.ROOT) + ": " + ruleResponse.get("data").get("result").get(0)
							.get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_SUBJECT").asText();
				}
				
				 
				subject = subject.replace("<processType>", "Extended").replace("<Request Id>",
						bpCreationFromWorkflowRequest.getRequestId());
				
				String emailBody = ruleResponse.get("data").get("result").get(0)
						.get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_BODY").asText();
				
				String countryName="";
				if(bpCreationFromWorkflowRequest.getCountryName() != null&&!bpCreationFromWorkflowRequest.getCountryName().equalsIgnoreCase("null")) {
					countryName=bpCreationFromWorkflowRequest.getCountryName();
				}
				else {
					countryName="";
				}
				 
				emailBody = emailBody.replace("<processType>", "extension").replace("<crNumber>", crNumber)
						.replace("<Business Partner name>", bpCreationFromWorkflowRequest.getBusinessPartnerName() != null ? bpCreationFromWorkflowRequest.getBusinessPartnerName() : "")
						.replace("<countryName>", countryName)
						.replace("<companyCode>", bpCreationFromWorkflowRequest.getCompanyCode() != null ? bpCreationFromWorkflowRequest.getCompanyCode() : "" )
						.replace("<purchasingOrg>", bpCreationFromWorkflowRequest.getPurchasingOrg() != null ? bpCreationFromWorkflowRequest.getPurchasingOrg() : "")
						.replace("|", "<br>").replace("\'", "");

				mailRequestDto.setSubject(subject);
				mailRequestDto.setBodyMessage(emailBody);
				emailNotificationService.sendMailThroughCPI(mailRequestDto);
//		            responseMessage=mailService.sendMail(mailRequestDto);
			} else {
				responseMessage.setMessage("Sending Mail Failed!!");
				responseMessage.setStatus(AppConstants.FAIL_MESSAGE_MAIL);
				responseMessage.setError(null);
				log.error("No Response received from the rules");
			}
		}
		result.setMessage(objectMapper.readTree("{\"key\": \"Validated Successfully!!\"}").get("key"));
		return result;
	}

	public CPIVendorDetailsDto convertExtend(BPRequestGeneralDataDto bpRequestGeneralDataDto, boolean validate) {

		CPIVendorDetailsDto object = new CPIVendorDetailsDto();
		if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0))) {
			object.setType("");
			object.setRequestId(
					bpRequestGeneralDataDto.getRequestId() == null ? "" : bpRequestGeneralDataDto.getRequestId());
			if (validate) {
				object.setValidation("X");
			} else {
				object.setValidation("");
			}
			object.setChangeIndObject("U");
			object.setVendor(bpRequestGeneralDataDto.getBupaNo());
			object.setSystemId(bpRequestGeneralDataDto.getSystemId());
			object.setTrainstation("");
			object.setLocationno1(bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo1() == null ? "0000000"
					: bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo1());
			object.setLocationno2(bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo2() == null ? "00000"
					: bpRequestGeneralDataDto.getBpControlData().get(0).getLocationNo2());
			object.setAuthorization(bpRequestGeneralDataDto.getBpControlData().get(0).getAuthorization() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getAuthorization());
			object.setIndustry(bpRequestGeneralDataDto.getBpControlData().get(0).getIndustry() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getIndustry());
			object.setCheckdigit(bpRequestGeneralDataDto.getBpControlData().get(0).getCheckDigit() == null ? "0"
					: bpRequestGeneralDataDto.getBpControlData().get(0).getCheckDigit());
			object.setDMEIndicator(
					bpRequestGeneralDataDto.getDmeIndicator() == null ? "" : bpRequestGeneralDataDto.getDmeIndicator());
			object.setInstructionkey(bpRequestGeneralDataDto.getInstructionKey() == null ? ""
					: bpRequestGeneralDataDto.getInstructionKey());
			object.setISRNumber(
					bpRequestGeneralDataDto.getIsrNumber() == null ? "" : bpRequestGeneralDataDto.getIsrNumber());
			object.setCorporateGroup(bpRequestGeneralDataDto.getBpControlData().get(0).getCorporateGroup() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getCorporateGroup());
			object.setAccountgroup(bpRequestGeneralDataDto.getBupaAccountGrp() == null ? ""
					: bpRequestGeneralDataDto.getBupaAccountGrp());
//				    object.setCustomer(bpRequestGeneralDataDto.getBpControlData().get(0).getCustomer() == null ? "" : bpRequestGeneralDataDto.getBpControlData().get(0).getCustomer());
			object.setCustomer("");
			object.setAlternatpayee(bpRequestGeneralDataDto.getAlternativePayee() == null ? ""
					: bpRequestGeneralDataDto.getAlternativePayee());
			object.setDeletionflag(false);
			object.setPostingBlock(false);
			object.setPurchblock(false);
			object.setTaxNumber1(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo1() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo1());
			object.setTaxNumber2(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo2() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo2());
			object.setEqualizatntax("");
			object.setLiableforVAT(false);
			object.setPayeeindoc(false);
			object.setTradingPartner(bpRequestGeneralDataDto.getBpControlData().get(0).getTradingPartner() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTradingPartner());
			object.setFiscaladdress(bpRequestGeneralDataDto.getBpControlData().get(0).getFiscalAddress() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getFiscalAddress());
			object.setVATRegNo(bpRequestGeneralDataDto.getBpControlData().get(0).getVatRegNo() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getVatRegNo());
			object.setNaturalperson(
					bpRequestGeneralDataDto.getNaturalPer() == null ? "" : bpRequestGeneralDataDto.getNaturalPer());
			object.setBlockfunction("");
			object.setAddress("");
			object.setPlaceofbirth(bpRequestGeneralDataDto.getBpControlData().get(0).getPlaceOfBirth() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getPlaceOfBirth());
			String output = null;
			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getDob())) {
//				SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");

				try {
					Date date = inputFormat.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getDob());

					output = outputFormat.format(date);

					System.out.println("Input: " + bpRequestGeneralDataDto.getBpControlData().get(0).getDob());
					System.out.println("Output: " + output);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			object.setBirthdate(output == null ? "" : output);
			object.setSex(bpRequestGeneralDataDto.getBpControlData().get(0).getSex() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getSex());
			object.setCredinfono(bpRequestGeneralDataDto.getCreditInformationNumber() == null ? ""
					: bpRequestGeneralDataDto.getCreditInformationNumber());
//				    object.setLastextreview(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview() == null ? null : bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview());
			long lastExterReview = 0;
			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview())) {
				try {
					Date setLastextreviewDate = sdf
							.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getLastExtReview());
					lastExterReview = setLastextreviewDate.getTime();
					System.out.println("Timestamp in milliseconds: " + lastExterReview);
				} catch (ParseException e) {
					System.err.println("Error parsing the date string: " + e.getMessage());
				}
				object.setLastextreview("/Date(" + lastExterReview + ")/");
			} else {
				object.setLastextreview(null);
			}
			object.setActualQMsys(bpRequestGeneralDataDto.getBpControlData().get(0).getActualQnSys() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getActualQnSys());
			object.setRefacctgroup("");
			object.setPlant("");
			object.setVSRrelevant(true);
			object.setPlantrelevant(true);
			object.setFactorycalend("");
			object.setSCAC(bpRequestGeneralDataDto.getBpControlData().get(0).getScac() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getScac());
			object.setCarfreightgrp(bpRequestGeneralDataDto.getBpControlData().get(0).getCarFreughtGrp() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getCarFreughtGrp());
			object.setServAgntProcGrp(
					bpRequestGeneralDataDto.getBpControlData().get(0).getServAgntProcGrp() == null ? ""
							: bpRequestGeneralDataDto.getBpControlData().get(0).getServAgntProcGrp());
			object.setTaxtype(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxType() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxType());
			object.setTaxnumbertype(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNoType() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNoType());
			object.setSocialIns(false);
			object.setSocInsCode(bpRequestGeneralDataDto.getBpControlData().get(0).getSocInsCode() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getSocInsCode());
			object.setTaxNumber3(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo3() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo3());
			object.setTaxNumber4(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo4() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo4());
			object.setTaxsplit(false);
			object.setTaxbase(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxBase() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxBase());
			object.setProfession(bpRequestGeneralDataDto.getBpControlData().get(0).getProfession() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getProfession());
			object.setStatgrpagent("");
			object.setExternalmanuf(bpRequestGeneralDataDto.getBpControlData().get(0).getExternalManuf() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getExternalManuf());
			object.setDeletionblock(false);
			object.setRepsName(bpRequestGeneralDataDto.getBpControlData().get(0).getRepsName() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getRepsName());
			object.setTypeofBusiness(bpRequestGeneralDataDto.getBpControlData().get(0).getTypeOfBusiness() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTypeOfBusiness());
			object.setTypeofIndustry(bpRequestGeneralDataDto.getBpControlData().get(0).getTypeOfIndustr() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTypeOfIndustr());
			long qmSystemTo = 0;
			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpControlData().get(0).getQmSystemTo())) {
				try {
					Date qmSystemToDate = sdf.parse(bpRequestGeneralDataDto.getBpControlData().get(0).getQmSystemTo());
					qmSystemTo = qmSystemToDate.getTime();
					System.out.println("Timestamp in milliseconds: " + qmSystemTo);
				} catch (ParseException e) {
					System.err.println("Error parsing the date string: " + e.getMessage());
				}
				object.setQMsystemto("/Date(" + qmSystemTo + ")/");
			} else {
				object.setQMsystemto(null);
			}

			object.setPODrelevant(bpRequestGeneralDataDto.getBpControlData().get(0).getPodRelevant() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getPodRelevant());
			object.setTaxoffice(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxOffice() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxOffice());
			object.setTaxNumber(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNumber() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNumber());
			object.setTaxNumber5(bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo5() == null ? ""
					: bpRequestGeneralDataDto.getBpControlData().get(0).getTaxNo5());
			object.setPurposeCompleteFlag("");
			object.setAddressVersion("");
			object.setFrom("/Date(253402214400000)/");
			object.setTo("/Date(253402214400000)/");
			object.setTitle(bpRequestGeneralDataDto.getTitle());
			object.setName(bpRequestGeneralDataDto.getName1() == null ? "" : bpRequestGeneralDataDto.getName1());
			object.setName2(bpRequestGeneralDataDto.getName2() == null ? "" : bpRequestGeneralDataDto.getName2());
			object.setName3(bpRequestGeneralDataDto.getName3() == null ? "" : bpRequestGeneralDataDto.getName3());
			object.setName4(bpRequestGeneralDataDto.getName4() == null ? "" : bpRequestGeneralDataDto.getName4());
			object.setConvname("");
			object.setCo(bpRequestGeneralDataDto.getBpAddressInfo().getCo() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getCo());
			object.setCity(bpRequestGeneralDataDto.getBpAddressInfo().getCity() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getCity());
			object.setDistrict(bpRequestGeneralDataDto.getBpAddressInfo().getDistrict() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getDistrict());
			object.setCityNo("");
			object.setDistrictNo("");
			object.setCheckStatus("");
			object.setRegStrGrp(bpRequestGeneralDataDto.getBpAddressInfo().getRegStructGrp() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getRegStructGrp());
			object.setPostalCode(bpRequestGeneralDataDto.getBpAddressInfo().getPostalCode() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getPostalCode());
			object.setPOBoxPostCde(
					bpRequestGeneralDataDto.getPoPostalCode() == null ? "" : bpRequestGeneralDataDto.getPoPostalCode());
			object.setCompanyPostCd(bpRequestGeneralDataDto.getPoCompanyPostalCode() == null ? ""
					: bpRequestGeneralDataDto.getPoCompanyPostalCode());
			object.setPostalCodeExt("");
			object.setPostalCodeExt2("");
			object.setPostalCodeExt3("");
			object.setPOBox(bpRequestGeneralDataDto.getPoBox() == null ? "" : bpRequestGeneralDataDto.getPoBox());
			object.setPOBoxwono(false);
			object.setPOBoxCity("");
			object.setPOCitNo("");
			object.setPORegion("");
			object.setPOboxcountry("");
			object.setISOcode("");
			object.setDeliveryDist("");
			object.setTransportzone(bpRequestGeneralDataDto.getBpAddressInfo().getTransportZone() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getTransportZone());
			object.setStreet(bpRequestGeneralDataDto.getBpAddressInfo().getStreet() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStreet());
			object.setStreetCode("");
			object.setStreetAbbrev("");
			object.setHouseNumber(bpRequestGeneralDataDto.getBpAddressInfo().getHouseNo() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getHouseNo());
			object.setSupplement(bpRequestGeneralDataDto.getBpAddressInfo().getSuppl() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getSuppl());
			object.setNumberRange("");
			object.setStreet2(bpRequestGeneralDataDto.getBpAddressInfo().getStreet2() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStreet2());
			object.setStreet3(bpRequestGeneralDataDto.getBpAddressInfo().getStreet3() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStreet3());
			object.setStreet4(bpRequestGeneralDataDto.getBpAddressInfo().getStreet4() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStreet4());
			object.setStreet5(bpRequestGeneralDataDto.getBpAddressInfo().getStreet5() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStreet5());
			object.setBuildingCode(bpRequestGeneralDataDto.getBpAddressInfo().getBuildingCode() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getBuildingCode());
			object.setFloor(bpRequestGeneralDataDto.getBpAddressInfo().getFloor() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getFloor());
			object.setRoomNumber(bpRequestGeneralDataDto.getBpAddressInfo().getRoom() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getRoom());
			object.setCountry(bpRequestGeneralDataDto.getBpAddressInfo().getCountry() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getCountry());
			object.setCountryISO(bpRequestGeneralDataDto.getBpAddressInfo().getCountry() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getCountry());
			object.setLanguage(bpRequestGeneralDataDto.getBpAddressInfo().getLanguage() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getLanguage());
			object.setLangISO(bpRequestGeneralDataDto.getBpAddressInfo().getLanguage() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getLanguage());
			object.setRegion(bpRequestGeneralDataDto.getBpAddressInfo().getRegion() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getRegion());
			object.setSearchTerm1(
					bpRequestGeneralDataDto.getSearchTerm1() == null ? "" : bpRequestGeneralDataDto.getSearchTerm1());
			object.setSearchTerm2(
					bpRequestGeneralDataDto.getSearchTerm2() == null ? "" : bpRequestGeneralDataDto.getSearchTerm2());
			object.setDataline("");
			object.setTelebox("");
			object.setTimezone(bpRequestGeneralDataDto.getBpAddressInfo().getTimeZone() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getTimeZone());
			object.setTaxJurisdictn(bpRequestGeneralDataDto.getBpAddressInfo().getTaxJurisdiction() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getTaxJurisdiction());
			object.setAddressID("");
			object.setCreationlang("EN");
			object.setLangCRISO("EN");
			object.setCommMethod(bpRequestGeneralDataDto.getBpAddressInfo().getStandardCommMethod() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getStandardCommMethod());
			object.setAddressgroup("");
			object.setDifferentCity(bpRequestGeneralDataDto.getBpAddressInfo().getDifferentCity() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getDifferentCity());
			object.setCityCode("");
			object.setUndeliverable(bpRequestGeneralDataDto.getBpAddressInfo().getUndeliverable() == null ? ""
					: bpRequestGeneralDataDto.getBpAddressInfo().getUndeliverable());
			object.setUndeliverable1("");
			object.setPOBoxLobby("");
			object.setDelvryServType("");
			object.setDeliveryServiceNo("");
			object.setCountycode("");
			object.setCounty("");
			object.setTownshipcode("");
			object.setTownship("");
			object.setPAN("");

			// Setting To Address Data

			object.setToAddressData(new ArrayList<>());

			// Setting To Company Data

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())
					&& bpRequestGeneralDataDto.getExtendCompanyCode()) {
				List<CPICompanyDataDto> cpiCompanyDataDto = new ArrayList<>();

				for (BPCompanyCodeInfoDto companyCodeInfoDto : bpRequestGeneralDataDto.getBpCompanyCodeInfo()) {
					if (companyCodeInfoDto.getExtend())
						cpiCompanyDataDto.add(convertExtend(companyCodeInfoDto));
				}
				object.setToCompanyData(cpiCompanyDataDto);
			}

//				    if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())&&bpRequestGeneralDataDto.getExtendCompanyCode()) {
//				        object.setToCompanyData(bpRequestGeneralDataDto.getBpCompanyCodeInfo().stream()
//				                .map(companyCodeDto -> convertExtend(companyCodeDto)).collect(Collectors.toList()));
//				    }
			else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCompanyCodeInfo())
					|| !bpRequestGeneralDataDto.getExtendCompanyCode()) {
				object.setToCompanyData(new ArrayList<>());
			}

			// Setting To Purchase Org Data without additional Data

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpPurchasingOrgDetail())
					&& bpRequestGeneralDataDto.getExtendPurchaseOrg()) {
				System.out.println("Inside extend purchase org without add data");
				List<CPIPurchaseOrgDataDto> cpiPurchaseOrgDto = new ArrayList<>();

				
				for (BPPurchasingOrgDetailDto purchaseOrgDto : bpRequestGeneralDataDto.getBpPurchasingOrgDetail()) {
					System.out.println("purchaseOrgDto for cpi " + new Gson().toJson(purchaseOrgDto));
					System.out.println(" PurchaseOrgDto To Get Extend" + purchaseOrgDto.getExtend());
					if (purchaseOrgDto.getExtend())
						cpiPurchaseOrgDto.add(convertExtend(purchaseOrgDto, bpRequestGeneralDataDto));
				}
//					if (!HelperClass.isEmpty(purchaseOrgDto.getExtend()))
//						cpiPurchaseOrgDto.add(convertExtend(purchaseOrgDto, bpRequestGeneralDataDto));
//				}
				object.setToPurchaseOrgData(cpiPurchaseOrgDto);
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpPurchasingOrgDetail())
					&& !bpRequestGeneralDataDto.getExtendPurchaseOrg()
					&& !bpRequestGeneralDataDto.getExtendAdditionalData()) {
				System.out.println("Inside extend purchase org without add data empty");
				object.setToPurchaseOrgData(new ArrayList<>());
			}

			// Setting To Purchase Org Data with additional Data

			if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpPurchasingOrgDetail())
					&& bpRequestGeneralDataDto.getExtendAdditionalData()) {
				System.out.println("Inside extend purchase org with add data");
				List<CPIPurchaseOrgDataDto> cpiPurchaseOrgDto = new ArrayList<>();

				for (BPPurchasingOrgDetailDto purchaseOrgDto : bpRequestGeneralDataDto.getBpPurchasingOrgDetail()) {
					if (purchaseOrgDto.getExtendAdditionalData())
						cpiPurchaseOrgDto.add(convertExtendForAdditionalData(purchaseOrgDto, bpRequestGeneralDataDto));
				}
				object.setToPurchaseOrgData(cpiPurchaseOrgDto);
			} else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpPurchasingOrgDetail())
					&& !bpRequestGeneralDataDto.getExtendAdditionalData()) {
				System.out.println("Inside extend purchase org with add data empty");
				object.setToPurchaseOrgData(new ArrayList<>());
			}

//				    if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpPurchasingOrgDetail())) {
//				        object.setToPurchaseOrgData(bpRequestGeneralDataDto.getBpPurchasingOrgDetail().stream()
//				                .map(purchasingOrgDto -> convertExtend(purchasingOrgDto)).collect(Collectors.toList()));
//				    }

			// Setting To Classification

			object.setToClassification(new ArrayList<>());

//				    if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpVendorClassificationEntity())) {
//				        object.setToClassification(bpRequestGeneralDataDto.getBpVendorClassificationEntity().stream()
//				                .map(vendorClassificationEntityDto -> convertExtend(vendorClassificationEntityDto)).collect(Collectors.toList()));
//				    }
//					else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpVendorClassificationEntity())) {
//						object.setToClassification(new ArrayList<>());
//					}

			// Setting To Email

			object.setToEmail(new ArrayList<>());

//				    if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpEmail())) {
//				        object.setToEmail(bpRequestGeneralDataDto.getBpCommunication().getBpEmail().stream()
//				                .map(emailDto -> extendConvert(emailDto)).collect(Collectors.toList()));
//				    }
//				    else if(HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpEmail())) {
//				    	object.setToEmail(new ArrayList<>());
//				    }

			// Setting To Phone

			object.setToPhone(new ArrayList<>());

//				    if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpTelephone())) {
//				        object.setToPhone(bpRequestGeneralDataDto.getBpCommunication().getBpTelephone().stream()
//				                .map(telephoneDto -> extendConvert(telephoneDto)).collect(Collectors.toList()));
//				    }
//				    else if(HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpTelephone())) {
//				    	object.setToPhone(new ArrayList<>());
//				    }
//				    if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpMobilePhone())) {
//				    object.getToPhone().addAll(bpRequestGeneralDataDto.getBpCommunication().getBpMobilePhone().stream()
//				            .map(mobileDto -> extendConvert(mobileDto)).collect(Collectors.toList()));
//				    }
//				    else if(HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpMobilePhone())) {
//				    	object.getToPhone().addAll(new ArrayList<>());
//				    }

			// Setting To Fax

			object.setToFax(new ArrayList<>());

//				    if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpFaxInfo())) {
//				        object.setToFax(bpRequestGeneralDataDto.getBpCommunication().getBpFaxInfo().stream()
//				                .map(faxDto -> extendConvert(faxDto)).collect(Collectors.toList()));
//				    }
//				    else if(HelperClass.isEmpty(bpRequestGeneralDataDto.getBpCommunication().getBpFaxInfo())) {
//				    	object.setToFax(new ArrayList<>());
//				    	}
			// Setting To Bank

			object.setToBank(new ArrayList<>());

//				    if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation())) {
//				        object.setToBank(bpRequestGeneralDataDto.getBpBankInformation().stream()
//				                .map(bankDto -> extendConvert(bankDto)).collect(Collectors.toList()));
//				    }
//				    else if(HelperClass.isEmpty(bpRequestGeneralDataDto.getBpBankInformation())) {
//				    	object.setToBank(new ArrayList<>());
//				    }
			// Setting To Contact Info

			object.setToContact(new ArrayList<>());

//				    if (!HelperClass.isEmpty(bpRequestGeneralDataDto.getBpContactInformation())) {
//				        object.setToContact(bpRequestGeneralDataDto.getBpContactInformation().stream()
//				                .map(contactDto -> convertExtend(contactDto)).collect(Collectors.toList()));
//				    }
//					else if (HelperClass.isEmpty(bpRequestGeneralDataDto.getBpContactInformation())) {
//						object.setToContact(new ArrayList<>());
//					}

			// Setting To Return Messages

//				    object.setToReturnMessages(new ArrayList<>());
//				    object.setToTaxData(new ArrayList<>());
			return object;
		} else {
			return object;
		}
	}

	public CPICompanyDataDto convertExtend(BPCompanyCodeInfoDto companyCodeDto) {
		CPICompanyDataDto object = new CPICompanyDataDto();
		object.setChangeIndObject("I");
		object.setVendor("");
		object.setCompanyCode(companyCodeDto.getCompanyCode() == null ? "" : companyCodeDto.getCompanyCode());
		object.setCocodepostblock(false);
		object.setCocdedeletionflag(false);
		object.setSortkey(companyCodeDto.getBpAccountingInformation().getSortKey() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getSortKey());
		object.setReconaccount(
				companyCodeDto.getBpAccountingInformation().getReconcilliationAccountInGeneralLedger() == null ? ""
						: companyCodeDto.getBpAccountingInformation().getReconcilliationAccountInGeneralLedger());
		object.setAuthorization(companyCodeDto.getBpAccountingInformation().getAuthorization() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getAuthorization());
		object.setInterestindic(companyCodeDto.getBpAccountingInformation().getInterestInd() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getInterestInd());
		object.setPaymentmethods(companyCodeDto.getBpPaymentTransaction().getPaymentMethods() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPaymentMethods());
		object.setClrgwithcust(false);
		object.setPaymentblock(companyCodeDto.getBpPaymentTransaction().getPaymentBlock() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPaymentBlock());
		object.setPaytTerms(companyCodeDto.getBpPaymentTransaction().getPaymentTerms() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPaymentTerms());
		object.setAcctvendor("");
		object.setClerkatvendor(companyCodeDto.getBpCorrespondance().getClerkAtVendor() == null ? ""
				: companyCodeDto.getBpCorrespondance().getClerkAtVendor());
		object.setAccountmemo(companyCodeDto.getBpCorrespondance().getAccountMemo() == null ? ""
				: companyCodeDto.getBpCorrespondance().getAccountMemo());
		object.setPlanninggroup("");
		object.setAcctgclerk(companyCodeDto.getBpCorrespondance().getAccountingClerk() == null ? ""
				: companyCodeDto.getBpCorrespondance().getAccountingClerk());
		object.setHeadoffice(companyCodeDto.getBpAccountingInformation().getHeadOffice() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getHeadOffice());
		object.setAlternatpayee(companyCodeDto.getBpPaymentTransaction().getAlternatePayee() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getAlternatePayee());
		long lastKey = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getLastKeyDate())) {
			try {
				Date lastKeyDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getLastKeyDate());
				lastKey = lastKeyDate.getTime();
				System.out.println("Timestamp in milliseconds: " + lastKey);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setLastkeydate("/Date(" + lastKey + ")/");
		} else {
			object.setLastkeydate(null);
		}
		object.setIntcalcfreq("00");
		long lastintcalc = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getLastInterestRun())) {
			try {
				Date intcalcDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getLastInterestRun());
				lastintcalc = intcalcDate.getTime();
				System.out.println("Timestamp in milliseconds: " + lastintcalc);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setLastintcalc("/Date(" + lastintcalc + ")/");
		} else {
			object.setLastintcalc(null);
		}
		object.setLocalprocess(companyCodeDto.getBpCorrespondance().getLocalProcess() == null ? false
				: companyCodeDto.getBpCorrespondance().getLocalProcess());
		object.setBexchlimit(companyCodeDto.getBpPaymentTransaction().getBExchLimit() == null ? "0.000"
				: companyCodeDto.getBpPaymentTransaction().getBExchLimit());
		object.setChkcashngtime(companyCodeDto.getBpPaymentTransaction().getChkCashingTime() == null ? "0"
				: companyCodeDto.getBpPaymentTransaction().getChkCashingTime());
		object.setChkdoubleinv(companyCodeDto.getBpPaymentTransaction().getChkDoubleInv() == null ? true
				: companyCodeDto.getBpPaymentTransaction().getChkDoubleInv());
		object.setTolerancegroup(companyCodeDto.getBpPaymentTransaction().getPayDataToleranceGroup() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPayDataToleranceGroup());
		object.setHouseBank(companyCodeDto.getBpPaymentTransaction().getHouseBank() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getHouseBank());
		object.setIndividualpmnt(companyCodeDto.getBpPaymentTransaction().getIndividualPermit() == null ? false
				: companyCodeDto.getBpPaymentTransaction().getIndividualPermit());
		object.setPmtmethsupl(companyCodeDto.getBpPaymentTransaction().getPmtmethsupl() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPmtmethsupl());
		object.setExemptionno(companyCodeDto.getBpAccountingInformation().getExemptionNumber() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getExemptionNumber());
		long validUntil = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getValidUntil())) {
			try {
				Date validUntilDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getValidUntil());
				validUntil = validUntilDate.getTime();
				System.out.println("Timestamp in milliseconds: " + validUntil);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setValiduntil("/Date(" + validUntil + ")/");
		} else {
			object.setValiduntil(null);
		}
		object.setWTaxCode(companyCodeDto.getBpAccountingInformation().getWtaxCode() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getWtaxCode());
		object.setSubsind("");
		object.setMaineconomicact("0000");
		object.setMinorityindic(companyCodeDto.getBpAccountingInformation().getMinorityIndicator() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getMinorityIndicator());
		object.setPrevacctno(companyCodeDto.getBpAccountingInformation().getPrevAcctNo() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getPrevAcctNo());
		object.setGroupingkey1(companyCodeDto.getBpPaymentTransaction().getGroupingKey() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getGroupingKey());
		object.setGroupingkey2("");
		object.setPmtmethsupl("");
		object.setRecipienttype(companyCodeDto.getBpAccountingInformation().getRecipientType() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getRecipientType());
		object.setExmptauthority(companyCodeDto.getBpAccountingInformation().getExemptionAuthority() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getExemptionAuthority());
		object.setCountryForWT(companyCodeDto.getWhTaxCountry());
		object.setPmtadvbyEDI(companyCodeDto.getBpPaymentTransaction().getPmtAdvByEDI() == null ? false
				: companyCodeDto.getBpPaymentTransaction().getPmtAdvByEDI());
		object.setReleasegroup(companyCodeDto.getBpAccountingInformation().getReleaseGroup() == null ? ""
				: companyCodeDto.getBpAccountingInformation().getReleaseGroup());
		object.setClerksfax(companyCodeDto.getBpCorrespondance().getClerkFax() == null ? ""
				: companyCodeDto.getBpCorrespondance().getClerkFax());
		object.setClrksinternet(companyCodeDto.getBpCorrespondance().getClerkInternet() == null ? ""
				: companyCodeDto.getBpCorrespondance().getClerkInternet());
		object.setCrmemoterms("");
		object.setActivityCode("");
		object.setDistrType("");
		object.setAcctstatement("");
		long timestampOfExemptFromDate = 0;
		if (!HelperClass.isEmpty(companyCodeDto.getBpAccountingInformation().getCertificationDate())) {
			try {
				Date exemptFromDate = sdf.parse(companyCodeDto.getBpAccountingInformation().getCertificationDate());
				timestampOfExemptFromDate = exemptFromDate.getTime();
				System.out.println("Timestamp in milliseconds: " + timestampOfExemptFromDate);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setCertifictnDate("/Date(" + timestampOfExemptFromDate + ")/");
		} else {
			object.setCertifictnDate(null);
		}
		object.setTolerancegrp(
				companyCodeDto.getBpPaymentTransaction().getInvoiceVerificationToleranceGroup() == null ? ""
						: companyCodeDto.getBpPaymentTransaction().getInvoiceVerificationToleranceGroup());
		object.setPersonnelNo(companyCodeDto.getBpAccountingInformation().getPersonnelNumber() == null ? "00000000"
				: companyCodeDto.getBpAccountingInformation().getPersonnelNumber());
		object.setCoCddelblock(false);
		object.setActclktelno(companyCodeDto.getBpCorrespondance().getActingClerksTelephone() == null ? ""
				: companyCodeDto.getBpCorrespondance().getActingClerksTelephone());
		object.setPrepaymentRelevant(companyCodeDto.getBpPaymentTransaction().getPrePayment() == null ? ""
				: companyCodeDto.getBpPaymentTransaction().getPrePayment());
		object.setAssignmTestGroup("");
		object.setPurposeCompleteFlag("");
		object.setBranchCode(companyCodeDto.getBranchCode() == null ? "" : companyCodeDto.getBranchCode());
		object.setBranchCodeDescription(
				companyCodeDto.getBranchCodeDescription() == null ? "" : companyCodeDto.getBranchCodeDescription());

		List<CPIDunningDataDto> cpiDunningDataDto = new ArrayList<>();
		if (!HelperClass.isEmpty(companyCodeDto.getBpCorrespondance().getDunnProcedure())) {
			CPIDunningDataDto dunningDataObject = new CPIDunningDataDto();
			dunningDataObject.setChangeIndObject("I");
			dunningDataObject.setVendor("");
			dunningDataObject
					.setCompanyCode(companyCodeDto.getCompanyCode() == null ? "" : companyCodeDto.getCompanyCode());
			dunningDataObject.setDunningArea("");
			dunningDataObject.setDunnProcedure(companyCodeDto.getBpCorrespondance().getDunnProcedure() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunnProcedure());
			dunningDataObject.setDunnBlock(companyCodeDto.getBpCorrespondance().getDunningBlock() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunningBlock());
			long lastDunned = 0;
			if (!HelperClass.isEmpty(companyCodeDto.getBpCorrespondance().getLastDunned())) {
				try {
					Date lastDunnedDate = sdf.parse(companyCodeDto.getBpCorrespondance().getLastDunned());
					lastDunned = lastDunnedDate.getTime();
					System.out.println("Timestamp in milliseconds: " + lastDunned);
				} catch (ParseException e) {
					System.err.println("Error parsing the date string: " + e.getMessage());
				}
				dunningDataObject.setLastDunned("/Date(" + lastDunned + ")/");
			} else {
				dunningDataObject.setLastDunned(null);
			}
			dunningDataObject.setDunningLevel(companyCodeDto.getBpCorrespondance().getDunningLevel() == null ? "0"
					: companyCodeDto.getBpCorrespondance().getDunningLevel().toString());
			dunningDataObject.setDunnrecipient(companyCodeDto.getBpCorrespondance().getDunnRecepient() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunnRecepient());
			dunningDataObject.setLegdunnproc(null);
			dunningDataObject.setDunningclerk(companyCodeDto.getBpCorrespondance().getDunningClerk() == null ? ""
					: companyCodeDto.getBpCorrespondance().getDunningClerk());

			cpiDunningDataDto.add(dunningDataObject);

			object.setToDunningData(cpiDunningDataDto);
		} else {
			object.setToDunningData(new ArrayList<>());
		}
//		object.setToWtax(new ArrayList<>());
		if (!HelperClass.isEmpty(companyCodeDto.getBpWithholdingTax())) {

			List<CPIwTaxDto> cpiwTaxDto = new ArrayList<>();
			for (BPWithholdingTaxDto withholdingTaxDto : companyCodeDto.getBpWithholdingTax()) {
				if (!HelperClass.isEmpty(withholdingTaxDto.getWithholdingTaxType()))
					cpiwTaxDto.add(convert(withholdingTaxDto, companyCodeDto));
			}
			object.setToWtax(cpiwTaxDto);
		} else if (HelperClass.isEmpty(companyCodeDto.getBpWithholdingTax())) {
			object.setToWtax(new ArrayList<>());
		}
		return object;
	}

	private CPIOrderingAddressDto convertChange(
			BPBusinessPartnerOrderingAddressDto bpBusinessPartnerOrderingAddressDto) {
		CPIOrderingAddressDto cpiOrderingAddressDto = new CPIOrderingAddressDto();
		cpiOrderingAddressDto.setType("");
		cpiOrderingAddressDto.setValidation("");
		cpiOrderingAddressDto.setChangeIndObject("U");
		cpiOrderingAddressDto.setVendor("");
		cpiOrderingAddressDto.setTrainstation("");
		cpiOrderingAddressDto.setLocationno1("");
		cpiOrderingAddressDto.setLocationno2("");
		cpiOrderingAddressDto.setAuthorization("");
		cpiOrderingAddressDto.setIndustry("");
		cpiOrderingAddressDto.setCheckdigit("");
		cpiOrderingAddressDto.setDMEIndicator("");
		cpiOrderingAddressDto.setInstructionkey("");
		cpiOrderingAddressDto.setISRNumber("");
		cpiOrderingAddressDto.setCorporateGroup("");
		cpiOrderingAddressDto.setAccountgroup("");
		cpiOrderingAddressDto.setCustomer("");
		cpiOrderingAddressDto.setAlternatpayee("");
		cpiOrderingAddressDto.setDeletionflag(false);
		cpiOrderingAddressDto.setPostingBlock(false);
		cpiOrderingAddressDto.setPurchblock(false);
		cpiOrderingAddressDto.setTaxNumber1("");
		cpiOrderingAddressDto.setTaxNumber2("");
		cpiOrderingAddressDto.setEqualizatntax("");
		cpiOrderingAddressDto.setLiableforVAT(false);
		cpiOrderingAddressDto.setPayeeindoc(false);
		cpiOrderingAddressDto.setTradingPartner("");
		cpiOrderingAddressDto.setFiscaladdress("");
		cpiOrderingAddressDto.setVATRegNo("");
		cpiOrderingAddressDto.setNaturalperson("");
		cpiOrderingAddressDto.setBlockfunction("");
		cpiOrderingAddressDto.setAddress("");
		cpiOrderingAddressDto.setPlaceofbirth("");
		cpiOrderingAddressDto.setBirthdate("");
		cpiOrderingAddressDto.setSex("");
		cpiOrderingAddressDto.setCredinfono("");
		cpiOrderingAddressDto.setLastextreview("");
		cpiOrderingAddressDto.setActualQMsys("");
		cpiOrderingAddressDto.setRefacctgroup("");
		cpiOrderingAddressDto.setPlant("");
		cpiOrderingAddressDto.setPlant("");
		cpiOrderingAddressDto.setFactorycalend("");
		cpiOrderingAddressDto.setSCAC("");
		cpiOrderingAddressDto.setCarfreightgrp("");
		cpiOrderingAddressDto.setServAgntProcGrp("");
		cpiOrderingAddressDto.setTaxtype("");
		cpiOrderingAddressDto.setTaxnumbertype("");
		cpiOrderingAddressDto.setSocialIns(false);
		cpiOrderingAddressDto.setSocInsCode("");
		cpiOrderingAddressDto.setTaxNumber3("");
		cpiOrderingAddressDto.setTaxNumber4("");
		cpiOrderingAddressDto.setTaxsplit(false);
		cpiOrderingAddressDto.setTaxbase("");
		cpiOrderingAddressDto.setProfession("");
		cpiOrderingAddressDto.setStatgrpagent("");
		cpiOrderingAddressDto.setExternalmanuf("");
		cpiOrderingAddressDto.setDeletionblock(false);
		cpiOrderingAddressDto.setRepsName("");
		cpiOrderingAddressDto.setTypeofBusiness("");
		cpiOrderingAddressDto.setTypeofIndustry("");
		cpiOrderingAddressDto.setPODrelevant("");
		cpiOrderingAddressDto.setTaxoffice("");
		cpiOrderingAddressDto.setTaxNumber("");
		cpiOrderingAddressDto.setTaxNumber5("");
		cpiOrderingAddressDto.setPurposeCompleteFlag("");
		cpiOrderingAddressDto.setAddressVersion("");
		cpiOrderingAddressDto.setFrom("/Date(253402214400000)/");
		cpiOrderingAddressDto.setTo("/Date(253402214400000)/");
		cpiOrderingAddressDto.setTitle(bpBusinessPartnerOrderingAddressDto.getTitle() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getTitle());
		cpiOrderingAddressDto.setName(bpBusinessPartnerOrderingAddressDto.getName1() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getName1());
		cpiOrderingAddressDto.setName2(bpBusinessPartnerOrderingAddressDto.getName2() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getName2());
		cpiOrderingAddressDto.setName3(bpBusinessPartnerOrderingAddressDto.getName3() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getName3());
		cpiOrderingAddressDto.setName4(bpBusinessPartnerOrderingAddressDto.getName4() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getName4());
		cpiOrderingAddressDto.setConvname("");
		cpiOrderingAddressDto.setCo(
				bpBusinessPartnerOrderingAddressDto.getCo() == null ? "" : bpBusinessPartnerOrderingAddressDto.getCo());
		cpiOrderingAddressDto.setCity(bpBusinessPartnerOrderingAddressDto.getCity() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getCity());
		cpiOrderingAddressDto.setDistrict(bpBusinessPartnerOrderingAddressDto.getDistrict() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getDistrict());
		cpiOrderingAddressDto.setCityNo("");
		cpiOrderingAddressDto.setCheckStatus("");
		cpiOrderingAddressDto.setRegStrGrp(bpBusinessPartnerOrderingAddressDto.getRegStructGrp() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getRegStructGrp());
		cpiOrderingAddressDto.setPostalCode(bpBusinessPartnerOrderingAddressDto.getPostalCode() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getPostalCode());
		cpiOrderingAddressDto.setPOBoxPostCde("");
		cpiOrderingAddressDto.setCompanyPostCd("");
		cpiOrderingAddressDto.setPostalCodeExt("");
		cpiOrderingAddressDto.setPostalCodeExt2("");
		cpiOrderingAddressDto.setPostalCodeExt3("");
		cpiOrderingAddressDto.setPOBox("");
		cpiOrderingAddressDto.setPOBoxwono(false);
		cpiOrderingAddressDto.setPOBoxCity("");
		cpiOrderingAddressDto.setPOCitNo("");
		cpiOrderingAddressDto.setPORegion("");
		cpiOrderingAddressDto.setPOboxcountry("");
		cpiOrderingAddressDto.setISOcode("");
		cpiOrderingAddressDto.setDeliveryDist("");
		cpiOrderingAddressDto.setTransportzone(bpBusinessPartnerOrderingAddressDto.getTransportZone() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getTransportZone());
		cpiOrderingAddressDto.setStreet(bpBusinessPartnerOrderingAddressDto.getStreet() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStreet());
		cpiOrderingAddressDto.setStreetCode("");
		cpiOrderingAddressDto.setStreetAbbrev("");
		cpiOrderingAddressDto.setHouseNumber(bpBusinessPartnerOrderingAddressDto.getHouseNo() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getHouseNo());
		cpiOrderingAddressDto.setSupplement(bpBusinessPartnerOrderingAddressDto.getSuppl() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getSuppl());
		cpiOrderingAddressDto.setNumberRange("");
		cpiOrderingAddressDto.setStreet2(bpBusinessPartnerOrderingAddressDto.getStreet2() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStreet2());
		cpiOrderingAddressDto.setStreet3(bpBusinessPartnerOrderingAddressDto.getStreet3() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStreet3());
		cpiOrderingAddressDto.setStreet4(bpBusinessPartnerOrderingAddressDto.getStreet4() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStreet4());
		cpiOrderingAddressDto.setStreet5(bpBusinessPartnerOrderingAddressDto.getStreet5() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStreet5());
		cpiOrderingAddressDto.setBuildingCode(bpBusinessPartnerOrderingAddressDto.getBuildingCode() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getBuildingCode());
		cpiOrderingAddressDto.setFloor(bpBusinessPartnerOrderingAddressDto.getFloor() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getFloor());
		cpiOrderingAddressDto.setRoomNumber(bpBusinessPartnerOrderingAddressDto.getRoom() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getRoom());
		cpiOrderingAddressDto.setCountry(bpBusinessPartnerOrderingAddressDto.getCountry() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getCountry());
		cpiOrderingAddressDto.setCountryISO("");
		cpiOrderingAddressDto.setLanguage(bpBusinessPartnerOrderingAddressDto.getLanguage() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getLanguage());
		cpiOrderingAddressDto.setLangISO("");
		cpiOrderingAddressDto.setRegion(bpBusinessPartnerOrderingAddressDto.getRegion() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getRegion());
		cpiOrderingAddressDto.setSearchTerm1("");
		cpiOrderingAddressDto.setSearchTerm2("");
		cpiOrderingAddressDto.setDataline("");
		cpiOrderingAddressDto.setTelebox("");
		cpiOrderingAddressDto.setTimezone(bpBusinessPartnerOrderingAddressDto.getTimeZone() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getTimeZone());
		cpiOrderingAddressDto.setTaxJurisdictn(bpBusinessPartnerOrderingAddressDto.getTaxJurisdiction() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getTaxJurisdiction());
		cpiOrderingAddressDto.setAddressID("");
		cpiOrderingAddressDto.setCreationlang("");
		cpiOrderingAddressDto.setLangCRISO("");
		cpiOrderingAddressDto.setCommMethod(bpBusinessPartnerOrderingAddressDto.getStandardCommMethod() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStandardCommMethod());
		cpiOrderingAddressDto.setAddressgroup("");
		cpiOrderingAddressDto.setDifferentCity(bpBusinessPartnerOrderingAddressDto.getDifferentCity() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getDifferentCity());
		cpiOrderingAddressDto.setCityCode("");
		cpiOrderingAddressDto.setUndeliverable(bpBusinessPartnerOrderingAddressDto.getUndeliverable() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getUndeliverable());
		cpiOrderingAddressDto.setUndeliverable1("");
		cpiOrderingAddressDto.setPOBoxLobby("");
		cpiOrderingAddressDto.setDelvryServType("");
		cpiOrderingAddressDto.setDeliveryServiceNo("");
		cpiOrderingAddressDto.setCountycode("");
		cpiOrderingAddressDto.setCounty("");
		cpiOrderingAddressDto.setTownshipcode("");
		cpiOrderingAddressDto.setTownship("");
		cpiOrderingAddressDto.setPAN("");
		return cpiOrderingAddressDto;
	}

	private CPIInvoicePartyDto convertChange(
			BPBusinessPartnerRemittanceAddressDto bpBusinessPartnerRemittanceAddressDto) {
		CPIInvoicePartyDto cpiInvoicePartyDto = new CPIInvoicePartyDto();
		cpiInvoicePartyDto.setType("");
		cpiInvoicePartyDto.setValidation("");
		cpiInvoicePartyDto.setChangeIndObject("U");
		cpiInvoicePartyDto.setVendor("");
		cpiInvoicePartyDto.setTrainstation("");
		cpiInvoicePartyDto.setLocationno1("");
		cpiInvoicePartyDto.setLocationno2("");
		cpiInvoicePartyDto.setAuthorization("");
		cpiInvoicePartyDto.setIndustry("");
		cpiInvoicePartyDto.setCheckdigit("");
		cpiInvoicePartyDto.setDMEIndicator("");
		cpiInvoicePartyDto.setInstructionkey("");
		cpiInvoicePartyDto.setISRNumber("");
		cpiInvoicePartyDto.setCorporateGroup("");
		cpiInvoicePartyDto.setAccountgroup("");
		cpiInvoicePartyDto.setCustomer("");
		cpiInvoicePartyDto.setAlternatpayee("");
		cpiInvoicePartyDto.setDeletionflag(false);
		cpiInvoicePartyDto.setPostingBlock(false);
		cpiInvoicePartyDto.setPurchblock(false);
		cpiInvoicePartyDto.setTaxNumber1("");
		cpiInvoicePartyDto.setTaxNumber2("");
		cpiInvoicePartyDto.setEqualizatntax("");
		cpiInvoicePartyDto.setLiableforVAT(false);
		cpiInvoicePartyDto.setPayeeindoc(false);
		cpiInvoicePartyDto.setTradingPartner("");
		cpiInvoicePartyDto.setFiscaladdress("");
		cpiInvoicePartyDto.setVATRegNo("");
		cpiInvoicePartyDto.setNaturalperson("");
		cpiInvoicePartyDto.setBlockfunction("");
		cpiInvoicePartyDto.setAddress("");
		cpiInvoicePartyDto.setPlaceofbirth("");
		cpiInvoicePartyDto.setBirthdate("");
		cpiInvoicePartyDto.setSex("");
		cpiInvoicePartyDto.setCredinfono("");
		cpiInvoicePartyDto.setLastextreview("");
		cpiInvoicePartyDto.setActualQMsys("");
		cpiInvoicePartyDto.setRefacctgroup("");
		cpiInvoicePartyDto.setPlant("");
		cpiInvoicePartyDto.setPlant("");
		cpiInvoicePartyDto.setFactorycalend("");
		cpiInvoicePartyDto.setSCAC("");
		cpiInvoicePartyDto.setCarfreightgrp("");
		cpiInvoicePartyDto.setServAgntProcGrp("");
		cpiInvoicePartyDto.setTaxtype("");
		cpiInvoicePartyDto.setTaxnumbertype("");
		cpiInvoicePartyDto.setSocialIns(false);
		cpiInvoicePartyDto.setSocInsCode("");
		cpiInvoicePartyDto.setTaxNumber3("");
		cpiInvoicePartyDto.setTaxNumber4("");
		cpiInvoicePartyDto.setTaxsplit(false);
		cpiInvoicePartyDto.setTaxbase("");
		cpiInvoicePartyDto.setProfession("");
		cpiInvoicePartyDto.setStatgrpagent("");
		cpiInvoicePartyDto.setExternalmanuf("");
		cpiInvoicePartyDto.setDeletionblock(false);
		cpiInvoicePartyDto.setRepsName("");
		cpiInvoicePartyDto.setTypeofBusiness("");
		cpiInvoicePartyDto.setTypeofIndustry("");
		cpiInvoicePartyDto.setPODrelevant("");
		cpiInvoicePartyDto.setTaxoffice("");
		cpiInvoicePartyDto.setTaxNumber("");
		cpiInvoicePartyDto.setTaxNumber5("");
		cpiInvoicePartyDto.setPurposeCompleteFlag("");
		cpiInvoicePartyDto.setAddressVersion("");
		cpiInvoicePartyDto.setFrom("/Date(253402214400000)/");
		cpiInvoicePartyDto.setTo("/Date(253402214400000)/");
		cpiInvoicePartyDto.setTitle(bpBusinessPartnerRemittanceAddressDto.getTitle() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getTitle());
		cpiInvoicePartyDto.setName(bpBusinessPartnerRemittanceAddressDto.getName1() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getName1());
		cpiInvoicePartyDto.setName2(bpBusinessPartnerRemittanceAddressDto.getName2() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getName2());
		cpiInvoicePartyDto.setName3(bpBusinessPartnerRemittanceAddressDto.getName3() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getName3());
		cpiInvoicePartyDto.setName4(bpBusinessPartnerRemittanceAddressDto.getName4() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getName4());
		cpiInvoicePartyDto.setConvname("");
		cpiInvoicePartyDto.setCo(bpBusinessPartnerRemittanceAddressDto.getCo() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getCo());
		cpiInvoicePartyDto.setCity(bpBusinessPartnerRemittanceAddressDto.getCity() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getCity());
		cpiInvoicePartyDto.setDistrict(bpBusinessPartnerRemittanceAddressDto.getDistrict() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getDistrict());
		cpiInvoicePartyDto.setCityNo("");
		cpiInvoicePartyDto.setCheckStatus("");
		cpiInvoicePartyDto.setRegStrGrp(bpBusinessPartnerRemittanceAddressDto.getRegStructGrp() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getRegStructGrp());
		cpiInvoicePartyDto.setPostalCode(bpBusinessPartnerRemittanceAddressDto.getPostalCode() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getPostalCode());
		cpiInvoicePartyDto.setPOBoxPostCde("");
		cpiInvoicePartyDto.setCompanyPostCd("");
		cpiInvoicePartyDto.setPostalCodeExt("");
		cpiInvoicePartyDto.setPostalCodeExt2("");
		cpiInvoicePartyDto.setPostalCodeExt3("");
		cpiInvoicePartyDto.setPOBox("");
		cpiInvoicePartyDto.setPOBoxwono(false);
		cpiInvoicePartyDto.setPOBoxCity("");
		cpiInvoicePartyDto.setPOCitNo("");
		cpiInvoicePartyDto.setPORegion("");
		cpiInvoicePartyDto.setPOboxcountry("");
		cpiInvoicePartyDto.setISOcode("");
		cpiInvoicePartyDto.setDeliveryDist("");
		cpiInvoicePartyDto.setTransportzone(bpBusinessPartnerRemittanceAddressDto.getTransportZone() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getTransportZone());
		cpiInvoicePartyDto.setStreet(bpBusinessPartnerRemittanceAddressDto.getStreet() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStreet());
		cpiInvoicePartyDto.setStreetCode("");
		cpiInvoicePartyDto.setStreetAbbrev("");
		cpiInvoicePartyDto.setHouseNumber(bpBusinessPartnerRemittanceAddressDto.getHouseNo() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getHouseNo());
		cpiInvoicePartyDto.setSupplement(bpBusinessPartnerRemittanceAddressDto.getSuppl() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getSuppl());
		cpiInvoicePartyDto.setNumberRange("");
		cpiInvoicePartyDto.setStreet2(bpBusinessPartnerRemittanceAddressDto.getStreet2() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStreet2());
		cpiInvoicePartyDto.setStreet3(bpBusinessPartnerRemittanceAddressDto.getStreet3() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStreet3());
		cpiInvoicePartyDto.setStreet4(bpBusinessPartnerRemittanceAddressDto.getStreet4() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStreet4());
		cpiInvoicePartyDto.setStreet5(bpBusinessPartnerRemittanceAddressDto.getStreet5() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStreet5());
		cpiInvoicePartyDto.setBuildingCode(bpBusinessPartnerRemittanceAddressDto.getBuildingCode() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getBuildingCode());
		cpiInvoicePartyDto.setFloor(bpBusinessPartnerRemittanceAddressDto.getFloor() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getFloor());
		cpiInvoicePartyDto.setRoomNumber(bpBusinessPartnerRemittanceAddressDto.getRoom() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getRoom());
		cpiInvoicePartyDto.setCountry(bpBusinessPartnerRemittanceAddressDto.getCountry() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getCountry());
		cpiInvoicePartyDto.setCountryISO("");
		cpiInvoicePartyDto.setLanguage(bpBusinessPartnerRemittanceAddressDto.getLanguage() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getLanguage());
		cpiInvoicePartyDto.setLangISO("");
		cpiInvoicePartyDto.setRegion(bpBusinessPartnerRemittanceAddressDto.getRegion() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getRegion());
		cpiInvoicePartyDto.setSearchTerm1("");
		cpiInvoicePartyDto.setSearchTerm2("");
		cpiInvoicePartyDto.setDataline("");
		cpiInvoicePartyDto.setTelebox("");
		cpiInvoicePartyDto.setTimezone(bpBusinessPartnerRemittanceAddressDto.getTimeZone() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getTimeZone());
		cpiInvoicePartyDto.setTaxJurisdictn(bpBusinessPartnerRemittanceAddressDto.getTaxJurisdiction() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getTaxJurisdiction());
		cpiInvoicePartyDto.setAddressID("");
		cpiInvoicePartyDto.setCreationlang("");
		cpiInvoicePartyDto.setLangCRISO("");
		cpiInvoicePartyDto.setCommMethod(bpBusinessPartnerRemittanceAddressDto.getStandardCommMethod() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStandardCommMethod());
		cpiInvoicePartyDto.setAddressgroup("");
		cpiInvoicePartyDto.setDifferentCity(bpBusinessPartnerRemittanceAddressDto.getDifferentCity() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getDifferentCity());
		cpiInvoicePartyDto.setCityCode("");
		cpiInvoicePartyDto.setUndeliverable(bpBusinessPartnerRemittanceAddressDto.getUndeliverable() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getUndeliverable());
		cpiInvoicePartyDto.setUndeliverable1("");
		cpiInvoicePartyDto.setPOBoxLobby("");
		cpiInvoicePartyDto.setDelvryServType("");
		cpiInvoicePartyDto.setDeliveryServiceNo("");
		cpiInvoicePartyDto.setCountycode("");
		cpiInvoicePartyDto.setCounty("");
		cpiInvoicePartyDto.setTownshipcode("");
		cpiInvoicePartyDto.setTownship("");
		cpiInvoicePartyDto.setPAN("");
		return cpiInvoicePartyDto;
	}

	public CPIDunningDataDto convertDunningData12() {
		CPIDunningDataDto object = new CPIDunningDataDto();
		object.setChangeIndObject("");
		object.setVendor("");
		object.setCompanyCode("");
		object.setDunningArea("");
		object.setDunnProcedure("");
		object.setDunnBlock("");
		object.setLastDunned("");
		object.setDunningLevel("");
		object.setDunnrecipient("");
		object.setLegdunnproc("");
		object.setDunningclerk("");
		return object;
	}

	public CPIwTaxDto convertExtend(BPWithholdingTaxDto withholdingTaxDto, BPCompanyCodeInfoDto companyCodeDto) {
		CPIwTaxDto object = new CPIwTaxDto();
		object.setChangeIndObject("I");
		object.setVendor("");
		object.setCompanyCode(companyCodeDto.getCompanyCode() == null ? "" : companyCodeDto.getCompanyCode());
		object.setCountry(companyCodeDto.getWhTaxCountry() == null ? "" : companyCodeDto.getWhTaxCountry());
		object.setWithhldtaxtype(
				withholdingTaxDto.getWithholdingTaxType() == null ? "" : withholdingTaxDto.getWithholdingTaxType());
		object.setSubjecttowtx(false);
		object.setRecipienttype(
				withholdingTaxDto.getRecipientType() == null ? "" : withholdingTaxDto.getRecipientType());
		object.setWtaxnumber(withholdingTaxDto.getWTaxId() == null ? "" : withholdingTaxDto.getWTaxId());
		object.setWtaxcode(
				withholdingTaxDto.getWithholdingTaxCode() == null ? "00" : withholdingTaxDto.getWithholdingTaxCode());

		object.setExemptionnumber(withholdingTaxDto.getExemptionNo() == null ? "" : withholdingTaxDto.getExemptionNo());
		object.setExemptionrate(
				withholdingTaxDto.getExemPercentage() == null ? "0.00" : withholdingTaxDto.getExemPercentage());
		long timestampOfExemptFromDate = 0;
		long timestampOfExemptToDate = 0;
		if (!HelperClass.isEmpty(withholdingTaxDto.getExemptFrom())) {
			try {
				Date exemptFromDate = sdf.parse(withholdingTaxDto.getExemptFrom());

				timestampOfExemptFromDate = exemptFromDate.getTime();

				System.out.println("Timestamp in milliseconds: " + timestampOfExemptFromDate);

			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setExemptfrom("/Date(" + timestampOfExemptFromDate + ")/");
		} else {
			object.setExemptfrom(null);
		}
		if (!HelperClass.isEmpty(withholdingTaxDto.getExemptTo())) {
			try {

				Date exemptToDate = sdf.parse(withholdingTaxDto.getExemptTo());

				timestampOfExemptToDate = exemptToDate.getTime();

				System.out.println("Timestamp in milliseconds: " + timestampOfExemptToDate);
			} catch (ParseException e) {
				System.err.println("Error parsing the date string: " + e.getMessage());
			}
			object.setExemptTo("/Date(" + timestampOfExemptToDate + ")/");
		} else {
			object.setExemptTo(null);
		}
		object.setExemptionreas(withholdingTaxDto.getExemResn() == null ? "" : withholdingTaxDto.getExemResn());

		return object;
	}

	private CPIContactDto convertExtend(BPContactInformationDto bpContactDto) {
		CPIContactDto contactDto = new CPIContactDto();
		contactDto.setChangeIndObject("I");
		contactDto.setVendor("");
//				contactDto.setContactPerson("");
		contactDto.setContactPerson(bpContactDto.getContactFunction());

		contactDto.setDepartment(bpContactDto.getDepartment());
//				contactDto.setDepartment("0001");
		contactDto.setHighLevelPerson("0000000000");
		contactDto.setFunction(bpContactDto.getContactFunction() == null ? "01" : bpContactDto.getContactFunction());
//				contactDto.setFunction("");
		contactDto.setAuthority("");
		contactDto.setVIP("1");
		contactDto.setGender("");
		contactDto.setRepresentno("0000000000");
		contactDto.setCallfrequency("");
		contactDto.setBuyinghabits("");
		contactDto.setNotes("");
		contactDto.setMaritalStat("0");
		contactDto.setTitle(bpContactDto.getFormOfAddress() == null ? "" : bpContactDto.getFormOfAddress());
		contactDto.setLastname(bpContactDto.getLastName() == null ? "" : bpContactDto.getLastName());
		contactDto.setFirstname(bpContactDto.getFirstName() == null ? "" : bpContactDto.getFirstName());
		contactDto.setNameatBirth("");
		contactDto.setFamilynameSecond("");
		contactDto.setCompletename("");
		contactDto.setAcademicTitle("");
		contactDto.setAcadtitlesecond("");
		contactDto.setPrefix("");
		contactDto.setPrefixSecond("");
		contactDto.setNameSupplement("");
		contactDto.setNickname("");
		contactDto.setFormatname("");
		contactDto.setFormatcountry("");
		return contactDto;
	}

	public CPIClassificationDto convertExtend(BPVendorClassificationEntityDto classificationEntityDto) {
		CPIClassificationDto object = new CPIClassificationDto();
		object.setChangeIndObject("I");
		object.setVendor("");
		object.setClassnum(classificationEntityDto.getClassnum() == null ? "" : classificationEntityDto.getClassnum());
//		        object.setClassnum("SBA_SMALL_BUS_ADM");
		object.setClasstype("010");
		object.setObject("");
		object.setObjecttable("");
		object.setKeydate(null);
		object.setDescription(
				classificationEntityDto.getDescription() == null ? "" : classificationEntityDto.getDescription());
		object.setStatus("");
		object.setChangenumber("");
		object.setStdClass("");
		object.setFlag(false);
		object.setObjectGuid("000000000000000000");
		if (!HelperClass.isEmpty(classificationEntityDto.getBpVendorClassificationAttribute())) {
			object.setToClassificationItem(classificationEntityDto.getBpVendorClassificationAttribute().stream()
					.map(vendorClassificationAttributeDto -> changeConvert(vendorClassificationAttributeDto))
					.collect(Collectors.toList()));
		} else if (HelperClass.isEmpty(classificationEntityDto.getBpVendorClassificationAttribute())) {
			object.setToClassificationItem(new ArrayList<>());
		}
		return object;

	}

	public CPIClassificationItemDto extendConvert(BPVendorClassificationAttributeDto classificationAttributeDto) {
		CPIClassificationItemDto object = new CPIClassificationItemDto();
		object.setChangeIndObject("I");
		object.setVendor("");
//		        object.setCharact("CERTIFICATION_TYPE");
		object.setCharact(
				classificationAttributeDto.getCharact() == null ? "" : classificationAttributeDto.getCharact());
		object.setValuChar("");
//		        object.setValueChar("STATE");
		object.setInherited("");
		object.setInstance("");
		object.setValueNeutral(classificationAttributeDto.getValueNeutral() == null ? ""
				: classificationAttributeDto.getValueNeutral());
//		        object.setCharactDescr("Certification Type");
		object.setCharactDescr(classificationAttributeDto.getCharactDescr() == null ? ""
				: classificationAttributeDto.getCharactDescr());
		object.setValueCharLong("");
		object.setValueNeutralLong("");
		return object;
	}

	public CPIPurchaseOrgDataDto convertExtend(BPPurchasingOrgDetailDto bpPurchasingOrgDetailDto,
			BPRequestGeneralDataDto bpRequestGeneralDataDto) {
		CPIPurchaseOrgDataDto object = new CPIPurchaseOrgDataDto();
		object.setChangeIndObject("I");
		object.setVendor("");
		object.setAllvendor("");
		object.setPurchasingOrg(
				bpPurchasingOrgDetailDto.getPurchasingOrg() == null ? "" : bpPurchasingOrgDetailDto.getPurchasingOrg());
//				object.setPurchasingOrg("1234");

		object.setPurblockPOrg(false);
		object.setDelflagPOrg(false);
		object.setABCindicator(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAbcIndicator() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAbcIndicator());
		object.setOrdercurrency(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getOrderCurrency() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getOrderCurrency());
		object.setSalesperson(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSalesPerson() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSalesPerson());
		object.setTelephone(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTelephone() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTelephone());
		object.setMinimumvalue(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue() == null ? "0.00"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue());
		object.setPaytTerms(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTermsOfPayment() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTermsOfPayment());
		object.setIncoterms(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms());
		object.setIncoterms2(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms2() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms2());
		object.setGRBasedIV(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getGrBasedInvVerify() == null ? true
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getGrBasedInvVerify());
		object.setAcknowlReqd(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAcknowledgementReqd() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAcknowledgementReqd());
		object.setSchemaGrpVndr(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSchemaGroupVendor() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSchemaGroupVendor());
		object.setAutomaticPO(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutomaticPurchaseOrder() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutomaticPurchaseOrder());
		object.setModeOfTrBorder(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getModeOfTransportBorder() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getModeOfTransportBorder());
		object.setCustomsoffice("");
		object.setPrDateCat("");
		object.setPurchGroup(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPurchasingGroup() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPurchasingGroup());
		object.setSubseqsett(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubsequentSettlement() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubsequentSettlement());
		object.setBvolcompag(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getBVolComp() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getBVolComp());
		object.setERS(false);
		object.setPlDelivTime(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime() == null ? "0"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime());
		object.setPlanningcal("");
		object.setPlanningcycle("");
		object.setPOentryvend("");
		object.setPricemkgvnd(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPriceMarkingAgreed() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPriceMarkingAgreed());
		object.setRackjobbing("");
		object.setSSindexactive(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubSeqSettIndex() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubSeqSettIndex());
		object.setPricedetermin(false);
		object.setQualiffDKd("");
		object.setDocumentIndex(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getDocIndexActive() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getDocIndexActive());
		object.setSortcriterion(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSortCriterion() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSortCriterion());
		object.setConfControl(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getConfirmationControl() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getConfirmationControl());
		object.setRndingProfile(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getRoundingProfile() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getRoundingProfile());
		object.setUoMGroup("");
		object.setVenServLevl(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getServLevel() == null ? "0.0"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getServLevel());
		object.setLBprofile("");
		object.setAutGRSetRet(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutoEvalGRSetMtRet() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutoEvalGRSetMtRet());
		object.setAccwvendor(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAccWithVendor() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAccWithVendor());
		object.setPROACTcontrolprof(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getProActControlProf() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getProActControlProf());
		object.setAgencybusiness(
				bpPurchasingOrgDetailDto.getBpPurchaseOrg().getRelevantForAgencyBusiness() == null ? false
						: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getRelevantForAgencyBusiness());
		object.setRevaluation(false);
		object.setShippingCond(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getShippingConditions() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getShippingConditions());
		object.setSrvBasedInvVer(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSrvBasedInvVar() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSrvBasedInvVar());
		if (!HelperClass.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData())) {
			List<CPIPlantDto> cpiPlantDto = new ArrayList<>();
			for (BPPurchaseOrgAdditionalDataDto purchaseOrgAdditionalDto : bpPurchasingOrgDetailDto
					.getBpPurchaseOrgAdditionalData()) {
				if (!HelperClass.isEmpty(purchaseOrgAdditionalDto.getPlant()) && purchaseOrgAdditionalDto.getExtend()
						&& !HelperClass.isEmpty(purchaseOrgAdditionalDto.getExtend()))
					cpiPlantDto.add(extendPlant(purchaseOrgAdditionalDto, bpPurchasingOrgDetailDto.getPurchasingOrg()));
			}
			object.setToPlant(cpiPlantDto);
		} else if (HelperClass.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData())) {
			object.setToPlant(new ArrayList<>());
		}
		if (bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			List<CPIOrderingAddressDto> cpiOrderingAddressDto = new ArrayList<>();
			cpiOrderingAddressDto.add(
					convert(bpPurchasingOrgDetailDto.getBpBusinessPartnerOrderingAddress(), bpRequestGeneralDataDto));
			object.setToOderingAddress(cpiOrderingAddressDto);
		} else if (!bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			object.setToOderingAddress(new ArrayList<>());
		}

		if (bpPurchasingOrgDetailDto.getRemittanceAddressCheck()) {
			List<CPIInvoicePartyDto> cpiInvoicePartyDto = new ArrayList<>();
			cpiInvoicePartyDto.add(
					convert(bpPurchasingOrgDetailDto.getBpBusinessPartnerRemittanceAddress(), bpRequestGeneralDataDto));
			object.setToInvoiceParty(cpiInvoicePartyDto);
		} else if (!bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			object.setToInvoiceParty(new ArrayList<>());
		}
		return object;
	}

	public CPIPurchaseOrgDataDto convertExtendForAdditionalData(BPPurchasingOrgDetailDto bpPurchasingOrgDetailDto,
			BPRequestGeneralDataDto bpRequestGeneralDataDto) {
		CPIPurchaseOrgDataDto object = new CPIPurchaseOrgDataDto();
		object.setChangeIndObject("U");
		object.setVendor("");
		object.setAllvendor("");
		object.setPurchasingOrg(
				bpPurchasingOrgDetailDto.getPurchasingOrg() == null ? "" : bpPurchasingOrgDetailDto.getPurchasingOrg());
//				object.setPurchasingOrg("1234");

		object.setPurblockPOrg(false);
		object.setDelflagPOrg(false);
		object.setABCindicator("");
		object.setOrdercurrency(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getOrderCurrency() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getOrderCurrency());
		object.setSalesperson(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSalesPerson() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSalesPerson());
		object.setTelephone(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTelephone() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTelephone());
		object.setMinimumvalue(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue() == null ? "0.00"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue());
		object.setPaytTerms(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTermsOfPayment() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTermsOfPayment());
		object.setIncoterms(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms());
		object.setIncoterms2(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms2() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms2());
		object.setGRBasedIV(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getGrBasedInvVerify() == null ? true
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getGrBasedInvVerify());
		object.setAcknowlReqd(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAcknowledgementReqd() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAcknowledgementReqd());
		object.setSchemaGrpVndr(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSchemaGroupVendor() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSchemaGroupVendor());
		object.setAutomaticPO(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutomaticPurchaseOrder() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutomaticPurchaseOrder());
		object.setModeOfTrBorder(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getModeOfTransportBorder() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getModeOfTransportBorder());
		object.setCustomsoffice("");
		object.setPrDateCat("");
		object.setPurchGroup(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPurchasingGroup() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPurchasingGroup());
		object.setSubseqsett(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubsequentSettlement() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubsequentSettlement());
		object.setBvolcompag(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getBVolComp() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getBVolComp());
		object.setERS(false);
		object.setPlDelivTime(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime() == null ? "0"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime());
		object.setPlanningcal("");
		object.setPlanningcycle("");
		object.setPOentryvend("");
		object.setPricemkgvnd(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPriceMarkingAgreed() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPriceMarkingAgreed());
		object.setRackjobbing("");
		object.setSSindexactive(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubSeqSettIndex() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubSeqSettIndex());
		object.setPricedetermin(false);
		object.setQualiffDKd("");
		object.setDocumentIndex(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getDocIndexActive() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getDocIndexActive());
		object.setSortcriterion(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSortCriterion() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSortCriterion());
		object.setConfControl(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getConfirmationControl() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getConfirmationControl());
		object.setRndingProfile("");
		object.setUoMGroup("");
		object.setVenServLevl(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getServLevel() == null ? "0.0"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getServLevel());
		object.setLBprofile("");
		object.setAutGRSetRet(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutoEvalGRSetMtRet() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutoEvalGRSetMtRet());
		object.setAccwvendor(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAccWithVendor() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAccWithVendor());
		object.setPROACTcontrolprof(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getProActControlProf() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getProActControlProf());
		object.setAgencybusiness(false);
		object.setRevaluation(false);
		object.setShippingCond(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getShippingConditions() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getShippingConditions());
		object.setSrvBasedInvVer(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSrvBasedInvVar() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSrvBasedInvVar());
		if (!HelperClass.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData())) {
			List<CPIPlantDto> cpiPlantDto = new ArrayList<>();
			for (BPPurchaseOrgAdditionalDataDto purchaseOrgAdditionalDto : bpPurchasingOrgDetailDto
					.getBpPurchaseOrgAdditionalData()) {
				if (purchaseOrgAdditionalDto.getExtend())
					cpiPlantDto.add(extendPlant(purchaseOrgAdditionalDto, bpPurchasingOrgDetailDto.getPurchasingOrg()));
			}
			object.setToPlant(cpiPlantDto);
		} else if (HelperClass.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData())) {
			object.setToPlant(new ArrayList<>());
		}
		if (bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			List<CPIOrderingAddressDto> cpiOrderingAddressDto = new ArrayList<>();
			cpiOrderingAddressDto.add(
					convert(bpPurchasingOrgDetailDto.getBpBusinessPartnerOrderingAddress(), bpRequestGeneralDataDto));
			object.setToOderingAddress(cpiOrderingAddressDto);
		} else if (!bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			object.setToOderingAddress(new ArrayList<>());
		}

		if (bpPurchasingOrgDetailDto.getRemittanceAddressCheck()) {
			List<CPIInvoicePartyDto> cpiInvoicePartyDto = new ArrayList<>();
			cpiInvoicePartyDto.add(
					convert(bpPurchasingOrgDetailDto.getBpBusinessPartnerRemittanceAddress(), bpRequestGeneralDataDto));
			object.setToInvoiceParty(cpiInvoicePartyDto);
		} else if (!bpPurchasingOrgDetailDto.getOrderingAddressCheck()) {
			object.setToInvoiceParty(new ArrayList<>());
		}
		return object;
	}
//			

	private CPIPhoneDto extendConvert(BPTelephoneDto telephoneDto) {
		CPIPhoneDto cpiPhoneDto = new CPIPhoneDto();

		cpiPhoneDto.setChangeIndObject("I");
		cpiPhoneDto.setVendor("");
		cpiPhoneDto.setCountry(telephoneDto.getCountry() == null ? "" : telephoneDto.getCountry());
		cpiPhoneDto.setISOcode(telephoneDto.getCountry() == null ? "" : telephoneDto.getCountry());
		cpiPhoneDto.setStandardNo(telephoneDto.isStandardNumber() == true ? true : telephoneDto.isStandardNumber());
		cpiPhoneDto.setTelephone(telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone());
		cpiPhoneDto.setExtension(telephoneDto.getExtension() == null ? "" : telephoneDto.getExtension());
		cpiPhoneDto.setTelephoneno((telephoneDto.getExtension() == null ? "" : telephoneDto.getExtension())
				+ (telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone()));
		cpiPhoneDto.setCallernumber(telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone());
		cpiPhoneDto.setSMSEnab("");
		cpiPhoneDto.setMobilephone("1");
		cpiPhoneDto.setHomeaddress(true);
		cpiPhoneDto.setSequenceNumber("1");
		cpiPhoneDto.setError(false);
		cpiPhoneDto.setDonotuse(telephoneDto.isDoNotUse());
		cpiPhoneDto.setValidFrom("");
		cpiPhoneDto.setValidTo("");
		;

		return cpiPhoneDto;

	}

	private CPIPhoneDto extendConvert(BPMobilePhoneDto telephoneDto) {
		CPIPhoneDto cpiPhoneDto = new CPIPhoneDto();

		cpiPhoneDto.setChangeIndObject("I");
		cpiPhoneDto.setVendor("");
		cpiPhoneDto.setCountry(telephoneDto.getCountry() == null ? "" : telephoneDto.getCountry());
		cpiPhoneDto.setISOcode(telephoneDto.getCountry() == null ? "" : telephoneDto.getCountry());
		cpiPhoneDto.setStandardNo(telephoneDto.isStandardNumber() == true ? true : telephoneDto.isStandardNumber());
		cpiPhoneDto.setTelephone(telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone());
		cpiPhoneDto.setExtension(telephoneDto.getExtension() == null ? "" : telephoneDto.getExtension());
		cpiPhoneDto.setTelephoneno((telephoneDto.getExtension() == null ? "" : telephoneDto.getExtension())
				+ (telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone()));
		cpiPhoneDto.setCallernumber(telephoneDto.getTelephone() == null ? "" : telephoneDto.getTelephone());
		cpiPhoneDto.setSMSEnab("");
		cpiPhoneDto.setMobilephone("3");
		cpiPhoneDto.setHomeaddress(true);
		cpiPhoneDto.setSequenceNumber("001");
		cpiPhoneDto.setError(false);
		cpiPhoneDto.setDonotuse(telephoneDto.isDoNotUse());
		cpiPhoneDto.setValidFrom("");
		cpiPhoneDto.setValidTo("");
		return cpiPhoneDto;

	}

//			private CPIPhoneDto convert1(BPTelephoneDto telephoneDto) {
//		    	CPIPhoneDto cpiPhoneDto = new CPIPhoneDto();
	//
//		        // Set the properties using the setters
//		        cpiPhoneDto.setChangeIndObject("I");
//		        cpiPhoneDto.setVendor("");
//		        cpiPhoneDto.setCountry("IN");
//		        cpiPhoneDto.setISOcode("IN");
//		        cpiPhoneDto.setStandardNo(true);
//		        cpiPhoneDto.setTelephone("9583197362");
//		        cpiPhoneDto.setExtension("");
//		        cpiPhoneDto.setTelephoneno("+919583197362");
//		        cpiPhoneDto.setCallernumber("9583197362");
//		        cpiPhoneDto.setSMSEnab("");
//		        cpiPhoneDto.setMobilephone("1");
//		        cpiPhoneDto.setHomeaddress(true);
//		        cpiPhoneDto.setSequenceNumber("001");
//		        cpiPhoneDto.setError(false);
//		        cpiPhoneDto.setDonotuse(false);
//		    	
//		    	return cpiPhoneDto;
	//
//			}

	private CPIEmailDto extendConvert(BPEmailDto bpEmailDto) {
		CPIEmailDto cpiEmailDto = new CPIEmailDto();
		cpiEmailDto.setChangeIndObject("I");
		cpiEmailDto.setVendor("");
		cpiEmailDto.setStandardNo(bpEmailDto.getStandardNumber() == null ? true : bpEmailDto.getStandardNumber());
		cpiEmailDto.setEMailAddress(bpEmailDto.getEmailAddress() == null ? "" : bpEmailDto.getEmailAddress());
//				cpiEmailDto.setEMailAddress("vaibhav.anand@incture.com");
		cpiEmailDto.setEMailAddressSearch("");
		cpiEmailDto.setStdrecipient(false);
		cpiEmailDto.setSAPConnection(false);
		cpiEmailDto.setCoding("");
		cpiEmailDto.setTNEF(false);
		cpiEmailDto.setHomeaddress(true);
		cpiEmailDto.setSequenceNumber("001");
		cpiEmailDto.setError(false);
		cpiEmailDto.setDonotuse(HelperClass.isEmpty(bpEmailDto.isDoNotUse()) == true ? false : bpEmailDto.isDoNotUse());
		cpiEmailDto.setValidFrom("");
		cpiEmailDto.setValidTo("");
		return cpiEmailDto;
	}
//			private CPIEmailDto convert1(BPEmailDto bpEmailDto) {
//		    	CPIEmailDto cpiEmailDto=new CPIEmailDto();
//		    	cpiEmailDto.setChangeIndObject("I");
//		        cpiEmailDto.setVendor("");
//		        cpiEmailDto.setStandardNo(true);
//		        cpiEmailDto.setEMailAddress("Test@incture.com");
//		        cpiEmailDto.setEMailAddressSearch("");
//		        cpiEmailDto.setStdrecipient(false);
//		        cpiEmailDto.setSAPConnection(false);
//		        cpiEmailDto.setCoding("");
//		        cpiEmailDto.setTNEF(false);
//		        cpiEmailDto.setHomeaddress(true);
//		        cpiEmailDto.setSequenceNumber("001");
//		        cpiEmailDto.setError(false);
//		        cpiEmailDto.setDonotuse(false);
//		    	
//		    	return cpiEmailDto;
//		    }

	private CPIBankDto extendConvert(BPBankInformationDto bpBankInformationDto) {
		CPIBankDto cpiBankDto = new CPIBankDto();
		cpiBankDto.setChangeIndObject("I");
		cpiBankDto.setVendor("");
		cpiBankDto.setBanknumber(
				bpBankInformationDto.getBankCountry() == null ? "" : bpBankInformationDto.getBankCountry());
		cpiBankDto.setBankCountry(bpBankInformationDto.getBankKey() == null ? "" : bpBankInformationDto.getBankKey());
		cpiBankDto.setBankAccount(
				bpBankInformationDto.getBankAccountNo() == null ? "" : bpBankInformationDto.getBankAccountNo());
		cpiBankDto.setControlkey(
				bpBankInformationDto.getControlKey() == null ? "" : bpBankInformationDto.getControlKey());
		cpiBankDto
				.setPartBankType(bpBankInformationDto.getBankType() == null ? "" : bpBankInformationDto.getBankType());
		cpiBankDto.setCollectauthor(false);
		cpiBankDto.setReference(
				bpBankInformationDto.getReferenceDetails() == null ? "" : bpBankInformationDto.getReferenceDetails());
		cpiBankDto.setAccountholder(
				bpBankInformationDto.getAccHolderName() == null ? "" : bpBankInformationDto.getAccHolderName());
		cpiBankDto.setIBAN(bpBankInformationDto.getIban() == null ? "" : bpBankInformationDto.getIban());
		cpiBankDto.setIBANvalidfrom(null);

		return cpiBankDto;
	}

//			private CPIBankDto convert1(BPBankInformationDto bpBankInformationDto) {
//				CPIBankDto cpiBankDto = new CPIBankDto();
//				cpiBankDto.setChangeIndObject("I");
//		        cpiBankDto.setVendor("");
//		        cpiBankDto.setBanknumber("122");
//		        cpiBankDto.setBankCountry("IN");
//		        cpiBankDto.setBankAccount("INC123456");
//		        cpiBankDto.setControlkey("");
//		        cpiBankDto.setPartBankType("AN");
//		        cpiBankDto.setCollectauthor(false);
//		        cpiBankDto.setReference("");
//		        cpiBankDto.setAccountholder("Naveen Inc");
//		        cpiBankDto.setIBAN("");
//		        cpiBankDto.setIBANvalidfrom(null);
//				return cpiBankDto;
//			}
	private CPIFaxDto extendConvert(BPFaxInfoDto faxDto) {
		// TODO Auto-generated method stub
		CPIFaxDto cpiFaxDto = new CPIFaxDto();
		cpiFaxDto.setChangeIndObject("I");
		cpiFaxDto.setVendor("");
		cpiFaxDto.setCountry(faxDto.getCountry() == null ? "" : faxDto.getCountry());
		cpiFaxDto.setISOcode(faxDto.getCountry() == null ? "" : faxDto.getCountry());
		cpiFaxDto.setStandardNo(false);
		cpiFaxDto.setFax(faxDto.getFax() == null ? "" : faxDto.getFax());
		cpiFaxDto.setExtension(faxDto.getExtension() == null ? "" : faxDto.getExtension());
		cpiFaxDto.setFaxnumber((faxDto.getExtension() == null ? "" : faxDto.getExtension())
				+ (faxDto.getFax() == null ? "" : faxDto.getFax()));
		cpiFaxDto.setSendernumber(faxDto.getFax() == null ? "" : faxDto.getFax());
		cpiFaxDto.setFaxgroup("");
		cpiFaxDto.setStdrecipient(false);
		cpiFaxDto.setSAPConnection(false);
		cpiFaxDto.setHomeaddress(true);
		cpiFaxDto.setSequenceNumber("001");
		cpiFaxDto.setError(false);
		cpiFaxDto.setDonotuse(false);
		cpiFaxDto.setValidFrom("");
		cpiFaxDto.setValidTo("");
		return cpiFaxDto;
	}

//			private CPIFaxDto convert1(BPFaxInfoDto faxDto) {
//				// TODO Auto-generated method stub
//				CPIFaxDto cpiFaxDto=new CPIFaxDto();
//				cpiFaxDto.setChangeIndObject("I");
//				cpiFaxDto.setVendor("");
//				cpiFaxDto.setCountry("IN");
//				cpiFaxDto.setISOcode("IN");
//				cpiFaxDto.setStandardNo(true);
//				cpiFaxDto.setFax("8665101294");
//				cpiFaxDto.setExtension("");
//				cpiFaxDto.setFaxnumber("+918665101294");
//				cpiFaxDto.setSendernumber("8665101294");
//				cpiFaxDto.setFaxgroup("");
//				cpiFaxDto.setStdrecipient(false);
//				cpiFaxDto.setSAPConnection(false);
//				cpiFaxDto.setHomeaddress(true);
//				cpiFaxDto.setSequenceNumber("001");
//				cpiFaxDto.setError(false);
//				cpiFaxDto.setDonotuse(false);;
//				return cpiFaxDto;
//			}
	private CPIPlantDto extendPlantConvert(BPPurchasingOrgDetailDto bpPurchasingOrgDetailDto) {
		CPIPlantDto cpiPlantDto = new CPIPlantDto();

		cpiPlantDto.setChangeIndObject("I");
		cpiPlantDto.setVendor("");
		cpiPlantDto.setVendorSubrange("");
		cpiPlantDto.setPlant(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getPlant() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getPlant());
		cpiPlantDto.setPurblockPOrg(false);
		cpiPlantDto.setDelflagPOrg(false);
		cpiPlantDto.setABCindicator(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAbcIndicator() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAbcIndicator());
		cpiPlantDto.setOrdercurrency(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getOrderCurrency() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getOrderCurrency());
		cpiPlantDto.setSalesperson(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSalesPerson() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSalesPerson());
		cpiPlantDto.setMinimumvalue(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue() == null ? "0.00"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getMinOrderValue());
		cpiPlantDto.setPaytTerms(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTermsOfPayment() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getTermsOfPayment());
		cpiPlantDto.setIncoterms(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getIncoTerms());
		cpiPlantDto.setIncoterms2("Free On Board");
		cpiPlantDto.setGRBasedIV(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getGrBasedInvVerify() == null ? true
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getGrBasedInvVerify());
		cpiPlantDto.setAcknowlReqd(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAcknowledgementReqd() == null ? true
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAcknowledgementReqd());
		cpiPlantDto.setSchemaGrpVndr(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSchemaGroupVendor() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSchemaGroupVendor());
		cpiPlantDto.setAutomaticPO(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getAutomaticPurchaseOrder() == null
						? false
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getAutomaticPurchaseOrder());
		cpiPlantDto
				.setModeOfTrBorder(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getModeOfTransportBorder() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getModeOfTransportBorder());
		cpiPlantDto.setCustomsoffice("");
		cpiPlantDto.setPrDateCat("");
		cpiPlantDto.setPurchGroup(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPurchasingGroup() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPurchasingGroup());
		cpiPlantDto.setSubseqsett(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubsequentSettlement() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSubsequentSettlement());
		cpiPlantDto.setBvolcompag(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getBVolComp() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getBVolComp());
		cpiPlantDto.setERS(false);
		cpiPlantDto.setPlDelivTime(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime() == null ? "0"
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPlannedDelivTime());
		cpiPlantDto.setPlanningcal("");
		cpiPlantDto.setPlanningcycle(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getPlanningCycle() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getPlanningCycle());
		cpiPlantDto.setPOentryvend("");
		cpiPlantDto.setPricemkgvnd(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPriceMarkingAgreed() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getPriceMarkingAgreed());
		cpiPlantDto.setRackjobbing("");
		cpiPlantDto.setMRPController(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getMrpController() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getMrpController());
		cpiPlantDto.setConfControl(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getConfirmationControl() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getConfirmationControl());
		cpiPlantDto.setRndingProfile(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getRoundingProfile() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getRoundingProfile());
		cpiPlantDto.setUoMGroup(
				bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getUnitofMeasureGroup() == null ? ""
						: bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData().get(0).getUnitofMeasureGroup());
		cpiPlantDto.setLBprofile("");
		cpiPlantDto.setAutGRSetRet(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutoEvalGRSetMtRet() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getAutoEvalGRSetMtRet());
		cpiPlantDto.setPROACTcontrolprof(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getProActControlProf() == null ? ""
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getProActControlProf());
		cpiPlantDto.setRevaluation(false);
		cpiPlantDto.setSrvBasedInvVer(bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSrvBasedInvVar() == null ? false
				: bpPurchasingOrgDetailDto.getBpPurchaseOrg().getSrvBasedInvVar());

		return cpiPlantDto;
	}

	private CPIPlantDto extendPlant(BPPurchaseOrgAdditionalDataDto bpPurchaseOrgAdditionalDataDto,
			String purchasingOrg) {
		CPIPlantDto cpiPlantDto = new CPIPlantDto();

		cpiPlantDto.setChangeIndObject("I");
		cpiPlantDto.setVendor("");
		cpiPlantDto.setVendorSubrange("");
		cpiPlantDto.setPurchasingOrg(purchasingOrg == null ? "" : purchasingOrg);
		cpiPlantDto.setPlant(
				bpPurchaseOrgAdditionalDataDto.getPlant() == null ? "" : bpPurchaseOrgAdditionalDataDto.getPlant());
		cpiPlantDto.setPurblockPOrg(false);
		cpiPlantDto.setDelflagPOrg(false);
		cpiPlantDto.setABCindicator(bpPurchaseOrgAdditionalDataDto.getAbcIndicator() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getAbcIndicator());
		cpiPlantDto.setOrdercurrency(bpPurchaseOrgAdditionalDataDto.getOrderCurrency() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getOrderCurrency());
		cpiPlantDto.setSalesperson("");
		cpiPlantDto.setMinimumvalue(bpPurchaseOrgAdditionalDataDto.getMinOrderValue() == null ? "0.00"
				: bpPurchaseOrgAdditionalDataDto.getMinOrderValue());
		cpiPlantDto.setPaytTerms(bpPurchaseOrgAdditionalDataDto.getTermsOfPayment() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getTermsOfPayment());
		cpiPlantDto.setIncoterms(bpPurchaseOrgAdditionalDataDto.getIncoTerms() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getIncoTerms());
		cpiPlantDto.setIncoterms2(bpPurchaseOrgAdditionalDataDto.getIncoTerms2() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getIncoTerms2());
		cpiPlantDto.setGRBasedIV(bpPurchaseOrgAdditionalDataDto.getGrBasedInvVerify() == null ? false
				: bpPurchaseOrgAdditionalDataDto.getGrBasedInvVerify());
		cpiPlantDto.setAcknowlReqd(bpPurchaseOrgAdditionalDataDto.getAcknowledgementReqd() == null ? false
				: bpPurchaseOrgAdditionalDataDto.getAcknowledgementReqd());
		cpiPlantDto.setSchemaGrpVndr("");
		cpiPlantDto.setAutomaticPO(bpPurchaseOrgAdditionalDataDto.getAutomaticPurchaseOrder() == null ? false
				: bpPurchaseOrgAdditionalDataDto.getAutomaticPurchaseOrder());
		cpiPlantDto.setModeOfTrBorder("");
		cpiPlantDto.setCustomsoffice("");
		cpiPlantDto.setPrDateCat("");
		cpiPlantDto.setPurchGroup(bpPurchaseOrgAdditionalDataDto.getPurchasingGroup() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getPurchasingGroup());
		cpiPlantDto.setSubseqsett(false);
		cpiPlantDto.setBvolcompag(false);
		cpiPlantDto.setERS(false);
		cpiPlantDto.setPlDelivTime(bpPurchaseOrgAdditionalDataDto.getPlanneddelivtime() == null ? "0"
				: bpPurchaseOrgAdditionalDataDto.getPlanneddelivtime());
		cpiPlantDto.setPlanningcal("");
		cpiPlantDto.setPlanningcycle(bpPurchaseOrgAdditionalDataDto.getPlanningCycle() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getPlanningCycle());
		cpiPlantDto.setPOentryvend("");
		cpiPlantDto.setPricemkgvnd("");
		cpiPlantDto.setRackjobbing("");
		cpiPlantDto.setMRPController(bpPurchaseOrgAdditionalDataDto.getMrpController() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getMrpController());
		cpiPlantDto.setConfControl(bpPurchaseOrgAdditionalDataDto.getConfirmationControl() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getConfirmationControl());
		cpiPlantDto.setRndingProfile(bpPurchaseOrgAdditionalDataDto.getRoundingProfile() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getRoundingProfile());
		cpiPlantDto.setUoMGroup(bpPurchaseOrgAdditionalDataDto.getUnitofMeasureGroup() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getUnitofMeasureGroup());
		cpiPlantDto.setLBprofile("");
		cpiPlantDto.setAutGRSetRet(bpPurchaseOrgAdditionalDataDto.getAutoEvalGRSetMtRet() == null ? false
				: bpPurchaseOrgAdditionalDataDto.getAutoEvalGRSetMtRet());
		cpiPlantDto.setPROACTcontrolprof(bpPurchaseOrgAdditionalDataDto.getProActControlProf() == null ? ""
				: bpPurchaseOrgAdditionalDataDto.getProActControlProf());
		cpiPlantDto.setRevaluation(false);
		cpiPlantDto.setSrvBasedInvVer(bpPurchaseOrgAdditionalDataDto.getSrvBasedInvVar() == null ? false
				: bpPurchaseOrgAdditionalDataDto.getSrvBasedInvVar());

		return cpiPlantDto;
	}

	private CPIOrderingAddressDto convertExtend(
			BPBusinessPartnerOrderingAddressDto bpBusinessPartnerOrderingAddressDto) {
		CPIOrderingAddressDto cpiOrderingAddressDto = new CPIOrderingAddressDto();
		cpiOrderingAddressDto.setType("");
		cpiOrderingAddressDto.setValidation("");
		cpiOrderingAddressDto.setChangeIndObject("I");
		cpiOrderingAddressDto.setVendor("");
		cpiOrderingAddressDto.setTrainstation("");
		cpiOrderingAddressDto.setLocationno1("");
		cpiOrderingAddressDto.setLocationno2("");
		cpiOrderingAddressDto.setAuthorization("");
		cpiOrderingAddressDto.setIndustry("");
		cpiOrderingAddressDto.setCheckdigit("");
		cpiOrderingAddressDto.setDMEIndicator("");
		cpiOrderingAddressDto.setInstructionkey("");
		cpiOrderingAddressDto.setISRNumber("");
		cpiOrderingAddressDto.setCorporateGroup("");
		cpiOrderingAddressDto.setAccountgroup("");
		cpiOrderingAddressDto.setCustomer("");
		cpiOrderingAddressDto.setAlternatpayee("");
		cpiOrderingAddressDto.setDeletionflag(false);
		cpiOrderingAddressDto.setPostingBlock(false);
		cpiOrderingAddressDto.setPurchblock(false);
		cpiOrderingAddressDto.setTaxNumber1("");
		cpiOrderingAddressDto.setTaxNumber2("");
		cpiOrderingAddressDto.setEqualizatntax("");
		cpiOrderingAddressDto.setLiableforVAT(false);
		cpiOrderingAddressDto.setPayeeindoc(false);
		cpiOrderingAddressDto.setTradingPartner("");
		cpiOrderingAddressDto.setFiscaladdress("");
		cpiOrderingAddressDto.setVATRegNo("");
		cpiOrderingAddressDto.setNaturalperson("");
		cpiOrderingAddressDto.setBlockfunction("");
		cpiOrderingAddressDto.setAddress("");
		cpiOrderingAddressDto.setPlaceofbirth("");
		cpiOrderingAddressDto.setBirthdate("");
		cpiOrderingAddressDto.setSex("");
		cpiOrderingAddressDto.setCredinfono("");
		cpiOrderingAddressDto.setLastextreview("");
		cpiOrderingAddressDto.setActualQMsys("");
		cpiOrderingAddressDto.setRefacctgroup("");
		cpiOrderingAddressDto.setPlant("");
		cpiOrderingAddressDto.setPlant("");
		cpiOrderingAddressDto.setFactorycalend("");
		cpiOrderingAddressDto.setSCAC("");
		cpiOrderingAddressDto.setCarfreightgrp("");
		cpiOrderingAddressDto.setServAgntProcGrp("");
		cpiOrderingAddressDto.setTaxtype("");
		cpiOrderingAddressDto.setTaxnumbertype("");
		cpiOrderingAddressDto.setSocialIns(false);
		cpiOrderingAddressDto.setSocInsCode("");
		cpiOrderingAddressDto.setTaxNumber3("");
		cpiOrderingAddressDto.setTaxNumber4("");
		cpiOrderingAddressDto.setTaxsplit(false);
		cpiOrderingAddressDto.setTaxbase("");
		cpiOrderingAddressDto.setProfession("");
		cpiOrderingAddressDto.setStatgrpagent("");
		cpiOrderingAddressDto.setExternalmanuf("");
		cpiOrderingAddressDto.setDeletionblock(false);
		cpiOrderingAddressDto.setRepsName("");
		cpiOrderingAddressDto.setTypeofBusiness("");
		cpiOrderingAddressDto.setTypeofIndustry("");
		cpiOrderingAddressDto.setPODrelevant("");
		cpiOrderingAddressDto.setTaxoffice("");
		cpiOrderingAddressDto.setTaxNumber("");
		cpiOrderingAddressDto.setTaxNumber5("");
		cpiOrderingAddressDto.setPurposeCompleteFlag("");
		cpiOrderingAddressDto.setAddressVersion("");
		cpiOrderingAddressDto.setFrom("/Date(253402214400000)/");
		cpiOrderingAddressDto.setTo("/Date(253402214400000)/");
		cpiOrderingAddressDto.setTitle(bpBusinessPartnerOrderingAddressDto.getTitle() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getTitle());
		cpiOrderingAddressDto.setName(bpBusinessPartnerOrderingAddressDto.getName1() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getName1());
		cpiOrderingAddressDto.setName2(bpBusinessPartnerOrderingAddressDto.getName2() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getName2());
		cpiOrderingAddressDto.setName3(bpBusinessPartnerOrderingAddressDto.getName3() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getName3());
		cpiOrderingAddressDto.setName4(bpBusinessPartnerOrderingAddressDto.getName4() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getName4());
		cpiOrderingAddressDto.setConvname("");
		cpiOrderingAddressDto.setCo(
				bpBusinessPartnerOrderingAddressDto.getCo() == null ? "" : bpBusinessPartnerOrderingAddressDto.getCo());
		cpiOrderingAddressDto.setCity(bpBusinessPartnerOrderingAddressDto.getCity() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getCity());
		cpiOrderingAddressDto.setDistrict(bpBusinessPartnerOrderingAddressDto.getDistrict() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getDistrict());
		cpiOrderingAddressDto.setCityNo("");
		cpiOrderingAddressDto.setCheckStatus("");
		cpiOrderingAddressDto.setRegStrGrp(bpBusinessPartnerOrderingAddressDto.getRegStructGrp() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getRegStructGrp());
		cpiOrderingAddressDto.setPostalCode(bpBusinessPartnerOrderingAddressDto.getPostalCode() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getPostalCode());
		cpiOrderingAddressDto.setPOBoxPostCde("");
		cpiOrderingAddressDto.setCompanyPostCd("");
		cpiOrderingAddressDto.setPostalCodeExt("");
		cpiOrderingAddressDto.setPostalCodeExt2("");
		cpiOrderingAddressDto.setPostalCodeExt3("");
		cpiOrderingAddressDto.setPOBox("");
		cpiOrderingAddressDto.setPOBoxwono(false);
		cpiOrderingAddressDto.setPOBoxCity("");
		cpiOrderingAddressDto.setPOCitNo("");
		cpiOrderingAddressDto.setPORegion("");
		cpiOrderingAddressDto.setPOboxcountry("");
		cpiOrderingAddressDto.setISOcode("");
		cpiOrderingAddressDto.setDeliveryDist("");
		cpiOrderingAddressDto.setTransportzone(bpBusinessPartnerOrderingAddressDto.getTransportZone() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getTransportZone());
		cpiOrderingAddressDto.setStreet(bpBusinessPartnerOrderingAddressDto.getStreet() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStreet());
		cpiOrderingAddressDto.setStreetCode("");
		cpiOrderingAddressDto.setStreetAbbrev("");
		cpiOrderingAddressDto.setHouseNumber(bpBusinessPartnerOrderingAddressDto.getHouseNo() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getHouseNo());
		cpiOrderingAddressDto.setSupplement(bpBusinessPartnerOrderingAddressDto.getSuppl() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getSuppl());
		cpiOrderingAddressDto.setNumberRange("");
		cpiOrderingAddressDto.setStreet2(bpBusinessPartnerOrderingAddressDto.getStreet2() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStreet2());
		cpiOrderingAddressDto.setStreet3(bpBusinessPartnerOrderingAddressDto.getStreet3() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStreet3());
		cpiOrderingAddressDto.setStreet4(bpBusinessPartnerOrderingAddressDto.getStreet4() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStreet4());
		cpiOrderingAddressDto.setStreet5(bpBusinessPartnerOrderingAddressDto.getStreet5() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getStreet5());
		cpiOrderingAddressDto.setBuildingCode(bpBusinessPartnerOrderingAddressDto.getBuildingCode() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getBuildingCode());
		cpiOrderingAddressDto.setFloor(bpBusinessPartnerOrderingAddressDto.getFloor() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getFloor());
		cpiOrderingAddressDto.setRoomNumber(bpBusinessPartnerOrderingAddressDto.getRoom() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getRoom());
		cpiOrderingAddressDto.setCountry(bpBusinessPartnerOrderingAddressDto.getCountry() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getCountry());
		cpiOrderingAddressDto.setCountryISO("");
		cpiOrderingAddressDto.setLanguage(bpBusinessPartnerOrderingAddressDto.getLanguage() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getLanguage());
		cpiOrderingAddressDto.setLangISO("");
		cpiOrderingAddressDto.setRegion(bpBusinessPartnerOrderingAddressDto.getRegion() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getRegion());
		cpiOrderingAddressDto.setSearchTerm1("");
		cpiOrderingAddressDto.setSearchTerm2("");
		cpiOrderingAddressDto.setDataline("");
		cpiOrderingAddressDto.setTelebox("");
		cpiOrderingAddressDto.setTimezone(bpBusinessPartnerOrderingAddressDto.getTimeZone() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getTimeZone());
		cpiOrderingAddressDto.setTaxJurisdictn(bpBusinessPartnerOrderingAddressDto.getTaxJurisdiction() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getTaxJurisdiction());
		cpiOrderingAddressDto.setAddressID("");
		cpiOrderingAddressDto.setCreationlang("");
		cpiOrderingAddressDto.setLangCRISO("");
		cpiOrderingAddressDto.setCommMethod(bpBusinessPartnerOrderingAddressDto.getCommMethod() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getCommMethod());
		cpiOrderingAddressDto.setAddressgroup("");
		cpiOrderingAddressDto.setDifferentCity(bpBusinessPartnerOrderingAddressDto.getDifferentCity() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getDifferentCity());
		cpiOrderingAddressDto.setCityCode("");
		cpiOrderingAddressDto.setUndeliverable(bpBusinessPartnerOrderingAddressDto.getUndeliverable() == null ? ""
				: bpBusinessPartnerOrderingAddressDto.getUndeliverable());
		cpiOrderingAddressDto.setUndeliverable1("");
		cpiOrderingAddressDto.setPOBoxLobby("");
		cpiOrderingAddressDto.setDelvryServType("");
		cpiOrderingAddressDto.setDeliveryServiceNo("");
		cpiOrderingAddressDto.setCountycode("");
		cpiOrderingAddressDto.setCounty("");
		cpiOrderingAddressDto.setTownshipcode("");
		cpiOrderingAddressDto.setTownship("");
		cpiOrderingAddressDto.setPAN("");
		return cpiOrderingAddressDto;
	}

	private CPIInvoicePartyDto convertExtend(
			BPBusinessPartnerRemittanceAddressDto bpBusinessPartnerRemittanceAddressDto) {
		CPIInvoicePartyDto cpiInvoicePartyDto = new CPIInvoicePartyDto();
		cpiInvoicePartyDto.setType("");
		cpiInvoicePartyDto.setValidation("");
		cpiInvoicePartyDto.setChangeIndObject("I");
		cpiInvoicePartyDto.setVendor("");
		cpiInvoicePartyDto.setTrainstation("");
		cpiInvoicePartyDto.setLocationno1("");
		cpiInvoicePartyDto.setLocationno2("");
		cpiInvoicePartyDto.setAuthorization("");
		cpiInvoicePartyDto.setIndustry("");
		cpiInvoicePartyDto.setCheckdigit("");
		cpiInvoicePartyDto.setDMEIndicator("");
		cpiInvoicePartyDto.setInstructionkey("");
		cpiInvoicePartyDto.setISRNumber("");
		cpiInvoicePartyDto.setCorporateGroup("");
		cpiInvoicePartyDto.setAccountgroup("");
		cpiInvoicePartyDto.setCustomer("");
		cpiInvoicePartyDto.setAlternatpayee("");
		cpiInvoicePartyDto.setDeletionflag(false);
		cpiInvoicePartyDto.setPostingBlock(false);
		cpiInvoicePartyDto.setPurchblock(false);
		cpiInvoicePartyDto.setTaxNumber1("");
		cpiInvoicePartyDto.setTaxNumber2("");
		cpiInvoicePartyDto.setEqualizatntax("");
		cpiInvoicePartyDto.setLiableforVAT(false);
		cpiInvoicePartyDto.setPayeeindoc(false);
		cpiInvoicePartyDto.setTradingPartner("");
		cpiInvoicePartyDto.setFiscaladdress("");
		cpiInvoicePartyDto.setVATRegNo("");
		cpiInvoicePartyDto.setNaturalperson("");
		cpiInvoicePartyDto.setBlockfunction("");
		cpiInvoicePartyDto.setAddress("");
		cpiInvoicePartyDto.setPlaceofbirth("");
		cpiInvoicePartyDto.setBirthdate("");
		cpiInvoicePartyDto.setSex("");
		cpiInvoicePartyDto.setCredinfono("");
		cpiInvoicePartyDto.setLastextreview("");
		cpiInvoicePartyDto.setActualQMsys("");
		cpiInvoicePartyDto.setRefacctgroup("");
		cpiInvoicePartyDto.setPlant("");
		cpiInvoicePartyDto.setPlant("");
		cpiInvoicePartyDto.setFactorycalend("");
		cpiInvoicePartyDto.setSCAC("");
		cpiInvoicePartyDto.setCarfreightgrp("");
		cpiInvoicePartyDto.setServAgntProcGrp("");
		cpiInvoicePartyDto.setTaxtype("");
		cpiInvoicePartyDto.setTaxnumbertype("");
		cpiInvoicePartyDto.setSocialIns(false);
		cpiInvoicePartyDto.setSocInsCode("");
		cpiInvoicePartyDto.setTaxNumber3("");
		cpiInvoicePartyDto.setTaxNumber4("");
		cpiInvoicePartyDto.setTaxsplit(false);
		cpiInvoicePartyDto.setTaxbase("");
		cpiInvoicePartyDto.setProfession("");
		cpiInvoicePartyDto.setStatgrpagent("");
		cpiInvoicePartyDto.setExternalmanuf("");
		cpiInvoicePartyDto.setDeletionblock(false);
		cpiInvoicePartyDto.setRepsName("");
		cpiInvoicePartyDto.setTypeofBusiness("");
		cpiInvoicePartyDto.setTypeofIndustry("");
		cpiInvoicePartyDto.setPODrelevant("");
		cpiInvoicePartyDto.setTaxoffice("");
		cpiInvoicePartyDto.setTaxNumber("");
		cpiInvoicePartyDto.setTaxNumber5("");
		cpiInvoicePartyDto.setPurposeCompleteFlag("");
		cpiInvoicePartyDto.setAddressVersion("");
		cpiInvoicePartyDto.setFrom("/Date(253402214400000)/");
		cpiInvoicePartyDto.setTo("/Date(253402214400000)/");
		cpiInvoicePartyDto.setTitle(bpBusinessPartnerRemittanceAddressDto.getTitle() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getTitle());
		cpiInvoicePartyDto.setName(bpBusinessPartnerRemittanceAddressDto.getName1() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getName1());
		cpiInvoicePartyDto.setName2(bpBusinessPartnerRemittanceAddressDto.getName2() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getName2());
		cpiInvoicePartyDto.setName3(bpBusinessPartnerRemittanceAddressDto.getName3() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getName3());
		cpiInvoicePartyDto.setName4(bpBusinessPartnerRemittanceAddressDto.getName4() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getName4());
		cpiInvoicePartyDto.setConvname("");
		cpiInvoicePartyDto.setCo(bpBusinessPartnerRemittanceAddressDto.getCo() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getCo());
		cpiInvoicePartyDto.setCity(bpBusinessPartnerRemittanceAddressDto.getCity() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getCity());
		cpiInvoicePartyDto.setDistrict(bpBusinessPartnerRemittanceAddressDto.getDistrict() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getDistrict());
		cpiInvoicePartyDto.setCityNo("");
		cpiInvoicePartyDto.setCheckStatus("");
		cpiInvoicePartyDto.setRegStrGrp(bpBusinessPartnerRemittanceAddressDto.getRegStructGrp() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getRegStructGrp());
		cpiInvoicePartyDto.setPostalCode(bpBusinessPartnerRemittanceAddressDto.getPostalCode() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getPostalCode());
		cpiInvoicePartyDto.setPOBoxPostCde("");
		cpiInvoicePartyDto.setCompanyPostCd("");
		cpiInvoicePartyDto.setPostalCodeExt("");
		cpiInvoicePartyDto.setPostalCodeExt2("");
		cpiInvoicePartyDto.setPostalCodeExt3("");
		cpiInvoicePartyDto.setPOBox("");
		cpiInvoicePartyDto.setPOBoxwono(false);
		cpiInvoicePartyDto.setPOBoxCity("");
		cpiInvoicePartyDto.setPOCitNo("");
		cpiInvoicePartyDto.setPORegion("");
		cpiInvoicePartyDto.setPOboxcountry("");
		cpiInvoicePartyDto.setISOcode("");
		cpiInvoicePartyDto.setDeliveryDist("");
		cpiInvoicePartyDto.setTransportzone(bpBusinessPartnerRemittanceAddressDto.getTransportZone() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getTransportZone());
		cpiInvoicePartyDto.setStreet(bpBusinessPartnerRemittanceAddressDto.getStreet() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStreet());
		cpiInvoicePartyDto.setStreetCode("");
		cpiInvoicePartyDto.setStreetAbbrev("");
		cpiInvoicePartyDto.setHouseNumber(bpBusinessPartnerRemittanceAddressDto.getHouseNo() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getHouseNo());
		cpiInvoicePartyDto.setSupplement(bpBusinessPartnerRemittanceAddressDto.getSuppl() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getSuppl());
		cpiInvoicePartyDto.setNumberRange("");
		cpiInvoicePartyDto.setStreet2(bpBusinessPartnerRemittanceAddressDto.getStreet2() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStreet2());
		cpiInvoicePartyDto.setStreet3(bpBusinessPartnerRemittanceAddressDto.getStreet3() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStreet3());
		cpiInvoicePartyDto.setStreet4(bpBusinessPartnerRemittanceAddressDto.getStreet4() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStreet4());
		cpiInvoicePartyDto.setStreet5(bpBusinessPartnerRemittanceAddressDto.getStreet5() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStreet5());
		cpiInvoicePartyDto.setBuildingCode(bpBusinessPartnerRemittanceAddressDto.getBuildingCode() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getBuildingCode());
		cpiInvoicePartyDto.setFloor(bpBusinessPartnerRemittanceAddressDto.getFloor() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getFloor());
		cpiInvoicePartyDto.setRoomNumber(bpBusinessPartnerRemittanceAddressDto.getRoom() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getRoom());
		cpiInvoicePartyDto.setCountry(bpBusinessPartnerRemittanceAddressDto.getCountry() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getCountry());
		cpiInvoicePartyDto.setCountryISO("");
		cpiInvoicePartyDto.setLanguage(bpBusinessPartnerRemittanceAddressDto.getLanguage() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getLanguage());
		cpiInvoicePartyDto.setLangISO("");
		cpiInvoicePartyDto.setRegion(bpBusinessPartnerRemittanceAddressDto.getRegion() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getRegion());
		cpiInvoicePartyDto.setSearchTerm1("");
		cpiInvoicePartyDto.setSearchTerm2("");
		cpiInvoicePartyDto.setDataline("");
		cpiInvoicePartyDto.setTelebox("");
		cpiInvoicePartyDto.setTimezone(bpBusinessPartnerRemittanceAddressDto.getTimeZone() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getTimeZone());
		cpiInvoicePartyDto.setTaxJurisdictn(bpBusinessPartnerRemittanceAddressDto.getTaxJurisdiction() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getTaxJurisdiction());
		cpiInvoicePartyDto.setAddressID("");
		cpiInvoicePartyDto.setCreationlang("");
		cpiInvoicePartyDto.setLangCRISO("");
		cpiInvoicePartyDto.setCommMethod(bpBusinessPartnerRemittanceAddressDto.getStandardCommMethod() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getStandardCommMethod());
		cpiInvoicePartyDto.setAddressgroup("");
		cpiInvoicePartyDto.setDifferentCity(bpBusinessPartnerRemittanceAddressDto.getDifferentCity() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getDifferentCity());
		cpiInvoicePartyDto.setCityCode("");
		cpiInvoicePartyDto.setUndeliverable(bpBusinessPartnerRemittanceAddressDto.getUndeliverable() == null ? ""
				: bpBusinessPartnerRemittanceAddressDto.getUndeliverable());
		cpiInvoicePartyDto.setUndeliverable1("");
		cpiInvoicePartyDto.setPOBoxLobby("");
		cpiInvoicePartyDto.setDelvryServType("");
		cpiInvoicePartyDto.setDeliveryServiceNo("");
		cpiInvoicePartyDto.setCountycode("");
		cpiInvoicePartyDto.setCounty("");
		cpiInvoicePartyDto.setTownshipcode("");
		cpiInvoicePartyDto.setTownship("");
		cpiInvoicePartyDto.setPAN("");
		return cpiInvoicePartyDto;
	}
}