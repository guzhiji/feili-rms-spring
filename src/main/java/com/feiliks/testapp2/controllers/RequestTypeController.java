package com.feiliks.testapp2.controllers;

import com.feiliks.testapp2.JpaUtils;
import com.feiliks.testapp2.NotFoundException;
import com.feiliks.testapp2.ValidationException;
import com.feiliks.testapp2.dto.EntityMessage;
import com.feiliks.testapp2.dto.RequestTypeDTO;
import com.feiliks.testapp2.jpa.entities.RequestType;
import com.feiliks.testapp2.jpa.entities.User;
import com.feiliks.testapp2.jpa.repositories.RequestTypeRepository;
import com.feiliks.testapp2.jpa.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/request-types")
public class RequestTypeController extends AbstractController {

    @Autowired
    private RequestTypeRepository repo;

    @Autowired
    private UserRepository userRepo;

    @GetMapping
    @Transactional(readOnly = true)
    public EntityMessage<List<RequestTypeDTO>> getRequestTypes() {
        return respondListWithType(repo.findAll(), RequestType.class, RequestTypeDTO.class);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<EntityMessage<RequestTypeDTO>> createRequestType(
            HttpServletRequest req,
            @Valid @RequestBody RequestTypeDTO data,
            BindingResult validationResult) {

        requiresPermissions(req, User.Permission.MANAGE_REQUEST_TYPES);

        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }

        RequestType entity = data.toEntity();
        JpaUtils.fetchManager(userRepo, entity);

        RequestTypeDTO out = new RequestTypeDTO(repo.save(entity));
        return respondCreatedStatus(out, getClass(), "getRequestType", out.getId());
    }

    @PutMapping("/{typeid}")
    @Transactional
    public ResponseEntity<EntityMessage<RequestTypeDTO>> updateRequestType(
            HttpServletRequest req,
            @PathVariable Long typeid,
            @Valid @RequestBody RequestTypeDTO data,
            BindingResult validationResult) {

        requiresPermissions(req, User.Permission.MANAGE_REQUEST_TYPES);

        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }
        if (!repo.exists(typeid)) {
            throw new NotFoundException(RequestType.class, typeid.toString());
        }

        RequestType entity = data.toEntity();
        entity.setId(typeid);
        JpaUtils.fetchManager(userRepo, entity);

        RequestTypeDTO out = new RequestTypeDTO(repo.save(entity));
        EntityMessage<RequestTypeDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

    @GetMapping("/{typeid}")
    @Transactional(readOnly = true)
    public EntityMessage<RequestTypeDTO> getRequestType(@PathVariable Long typeid) {
        RequestType rt = repo.findOne(typeid);
        if (rt == null) {
            throw new NotFoundException(RequestType.class, typeid.toString());
        }
        return new EntityMessage<>("success", new RequestTypeDTO(rt));
    }

    @DeleteMapping("/{typeid}")
    @Transactional
    public ResponseEntity<?> deleteRequestType(
            HttpServletRequest req,
            @PathVariable Long typeid) {

        requiresPermissions(req, User.Permission.MANAGE_REQUEST_TYPES);

        RequestType entity = repo.findOne(typeid);
        if (entity == null) {
            throw new NotFoundException(RequestType.class, typeid.toString());
        }
        repo.delete(entity);

        return ResponseEntity.noContent().build();
    }

}
