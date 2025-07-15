package com.yourorg.routedashboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // Uncomment and configure proxy if needed
        // Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy-server", 8080));
        // factory.setProxy(proxy);
        
        // Set longer timeout for API calls
        factory.setConnectTimeout(30000); // 30 seconds
        factory.setReadTimeout(30000); // 30 seconds
        
        return new RestTemplate(factory);
    }
} 