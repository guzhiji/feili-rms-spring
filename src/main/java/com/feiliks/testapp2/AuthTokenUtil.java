package com.feiliks.testapp2;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiliks.testapp2.jpa.entities.User;
import com.feiliks.testapp2.jpa.repositories.UserRepository;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class AuthTokenUtil {

    public static class TokenAuthException extends Exception {

        public TokenAuthException(String msg) {
            super(msg);
        }
    }

    public static Jwt create(User u, Signer signer) throws IOException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1);
        Map<String, Object> data = new HashMap<>();
        data.put("username", u.getUsername());
        data.put("hash", DigestUtils.md5Hex(String.format(
                "%s:%s:%f", u.getUsername(), u.getPassword(), Math.random())));
        data.put("expires", cal.getTime().getTime());
        StringWriter sw = new StringWriter();
        try (JsonGenerator g = new JsonFactory().createGenerator(sw)) {
            new ObjectMapper().writeValue(g, data);
            return JwtHelper.encode(sw.toString(), signer);
        }
    }

    public static Map<String, Object> decode(String token, SignatureVerifier verifier)
            throws IOException {
        Jwt jwt = JwtHelper.decodeAndVerify(token, verifier);
        JsonParser p = new JsonFactory().createParser(jwt.getClaims());
        return new ObjectMapper().readValue(
                p, new TypeReference<Map<String, Object>>() {
        });
    }

    static AuthToken check(HttpServletRequest req, String token)
            throws TokenAuthException, IOException {

        // get token
        if (token == null) {
            throw new TokenAuthException("no token provided");
        }

        // validate token
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(
                req.getServletContext());
        KeyPairProvider keyPairProvider = context.getBean(KeyPairProvider.class);
        Map<String, Object> data = decode(token, keyPairProvider.getVerifier());
        Long expires = (Long) data.get("expires");
        if (expires < new Date().getTime()) {
            throw new TokenAuthException("token already expired");
        }

        // get user
        UserRepository userRepo = context.getBean(UserRepository.class);
        User user = userRepo.findByUsername((String) data.get("username"));
        if (user == null) {
            throw new TokenAuthException("user not found");
        }

        req.setAttribute("user_object", user);
        req.setAttribute("user_token_data", data);
        req.setAttribute("user_token", token);

        return new AuthToken(user.getUsername(), (String) data.get("hash"));
    }

    public static User getUser(HttpServletRequest req) {
        return (User) req.getAttribute("user_object");
    }

    /**
     * get the decoded data from the requested token, not the refreshed one.
     *
     * @param req
     * @return
     */
    public static Map<String, Object> getUserTokenData(HttpServletRequest req) {
        return (Map<String, Object>) req.getAttribute("user_token_data");
    }

}
