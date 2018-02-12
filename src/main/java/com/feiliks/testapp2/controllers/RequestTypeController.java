package com.feiliks.testapp2.controllers;

import com.feiliks.testapp2.NotFoundException;
import com.feiliks.testapp2.dto.EntityMessage;
import com.feiliks.testapp2.dto.Message;
import com.feiliks.testapp2.dto.RequestTypeDTO;
import com.feiliks.testapp2.jpa.entities.RequestType;
import com.feiliks.testapp2.jpa.repositories.RequestTypeRepository;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

@RestController
@RequestMapping("/request-types")
public class RequestTypeController {

    @Autowired
    private RequestTypeRepository repo;

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Message> handleNotFound(NotFoundException ex) {
        Message msg = new Message("failure", "request type not found:" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
    }

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
            @RequestBody RequestType requestType) {
        RequestTypeDTO out = new RequestTypeDTO(repo.save(requestType));
        UriComponents uriComponents = MvcUriComponentsBuilder.fromMethodName(
                getClass(), "getRequestType", out.getId()).build();
        URI uri = uriComponents.encode().toUri();
        EntityMessage<RequestTypeDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.created(uri).body(msg);
    }

    @PutMapping("/{typeid}")
    public ResponseEntity<EntityMessage<RequestTypeDTO>> updateRequestType(
            @PathVariable Long typeid,
            @RequestBody RequestType type) {
        if (!repo.exists(typeid)) {
            throw new NotFoundException(typeid.toString());
        }
        type.setId(typeid);
        RequestTypeDTO out = new RequestTypeDTO(repo.save(type));
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
