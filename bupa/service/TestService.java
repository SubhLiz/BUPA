package com.incture.bupa.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incture.bupa.dto.BPDMSAttachmentsDto;
import com.incture.bupa.utils.DestinationUtil;
import com.incture.bupa.utils.HelperClass;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.jdbc.CfJdbcEnv;


@Service
public class TestService {
	
	private static String eccProxyHost;

    private static Integer eccProxyPort;

    private static String odataUser;

    private static String odataPassword;
    
    private static String odataEccURL;
    
    private static String odataSapEccClient;
    
    private static String odataCloudConnectorLocationId;
    
    private static String connectivitytokenURL;
    
    private static String connectivityClientId;
    
    private static String connectivityClientSecret;
    
    
    
    
    
    
    
    
    private static final Logger logger = LoggerFactory.getLogger(TestService.class);
    @Autowired
	private DestinationUtil destinationUtil;
    @Autowired
    private BPFileService bPFileService;
	
//	@Value("${ecc.onpremise-proxy-host}")
//	private String proxyhost;
//	
//	@Value("${ecc.onpremise-proxy-port}")
//	private int proxyport;
//	
//	@Value("${ecc.user}")
//	private String user;
//	
//	@Value("${ecc.password}")
//	private String password;
//	
//	@Value("${ecc.url}")
//	private String eccURL;
//	
//	@Value("${ecc.cloudConnectorLocationId}")
//	private String cloudConnectorLocationId;
//	
//	@Value("${connectivity-instance.tokenurl}")
//	private String tokenURL;
//	
//	@Value("${connectivity-instance.clientid}")
//	private String clientId;
//	
//	@Value("${connectivity-instance.clientsecret}")
//	private String clientSecret;
	
	
	
	
	private  String globalToken = null;
	
	private HashMap<String,String> getDestinationDetails() throws ClientProtocolException, IOException {
		HashMap<String, String> hashMap = new HashMap<>();
		String destDetails = destinationUtil.readMdgDestination("vm-fiori-odata-basic", null, null);
		System.out.println("****");

        org.json.JSONObject resObj = new org.json.JSONObject(destDetails);
        logger.info("Json object from destination :"+resObj);
        
        eccProxyHost =resObj.optJSONObject("destinationConfiguration").optString("onpremise-proxy-host");
        eccProxyPort=Integer.parseInt(resObj.optJSONObject("destinationConfiguration").optString("onpremise-proxy-port"));
        odataUser=resObj.optJSONObject("destinationConfiguration").optString("User");
        odataPassword=resObj.optJSONObject("destinationConfiguration").optString("Password");
        odataEccURL=resObj.optJSONObject("destinationConfiguration").optString("URL");
        odataSapEccClient=resObj.optJSONObject("destinationConfiguration").optString("sap-client");
        odataCloudConnectorLocationId=resObj.optJSONObject("destinationConfiguration").optString("CloudConnectorLocationId");
        hashMap.put("eccProxyHost",eccProxyHost);
        hashMap.put("eccProxyPort",eccProxyPort.toString());
        hashMap.put("odataUser",odataUser);
        hashMap.put("odataPassword",odataPassword);
        hashMap.put("odataEccURL",odataEccURL);
        hashMap.put("odataSapEccClient",odataSapEccClient);
        hashMap.put("odataCloudConnectorLocationId",odataCloudConnectorLocationId);
        
        
        System.out.println("****");
		return hashMap;
	}
	
