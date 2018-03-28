package com.feiliks.testapp2;

import java.io.IOException;
import java.util.*;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class TokenAuthFilter implements Filter {

    public final static String DEFAULT_TOKEN_HEADER = "X-TOKEN";
    private final static Set<String> allowedMethods = new HashSet<>(
            Arrays.asList("GET", "POST", "PUT", "DELETE"));
    private List<AntPathRequestMatcher> protectedPaths;
    private List<AntPathRequestMatcher> openPaths;
    private String tokenHeaderName;

    public void setProtectedPaths(String paths) {
        if (paths != null) {
            setProtectedPaths(Arrays.asList(paths.split(":")));
        }
    }

    public void setProtectedPaths(List<String> paths) {
        List<AntPathRequestMatcher> pathList = new ArrayList<>();
        for (String path : paths) {
            AntPathRequestMatcher m = new AntPathRequestMatcher(path);
            if (!pathList.contains(m)) {
                pathList.add(m);
            }
        }
        protectedPaths = pathList;
    }

    public List<String> getProtectedPaths() {
        List<String> out = new ArrayList<>();
        if (protectedPaths != null) {
            for (AntPathRequestMatcher m : protectedPaths) {
                out.add(m.getPattern());
            }
        }
        return out;
    }

    public void setOpenPaths(String paths) {
        if (paths != null) {
            setOpenPaths(Arrays.asList(paths.split(":")));
        }
    }

    public void setOpenPaths(List<String> paths) {
        List<AntPathRequestMatcher> pathList = new ArrayList<>();
        for (String path : paths) {
            AntPathRequestMatcher m = new AntPathRequestMatcher(path);
            if (!pathList.contains(m)) {
                pathList.add(m);
            }
        }
        openPaths = pathList;
    }

    public List<String> getOpenPaths() {
        List<String> out = new ArrayList<>();
        if (openPaths != null) {
            for (AntPathRequestMatcher m : openPaths) {
                out.add(m.getPattern());
            }
        }
        return out;
    }

    public void setTokenHeaderName(String name) {
        tokenHeaderName = name;
    }

    public String getTokenHeaderName() {
        return tokenHeaderName;
    }

    private boolean shouldAuthenticate(HttpServletRequest request) {
        if (!allowedMethods.contains(request.getMethod())) {
            return false;
        }
        if (openPaths != null) {
            for (AntPathRequestMatcher m : openPaths) {
                if (m.matches(request)) {
                    return false;
                }
            }
        }
        if (protectedPaths != null) {
            for (AntPathRequestMatcher m : protectedPaths) {
                if (m.matches(request)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        setProtectedPaths(filterConfig.getInitParameter("protectedPaths"));
        setOpenPaths(filterConfig.getInitParameter("openPaths"));
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        if (shouldAuthenticate(req)) {
            String headerName = tokenHeaderName;
            if (headerName == null) {
                headerName = DEFAULT_TOKEN_HEADER;
            }
            try {

                // get token
                String token = req.getHeader(headerName);
                AuthToken authentication = AuthTokenUtil.check(req, token);

                // refresh token
                ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(
                        req.getServletContext());
                KeyPairProvider keyPairProvider = context.getBean(KeyPairProvider.class);
                HttpServletResponse res = (HttpServletResponse) response;
                res.setHeader(headerName, AuthTokenUtil.create(
                        AuthTokenUtil.getUser(req),
                        keyPairProvider.getSigner()).getEncoded());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ex) {
                SecurityContextHolder.clearContext();
                ((HttpServletResponse) response).sendError(
                        HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
                return;
            }
        }
        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {
    }

}
