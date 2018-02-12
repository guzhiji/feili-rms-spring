package com.feiliks.testapp2;

import java.util.Arrays;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class AuthToken extends AbstractAuthenticationToken {

    private final String username;
    private final String hash;

    public AuthToken(String username, String hash) {
        super(Arrays.asList(new GrantedAuthority[]{
            new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return "my-authority";
                }
            }
        }));
        this.username = username;
        this.hash = hash;
    }

    @Override
    public Object getCredentials() {
        return hash;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

}