	private  Header[] getAccessToken(String url, String username, String password, CloseableHttpClient  client,
            String proxyHost, int proxyPort, String sapClient, String token)
            throws ClientProtocolException, IOException {
		
		
		
        
        


        
        System.out.println("****");

        HttpGet httpGet = new HttpGet(url);

        String userpass = username + ":" + password;

        httpGet.setHeader("Proxy-Authorization", "Bearer " + token);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,
                "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes()));
        httpGet.addHeader("X-CSRF-Token", "Fetch");
        httpGet.addHeader("Content-Type", "application/json");
        httpGet.addHeader("sap-client", sapClient);
        httpGet.addHeader("SAP-Connectivity-SCC-Location_ID", "DEVHEC");
        
        CloseableHttpResponse  response = client.execute(httpGet);
        
        logger.info("313 response {}", response);

        return response.getAllHeaders();

    }
	public  String encodeUsernameAndPassword(String username, String password) {
		String encodeUsernamePassword = username + ":" + password;
		return "Basic " + DatatypeConverter.printBase64Binary(encodeUsernamePassword.getBytes());
	}
	public HashMap<String,String> instanceDetails() throws JsonMappingException, JsonProcessingException {
		HashMap<String, String> instanceDetails = new HashMap<>();
		CfJdbcEnv cfJdbcEnv = new CfJdbcEnv();
		CfCredentials cfCredentials = cfJdbcEnv.findCredentialsByTag("connectivity");

		Map<String, Object> map = cfCredentials.getMap();

		connectivitytokenURL = (String) map.get("url")+"/oauth/token?grant_type=client_credentials";
		
//		tokenUrl = (String) map.get("url");
		connectivityClientId = (String) map.get("clientid");
		connectivityClientSecret = (String) map.get("clientsecret");
		instanceDetails.put("connectivitytokenURL", connectivitytokenURL);
		instanceDetails.put("connectivityClientId", connectivityClientId);
		instanceDetails.put("connectivityClientSecret", connectivityClientSecret);
		return instanceDetails;
		

	}
	public  String getConectivityProxy() throws URISyntaxException, IOException {
		HashMap<String, String> instanceDetails=instanceDetails();
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(instanceDetails.get("connectivitytokenURL"));
        String auth = encodeUsernameAndPassword(instanceDetails.get("connectivityClientId"),
        		instanceDetails.get("connectivityClientSecret"));
        httpPost.addHeader("Authorization", auth);
        HttpResponse res = client.execute(httpPost);
        String data = getDataFromStream(res.getEntity().getContent());
        if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
            String jwtToken = new JSONObject(data).getString("access_token");
            logger.info("***%%%");
            logger.info(jwtToken);
            logger.info("***%%%");
            return jwtToken;
        }
        return null;
    }
	public  String getDataFromStream(InputStream stream) throws IOException {
		StringBuilder dataBuffer = new StringBuilder();
		BufferedReader inStream = new BufferedReader(new InputStreamReader(stream));
		String data = "";

		while ((data = inStream.readLine()) != null) {
			dataBuffer.append(data);
		}
		inStream.close();
		return dataBuffer.toString();
	}
	public  ResponseEntity<?> consumingOdataService(String url,  String entity,String method, String csrf) throws IOException, URISyntaxException {
		HashMap<String, String> destinationDetails = getDestinationDetails();
		
//        String proxyHost = proxyhost;
        String proxyHost = destinationDetails.get("eccProxyHost");
    
//        int proxyPort = proxyport;
        int proxyPort = Integer.parseInt(destinationDetails.get("eccProxyPort"));
        System.out.println(proxyHost+"######"+proxyPort);
        
//        String destinationName = (String)destinationInfo.get("Name");
//        if(destinationName.equalsIgnoreCase(environment.getProperty("ecc.destination.name")) ) {
//            proxyHost = environment.getProperty("ecc.onpremise.proxy.host");
//            proxyPort = Integer.parseInt(environment.getProperty("ecc.onpremise.proxy.port"));
//        } else {
//            proxyHost = environment.getProperty("s4hana.onpremise.proxy.host");
//            proxyPort = Integer.parseInt(environment.getProperty("s4hana.onpremise.proxy.port"));
//        }
        
        logger.info("proxyHost-- {}",  proxyHost);

        

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
//        credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(
//                user, password));
        credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(
        		destinationDetails.get("odataUser"), destinationDetails.get("odataPassword")));

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();

        clientBuilder.setProxy(new HttpHost(proxyHost, proxyPort))
                .setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy())
                .setDefaultCredentialsProvider(credsProvider).disableCookieManagement();

        CloseableHttpClient  httpClient = clientBuilder.build();
        HttpRequestBase httpRequestBase = null;
        String jsonMessage = "Empty BODY";
        HttpResponse httpResponse = null;
        StringEntity input = null;
        
        
        
        String jwToken = null;
        jwToken=getConectivityProxy();
        globalToken = jwToken;
        
        
        
        if (url != null) {
            if (method.equalsIgnoreCase("GET")) {
                httpRequestBase = new HttpGet(destinationDetails.get("odataEccURL") + url);
            } else if (method.equalsIgnoreCase("POST")) {
                httpRequestBase = new HttpPost(destinationDetails.get("odataEccURL") + url);
                try {

                    logger.info("entity {}", entity);
                    input = new StringEntity(entity);
                    input.setContentType("application/json");
                } catch (UnsupportedEncodingException e) {
                }
                logger.info("inputEntity {}",  input);
                ((HttpPost) httpRequestBase).setEntity(input);
            }
//            if (destinationInfo.get("sap-client") != null) {
                httpRequestBase.addHeader("sap-client", destinationDetails.get("odataSapEccClient"));
//            }
            httpRequestBase.addHeader("accept", "application/json");
            Header[] headers = getAccessToken(destinationDetails.get("odataEccURL") + url,
            		destinationDetails.get("odataUser"), destinationDetails.get("odataPassword"), httpClient,
                        proxyHost, proxyPort, destinationDetails.get("odataSapEccClient"), jwToken);

            
            
            String token = null;
            List<String> cookies = new ArrayList<>();
            if (headers.length != 0) {

                for (Header header : headers) {

                    if (header.getName().equalsIgnoreCase("x-csrf-token")) {
                        token = header.getValue();
                        logger.info("token --- {}", token);
                    }

                    if (header.getName().equalsIgnoreCase("set-cookie")) {
                        cookies.add(header.getValue());
                    }

                }
            }
            System.out.println("%%%%%"+odataUser+"%%%%%"+odataPassword);
            if (destinationDetails.get("odataUser") != null && destinationDetails.get("odataPassword") != null) {
                String encoded = encodeUsernameAndPassword(destinationDetails.get("odataUser"),
                		destinationDetails.get("odataPassword"));
                httpRequestBase.addHeader("Authorization", encoded);
                httpRequestBase.setHeader("Proxy-Authorization", "Bearer " + jwToken);
                httpRequestBase.addHeader("SAP-Connectivity-SCC-Location_ID",
                		destinationDetails.get("odataCloudConnectorLocationId"));

            }
            if (token != null) {
                httpRequestBase.addHeader("X-CSRF-Token", token);
            }
            if (!cookies.isEmpty()) {
                for (String cookie : cookies) {
                    String tmp = cookie.split(";", 2)[0];
                    httpRequestBase.addHeader("Cookie", tmp);
                }
            }

            
            try {
                logger.info("consuming odata try httpRequestBase :{}", httpRequestBase);

                long e1 = System.currentTimeMillis();
                httpResponse = httpClient.execute(httpRequestBase);
                long e2 = System.currentTimeMillis();
                logger.info("ECC call time : {}", (e2-e1));
                logger.info("consuming odata try httpResponse : {}", httpResponse);

                
                if (httpResponse.getStatusLine().getStatusCode() == 201) {
                    String dataFromStream = getDataFromStream(httpResponse.getEntity().getContent());
                    logger.info("created {}", dataFromStream);
                    return new ResponseEntity<>(dataFromStream, HttpStatus.CREATED);

                } else if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    String dataFromStream = getDataFromStream(httpResponse.getEntity().getContent());
                    logger.info("fetched{}", dataFromStream);
                    return new ResponseEntity<>(dataFromStream, HttpStatus.OK);
                } else {
                    String responseFromECC = getDataFromStream(httpResponse.getEntity().getContent());

                    logger.info("consuming odata try else responseFromEcc {}", responseFromECC);
                    return new ResponseEntity<>(responseFromECC,
                            HttpStatus.BAD_REQUEST);
                }

            } catch (IOException e) {
                logger.error("[Reusability][consumingOdataService] {}", e.getMessage());
                return new ResponseEntity<>("Exception in ODATA consumtion block" + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }

        return new ResponseEntity<String>(jsonMessage, HttpStatus.OK);

    }
	public  ResponseEntity<?> getDetailsfromOdata(String url, String entity, String method) {
        try {
            logger.info(" Request URL : {}", url);
            return consumingOdataService(url,  entity,method, null);
        } catch (Exception e) {
            logger.error("[Reusability][getDetailsfromOdata] error {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	 
	
	 
	
	public ResponseEntity<?> getCompanyCodeDetails(Optional<String> name) {
		try {

//			String unencodedFilter = "( Vendor , '2034966' )&$expand=ToAddressData,ToCompanyData/ToWtax,ToCompanyData/ToDunningData,ToContact,ToEmail,ToPhone,ToPurchaseOrgData/ToPlant,ToPurchaseOrgData/ToOderingAddress,ToPurchaseOrgData/ToInvoiceParty,ToBank,ToFax,ToReturnMessage,ToClassification/ToClassificationItem&$format=json";
//			String encodedFilter = URLEncoder.encode(unencodedFilter, StandardCharsets.UTF_8.toString());
//			String url="/sap/opu/odata/sap/ZVM_API_SRV/GeneralDataSet?$filter=substringof"+encodedFilter;
			String url="/sap/opu/odata/SAP/ZVM_API_SRV/GeneralDataSet?$filter=substringof(Vendor%2C%20%272033776%27)&$expand=ToAddressData%2CToCompanyData%2FToWtax%2CToCompanyData%2CToContact%2CToEmail%2CToPhone%2CToPurchaseOrgData%2FToPlant%2CToPurchaseOrgData%2FToOderingAddress%2CToPurchaseOrgData%2FToInvoiceParty%2CToBank%2CToFax%2CToReturnMessage%2CToClassification%2FToClassificationItem&$format=json&$top=10&$skip=0&$inlinecount=allpages";
			logger.info("[OdataHelperClass][getCompanyCodeDetails] url" + url);

			ResponseEntity<?> responseEnity = getDetailsfromOdata(url, null, "GET");
			return responseEnity;

		} catch (Exception e) {
			logger.error("[OdataHelperClass][getCompanyCodeDetails] error " + e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	public ResponseEntity<?> getVendorDetails(String vendorNo) {
		try {

//			String unencodedFilter = "( Vendor , '2034966' )&$expand=ToAddressData,ToCompanyData/ToWtax,ToCompanyData/ToDunningData,ToContact,ToEmail,ToPhone,ToPurchaseOrgData/ToPlant,ToPurchaseOrgData/ToOderingAddress,ToPurchaseOrgData/ToInvoiceParty,ToBank,ToFax,ToReturnMessage,ToClassification/ToClassificationItem&$format=json";
//			String encodedFilter = URLEncoder.encode(unencodedFilter, StandardCharsets.UTF_8.toString());
//			String url="/sap/opu/odata/sap/ZVM_API_SRV/GeneralDataSet?$filter=substringof"+encodedFilter;
			
			
		//	String url="/sap/opu/odata/SAP/ZVM_API_SRV/GeneralDataSet?$filter=substringof(Vendor%2C%20%27"+vendorNo+"%27)&$expand=ToAddressData%2CToCompanyData%2FToWtax%2CToCompanyData%2CToContact%2CToEmail%2CToPhone%2CToPurchaseOrgData%2FToPlant%2CToPurchaseOrgData%2FToOderingAddress%2CToPurchaseOrgData%2FToInvoiceParty%2CToBank%2CToFax%2CToReturnMessage%2CToClassification%2FToClassificationItem&$format=json&$top=10&$skip=0&$inlinecount=allpages";
			
			//String url = "/sap/opu/odata/SAP/ZVM_API_SRV/GeneralDataSet?$filter=(Vendor eq '" + vendorNo + "')&$expand=ToAddressData,ToCompanyData/ToWtax,ToCompanyData/ToDunningData,ToContact/ToContactEmail,ToContact/ToContactPhone,ToEmail,ToPhone,ToPurchaseOrgData/ToPlant,ToPurchaseOrgData/ToOderingAddress,ToPurchaseOrgData/ToInvoiceParty,ToBank,ToFax,ToReturnMessage,ToClassification/ToClassificationItem&$format=json&$top=10&$skip=0&$inlinecount=allpages";

			// Changes by AMS team
//						String vendorId="";
//						System.out.println("Vendor Number from UI"+vendorNo);
//						if (vendorNo.matches(".*\\D.*")) {
//						  vendorId = vendorNo;
//						        } else if (vendorNo.length() < 10 && vendorNo.matches("\\d+")) {
//						      
//						            int zerosToAdd = 10 - vendorNo.length();
//						           System.out.println("Zero to Add "+zerosToAdd);
//						            vendorId = "0".repeat(zerosToAdd) + vendorNo;
//						            System.out.println("Vendor Id from OData"+vendorId);
//						        }
//						String url="/sap/opu/odata/SAP/ZVM_API_SRV/GeneralDataSet?$filter=(Vendor%20eq%20%2C%20%27"+vendorId+"%27)&$expand=ToAddressData%2CToCompanyData%2FToWtax%2CToCompanyData%2CToContact%2CToEmail%2CToPhone%2CToPurchaseOrgData%2FToPlant%2CToPurchaseOrgData%2FToOderingAddress%2CToPurchaseOrgData%2FToInvoiceParty%2CToBank%2CToFax%2CToReturnMessage%2CToClassification%2FToClassificationItem&$format=json&$top=10&$skip=0&$inlinecount=allpages";
			 
			String vendorId="";
			System.out.println("Vendor Number from UI"+vendorNo);
			if (vendorNo.matches(".*\\D.*")) {
			  vendorId = vendorNo;
			        } else if (vendorNo.length() < 10 && vendorNo.matches("\\d+")) {
			            int zerosToAdd = 10 - vendorNo.length();
			           System.out.println("Zero to Add "+zerosToAdd);
//			            vendorId = "0".repeat(zerosToAdd) + vendorNo;
//			           StringBuilder sb = new StringBuilder();
//			            for (int i = 0; i < zerosToAdd; i++) {
//			                sb.append('0');
//			            }
			           vendorId = String.format("%0" + zerosToAdd + "d", 0) + vendorNo;
//			            vendorId = sb.toString() + vendorNo;
			            System.out.println("Vendor Id from OData: " + vendorId);
			             
			        }
			String url="/sap/opu/odata/SAP/ZVM_API_SRV/GeneralDataSet?$filter=(Vendor%20eq%20%27"+vendorId+"%27)&$expand=ToAddressData%2CToCompanyData%2FToWtax%2CToCompanyData%2CToContact%2CToEmail%2CToPhone%2CToPurchaseOrgData%2FToPlant%2CToPurchaseOrgData%2FToOderingAddress%2CToPurchaseOrgData%2FToInvoiceParty%2CToBank%2CToFax%2CToReturnMessage%2CToClassification%2FToClassificationItem&$format=json&$top=10&$skip=0&$inlinecount=allpages";
			
			
			
			logger.info("[OdataHelperClass][getCompanyCodeDetails] url" + url);

			ResponseEntity<?> responseEnity = getDetailsfromOdata(url, null, "GET");
			return responseEnity;

		} catch (Exception e) {
			logger.error("[OdataHelperClass][getCompanyCodeDetails] error " + e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	 

	public ResponseEntity<Object> getDataFromOdata() throws ClientProtocolException, IOException {
		    getDestinationDetails();
			
			ResponseEntity<?> res = getCompanyCodeDetails(null);
			String responseStr = res.toString();
			System.out.println(responseStr);
			if (responseStr.contains("200")) {
				responseStr = responseStr.replace("<200 OK OK,", "").replace(",[]>", "");
				System.out.println("****");
				System.out.println(responseStr);
				System.out.println("****");
				org.json.JSONObject jsonObject = new org.json.JSONObject(responseStr);
				org.json.JSONObject myResponse = jsonObject.getJSONObject("d");
				org.json.JSONObject result = (org.json.JSONObject) ((JSONArray) myResponse.get("results")).get(0);
				org.json.JSONObject toBank = (org.json.JSONObject) ((JSONArray) result.getJSONObject("ToBank")
						.get("results")).get(0);
				int toBank1 =  ((JSONArray) result.getJSONObject("ToBank")
						.get("results")).length();
				Map<String, String> bankData = new HashMap<>();
				bankData.put("BankCountry", toBank.getString("BankCountry"));
				bankData.put("Banknumber", toBank.getString("Banknumber"));
				System.out.println("####");
				System.out.println(bankData+"   "+toBank1);
				System.out.println("####");
				
			}
		return null;
	}
	public String getVendorDataFromOdata(String vendorNo) {
		
		ResponseEntity<?> res = getVendorDetails(vendorNo);
		String responseStr = res.toString();
		System.out.println(responseStr);
	return responseStr;
}
 

	public ResponseEntity<Object> docUpload() {
		// TODO Auto-generated method stub
		logger.info("[SalesOrderOdataServices][sendFileToECC] start{}");
		try {
			HashMap<String, String> destinationDetails = getDestinationDetails();
			String response = null;
			String odataURL = destinationDetails.get("odataEccURL") + "/AttachmentContentSet";
			String slug = "0002035267:3411067_VeryHigh-BTP Security Vulnerability Check.pdf";
			logger.info("[sendFileToECC] slug{}: " + slug);
			logger.info("[sendFileToECC] odataURL{}: " + odataURL);
			ObjectMapper mapper = new ObjectMapper();
//			
			List<BPDMSAttachmentsDto>ls=bPFileService.getAttachmentsByRequestID("2640");
//			System.out.println("****");
//			System.out.println(ls.get(0).getEncodedFileContent());
//			System.out.println("****");
//			FileInputStream fis = new FileInputStream(ls.get(0).getEncodedFileContent());
//			
//		    String stringTooLong = IOUtils.toString(fis, "UTF-8");
			String body = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ls.get(0).getEncodedFileContent());
//			logger.info("body{}: " + body);
			String contentType = "application/pdf";

				response = callOdata(odataURL, "POST", body, "fetch", slug,
						null, contentType);
			

			logger.info("[OdataServices][sendFileToECC] Response {}: " + response);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	public String callOdata(String serviceUrl, String methodType, String body, String csrfToken, String slug,
			String appToken, String contentType) throws Exception {
		HashMap<String, String> destRep = getDestinationDetails();

		logger.info("[VendorMasterOdataUtilService][callOdata] begins!");
		StringBuilder response = new StringBuilder();
		String XCSRF = null;
		String cookie = null;


		logger.info("dest repo details{}: " + destRep);
		try {
//			String userPassword = destRep.get("User") + ":" + destRep.get("Password");
			String userPassword = destRep.get("odataUser") + ":" + destRep.get("odataPassword");

			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.4.5",
					20003));
			if (methodType.equals("POST")) {
				Thread.sleep(10000);
				URL proxyURL_GET = new URL(serviceUrl);
				logger.info("[VendorMasterOdataUtilService][callOdata] proxyURL_GET url{}: " + proxyURL_GET);
				HttpURLConnection conn_GET = (HttpURLConnection) proxyURL_GET.openConnection(proxy);
				String jwToken = null;
		        jwToken=getConectivityProxy();
		        globalToken = jwToken;
				conn_GET.setRequestProperty("Proxy-Authorization",
						"Bearer " + jwToken);
				conn_GET.setRequestProperty("SAP-Connectivity-SCC-Location_ID",
						destRep.get("odataCloudConnectorLocationId"));
				conn_GET.setRequestProperty("x-csrf-token", "fetch");
				conn_GET.setRequestProperty(HttpHeaders.AUTHORIZATION,
						"Basic " + DatatypeConverter.printBase64Binary(userPassword.getBytes()));
				conn_GET.setRequestMethod("GET");
				
				conn_GET.connect();

				logger.info("[VendorMasterOdataUtilService][callOdata] connection status code{}: "
						+ conn_GET.getResponseCode() + "\nResponse msg{}: " + conn_GET.getResponseMessage()
						+ "\nComplete response{}: " + conn_GET.getHeaderFields());

				if (conn_GET.getResponseCode() == HttpURLConnection.HTTP_OK) {
					XCSRF = conn_GET.getHeaderField("x-csrf-token");
					cookie = conn_GET.getHeaderField("Set-Cookie").split(";", 2)[0];

					logger.info("[SalesOrderOdataUtilService][callOdata] x-csrf-token{}: " + XCSRF);
				}
				conn_GET.disconnect();
			}

			URL requestURL = new URL(serviceUrl);
			logger.info("[VendorMasterOdataUtilService][callOdata] REQUESTURL{}: " + requestURL);
			HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection(proxy);
			String jwToken = null;
	        jwToken=getConectivityProxy();
	        globalToken = jwToken;
			conn.setRequestProperty("Proxy-Authorization",
					"Bearer " + jwToken);
			conn.setRequestProperty("SAP-Connectivity-SCC-Location_ID",
					destRep.get("odataCloudConnectorLocationId"));
			conn.setRequestProperty("Authorization",
					"BASIC " + DatatypeConverter.printBase64Binary(userPassword.getBytes()));

			if (methodType.equals("POST")) {
				conn.setRequestProperty("x-csrf-token", XCSRF);
				conn.setRequestProperty("Cookie", cookie);
			} else
				conn.setRequestProperty("x-csrf-token", "fetch");

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			if (!HelperClass.isEmpty(contentType)) {
				conn.setRequestProperty("Content-Type", contentType);
			} else {
				conn.setRequestProperty("Content-Type", "application/json");
			}
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Slug", slug);
			conn.connect();

			if (methodType.equals("POST")) {
				try {
					OutputStream os = conn.getOutputStream();
					os.write(body.getBytes());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			logger.info("[VendorMasterOdataUtilService][callOdata] conn status code{}: " + conn.getResponseCode()
					+ "\nResponse msg{}: " + conn.getResponseMessage() + "\nComplete response{}: "
					+ conn.getHeaderFields());

			if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 207) {
				logger.info("[VendorMasterOdataUtilService][callOdata] Reading Data From Response");
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
			} else {
				logger.info("[VendorMasterOdataUtilService][callOdata] Error Code: " + conn.getResponseCode());

				logger.info("[VendorMasterOdataUtilService][callOdata] Reading data from error");
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				String responseLine = null;
				StringBuilder response1 = new StringBuilder();
				while ((responseLine = br.readLine()) != null) {

					response1.append(responseLine.trim());

				}
				if (conn.getResponseCode() == 502 || conn.getResponseCode() == 503) {
					// move message to unprocessed folder
				}
				return response1.toString();
			}
			conn.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[VendorMasterOdataUtilService][callOdata] error message{}: " + e.getMessage());
		}

		return response.toString();

	}

}
