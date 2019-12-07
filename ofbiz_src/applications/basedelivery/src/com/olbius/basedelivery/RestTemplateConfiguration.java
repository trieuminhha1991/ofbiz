package com.olbius.basedelivery;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RestTemplateConfiguration {
    public static int TIME_OUT = 3000;

    public static ClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setReadTimeout(TIME_OUT);
        return clientHttpRequestFactory;
    }

    public static RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
        for (HttpMessageConverter converter : restTemplate.getMessageConverters()) {
            if (!(converter instanceof StringHttpMessageConverter)) {
                break;
            }
            ((StringHttpMessageConverter) converter).setWriteAcceptCharset(false);
        }
        return restTemplate;
    }
}
