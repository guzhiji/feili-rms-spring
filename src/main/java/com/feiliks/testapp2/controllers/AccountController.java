package com.feiliks.testapp2.controllers;

import com.feiliks.testapp2.AuthTokenUtil;
import com.feiliks.testapp2.KeyPairProvider;
import com.feiliks.testapp2.TokenAuthFilter;
import com.feiliks.testapp2.ValidationException;
import com.feiliks.testapp2.dto.LoginDTO;
import com.feiliks.testapp2.dto.Message;
import com.feiliks.testapp2.dto.UserDTO;
import com.feiliks.testapp2.jpa.entities.User;
import com.feiliks.testapp2.jpa.repositories.UserRepository;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.codec.digest.DigestUtils;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeyPairProvider keyPairProvider;

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<Message> handleValidationError(ValidationException ex) {
        Message msg = new Message("failure", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(msg);
    }

    @PostMapping("/login")
    public ResponseEntity<Message> login(
            @RequestBody @Valid LoginDTO login,
            BindingResult result) throws IOException {
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }
        User u = userRepository.findByUsername(login.getUsername());
        if (u != null && u.getPassword().equals(DigestUtils.sha256Hex(login.getPassword()))) {
            String token = AuthTokenUtil.create(
                    u, keyPairProvider.getSigner()).getEncoded();
            Message msg = new Message("success", token);
            return ResponseEntity.ok()
                    .header(TokenAuthFilter.DEFAULT_TOKEN_HEADER, token)
                    .body(msg);
        }
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new Message("failure", "not authenticated"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Message> logout() {
        return ResponseEntity.ok(new Message("success", null));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(HttpServletRequest req) {
        User u = AuthTokenUtil.getUser(req);
        if (u == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserDTO(u));
    }
}
