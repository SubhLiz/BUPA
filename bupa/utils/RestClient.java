package com.incture.bupa.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.incture.bupa.dto.HttpResponseDto;

import org.apache.commons.io.IOUtils;

@Component
public class RestClient {
	private static Logger logger = LoggerFactory.getLogger(RestClient.class);

	@Autowired
	private DestinationReaderUtil destinationUtil;

	public HttpResponseDto execute(String methodType, String url, Map<String, String> headers,
			CloseableHttpClient client, HttpEntity data, Boolean isForToken, Boolean closeSession) throws IOException {

		HttpRequestBase httpRequestBase = null;
		HttpResponse httpResponse = null;
		HttpResponseDto httpResponseDto = new HttpResponseDto();
		CloseableHttpClient httpClient = client;

		if (httpClient == null) {
			httpClient = HttpClientBuilder.create().build();
		}

		switch (methodType) {
		case "GET":
			httpRequestBase = new HttpGet(url);
			break;
		case "POST":
			httpRequestBase = new HttpPost(url);
			break;
		case "PUT":
			httpRequestBase = new HttpPut(url);
			break;
		case "PATCH":
			httpRequestBase = new HttpPut(url);
			break;
		case "DELETE":
			httpRequestBase = new HttpDelete(url);
			break;
		default:
			httpRequestBase = new HttpGet(url);
			break;
		}

		// adding headers
		if (headers != null) {
			Set<Entry<String, String>> entrySet = headers.entrySet();
			for (Entry<String, String> entry : entrySet) {
				httpRequestBase.addHeader(entry.getKey(), entry.getValue());
			}

		}

		// adding data
		if (data != null) {
			// StringEntity data = new StringEntity(payload, "UTF-8");
			// data.setContentType("application/json");

			switch (methodType) {
			case "POST":
				((HttpPost) httpRequestBase).setEntity(data);
				break;
			case "PUT":
				((HttpPut) httpRequestBase).setEntity(data);
				break;
			case "PATCH":
				((HttpPatch) httpRequestBase).setEntity(data);
				break;
			}
		}

		httpResponse = httpClient.execute(httpRequestBase);

		httpResponseDto.setStatuscode(httpResponse.getStatusLine().getStatusCode());
		if (204 != httpResponseDto.getStatuscode()) {
			httpResponseDto.setResponseData(HelperClass.getDataFromStream(httpResponse.getEntity().getContent()));
		}

		if (closeSession) {
			httpClient.close();
		}

		return httpResponseDto;
	}
	
	public HttpResponseDto execute1(String methodType, String url, Map<String, String> headers,
			CloseableHttpClient client, String input, Boolean isForToken, Boolean closeSession) throws IOException {

		HttpRequestBase httpRequestBase = null;
		HttpResponse httpResponse = null;
		HttpResponseDto httpResponseDto = new HttpResponseDto();
		CloseableHttpClient httpClient = client;

		if (httpClient == null) {
			httpClient = HttpClientBuilder.create().build();
		}

		switch (methodType) {
		case "GET":
			httpRequestBase = new HttpGet(url);
			break;
		case "POST":
			httpRequestBase = new HttpPost(url);
			break;
		case "PUT":
			httpRequestBase = new HttpPut(url);
			break;
		case "PATCH":
			httpRequestBase = new HttpPut(url);
			break;
		case "DELETE":
			httpRequestBase = new HttpDelete(url);
			break;
		default:
			httpRequestBase = new HttpGet(url);
			break;
		}

		// adding headers
		if (headers != null) {
			Set<Entry<String, String>> entrySet = headers.entrySet();
			for (Entry<String, String> entry : entrySet) {
				httpRequestBase.addHeader(entry.getKey(), entry.getValue());
			}

		}

		// adding data
		if (input != null) {
			 StringEntity data = new StringEntity(input, "UTF-8");
			 data.setContentType("application/json");
             logger.info("data" +data);
			switch (methodType) {
			case "POST":
				((HttpPost) httpRequestBase).setEntity(data);
				break;
			case "PUT":
				((HttpPut) httpRequestBase).setEntity(data);
				break;
			case "PATCH":
				((HttpPatch) httpRequestBase).setEntity(data);
				break;
			}
		}

		httpResponse = httpClient.execute(httpRequestBase);

		httpResponseDto.setStatuscode(httpResponse.getStatusLine().getStatusCode());
		if (204 != httpResponseDto.getStatuscode()) {
			httpResponseDto.setResponseData(HelperClass.getDataFromStream(httpResponse.getEntity().getContent()));
		}

		if (closeSession) {
			httpClient.close();
		}

		return httpResponseDto;
	}

	/**
	 * @param path
	 * @param authorization
	 * @return
	 * @throws IOException
	 */
	public HttpResponseDto makeGetCallUsingURLconnection(String path, String authorization) throws IOException {

		URL url = new URL(path);
		HttpResponseDto httpResponseDto = new HttpResponseDto();
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestProperty("Authorization", authorization);
		urlConnection.setRequestMethod("GET");
		httpResponseDto.setStatuscode(urlConnection.getResponseCode());
		httpResponseDto.setResponseData(IOUtils.toByteArray(urlConnection.getInputStream()));

		return httpResponseDto;

	}
}
