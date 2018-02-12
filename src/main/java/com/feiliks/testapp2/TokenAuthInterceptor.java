package com.feiliks.testapp2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class TokenAuthInterceptor extends HandlerInterceptorAdapter {

    public final static String DEFAULT_TOKEN_HEADER = "X-TOKEN";
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
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler)
            throws Exception {
        String headerName = tokenHeaderName;
        if (headerName == null) {
            headerName = DEFAULT_TOKEN_HEADER;
        }
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            String token = req.getHeader(headerName);
            AuthToken authentication = AuthTokenUtil.check(req, token);

            // refresh token
            HttpServletResponse res = (HttpServletResponse) response;
            res.setHeader(headerName, AuthTokenUtil.create(
                    AuthTokenUtil.getUser(req),
                    keyPairProvider.getSigner()).getEncoded());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (Exception ex) {

            SecurityContextHolder.clearContext();
            ((HttpServletResponse) response).sendError(
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
