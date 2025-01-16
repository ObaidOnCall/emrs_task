package io.hahn_software.emrs.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.hahn_software.emrs.utils.PropertiesLoader;

@Configuration
public class UtilsConf {
    
    @Bean("propertiesLoader1")
    public PropertiesLoader propertiesLoader() {
        return PropertiesLoader.builder().build() ;
    }
}
