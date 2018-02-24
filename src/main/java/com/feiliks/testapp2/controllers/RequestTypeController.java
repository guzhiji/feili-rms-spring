package com.feiliks.testapp2.controllers;

import com.feiliks.testapp2.JpaUtils;
import com.feiliks.testapp2.NotFoundException;
import com.feiliks.testapp2.ValidationException;
import com.feiliks.testapp2.dto.EntityMessage;
import com.feiliks.testapp2.dto.RequestTypeDTO;
import com.feiliks.testapp2.jpa.entities.RequestType;
import com.feiliks.testapp2.jpa.repositories.RequestTypeRepository;
import com.feiliks.testapp2.jpa.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public List<RequestTypeDTO> getRequestTypes() {
        List<RequestTypeDTO> out = new ArrayList<>();
        for (RequestType rt : repo.findAll()) {
            out.add(new RequestTypeDTO(rt));
        }
        return out;
    }

    @PostMapping
    public ResponseEntity<EntityMessage<RequestTypeDTO>> createRequestType(
            @Valid @RequestBody RequestTypeDTO data,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }

        RequestType entity = data.toEntity();
        JpaUtils.fetchManager(userRepo, entity);

        RequestTypeDTO out = new RequestTypeDTO(repo.save(entity));
        return respondCreatedStatus(out, getClass(), "getRequestType", out.getId());
    }

    @PutMapping("/{typeid}")
    public ResponseEntity<EntityMessage<RequestTypeDTO>> updateRequestType(
            @PathVariable Long typeid,
            @Valid @RequestBody RequestTypeDTO data,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }
        if (!repo.exists(typeid)) {
            throw new NotFoundException(typeid.toString());
        }

        RequestType entity = data.toEntity();
        entity.setId(typeid);
        JpaUtils.fetchManager(userRepo, entity);

        RequestTypeDTO out = new RequestTypeDTO(repo.save(entity));
        EntityMessage<RequestTypeDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

    @GetMapping("/{typeid}")
    public RequestTypeDTO getRequestType(@PathVariable Long typeid) {
        RequestType rt = repo.findOne(typeid);
        if (rt == null) {
            throw new NotFoundException(typeid.toString());
        }
        return new RequestTypeDTO(rt);
    }

    @DeleteMapping("/{typeid}")
    public ResponseEntity<?> deleteRequestType(@PathVariable Long typeid) {
        RequestType entity = repo.findOne(typeid);
        if (entity == null) {
            throw new NotFoundException(typeid.toString());
        }
        repo.delete(entity);
        return ResponseEntity.noContent().build();
    }

}
