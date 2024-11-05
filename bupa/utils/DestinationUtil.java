package com.incture.bupa.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.naming.NamingException;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.jdbc.CfJdbcEnv;

@Component
public class DestinationUtil {
	private static String uri;

    private static String tokenUrl;

    private static String clientId;

    private static String clientSecret;
	
//	@Value("${mdg-destination.uri}")
//	private String mdgUri;
//	@Value("${mdg-destination.url}")
//	private String mdgTokenUrl;
//	@Value("${mdg-destination.clientid}")
//	private String mdgClientId;
//	@Value("${mdg-destination.clientsecret}")
//	private String mdgClientSecret;

	
	private static final Logger logger = LoggerFactory.getLogger(DestinationUtil.class);
	
	public String destinationClient;


	public String destinationServiceUrl;


	public String destinationSecret;

	public String dsetinationTokenUrl;

	public String destinationAccessToken;

	public Map<String, String> destinationMap;
	
	public ResponseEntity<String> execute(String destinationName, String suffixUrlPath, String method,
			String jsonString, Map<String, String> headers) throws IOException {

		System.err.println("DestinationUtil.execute():: jsonString:: " + jsonString);

		// read destination data
		destinationMap = new HttpClient().readDestination(dsetinationTokenUrl, destinationClient, destinationSecret,
				destinationServiceUrl, destinationName);
		System.out.println("Destination Data:::  DestinationUtil.execute() line 147" + destinationMap);

		String urlValue = destinationMap.get("URL") + suffixUrlPath;
		
		String tokenUrl = destinationMap.get("tokenServiceURL") + "?grant_type=client_credentials";
		String clientId = destinationMap.get("clientId");
		String clientSecret = destinationMap.get("clientSecret");

		HttpClient client = new HttpClient();

		// return client.execute(urlValue, HttpMethod.valueOf(method),
		// jsonString, tokenUrl, clientId, clientSecret,
		// headers);

		if (!HttpMethod.PATCH.toString().equals(method)) {
			System.out.println("****IN PATCH*****");
			return client.execute(urlValue, HttpMethod.valueOf(method), jsonString, tokenUrl, clientId, clientSecret,
					headers);
		} else {
			System.out.println("****IN ELSE*****");
			Response r = client.executeUsingokHttpClientRest(urlValue, HttpMethod.valueOf(method), jsonString, tokenUrl,
					clientId, clientSecret, headers);
			return ResponseEntity.status(r.code())
					.headers(new HttpHeaders(new LinkedMultiValueMap<>(r.headers().toMultimap()))).body("");
		}
	}
	public Map<String, String> execute1(String destinationName, String suffixUrlPath, String method,
			String jsonString, Map<String, String> headers) throws IOException {

		System.err.println("DestinationUtil.execute():: jsonString:: " + jsonString);

		// read destination data
		destinationMap = new HttpClient().readDestination(dsetinationTokenUrl, destinationClient, destinationSecret,
				destinationServiceUrl, destinationName);

		System.out.println("Destination Data::: " + destinationMap);

//		String urlValue = destinationMap.get("URL") + suffixUrlPath;
//		String tokenUrl = destinationMap.get("tokenServiceURL") + "?grant_type=client_credentials";
//		String clientId = destinationMap.get("clientId");
//		String clientSecret = destinationMap.get("clientSecret");
		return destinationMap;

	}
	public static String encodeUsernameAndPassword(String username, String password) {
        String encodeUsernamePassword = username + ":" + password;
        return "Basic " + DatatypeConverter.printBase64Binary(encodeUsernamePassword.getBytes());
    }
	public static String getDataFromStream(InputStream stream) throws IOException {
        StringBuilder dataBuffer = new StringBuilder();
        BufferedReader inStream = new BufferedReader(new InputStreamReader(stream));
        String data = "";
        while ((data = inStream.readLine()) != null) {
            dataBuffer.append(data);
        }
        inStream.close();
        return dataBuffer.toString();
    }
	
	public static boolean isEmpty(Object obj) {
        if (String.valueOf(obj).equals("null"))
            return true;
        else if (obj == null || obj.equals("NULL"))
            return true;
        else if (obj.toString().equals(""))
            return true;
        return false;
    }



   public static boolean isEmpty(Object[] objs) {
        if (objs == null || objs.length == 0) {
            return true;
        }
        return false;
    }



