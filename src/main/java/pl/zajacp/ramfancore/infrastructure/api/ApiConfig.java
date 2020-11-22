package pl.zajacp.ramfancore.infrastructure.api;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApiConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, RamExternalPath ramExternalPath) {
        return builder
                .rootUri(ramExternalPath.rootUrl)
                .build();
    }

    @Component
    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "api.ext")
    @Setter
    private class RamExternalPath {
        private String rootUrl;
    }
}
