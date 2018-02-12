package com.feiliks.testapp2.controllers;

import com.feiliks.testapp2.AlreadyExistsException;
import com.feiliks.testapp2.NotFoundException;
import com.feiliks.testapp2.ValidationException;
import com.feiliks.testapp2.dto.EntityMessage;
import com.feiliks.testapp2.dto.Message;
import com.feiliks.testapp2.jpa.entities.User;
import com.feiliks.testapp2.dto.UserDTO;
import com.feiliks.testapp2.dto.PasswordDTO;
import java.net.URI;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import com.feiliks.testapp2.jpa.repositories.UserRepository;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.validation.BindingResult;

@RestController
@RequestMapping("/users")
class UserController {

    @Autowired
    private UserRepository userRepository;

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Message> handleNotFound(NotFoundException ex) {
        Message msg = new Message("failure", "user not found:" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    protected ResponseEntity<Message> handleUsernameExists(AlreadyExistsException ex) {
        Message msg = new Message("failure", "username already exists:" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
    }

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<Message> handleValidationError(ValidationException ex) {
        Message msg = new Message("failure", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(msg);
    }

    @PostMapping
    public ResponseEntity<EntityMessage<UserDTO>> createUser(
            @RequestBody @Valid User user,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new AlreadyExistsException(user.getUsername());
        }
        user.setPassword(DigestUtils.sha256Hex(user.getPassword()));
        UserDTO out = new UserDTO(userRepository.save(user));
        UriComponents uriComponents = MvcUriComponentsBuilder.fromMethodName(
                getClass(), "getUser", out.getId()).build();
        URI uri = uriComponents.encode().toUri();
        EntityMessage<UserDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.created(uri).body(msg);
    }

    @PutMapping("/{userid}")
    public ResponseEntity<EntityMessage<UserDTO>> updateUser(
            @PathVariable Long userid,
            @RequestBody @Valid UserDTO user,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        User entity = userRepository.findOne(userid);
        if (entity == null) {
            throw new NotFoundException(userid.toString());
        }
        entity.setPhone(user.getPhone());
        entity.setEmail(user.getEmail());
        UserDTO out = new UserDTO(userRepository.save(entity));
        EntityMessage<UserDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

    @PutMapping("/{userid}/password")
    public ResponseEntity<Message> updatePassword(
            @PathVariable Long userid,
            @RequestBody @Valid PasswordDTO pass,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        User entity = userRepository.findOne(userid);
        if (entity == null) {
            throw new NotFoundException(userid.toString());
        }
        String hashed = DigestUtils.sha256Hex(pass.getOriginal());
        if (hashed.equals(entity.getPassword())) {
            entity.setPassword(DigestUtils.sha256Hex(pass.getPassword()));
            userRepository.save(entity);
            Message msg = new Message("success", "Password is updated.");
            return ResponseEntity.accepted().body(msg);
        } else {
            Message msg = new Message("failure", "Original password is incorrect.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
        }
    }

    @GetMapping("/{userid}")
    public UserDTO getUser(@PathVariable Long userid) {
        User u = userRepository.findOne(userid);
        if (u == null) {
            throw new NotFoundException(userid.toString());
        }
        return new UserDTO(u);
    }

    @DeleteMapping("/{userid}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userid) {
        User u = userRepository.findOne(userid);
        if (u == null) {
            throw new NotFoundException(userid.toString());
        }
        userRepository.delete(u);
        return ResponseEntity.noContent().build();
    }

}