   public static boolean isEmpty(String str) {
        if (str == null || str.trim().isEmpty()) {
            return true;
        }
        return false;
    }
   
   
   

	public String accessToken() throws JsonMappingException, JsonProcessingException {

		CfJdbcEnv cfJdbcEnv = new CfJdbcEnv();
		CfCredentials cfCredentials = cfJdbcEnv.findCredentialsByTag("destination");

		Map<String, Object> map = cfCredentials.getMap();

		uri = (String) map.get("uri");
		tokenUrl = (String) map.get("url");
		clientId = (String) map.get("clientid");
		clientSecret = (String) map.get("clientsecret");

		String url = tokenUrl + "/oauth/token?grant_type=client_credentials";
		RestTemplate template = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		com.nimbusds.jose.util.Base64 encode = com.nimbusds.jose.util.Base64.encode(clientId + ":" + clientSecret);
		headers.add("Authorization", "Basic " + encode.toString());
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = template.postForEntity(url, entity, String.class);

		return new ObjectMapper().readTree(response.getBody()).get("access_token").asText();
	}
	
//
//	public String accessMdgToken() throws JsonMappingException, JsonProcessingException {
//
//		System.out.println("****"+mdgTokenUrl+"*****");
//		String url = mdgTokenUrl + "/oauth/token?grant_type=client_credentials";
//		RestTemplate template = new RestTemplate();
//		HttpHeaders headers = new HttpHeaders();
//		com.nimbusds.jose.util.Base64 encode = com.nimbusds.jose.util.Base64.encode(mdgClientId + ":" + mdgClientSecret);
//		headers.add("Authorization", "Basic " + encode.toString());
//		HttpEntity<String> entity = new HttpEntity<>(headers);
//		ResponseEntity<String> response = template.postForEntity(url, entity, String.class);
//
//		return new ObjectMapper().readTree(response.getBody()).get("access_token").asText();
//	}
//	public String readDestination(String destinationName, String body,
//			CloseableHttpClient httpClientParam) throws ClientProtocolException, IOException {
//
//
//		String accessToken = accessToken();
//
//		String url = uri + "/destination-configuration/v1/destinations/" + destinationName;
////		String url = "https://destination-configuration.cfapps.us21.hana.ondemand.com" + "/destination-configuration/v1/destinations/" + destinationName;
//		HttpResponse httpResponse = null;
//		String jsonString = null;
//		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//		HttpRequestBase httpRequestBase = null;
//		if (httpClient == null) {
//			httpClient = HttpClientBuilder.create().build();
//		}
//
//		httpRequestBase = new HttpGet(url);
//
//		httpRequestBase.addHeader("Authorization", "Bearer " + accessToken);
////		httpRequestBase.addHeader("Authorization", "Bearer " + "eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vdmlhdHJpcy1pdHMtZGV2LTA5MnRsMzB1LmF1dGhlbnRpY2F0aW9uLnVzMjEuaGFuYS5vbmRlbWFuZC5jb20vdG9rZW5fa2V5cyIsImtpZCI6ImRlZmF1bHQtand0LWtleS04MjM1NzIzNDQiLCJ0eXAiOiJKV1QiLCJqaWQiOiAiZkVEVnMyYlovK1cvKzZtYTN6WHVteTVYYmJxaDA4ZVA5N2lvYzBlMmpYdz0ifQ.eyJqdGkiOiI0MmRiZTgzZDgzZWI0ZWQ5OGM5ZjQzZjVhOGY5M2U0MCIsImV4dF9hdHRyIjp7ImVuaGFuY2VyIjoiWFNVQUEiLCJzdWJhY2NvdW50aWQiOiJlOTRkMTc0Ni1kZWQ4LTRlN2ItYWI1MS0xNjY0OWZjZTc4NDEiLCJ6ZG4iOiJ2aWF0cmlzLWl0cy1kZXYtMDkydGwzMHUiLCJzZXJ2aWNlaW5zdGFuY2VpZCI6ImQ3OTRlNmE5LTE1MmMtNDRmZS05NDQzLTBjNDUxYjAwMzMwNSJ9LCJzdWIiOiJzYi1jbG9uZWQ3OTRlNmE5MTUyYzQ0ZmU5NDQzMGM0NTFiMDAzMzA1IWIxMTQxN3xkZXN0aW5hdGlvbi14c2FwcG5hbWUhYjgiLCJhdXRob3JpdGllcyI6WyJ1YWEucmVzb3VyY2UiXSwic2NvcGUiOlsidWFhLnJlc291cmNlIl0sImNsaWVudF9pZCI6InNiLWNsb25lZDc5NGU2YTkxNTJjNDRmZTk0NDMwYzQ1MWIwMDMzMDUhYjExNDE3fGRlc3RpbmF0aW9uLXhzYXBwbmFtZSFiOCIsImNpZCI6InNiLWNsb25lZDc5NGU2YTkxNTJjNDRmZTk0NDMwYzQ1MWIwMDMzMDUhYjExNDE3fGRlc3RpbmF0aW9uLXhzYXBwbmFtZSFiOCIsImF6cCI6InNiLWNsb25lZDc5NGU2YTkxNTJjNDRmZTk0NDMwYzQ1MWIwMDMzMDUhYjExNDE3fGRlc3RpbmF0aW9uLXhzYXBwbmFtZSFiOCIsImdyYW50X3R5cGUiOiJjbGllbnRfY3JlZGVudGlhbHMiLCJyZXZfc2lnIjoiY2IwNGYxMTQiLCJpYXQiOjE2ODkzNDc3NzcsImV4cCI6MTY4OTM5MDk3NywiaXNzIjoiaHR0cHM6Ly92aWF0cmlzLWl0cy1kZXYtMDkydGwzMHUuYXV0aGVudGljYXRpb24udXMyMS5oYW5hLm9uZGVtYW5kLmNvbS9vYXV0aC90b2tlbiIsInppZCI6ImU5NGQxNzQ2LWRlZDgtNGU3Yi1hYjUxLTE2NjQ5ZmNlNzg0MSIsImF1ZCI6WyJzYi1jbG9uZWQ3OTRlNmE5MTUyYzQ0ZmU5NDQzMGM0NTFiMDAzMzA1IWIxMTQxN3xkZXN0aW5hdGlvbi14c2FwcG5hbWUhYjgiLCJ1YWEiXX0.tMAl5Kxrbtboyw1B6VcXIZy2B9gWd5r4UdTm_Q0Bx2DkOgAO7bAKrxnK3OaRiexfJ9qjuUR-L9aExU6BeYkWt3xEaAYcExUOgNgsLbfXcqUOUntosIDNt9CU59mtZz_F72RAUJ6D0KpcWpnjQmHcBOWcbG9LD_fga6qJpCQVEYEIUOXCKNU5cay6mS62KUY2Cq8kQbKMbarfCMLBtfS4kDmL9diTQKPPRagT171E5u7Wp-jkCj87xxA42SL-3-UZDVTgP9tmaq_CdagCjgXNQiUU4fjk0bLiqkXlCWnBqR3JNAvp5DgKll2qLn8mdFvVhdeK84T2PS_kn4PVVwX2hg");
////		httpRequestBase.addHeader("Authorization", "Bearer " + "eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vdmlhdHJpcy1pdHMtZGV2LTA5MnRsMzB1LmF1dGhlbnRpY2F0aW9uLnVzMjEuaGFuYS5vbmRlbWFuZC5jb20vdG9rZW5fa2V5cyIsImtpZCI6ImRlZmF1bHQtand0LWtleS04MjM1NzIzNDQiLCJ0eXAiOiJKV1QiLCJqaWQiOiAiNzN4ZVFOTW8wWTdUUktzMWJGb0JuUjZrcUZiZjByd3p3N0ZEY3dwaXd6ST0ifQ.eyJqdGkiOiI5NDg4OGNmZGRjYjQ0MWRjODk0YWFkZDM1NzI4N2Q2NiIsImV4dF9hdHRyIjp7ImVuaGFuY2VyIjoiWFNVQUEiLCJzdWJhY2NvdW50aWQiOiJlOTRkMTc0Ni1kZWQ4LTRlN2ItYWI1MS0xNjY0OWZjZTc4NDEiLCJ6ZG4iOiJ2aWF0cmlzLWl0cy1kZXYtMDkydGwzMHUiLCJzZXJ2aWNlaW5zdGFuY2VpZCI6IjMxMzgzZjg2LTc0YjctNDZmMi1hZmNkLWU4MDYwNTIyZTQ1YSJ9LCJzdWIiOiJzYi1jbG9uZTMxMzgzZjg2NzRiNzQ2ZjJhZmNkZTgwNjA1MjJlNDVhIWIxMTQxN3xkZXN0aW5hdGlvbi14c2FwcG5hbWUhYjgiLCJhdXRob3JpdGllcyI6WyJ1YWEucmVzb3VyY2UiXSwic2NvcGUiOlsidWFhLnJlc291cmNlIl0sImNsaWVudF9pZCI6InNiLWNsb25lMzEzODNmODY3NGI3NDZmMmFmY2RlODA2MDUyMmU0NWEhYjExNDE3fGRlc3RpbmF0aW9uLXhzYXBwbmFtZSFiOCIsImNpZCI6InNiLWNsb25lMzEzODNmODY3NGI3NDZmMmFmY2RlODA2MDUyMmU0NWEhYjExNDE3fGRlc3RpbmF0aW9uLXhzYXBwbmFtZSFiOCIsImF6cCI6InNiLWNsb25lMzEzODNmODY3NGI3NDZmMmFmY2RlODA2MDUyMmU0NWEhYjExNDE3fGRlc3RpbmF0aW9uLXhzYXBwbmFtZSFiOCIsImdyYW50X3R5cGUiOiJjbGllbnRfY3JlZGVudGlhbHMiLCJyZXZfc2lnIjoiYWNkMmE2NjUiLCJpYXQiOjE2ODk1NjkzNjUsImV4cCI6MTY4OTYxMjU2NSwiaXNzIjoiaHR0cHM6Ly92aWF0cmlzLWl0cy1kZXYtMDkydGwzMHUuYXV0aGVudGljYXRpb24udXMyMS5oYW5hLm9uZGVtYW5kLmNvbS9vYXV0aC90b2tlbiIsInppZCI6ImU5NGQxNzQ2LWRlZDgtNGU3Yi1hYjUxLTE2NjQ5ZmNlNzg0MSIsImF1ZCI6WyJ1YWEiLCJzYi1jbG9uZTMxMzgzZjg2NzRiNzQ2ZjJhZmNkZTgwNjA1MjJlNDVhIWIxMTQxN3xkZXN0aW5hdGlvbi14c2FwcG5hbWUhYjgiXX0.BQpYEHFI8T2040zLcegjz4S7JsriKEurGkk7D3ahGjANUImZ_6MGB5ObeDa-pGuyUP4lWe9_ftWlZ7Sg1rm9MvHG1qejmELYiiny0gbh_E3z6cIY_bHo12gRuPLE0bGTLx9Oa0p9OOT0VSGyn37c9E4d2WKk1BrlxSO1Koc_YeZUYE3peEyJ1ESk8oi0xplKdP5pRMPJaFcnQMXrr7KNWv4BETNUI5pYLU8aGosRiOsEdXUfTSNb51tiqwqGMoja7q002mgX4aNAv9Udr8qkdrmu6xzlCBHYJo9wPGz5SAtu6YUkZa343GRSu_Y-nDcagWsRbV-VRcoJxLhEUCW8iw");
//		httpResponse = httpClient.execute(httpRequestBase);
//
//		jsonString = EntityUtils.toString(httpResponse.getEntity());
//		System.out.println("****");
//		System.out.println(jsonString);
//		System.out.println("****");
//		return jsonString;
//	}
	
	public String readMdgDestination(String destinationName, String body,
			CloseableHttpClient httpClientParam) throws ClientProtocolException, IOException {


		String accessToken = accessToken();
		//String accessToken = accessMdgToken();

	//String url = mdgUri + "/destination-configuration/v1/destinations/" + destinationName;
		String url = uri + "/destination-configuration/v1/destinations/" + destinationName;
		HttpResponse httpResponse = null;
		String jsonString = null;
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpRequestBase httpRequestBase = null;
		if (httpClient == null) {
			httpClient = HttpClientBuilder.create().build();
		}

		httpRequestBase = new HttpGet(url);

		httpRequestBase.addHeader("Authorization", "Bearer " + accessToken);
		httpResponse = httpClient.execute(httpRequestBase);

		jsonString = EntityUtils.toString(httpResponse.getEntity());
		return jsonString;
	}
}