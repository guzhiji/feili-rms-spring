package com.feiliks.testapp2.controllers;

import com.feiliks.testapp2.AuthTokenUtil;
import com.feiliks.testapp2.NotFoundException;
import com.feiliks.testapp2.dto.EntityMessage;
import com.feiliks.testapp2.dto.Message;
import com.feiliks.testapp2.dto.RequestDTO;
import com.feiliks.testapp2.jpa.entities.Request;
import com.feiliks.testapp2.jpa.repositories.RequestRepository;
import java.net.URI;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private RequestRepository repo;

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Message> handleNotFound(NotFoundException ex) {
        Message msg = new Message("failure", "request not found:" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
    }

    @PostMapping
    public ResponseEntity<EntityMessage<RequestDTO>> createRequest(
            HttpServletRequest req,
            @RequestBody Request request) {
        request.setOwner(AuthTokenUtil.getUser(req));
        RequestDTO out = new RequestDTO(repo.save(request));
        UriComponents uriComponents = MvcUriComponentsBuilder.fromMethodName(
                getClass(), "getRequest", out.getId()).build();
        URI uri = uriComponents.encode().toUri();
        EntityMessage<RequestDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.created(uri).body(msg);
    }

    @PutMapping("/{requestid}")
    public ResponseEntity<EntityMessage<RequestDTO>> updateRequest(
            @PathVariable Long requestid,
            @RequestBody Request request) {
        if (!repo.exists(requestid)) {
            throw new NotFoundException(requestid.toString());
        }
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
    public ResponseEntity<?> deleteRequest(@PathVariable Long requestid) {
        Request r = repo.findOne(requestid);
        if (r == null) {
            throw new NotFoundException(requestid.toString());
        }
        repo.delete(r);
        return ResponseEntity.noContent().build();
    }

}
