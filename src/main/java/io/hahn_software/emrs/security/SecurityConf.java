package io.hahn_software.emrs.security;



import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.oauth2.jwt.JwtDecoder;
// import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;


import org.springframework.beans.factory.annotation.Value;

import java.security.KeyFactory;
import java.util.Base64;

import org.springframework.core.io.ClassPathResource;



@Configuration
@EnableWebSecurity
public class SecurityConf {
    

    private final CorsConfigurationSource corsConfigurationSource ;




    private static final String[] AUTH_WHITELIST = {
        // -- Swagger UI v2
        "/v2/api-docs",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/swagger-ui.html",
        // -- Swagger UI v3 (OpenAPI)
        "/v3/api-docs/**",
        "/swagger-ui/**"  ,

        "/actuator/**"

    };


    @Autowired
    SecurityConf(
        CorsConfigurationSource corsConfigurationSource 
        ) {
        this.corsConfigurationSource = corsConfigurationSource ;
    }



    @Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http ) throws Exception {


        http
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests( authorize  ->  {
                authorize.requestMatchers(AUTH_WHITELIST).permitAll() ;
                authorize.anyRequest().authenticated() ;
            }
        )
        .sessionManagement(sessionManagement -> 
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable);

        return http.build() ;


    }
}
