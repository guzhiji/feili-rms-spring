package com.feiliks.common.controllers;

import com.feiliks.common.AlreadyExistsException;
import com.feiliks.common.NotFoundException;
import com.feiliks.common.PasswordUtil;
import com.feiliks.common.ValidationException;
import com.feiliks.common.dto.*;
import com.feiliks.rms.dto.*;
import com.feiliks.rms.entities.UserPermission;
import com.feiliks.rms.entities.Request;
import com.feiliks.rms.entities.RequestType;
import com.feiliks.rms.entities.Requirement;
import com.feiliks.common.entities.User;
import com.feiliks.common.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
class UserController extends AbstractRestController {

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

    @GetMapping
    @Transactional(readOnly = true)
    public EntityMessage<List<UserDTO>> getUsers() {
        return respondListWithType(userRepository.findAll(), User.class, UserDTO.class);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<EntityMessage<UserDTO>> createUser(
            HttpServletRequest req,
            @RequestBody @Valid User data,
            BindingResult bindingResult) {
        requiresPermissions(req, UserPermission.MANAGE_USERS);
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        if (userRepository.existsByUsername(data.getUsername())) {
            throw new AlreadyExistsException(data.getUsername());
        }
        data.setPassword(PasswordUtil.hash(data.getUsername(), data.getPassword()));

        UserDTO out = new UserDTO(userRepository.save(data));
        return respondCreatedStatus(out, getClass(), "getUser", out.getId());
    }

    @PutMapping("/{userid}")
    @Transactional
    public ResponseEntity<EntityMessage<UserDTO>> updateUser(
            HttpServletRequest req,
            @PathVariable Long userid,
            @RequestBody @Valid UserDTO data,
            BindingResult bindingResult) {
        requiresPermissions(req, UserPermission.MANAGE_USERS);
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        User entity = getUserOrRaiseEx(userid);
        // username is not allowed to be changed
        entity.setPhone(data.getPhone());
        entity.setEmail(data.getEmail());
        UserDTO out = new UserDTO(userRepository.save(entity));
        EntityMessage<UserDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

    @PutMapping("/{userid}/password")
    @Transactional
    public ResponseEntity<Message> resetPassword(
            HttpServletRequest req,
            @PathVariable Long userid,
            @RequestBody @Valid PasswordDTO data,
            BindingResult bindingResult) {
        requiresPermissions(req, UserPermission.MANAGE_USERS);
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        User entity = getUserOrRaiseEx(userid);
        entity.setPassword(PasswordUtil.hash(entity.getUsername(), data.getPassword()));
        userRepository.save(entity);
        Message msg = new Message("success", "Password is reset.");
        return ResponseEntity.accepted().body(msg);
    }

    @PutMapping("/{userid}/permissions")
    @Transactional
    public ResponseEntity<Message> updatePermissions(
            HttpServletRequest req,
            @PathVariable Long userid,
            @RequestBody @Valid PermissionsDTO data,
            BindingResult bindingResult) {
        requiresPermissions(req, UserPermission.MANAGE_USERS);
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        User entity = getUserOrRaiseEx(userid);
        entity.setPermissions(data.getPermissionsAsSet());
        userRepository.save(entity);
        Message msg = new Message("success", "Permissions are updated.");
        return ResponseEntity.accepted().body(msg);
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
    public EntityMessage<List<RequestTypeDTO>> getManagedRequestTypes(
            HttpServletRequest req,
            @PathVariable Long userid) {
        User owner = getUserOrRaiseEx(userid);
        return respondListWithType(
                owner.getRequestTypesManaged(),
                RequestType.class, RequestTypeDTO.class);
    }

    @GetMapping("/{userid}/requirements/owned")
    @Transactional(readOnly = true)
    public EntityMessage<List<RequirementDTO>> getOwnedRequirements(
            HttpServletRequest req,
            @PathVariable Long userid) {
        User owner = getUserOrRaiseEx(userid);
        return respondListWithType(
                owner.getRequirementsOwned(),
                Requirement.class, RequirementDTO.class);
    }

    @GetMapping("/{userid}/requirements/participated")
    @Transactional(readOnly = true)
    public EntityMessage<List<RequirementDTO>> getParticipatedRequirements(
            HttpServletRequest req,
            @PathVariable Long userid) {
        User owner = getUserOrRaiseEx(userid);
        return respondListWithType(
                owner.getRequirementsParticipated(),
                Requirement.class, RequirementDTO.class);
    }

    @GetMapping("/{userid}")
    @Transactional(readOnly = true)
    public EntityMessage<UserWithPermissionsDTO> getUser(@PathVariable Long userid) {
        return new EntityMessage<>(
                "success",
                new UserWithPermissionsDTO(getUserOrRaiseEx(userid)));
    }

    @DeleteMapping("/{userid}")
    @Transactional
    public ResponseEntity<?> deleteUser(HttpServletRequest req, @PathVariable Long userid) {
        requiresPermissions(req, UserPermission.MANAGE_USERS);
        userRepository.delete(getUserOrRaiseEx(userid));
        return ResponseEntity.noContent().build();
    }

}
