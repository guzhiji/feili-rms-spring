package com.feiliks.testapp2.controllers;

import com.feiliks.testapp2.*;
import com.feiliks.testapp2.dto.LoginDTO;
import com.feiliks.testapp2.dto.Message;
import com.feiliks.testapp2.dto.UserWithPermissionsDTO;
import com.feiliks.testapp2.jpa.entities.User;
import com.feiliks.testapp2.jpa.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/account")
public class AccountController extends AbstractController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeyPairProvider keyPairProvider;

    @PostMapping("/login")
    public ResponseEntity<Message> login(
            @RequestBody @Valid LoginDTO login,
            BindingResult result) throws IOException {
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }
        User u = userRepository.findByUsername(login.getUsername());
        if (u != null && u.getPassword().equals(
                PasswordUtil.hash(login.getUsername(), login.getPassword()))) {
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
    public ResponseEntity<UserWithPermissionsDTO> getCurrentUser(HttpServletRequest req) {
        User u = AuthTokenUtil.getUser(req);
        if (u == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserWithPermissionsDTO(u));
    }
}
