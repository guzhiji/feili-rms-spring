package com.feiliks.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TokenAuthInterceptor extends HandlerInterceptorAdapter {

    public final static String DEFAULT_TOKEN_HEADER = "X-TOKEN";
    private final static Set<String> allowedMethods = new HashSet<>(
            Arrays.asList("GET", "POST", "PUT", "DELETE"));
    private String tokenHeaderName;

    @Autowired
    private KeyPairProvider keyPairProvider;

    public void setTokenHeaderName(String name) {
        tokenHeaderName = name;
    }

    public String getTokenHeaderName() {
        return tokenHeaderName;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest req,
            HttpServletResponse res,
            Object handler)
            throws Exception {
        if (!allowedMethods.contains(req.getMethod())) {
            return true;
        }
        String headerName = tokenHeaderName;
        if (headerName == null) {
            headerName = DEFAULT_TOKEN_HEADER;
        }
        try {
            String token = req.getHeader(headerName);
            AuthToken authentication = AuthTokenUtil.check(req, token);

            // refresh token
            res.setHeader(headerName, AuthTokenUtil.create(
                    AuthTokenUtil.getUser(req),
                    keyPairProvider.getSigner()).getEncoded());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (Exception ex) {

            SecurityContextHolder.clearContext();
            res.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
            return false;
        }

    }
    /*
    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView)
            throws Exception {
    }
     */
}
