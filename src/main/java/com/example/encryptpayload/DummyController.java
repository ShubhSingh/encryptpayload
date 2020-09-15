package com.example.encryptpayload;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.codingrodent.jackson.crypto.CryptoModule;
import com.codingrodent.jackson.crypto.EncryptionService;
import com.codingrodent.jackson.crypto.PasswordCryptoContext;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
public class DummyController {

	private static final Logger log = LoggerFactory.getLogger(DummyController.class);

	@Autowired
	@Qualifier("restTemplateWithCrypto")
	private RestTemplate restTemplateWithCrypto;
	
	@Autowired
	private BasicTextEncryptor textEncryptor;
	
	@PostMapping(value = "/send-crypto", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CryptoRequestPayload> sendCryptoEncryptedRequest(@RequestBody CryptoRequestPayload request) throws IOException {

		log.info("Send request: {}", request);

		String url = "http://localhost:9045/api/receive-crypto";

		HttpEntity<CryptoRequestPayload> requestEntity = new HttpEntity<>(request, getHeaders());
		log.info("Calling url: {}", url);
		ResponseEntity<CryptoRequestPayload> response = restTemplateWithCrypto.exchange(url, HttpMethod.POST, requestEntity, CryptoRequestPayload.class);
		log.info("Received response back: {}", response.getBody());

		CryptoRequestPayload testingRequest = response.getBody();
		return ResponseEntity.accepted().body(testingRequest);
    	}
	
	@PostMapping(value = "/receive-crypto", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<String> receiveCryptoEncryptedRequest(@RequestBody String json) throws IOException {

		log.info("Received request: {}", json);

		ObjectMapper objectMapper = new ObjectMapper();
		EncryptionService encryptionService = new EncryptionService(objectMapper, new PasswordCryptoContext("Password1"));
		objectMapper.registerModule(new CryptoModule().addEncryptionService(encryptionService));

		CryptoRequestPayload testingRequest = objectMapper.readValue(json, CryptoRequestPayload.class);
		log.info("Converted to required object: {}", testingRequest);

		testingRequest.setEndDate(Date.from(Instant.now()));

		String response = objectMapper.writeValueAsString(testingRequest);
		log.info("Return response as encrypted json: {}", response);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}
	
	
	@PostMapping(value = "/send-jasypt", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<JasyptRequestPayload> sendRequestJasypt(@RequestBody JasyptRequestPayload request) {
		
		log.info("Intial request: {}", request);

		if (request.getPassword()!=null && !request.getPassword().isEmpty()) {
			request.setPassword(textEncryptor.encrypt(request.getPassword()));
		}
		if (request.getUserName()!=null && !request.getUserName().isEmpty()) {
			request.setUserName(textEncryptor.encrypt(request.getUserName()));
		}

		log.info("Send encrypted request: {}", request);
		String url = "http://localhost:9045/api/receive-jasypt";

		HttpEntity<JasyptRequestPayload> requestEntity = new HttpEntity<>(request, getHeaders());

		log.info("Calling url: {}", url);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<JasyptRequestPayload> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, JasyptRequestPayload.class);

		JasyptRequestPayload responsePayload = response.getBody();

		log.info("Received encrypted response back: {}", response.getBody());

		if (responsePayload.getPassword()!=null && !responsePayload.getPassword().isEmpty()) {
			responsePayload.setPassword(textEncryptor.decrypt(responsePayload.getPassword()));
		}
		if (responsePayload.getUserName()!=null && !responsePayload.getUserName().isEmpty()) {
			responsePayload.setUserName(textEncryptor.decrypt(responsePayload.getUserName()));
		}

		log.info("Decrypted to required response object: {}", responsePayload);

		return ResponseEntity.accepted().body(responsePayload);
    	}
	
	@PostMapping(value = "/receive-jasypt", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<JasyptRequestPayload> receiveRequestJasypt(@RequestBody JasyptRequestPayload request) throws IOException {

		log.info("Received encrypted incoming request: {}", request);

		if (request.getPassword()!=null && !request.getPassword().isEmpty()) {
			request.setPassword(textEncryptor.decrypt(request.getPassword()));
		}
		if (request.getUserName()!=null && !request.getUserName().isEmpty()) {
			request.setUserName(textEncryptor.decrypt(request.getUserName()));
		}
		
		log.info("Decrypted to required request object: {}", request);

		request.setEndDate(Date.from(Instant.now()));
		
		if (request.getPassword()!=null && !request.getPassword().isEmpty()) {
        	request.setPassword(textEncryptor.encrypt(request.getPassword()));
		}
		if (request.getUserName()!=null && !request.getUserName().isEmpty()) {
			request.setUserName(textEncryptor.encrypt(request.getUserName()));
		}
		
		log.info("Return response as encrypted json: {}", request);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(request);
	}
	
	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
		return headers;
	}
}
