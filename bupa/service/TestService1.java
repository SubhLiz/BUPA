package com.incture.bupa.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TestService1 {

    private static final Logger logger = LoggerFactory.getLogger(TestService1.class);
    
    protected  String globalToken = null;
    

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

    public  ResponseEntity<?> consumingOdataService(String url, String method, String entity, String csrf,
            Map<String, Object> destinationInfo,Environment environment) throws IOException, URISyntaxException {

        String proxyHost = null;
        int proxyPort = 0;
        
        String destinationName = (String)destinationInfo.get("Name");
        if(destinationName.equalsIgnoreCase(environment.getProperty("ecc.destination.name")) ) {
            proxyHost = environment.getProperty("ecc.onpremise.proxy.host");
            proxyPort = Integer.parseInt(environment.getProperty("ecc.onpremise.proxy.port"));
        } else {
            proxyHost = environment.getProperty("s4hana.onpremise.proxy.host");
            proxyPort = Integer.parseInt(environment.getProperty("s4hana.onpremise.proxy.port"));
        }
        logger.info("proxyHost-- {}",  proxyHost);

        

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(
                (String) destinationInfo.get("User"), (String) destinationInfo.get("Password")));

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
        if(null != globalToken ) {
            jwToken = globalToken;
        } else {
            jwToken = getConectivityProxy(environment);
            globalToken = jwToken;
        }
        
        
        
        if (url != null) {
            if (method.equalsIgnoreCase("GET")) {
                httpRequestBase = new HttpGet(destinationInfo.get("URL") + url);
            } else if (method.equalsIgnoreCase("POST")) {
                httpRequestBase = new HttpPost(destinationInfo.get("URL") + url);
                try {

                    logger.info("entity {}", entity);
                    input = new StringEntity(entity);
                    input.setContentType("application/json");
                } catch (UnsupportedEncodingException e) {
                }
                logger.info("inputEntity {}",  input);
                ((HttpPost) httpRequestBase).setEntity(input);
            }
            if (destinationInfo.get("sap-client") != null) {
                httpRequestBase.addHeader("sap-client", (String) destinationInfo.get("sap-client"));
            }
            httpRequestBase.addHeader("accept", "application/json");
            Header[] headers = getAccessToken((String) destinationInfo.get("URL") + url,
                        (String) destinationInfo.get("User"), (String) destinationInfo.get("Password"), httpClient,
                        proxyHost, proxyPort, (String) destinationInfo.get("sap-client"), jwToken);

            
            
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

            if (destinationInfo.get("User") != null && destinationInfo.get("Password") != null) {
                String encoded = encodeUsernameAndPassword((String) destinationInfo.get("User"),
                        (String) destinationInfo.get("Password"));
                httpRequestBase.addHeader("Authorization", encoded);
                httpRequestBase.setHeader("Proxy-Authorization", "Bearer " + jwToken);
                httpRequestBase.addHeader("SAP-Connectivity-SCC-Location_ID",
                        (String) destinationInfo.get("CloudConnectorLocationId"));

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

    private  Header[] getAccessToken(String url, String username, String password, CloseableHttpClient  client,
            String proxyHost, int proxyPort, String sapClient, String token)
            throws ClientProtocolException, IOException {

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

    public  String getConectivityProxy(Environment environment) throws URISyntaxException, IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(environment.getProperty("connectivity-tokenurl"));
        String auth = encodeUsernameAndPassword(environment.getProperty("connectivity-clientid"),
                environment.getProperty("connectivity-clientsecret"));
        httpPost.addHeader("Authorization", auth);
        HttpResponse res = client.execute(httpPost);
        String data = getDataFromStream(res.getEntity().getContent());
        if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
            String jwtToken = new JSONObject(data).getString("access_token");
            return jwtToken;
        }
        return null;
    }

    public  String encodeUsernameAndPassword(String username, String password) {
        String encodeUsernamePassword = username + ":" + password;
        return "Basic " + DatatypeConverter.printBase64Binary(encodeUsernamePassword.getBytes());
    }

    public  Map<String, Object> getDestination(String destinationName,Environment environment) throws URISyntaxException, IOException {

        logger.info("destination");
        HttpClient client = HttpClientBuilder.create().build();

        HttpPost httpPost = new HttpPost(environment.getProperty("destination-tokenurl"));
        httpPost.addHeader("Content-Type", "application/json");

        String auth = encodeUsernameAndPassword(environment.getProperty("destination-clientid"),
                environment.getProperty("destination-clientsecret"));

        httpPost.addHeader("Authorization", auth);

        HttpResponse res = client.execute(httpPost);

        logger.info("Response --->" + res);

        String data = getDataFromStream(res.getEntity().getContent());
        if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
            String jwtToken = new JSONObject(data).getString("access_token");

            logger.info("jwtdestinationToken " + jwtToken);

            HttpGet httpGet = new HttpGet(environment.getProperty("destination-baseurl") + destinationName);

            httpGet.addHeader("Content-Type", "application/json");

            httpGet.addHeader("Authorization", "Bearer " + jwtToken);

            HttpResponse response = client.execute(httpGet);
            String dataFromStream = getDataFromStream(response.getEntity().getContent());
            logger.info("dataFromStream : " + dataFromStream);
            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                // logger.error("60 " + dataFromStream);
                JSONObject json = new JSONObject(dataFromStream);
                Map<String, Object> mapResponse = json.getJSONObject("destinationConfiguration").toMap();
                mapResponse.put("token", jwtToken);
                return mapResponse;
            }
        }

        return null;
    }

    public  ResponseEntity<?> getDetailsfromOdata(String url, String method, String entity,
            Map<String, Object> destinationMap,Environment environment) {
        try {
            logger.info(" Request URL : {}", url);
            return consumingOdataService(url, method, entity, null, destinationMap,environment);
        } catch (Exception e) {
            logger.error("[Reusability][getDetailsfromOdata] error {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
        
//  public ResponseEntity<?> consumingOdataService(String URL, String methodType, String body, String csrfToken, Map<String, Object> destResp, Environment environment)
//          throws URISyntaxException, IOException {
//
//      logger.info("[BPOdataUtils][callOdata] Started");
//      StringBuilder response = new StringBuilder();
//      String XCSRF = null;
//      String cookie = null;
////        Map<String, Object> destResp = getDestination(destination, environment);
//      logger.info("[BPOdataUtils][callOdata] destResp: " + destResp);
//
//      try {
//          String userPassword = destResp.get("User") + ":" + destResp.get("Password");
//          Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(environment.getProperty("onpremise.proxy.host") ,
//                  Integer.parseInt(environment.getProperty("onpremise.proxy.port")) ));
//          if (methodType.equals("POST")) {
//              URL proxyURL_GET = new URL(destResp.get("URL") + URL);
//              HttpURLConnection conn_GET = (HttpURLConnection) proxyURL_GET.openConnection(proxy);
//              logger.info("[BPOdataUtils][callOdata] connection GET : " + conn_GET.toString());
//              String connectivity = getConectivityProxy(environment);
//              conn_GET.setRequestProperty("Proxy-Authorization",
//                      "Bearer " + connectivity);
//              conn_GET.setRequestProperty("SAP-Connectivity-SCC-Location_ID", environment.getProperty("location.connect") );
//              conn_GET.setRequestProperty("sap-client", "200");
//              conn_GET.setRequestProperty("Content-Type", "application/json");
//              conn_GET.setRequestProperty("Authorization",
//                      "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userPassword.getBytes()));
//              conn_GET.setRequestProperty("X-CSRF-Token", "Fetch");
//              conn_GET.setRequestMethod("GET");
//              
//              conn_GET.connect(); // Timed out on console.
//
//              logger.info("[BPOdataUtils][callOdata] GET response code : " + conn_GET.getResponseCode());
//              if (conn_GET.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                  System.out.println("in [callOdata] response code is 200 and setting x-csrf-token and cookie..");
//                  XCSRF = conn_GET.getHeaderField("X-CSRF-Token");
//                  cookie = conn_GET.getHeaderField("Set-Cookie").split(";", 2)[0];
//              }
//              conn_GET.disconnect();
//          }
//          logger.info("[BPOdataUtils][callOdata] csrf, cookie : " + XCSRF + " : " + cookie);
//          URL requestURL = new URL(destResp.get("URL") + URL);
//          logger.info("[BPOdataUtils][callOdata] proxyURL : " + requestURL.toString());
//          HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection(proxy);
//          logger.info("[BPOdataUtils][callOdata] connection : " + conn.toString());
//
//          conn.setRequestProperty("Proxy-Authorization",
//                  "Bearer " + getConectivityProxy(environment));
//          conn.setRequestProperty("SAP-Connectivity-SCC-Location_ID", environment.getProperty("location.connect"));
//
//          conn.setRequestProperty("Authorization",
//                  "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userPassword.getBytes()));
//          if (methodType.equals("POST")) {
//              logger.info("[callodata] x-csrf-token and cookie setting again..");
//              conn.setRequestProperty("x-csrf-token", XCSRF);
//              conn.setRequestProperty("Cookie", cookie);
//          } else
//              conn.setRequestProperty("x-csrf-token", "fetch");
//          conn.setDoOutput(true);
//          conn.setDoInput(true);
//          conn.setUseCaches(false);
//          conn.setRequestProperty("Content-Type", "application/json");
//          conn.setRequestProperty("Accept", "application/json");
//          conn.setRequestProperty("Cache-Control", "no-cache");
//          conn.connect();
//          if (methodType.equals("POST")) {
//              try (OutputStream os = conn.getOutputStream()) {
//                  os.write(body.getBytes());
//                  logger.info("in [callodata] body writing byte by byte..");
//              } catch (Exception e) {
//                  logger.error("[BPOdataUtils][PostCall] Exception in OutputStream: " + e);
//              }
//          }
//          
//          
//          if (conn.getResponseCode() == 201) {
//              String dataFromStream = getDataFromStream(conn.getInputStream());
//              logger.info("created {}", dataFromStream);
//              conn.disconnect();
//              return new ResponseEntity<String>(dataFromStream, HttpStatus.CREATED);
//
//          } else if(conn.getResponseCode() == 200) {
//              String dataFromStream = getDataFromStream(conn.getInputStream());
//              logger.info("fetched{}", dataFromStream);
//              conn.disconnect();
//              return new ResponseEntity<String>(dataFromStream, HttpStatus.OK);
//          }
//          else {
//              String responseFromECC = getDataFromStream(conn.getInputStream());
//
//              logger.info("consuming odata try else responseFromEcc {}", responseFromECC);
//              conn.disconnect();
//              return new ResponseEntity<String>("Response from odata call " + responseFromECC,
//                      HttpStatus.BAD_REQUEST);
//          }
//          
//          
////            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 207) {
////                logger.info("[BPOdataUtils][callOdata] again conn-code is 200..");
////                logger.info("[BPOdataUtils] [callOdata] ", conn.getResponseCode());
////                System.out.println("[BPOdataUtils] [callOdata] response code " + conn.getResponseCode());
////                System.out.println("[BPOdataUtils] [callOdata] request body " + body);
////                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
////                    String responseLine = null;
////
////                    logger.info("[BPOdataUtils][callOdata] reading inputstream on conn br: " + br.toString());
////                    while ((responseLine = br.readLine()) != null) {
////                        response.append(responseLine.trim());
////                    }
////                    logger.info("response line reading inputstream on Http URL COnnection: " + responseLine);
////                } catch (Exception e) {
////                    logger.error("[BPOdataUtils][callOdata] Exception in InputStream in if: " + e);
////                }
////            } else {
////                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
////                    logger.info("in [BPOdataUtils][callodata] again conn-code is not 200 and br(error) is: "
////                            + br.toString());
////                    String responseLine = null;
////                    while ((responseLine = br.readLine()) != null) {
////                        response.append(responseLine.trim());
////                    }
////                } catch (Exception e) {
////                    logger.error("[BPOdataUtils][callOdata] Exception in InputStream else: " + e.getMessage());
////                }
////            }
//          
//      } catch (Exception e) {
//          logger.error("[BPOdataUtils][callOdata] Exception : " + e);
//          return new ResponseEntity<String>("Exception in ODATA consumtion block" + e.getMessage(),
//                  HttpStatus.INTERNAL_SERVER_ERROR);
//      }
//      
//      
////        return response.toString();
//  }

}