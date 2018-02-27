package com.feiliks.testapp2.controllers;

import com.feiliks.testapp2.AlreadyExistsException;
import com.feiliks.testapp2.NotFoundException;
import com.feiliks.testapp2.ValidationException;
import com.feiliks.testapp2.dto.EntityMessage;
import com.feiliks.testapp2.dto.Message;
import com.feiliks.testapp2.jpa.entities.User;
import com.feiliks.testapp2.dto.UserDTO;
import com.feiliks.testapp2.dto.PasswordDTO;
import com.feiliks.testapp2.dto.RequestDTO;
import com.feiliks.testapp2.dto.RequestTypeDTO;
import com.feiliks.testapp2.dto.RequirementDTO;
import com.feiliks.testapp2.jpa.entities.Request;
import com.feiliks.testapp2.jpa.entities.RequestType;
import com.feiliks.testapp2.jpa.entities.Requirement;
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
import org.springframework.transaction.annotation.Transactional;
import com.feiliks.testapp2.jpa.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.validation.BindingResult;

@RestController
@RequestMapping("/users")
class UserController extends AbstractController {

    @Autowired
    private UserRepository userRepository;

    @ExceptionHandler(AlreadyExistsException.class)
    protected ResponseEntity<Message> handleUsernameExists(AlreadyExistsException ex) {
        Message msg = new Message("failure", "username already exists:" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
    }

    private User getUserOrRaiseEx(Long userid) {
        User entity = userRepository.findOne(userid);
        if (entity == null) {
            throw new NotFoundException(User.class, userid.toString());
        }
        return entity;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<EntityMessage<UserDTO>> createUser(
            @RequestBody @Valid User data,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        if (userRepository.existsByUsername(data.getUsername())) {
            throw new AlreadyExistsException(data.getUsername());
        }
        data.setPassword(DigestUtils.sha256Hex(data.getPassword()));

        UserDTO out = new UserDTO(userRepository.save(data));
        return respondCreatedStatus(out, getClass(), "getUser", out.getId());
    }

    @PutMapping("/{userid}")
    @Transactional
    public ResponseEntity<EntityMessage<UserDTO>> updateUser(
            @PathVariable Long userid,
            @RequestBody @Valid UserDTO data,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        User entity = getUserOrRaiseEx(userid);
        entity.setPhone(data.getPhone());
        entity.setEmail(data.getEmail());
        UserDTO out = new UserDTO(userRepository.save(entity));
        EntityMessage<UserDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

    @PutMapping("/{userid}/password")
    @Transactional
    public ResponseEntity<Message> updatePassword(
            @PathVariable Long userid,
            @RequestBody @Valid PasswordDTO data,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        User entity = getUserOrRaiseEx(userid);
        String hashed = DigestUtils.sha256Hex(data.getOriginal());
        if (hashed.equals(entity.getPassword())) {
            entity.setPassword(DigestUtils.sha256Hex(data.getPassword()));
            userRepository.save(entity);
            Message msg = new Message("success", "Password is updated.");
            return ResponseEntity.accepted().body(msg);
        } else {
            Message msg = new Message("failure", "Original password is incorrect.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
        }
    }

    @GetMapping("/{userid}/requests")
    @Transactional(readOnly = true)
    public List<RequestDTO> getOwnedRequests(
            HttpServletRequest req,
            @PathVariable Long userid) {
        User owner = getUserOrRaiseEx(userid);
        List<RequestDTO> out = new ArrayList<>();
        for (Request r : owner.getRequestsOwned()) {
            out.add(new RequestDTO(r));
        }
        return out;
    }

    @GetMapping("/{userid}/request-types")
    @Transactional(readOnly = true)
    public List<RequestTypeDTO> getManagedRequestTypes(
            HttpServletRequest req,
            @PathVariable Long userid) {
        User owner = getUserOrRaiseEx(userid);
        List<RequestTypeDTO> out = new ArrayList<>();
        for (RequestType r : owner.getRequestTypesManaged()) {
            out.add(new RequestTypeDTO(r));
        }
        return out;
    }

    @GetMapping("/{userid}/requirements/owned")
    @Transactional(readOnly = true)
    public List<RequirementDTO> getOwnedRequirements(
            HttpServletRequest req,
            @PathVariable Long userid) {
        User owner = getUserOrRaiseEx(userid);
        List<RequirementDTO> out = new ArrayList<>();
        for (Requirement r : owner.getRequirementsOwned()) {
            out.add(new RequirementDTO(r));
        }
        return out;
    }

    @GetMapping("/{userid}/requirements/participated")
    @Transactional(readOnly = true)
    public List<RequirementDTO> getParticipatedRequirements(
            HttpServletRequest req,
            @PathVariable Long userid) {
        User owner = getUserOrRaiseEx(userid);
        List<RequirementDTO> out = new ArrayList<>();
        for (Requirement r : owner.getRequirementsParticipated()) {
            out.add(new RequirementDTO(r));
        }
        return out;
    }

    @GetMapping("/{userid}")
    @Transactional(readOnly = true)
    public UserDTO getUser(@PathVariable Long userid) {
        return new UserDTO(getUserOrRaiseEx(userid));
    }

    @DeleteMapping("/{userid}")
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable Long userid) {
        userRepository.delete(getUserOrRaiseEx(userid));
        return ResponseEntity.noContent().build();
    }

}
