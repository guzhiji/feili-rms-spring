package com.feiliks.rms.controllers;

import com.feiliks.common.*;
import com.feiliks.common.controllers.AbstractRestController;
import com.feiliks.common.dto.*;
import com.feiliks.rms.dto.UserDTO;
import com.feiliks.rms.dto.UserWithPermissionsDTO;
import com.feiliks.rms.entities.User;
import com.feiliks.rms.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/account")
public class AccountController extends AbstractRestController {

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
    public EntityMessage<UserWithPermissionsDTO> getCurrentUser(HttpServletRequest req) {
        User u = AuthTokenUtil.getUser(req);
        return new EntityMessage<>("success", new UserWithPermissionsDTO(u));
    }

    @PutMapping("/me")
    @Transactional
    public ResponseEntity<Message> updateCurrentUser(
            HttpServletRequest req,
            @RequestBody @Valid UserDTO data,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        User u = AuthTokenUtil.getUser(req);
        u.setEmail(data.getEmail());
        u.setPhone(data.getPhone());
        userRepository.save(u);
        Message msg = new Message("success", "User profile is updated.");
        return ResponseEntity.accepted().body(msg);
    }

    @PutMapping("/password")
    @Transactional
    public ResponseEntity<Message> updatePassword(
            HttpServletRequest req,
            @RequestBody @Valid PasswordDTO data,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        User entity = AuthTokenUtil.getUser(req);
        String hashed = PasswordUtil.hash(entity.getUsername(), data.getOriginal());
        if (hashed.equals(entity.getPassword())) {
            entity.setPassword(PasswordUtil.hash(entity.getUsername(), data.getPassword()));
            userRepository.save(entity);
            Message msg = new Message("success", "Password is updated.");
            return ResponseEntity.accepted().body(msg);
        } else {
            Message msg = new Message("failure", "Original password is incorrect.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
        }
    }

}
