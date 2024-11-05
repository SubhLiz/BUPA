package com.incture.bupa.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.incture.bupa.utils.DestinationUtil;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.jdbc.CfJdbcEnv;


@Service
public class OdataService {
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
	public  String encodeUsernameAndPassword(String username, String password) {
		String encodeUsernamePassword = username + ":" + password;
		return "Basic " + DatatypeConverter.printBase64Binary(encodeUsernamePassword.getBytes());
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
	public ResponseEntity<?> getVendorDetails(String vendorNo) {
		try {

//			String unencodedFilter = "( Vendor , '2034966' )&$expand=ToAddressData,ToCompanyData/ToWtax,ToCompanyData/ToDunningData,ToContact,ToEmail,ToPhone,ToPurchaseOrgData/ToPlant,ToPurchaseOrgData/ToOderingAddress,ToPurchaseOrgData/ToInvoiceParty,ToBank,ToFax,ToReturnMessage,ToClassification/ToClassificationItem&$format=json";
//			String encodedFilter = URLEncoder.encode(unencodedFilter, StandardCharsets.UTF_8.toString());
//			String url="/sap/opu/odata/sap/ZVM_API_SRV/GeneralDataSet?$filter=substringof"+encodedFilter;
			String url="/sap/opu/odata/SAP/ZVM_API_SRV/GeneralDataSet?$filter=substringof(Vendor%2C%20%27"+vendorNo+"%27)&$expand=ToAddressData%2CToCompanyData%2FToWtax%2CToCompanyData%2CToContact%2CToEmail%2CToPhone%2CToPurchaseOrgData%2FToPlant%2CToPurchaseOrgData%2FToOderingAddress%2CToPurchaseOrgData%2FToInvoiceParty%2CToBank%2CToFax%2CToReturnMessage%2CToClassification%2FToClassificationItem&$format=json&$top=10&$skip=0&$inlinecount=allpages";
			logger.info("[OdataHelperClass][getCompanyCodeDetails] url" + url);

			ResponseEntity<?> responseEnity = getDetailsfromOdata(url, null, "GET");
			return responseEnity;

		} catch (Exception e) {
			logger.error("[OdataHelperClass][getCompanyCodeDetails] error " + e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
public String getVendorDataFromOdata(String vendorNo) {
		
		ResponseEntity<?> res = getVendorDetails(vendorNo);
		String responseStr = res.toString();
		System.out.println(responseStr);
	return responseStr;
}
}
