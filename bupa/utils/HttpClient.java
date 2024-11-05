package com.incture.bupa.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class HttpClient {
	public ResponseEntity<String> execute(String url, HttpMethod method, String body, Map<String, String> headers) {
		System.err.println("[HttpClient] : execute : Url :::: " + url);
		System.err.println("[HttpClient] : execute : Body :::: " + body);
		System.err.println("[HttpClient] : execute : headers :::: " + headers);
		RestTemplate template = new RestTemplate();
		HttpHeaders httpHeaders = new HttpHeaders();
		for (Entry<String, String> x : headers.entrySet()) {
			httpHeaders.add(x.getKey(), x.getValue());
		}
		HttpEntity<String> entity = new HttpEntity<>(body, httpHeaders);
		return template.exchange(url, method, entity, String.class);
	}
	public ResponseEntity<String> execute(String url, HttpMethod method, String body, Map<String, String> headers,
			String proxyHost, int proxyPort) {
		System.err.println("Url :: " + url);
		System.err.println("Body :::: " + body);
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
		factory.setProxy(proxy);
		RestTemplate template = new RestTemplate(factory);
		HttpHeaders httpHeaders = new HttpHeaders();
		for (Entry<String, String> x : headers.entrySet()) {
			httpHeaders.add(x.getKey(), x.getValue());
		}

		HttpEntity<?> entity;
		if (body != null) {
			entity = new HttpEntity<>(body, httpHeaders);
		} else {
			entity = new HttpEntity<>(httpHeaders);
		}

		System.err.println("Entity :: " + entity);
		ResponseEntity<String> exchange = template.exchange(url, method, entity, String.class);
		System.err.println("exchange :: " + exchange);
		return exchange;
	}
	public ResponseEntity<String> execute(String url, HttpMethod urlMethod, String body, String xsrfTokenUrl,
			String clientId, String secretId, Map<String, String> headers) {

		String accessToken = executeAccessToken(xsrfTokenUrl, clientId, secretId);

		System.out.println("[HttpClient] :: execute : xsrfToken ::::: " + accessToken);
		// Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", "Bearer " + accessToken);
		return execute(url, urlMethod, body, headers);

	}
	public String executeAccessToken(String tokenUrl, String clientId, String secretId) {
		Map<String, String> headers = new HashMap<>();
		String encoding = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + secretId).getBytes());
		headers.put("Authorization", encoding);
		ResponseEntity<String> execute = new HttpClient().execute(tokenUrl, HttpMethod.valueOf("POST"), null, headers);
		String body = execute.getBody();
		Gson gson = new Gson();
		HashMap map = gson.fromJson(body, HashMap.class);
		String token = map.get("access_token").toString();
		System.out.println("DestinationUtil.getAccessToken()::body: " + body);
		System.out.println("DestinationUtil.getAccessToken():: " + token);
		return token;
	}
	public Map<String, String> readDestination(String tokenUrl, String clientId, String secretId,
			String destinationBaseUrl, String destinationName) throws HttpClientErrorException {
		String accessToken = executeAccessToken(tokenUrl, clientId, secretId);
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", "Bearer " + accessToken);
		ResponseEntity<String> execute = new HttpClient().execute(destinationBaseUrl + "/" + destinationName,
				HttpMethod.valueOf("GET"), null, headers);
		String body = execute.getBody();
		Gson gson = new Gson();
		HashMap destinationMap = gson.fromJson(body, HashMap.class);
		System.out.println("DestinationUtil.getDestination() :: body : " + body);
		System.out.println("DestinationUtil.getDestination() :: destinationDto : " + destinationMap);
		return destinationMap;
	}
	public Response executeUsingokHttpClientRest(String urlValue, HttpMethod valueOf, String jsonString,
			String tokenUrl, String clientId, String clientSecret, Map<String, String> globalHeaders)
			throws IOException {
		System.out.println("HttpClient.executeUsingokHttpClientRest():: urlValue: " + urlValue);
		System.out.println("HttpClient.executeUsingokHttpClientRest():: jsonString: " + jsonString);
		System.out.println("HttpClient.executeUsingokHttpClientRest():: tokenUrl: " + tokenUrl);
		System.out.println("HttpClient.executeUsingokHttpClientRest():: clientId: " + clientId);
		System.out.println("HttpClient.executeUsingokHttpClientRest():: clientSecret: " + clientSecret);
		System.out.println("HttpClient.executeUsingokHttpClientRest():: globalHeaders: " + globalHeaders);
		
		String accessToken = executeAccessToken(tokenUrl, clientId, clientSecret);

		System.out.println("[HttpClient] :: execute : xsrfToken ::::: " + accessToken);
		// Map<String, String> headers = new HashMap<>();
		globalHeaders.put("Authorization", "Bearer " + accessToken);

		System.err.println("url ::: " + urlValue);

		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, jsonString);
		Headers headers = Headers.of(globalHeaders);
		Request request = new Request.Builder().url(urlValue).patch(body).headers(headers).build();
		Response response = client.newCall(request).execute();
		System.err.println("response :::::: " + response);
		return response;
	}
}
