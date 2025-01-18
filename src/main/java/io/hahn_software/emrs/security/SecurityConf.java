package io.hahn_software.emrs.security;



import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoderInitializationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;


import org.springframework.beans.factory.annotation.Value;

import java.security.KeyFactory;
import java.util.Base64;

import org.springframework.core.io.ClassPathResource;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
        .oauth2ResourceServer( oauth2 ->
            oauth2.jwt( jwt ->
                {
                    jwt.jwtAuthenticationConverter(new JwtConverter()) ;
                    jwt.decoder(jwtDecoder()) ;

                }
            )
        )
        .sessionManagement(sessionManagement -> 
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable);

        return http.build() ;


    }



    @Bean
    public JwtDecoder jwtDecoder() {
        try {

            ClassPathResource publicKeyResource = new ClassPathResource("key.pub");



            String publicKeyPEM;
            try (InputStream inputStream = publicKeyResource.getInputStream()) {
                publicKeyPEM = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "")
                                    .replace("-----END PUBLIC KEY-----", "")
                                    .replaceAll("\\s+", "");
            byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            
            // Create and return the JwtDecoder
            return NimbusJwtDecoder.withPublicKey((RSAPublicKey) publicKey).build();
        } catch (Exception e) {
            throw new JwtDecoderInitializationException("Failed to create JWT decoder", e);
        }
    }
}
