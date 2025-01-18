package io.hahn_software.emrs.security;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@NoArgsConstructor
@Slf4j
public class RolesGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>>    {


    private String authorityPrefix = "";


    public RolesGrantedAuthoritiesConverter setAuthorityPrefix(String authorityPrefix) {
        Assert.notNull(authorityPrefix, "authorityPrefix cannot be null");
        this.authorityPrefix = authorityPrefix;
        return this;
    }


    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {


        Map<String, Object> realmAccess = source.getClaim("realm_access");


        log.info("Extracted realm_access: {} 🔖", realmAccess);
        
        if (Objects.isNull(realmAccess) || realmAccess.isEmpty()) {
            return Collections.emptyList();
        }


        Set<GrantedAuthority> authorities = new HashSet<>();


        Object roles = realmAccess.get("roles");

        log.info("Extracted roles: {} 🔖", roles);

        if (Objects.isNull(roles) || !(roles instanceof Collection<?>)) {
            return Collections.emptyList();
        }

        Collection<?> rolesCollection = (Collection<?>) roles;


        //@ Convert roles to GrantedAuthority
        rolesCollection.stream()
            .filter(String.class::isInstance) //! Ensure roles are strings
            .map(role -> new SimpleGrantedAuthority(authorityPrefix  + (String) role))
            .forEach(authorities::add);


        log.info("Mapped authorities: {}", authorities);

        return authorities;
    }

}
