package com.feiliks.testapp2.controllers;

import com.feiliks.testapp2.AuthTokenUtil;
import com.feiliks.testapp2.AuthorizationException;
import com.feiliks.testapp2.NotFoundException;
import com.feiliks.testapp2.ValidationException;
import com.feiliks.testapp2.dto.EntityMessage;
import com.feiliks.testapp2.dto.Message;
import com.feiliks.testapp2.dto.RequestDTO;
import com.feiliks.testapp2.jpa.entities.Request;
import com.feiliks.testapp2.jpa.entities.RequestType;
import com.feiliks.testapp2.jpa.entities.User;
import com.feiliks.testapp2.jpa.repositories.RequestRepository;
import com.feiliks.testapp2.jpa.repositories.RequestTypeRepository;
import java.net.URI;
import java.util.Date;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private RequestRepository repo;

    @Autowired
    private RequestTypeRepository requestTypeRepo;

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Message> handleNotFound(NotFoundException ex) {
        Message msg = new Message("failure", "request not found:" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
    }

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<Message> handleValidationError(ValidationException ex) {
        Message msg = new Message("failure", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(msg);
    }

    @ExceptionHandler(AuthorizationException.class)
    protected ResponseEntity<Message> handleAuthorization(AuthorizationException ex) {
        Message msg = new Message("failure", "not authorized");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
    }

    private User checkAuthorization(HttpServletRequest req, Request request) {
        User owner = AuthTokenUtil.getUser(req);
        if (owner == null) {
            throw new AuthorizationException();
        }
        RequestType rtype = requestTypeRepo.findOne(request.getRequestType().getId()); // assumption: validated
        if (rtype == null) {
            throw new AuthorizationException();
        }
        User manager = rtype.getManager();
        if (manager == null || !Objects.equals(manager.getId(), owner.getId())) {
            throw new AuthorizationException();
        }
        request.setRequestType(rtype);
        return owner;
    }

    @PostMapping
    public ResponseEntity<EntityMessage<RequestDTO>> createRequest(
            HttpServletRequest req,
            @Valid @RequestBody Request request,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }
        request.setOwner(checkAuthorization(req, request));
        RequestDTO out = new RequestDTO(repo.save(request));
        UriComponents uriComponents = MvcUriComponentsBuilder.fromMethodName(
                getClass(), "getRequest", out.getId()).build();
        URI uri = uriComponents.encode().toUri();
        EntityMessage<RequestDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.created(uri).body(msg);
    }

    @PutMapping("/{requestid}")
    public ResponseEntity<EntityMessage<RequestDTO>> updateRequest(
            HttpServletRequest req,
            @PathVariable Long requestid,
            @Valid @RequestBody Request request,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }
        if (!repo.exists(requestid)) {
            throw new NotFoundException(requestid.toString());
        }
        request.setOwner(checkAuthorization(req, request));
        request.setId(requestid);
        request.setModified(new Date());
        RequestDTO out = new RequestDTO(repo.save(request));
        EntityMessage<RequestDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

    @GetMapping("/{requestid}")
    public RequestDTO getRequest(@PathVariable Long requestid) {
        Request r = repo.findOne(requestid);
        if (r == null) {
            throw new NotFoundException(requestid.toString());
        }
        return new RequestDTO(r);
    }

    @DeleteMapping("/{requestid}")
    public ResponseEntity<?> deleteRequest(
            HttpServletRequest req,
            @PathVariable Long requestid) {
        Request r = repo.findOne(requestid);
        if (r == null) {
            throw new NotFoundException(requestid.toString());
        }
        checkAuthorization(req, r);
        repo.delete(r);
        return ResponseEntity.noContent().build();
    }

}
