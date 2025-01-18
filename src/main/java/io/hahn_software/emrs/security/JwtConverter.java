package io.hahn_software.emrs.security;


import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

public class JwtConverter extends JwtAuthenticationConverter {
    

    public static final String PRINCIPAL_CLAIM_NAME = "preferred_username";
    public static final String AUTHORITY_PREFIX = "ROLE_";

    public JwtConverter() {
        RolesGrantedAuthoritiesConverter grantedAuthoritiesConverter = new RolesGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix(AUTHORITY_PREFIX);

        setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        setPrincipalClaimName(PRINCIPAL_CLAIM_NAME);
    }
}
