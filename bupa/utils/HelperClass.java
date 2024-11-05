package com.incture.bupa.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class HelperClass {
	private static Logger logger = LoggerFactory.getLogger(HelperClass.class);
	public static boolean checkString(String s) {
		if (s == null || s.equals("") || s.trim().isEmpty() || s.matches(""))
			return true;
		else
			return false;
	}
	public static boolean isEmpty(Collection<?> o) {
		if (o == null || o.isEmpty())
			return true;
		return false;
	}
	public static boolean isEmpty(Object o) {
		if (o == null || o.equals(""))
			return true;
		return false;
	}
	public static String getFileExtension(String fullName) {
		String fileName = new File(fullName).getName();
		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);

	}
	public static String getMIMEtype(String extension) {
		Map<String, String> map = new HashMap<>();
		map.put("3gp", "video/3gpp");
		map.put("3g2", "video/3gpp2");
		map.put("7z", "application/x-7z-compressed");
		map.put("aac", "audio/aac");
		map.put("abw", "application/x-abiword");
		map.put("arc", "application/x-freearc");
		map.put("avi", "video/x-msvideo");
		map.put("azw", "application/vnd.amazon.ebook");
		map.put("bin", "application/octet-stream");
		map.put("bmp", "image/bmp");
		map.put("bz", "application/x-bzip");
		map.put("bz2", "application/x-bzip2");
		map.put("csh", "application/x-csh");
		map.put("css ", "text/css");
		map.put("csv", "text/csv");
		map.put("doc", "application/msword");
		map.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		map.put("eot", "application/vnd.ms-fontobject");
		map.put("epub", "application/epub+zip");
		map.put("gif", "image/gif");
		map.put("htm/.html", "text/html");
		map.put("ico", "image/vnd.microsoft.icon");
		map.put("ics", "text/calendar");
		map.put("jar", "application/java-archive");
		map.put("jpg", "image/jpeg");
		map.put("jpeg", "image/jpeg");
		map.put("js", "text/javascript");
		map.put("json", "application/json");
		map.put("mid/.midi", "audio/midi, audio/x-midi");
		map.put("mjs", "text/javascript");
		map.put("mp3", "audio/mpeg");
		map.put("mpeg", "video/mpeg");
		map.put("mpkg", "application/vnd.apple.installer+xml");
		map.put("odp", "application/vnd.oasis.opendocument.presentation");
		map.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
		map.put("odt", "application/vnd.oasis.opendocument.text");
		map.put("oga", "audio/ogg");
		map.put("ogv", "video/ogg");
		map.put("ogx", "application/ogg");
		map.put("otf", "font/otf");
		map.put("png", "image/png");
		map.put("pdf", "application/pdf");
		map.put("ppt", "application/vnd.ms-powerpoint");
		map.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
		map.put("rar", "application/x-rar-compressed");
		map.put("rtf", "application/rtf");
		map.put("sh", "application/x-sh");
		map.put("svg", "image/svg+xml");
		map.put("swf", "application/x-shockwave-flash");
		map.put("tar", "application/x-tar");
		map.put("tif", "image/tiff");
		map.put("ttf", "font/ttf");
		map.put("txt", "text/plain");
		map.put("vsd", "application/vnd.visio");
		map.put("wav", "audio/wav");
		map.put("weba", "audio/webm");
		map.put("webm", "video/webm");
		map.put("webp", "image/webm");
		map.put("woff", "font/woff");
		map.put("woff2", "font/woff2");
		map.put("xhtml", "application/xhtml+xml");
		map.put("xls", "application/vnd.ms-excel");
		map.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		map.put("xml", "application/xml");
		map.put("xul", "application/vnd.mozilla.xul+xml");
		map.put("zip", "application/zip");
		return map.get(extension).toString();
	}
	public static String encodeUsernameAndPassword(String username, String password) {
		String encodeUsernamePassword = username + ":" + password;
		String auth = "Basic " + DatatypeConverter.printBase64Binary(encodeUsernamePassword.getBytes());
		return auth;
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
	public static String generateTokenForOauth(Map<String, Object> map) {
		try {

			HttpClient client = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost((String) map.get("tokenServiceURL") + "?grant_type=client_credentials");
			httpPost.addHeader("Content-Type", "application/json");

			// Encoding username and password
			String auth = encodeUsernameAndPassword((String) map.get("clientId"), (String) map.get("clientSecret"));
			httpPost.addHeader("Authorization", auth);

			HttpResponse response = client.execute(httpPost);

			String dataFromStream = getDataFromStream(response.getEntity().getContent());
			logger.info("Output : " + dataFromStream);
			if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
				JSONObject json = new JSONObject(dataFromStream);
				return (String) json.get("access_token");

			} else {
				return dataFromStream;
			}

		} catch (IOException e) {
			return "Failed to fetch roken";
		}

	}
	public static String bytesConversion(double bytes) {

		double convBytes = bytes + 'd';

		if (bytes < 1024) {
			// bytes
			String size = String.valueOf(bytes);
			return size + " bytes";
		}

		else if (bytes < 1048576) {
			convBytes = bytes / 1024;// kb
			String size = String.valueOf(Math.round(convBytes));
			return size + " KB";
		} else if (bytes < 1073741824) {
			convBytes = bytes / 1024 / 1024;// mb

			String size = String.valueOf(Math.round(convBytes));

			return size + " MB";
		} else {
			convBytes = bytes / 1024 / 1024 / 1024;// gb

			String size = String.valueOf(Math.round(convBytes));
			return size + " GB";
		}

	}
}
