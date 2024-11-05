package com.incture.bupa.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DestinationReaderUtil {
	private static Logger logger = LoggerFactory.getLogger(DestinationReaderUtil.class);

	private String clientId;
	private String clientSecret;
	private String url;
	private String uri;

//	public DestinationReaderUtil() {
//		CfJdbcEnv cfJdbcEnv = new CfJdbcEnv();
//		CfCredentials cfCredentials = cfJdbcEnv.findCredentialsByTag("destination");
//		Map<String, Object> map = cfCredentials.getMap();
//		url = (String) map.get("url");
//		clientId = (String) map.get("clientid");
//		clientSecret = (String) map.get("clientsecret");
//		uri = (String) map.get("uri");
//	}
	public Map<String, String> fetchDestinationCreds() {

		Map<String, String> map = new HashMap<>();

		map.put("url", url);
		map.put("clientid", clientId);
		map.put("clientsecret", clientSecret);
		map.put("destinationBaseUrl", uri);

		return map;
	}
	public Map<String, Object> getDestination(String destinationName) throws IOException {

		logger.info("inside dest");
		HttpClient client = HttpClientBuilder.create().build();

		HttpPost httpPost = new HttpPost(url + "/oauth/token?grant_type=client_credentials");
		httpPost.addHeader("Content-Type", "application/json");

		// Encoding username and password
		String auth = HelperClass.encodeUsernameAndPassword(clientId, clientSecret);

		httpPost.addHeader("Authorization", auth);
		HttpResponse res = client.execute(httpPost);

		String data = HelperClass.getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");

			logger.info("jwtdestinationToken " + jwtToken);

			HttpGet httpGet = new HttpGet(uri + "/destination-configuration/v1/destinations/" + destinationName);

			httpGet.addHeader("Content-Type", "application/json");
			httpGet.addHeader("Authorization", "Bearer " + jwtToken);
			HttpResponse response = client.execute(httpGet);
			String dataFromStream = HelperClass.getDataFromStream(response.getEntity().getContent());
			logger.info("dataFromStream : " + dataFromStream);
			if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
				logger.info("OK : " + dataFromStream);
				JSONObject json = new JSONObject(dataFromStream);
				Map<String, Object> mapResponse = json.getJSONObject("destinationConfiguration").toMap();

				if (json.has("authTokens") && json.getJSONArray("authTokens") != null
						&& !json.getJSONArray("authTokens").isEmpty()) {
					if (!json.getJSONArray("authTokens").getJSONObject(0).isNull("value")) {
						mapResponse.put("token", json.getJSONArray("authTokens").getJSONObject(0).getString("value"));
					}

				}

				return mapResponse;
			}
		}
		return null;
	}
}
