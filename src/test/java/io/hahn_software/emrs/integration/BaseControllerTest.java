package io.hahn_software.emrs.integration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.hahn_software.emrs.security.SecurityConf;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public abstract class BaseControllerTest {


    protected MockHttpServletRequestBuilder prepareRequestWithOAuthToken(HttpMethod httpMethod, String url, String role) {
        MockHttpServletRequestBuilder requestBuilder;
        switch (httpMethod.name()) {
            case "POST":
                requestBuilder = MockMvcRequestBuilders.post(url);
                break;
            case "PUT":
                requestBuilder = MockMvcRequestBuilders.put(url);
                break;
            case "GET":
                requestBuilder = MockMvcRequestBuilders.get(url);
                break;
            case "DELETE":
                requestBuilder = MockMvcRequestBuilders.delete(url);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + httpMethod);
        }

        // Define the roles list
        List<String> roles = List.of(
            role,
            "offline_access",
            "default-roles-hahn_software",
            "uma_authorization"
        );

        // Manually map roles to GrantedAuthority objects
        List<GrantedAuthority> authorities = roles.stream()
                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                .collect(Collectors.toList());

        // Create a JwtAuthenticationToken with the authorities
        Jwt jwt = Jwt.withTokenValue("mocked-token")
        .header("alg", "none")
        .claim("preferred_username", "testuser")
        .claim("realm_access", Map.of("roles", roles))
        .build();

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, authorities);

        
        // Add the authentication to the security context using RequestPostProcessor
        return requestBuilder
                .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)) // Use authentication() directly
                .contentType(MediaType.APPLICATION_JSON);


    }
}
