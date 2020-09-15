package com.example.encryptpayload;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.codingrodent.jackson.crypto.CryptoModule;
import com.codingrodent.jackson.crypto.EncryptionService;
import com.codingrodent.jackson.crypto.PasswordCryptoContext;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AppContext {
	
	@Bean(name = "restTemplateWithCrypto")
	public RestTemplate restTemplateWithCrypto() {
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(0, mappingJackson2HttpMessageConverter());
		return restTemplate;
	}

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(myObjectMapper());
        return converter;
    }

    @Bean
    public ObjectMapper myObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        EncryptionService encryptionService = new EncryptionService(objectMapper, new PasswordCryptoContext("Password1"));
        objectMapper.registerModule(new CryptoModule().addEncryptionService(encryptionService));
        return objectMapper;
    }
    
    @Bean
    public BasicTextEncryptor textEncryptor() {
    	BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
    	textEncryptor.setPassword("SecretKey1");
		return textEncryptor;
    }
    
}
