package com.feiliks.testapp2.controllers;

import com.feiliks.testapp2.AuthTokenUtil;
import com.feiliks.testapp2.AuthorizationException;
import com.feiliks.testapp2.JpaUtils;
import com.feiliks.testapp2.NotFoundException;
import com.feiliks.testapp2.ValidationException;
import com.feiliks.testapp2.dto.EntityMessage;
import com.feiliks.testapp2.dto.RequestDTO;
import com.feiliks.testapp2.dto.RequirementDTO;
import com.feiliks.testapp2.jpa.entities.Request;
import com.feiliks.testapp2.jpa.entities.RequestType;
import com.feiliks.testapp2.jpa.entities.Requirement;
import com.feiliks.testapp2.jpa.entities.User;
import com.feiliks.testapp2.jpa.repositories.CheckPointRepository;
import com.feiliks.testapp2.jpa.repositories.RequestRepository;
import com.feiliks.testapp2.jpa.repositories.RequestTypeRepository;
import com.feiliks.testapp2.jpa.repositories.RequirementRepository;
import com.feiliks.testapp2.jpa.repositories.TagRepository;
import com.feiliks.testapp2.jpa.repositories.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("/requests")
public class RequestController extends AbstractController {

    @Autowired
    private RequestRepository repo;

    @Autowired
    private RequestTypeRepository requestTypeRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CheckPointRepository checkpointRepo;

    @Autowired
    private TagRepository tagRepo;

    @Autowired
    private RequirementRepository requirementRepo;

    private User checkAuthorization(HttpServletRequest req, Request data) {
        User owner = AuthTokenUtil.getUser(req);
        if (owner == null) {
            throw new AuthorizationException();
        }
        RequestType rtype = requestTypeRepo.findOne(data.getRequestType().getId()); // assumption: type=>not-null
        if (rtype == null) {
            throw new AuthorizationException();
        }
        User manager = rtype.getManager();
        if (manager == null || !Objects.equals(manager.getId(), owner.getId())) {
            throw new AuthorizationException();
        }
        data.setRequestType(rtype);
        return owner;
    }

    @GetMapping
    public List<RequestDTO> getOwnRequests(HttpServletRequest req) {
        User owner = AuthTokenUtil.getUser(req);
        if (owner == null) {
            throw new AuthorizationException();
        }
        List<Request> result = repo.findByOwner(owner);
        List<RequestDTO> out = new ArrayList<>();
        for (Request r : result) {
            out.add(new RequestDTO(r));
        }
        return out;
    }

    @PostMapping
    public ResponseEntity<EntityMessage<RequestDTO>> createRequest(
            HttpServletRequest req,
            @Valid @RequestBody RequestDTO data,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }

        Request entity = data.toEntity();
        entity.setOwner(AuthTokenUtil.getUser(req));
        JpaUtils.fetchRequestType(requestTypeRepo, entity);
        entity.setCreated(new Date());

        RequestDTO out = new RequestDTO(repo.save(entity));
        return respondCreatedStatus(out, getClass(), "getRequest", out.getId());
    }

    @PutMapping("/{requestid}")
    public ResponseEntity<EntityMessage<RequestDTO>> updateRequest(
            HttpServletRequest req,
            @PathVariable Long requestid,
            @Valid @RequestBody RequestDTO data,
            BindingResult validationResult) {
        // 1.passes data validation
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }
        // 2.exists
        Request original = repo.findOne(requestid);
        if (original == null) {
            throw new NotFoundException(requestid.toString());
        }
        // 3.is owner
        User owner = AuthTokenUtil.getUser(req);
        if (!owner.equals(original.getOwner())) {
            throw new AuthorizationException();
        }

        Request entity = data.toEntity();
        entity.setId(requestid);
        entity.setRequestType(original.getRequestType()); // cannot change type
        entity.setModified(new Date());

        RequestDTO out = new RequestDTO(repo.save(entity));
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
        User owner = AuthTokenUtil.getUser(req);
        if (!owner.equals(r.getOwner())) {
            throw new AuthorizationException();
        }
        repo.delete(r);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{requestid}/requirements")
    public List<RequirementDTO> getRequirements(
            HttpServletRequest req,
            @PathVariable Long requestid) {
        Request request = repo.findOne(requestid);
        if (request == null) {
            throw new NotFoundException(requestid.toString());
        }
        Set<Requirement> requirements = request.getRequirements();
        List<RequirementDTO> out = new ArrayList<>();
        for (Requirement requirement : requirements) {
            out.add(new RequirementDTO(requirement));
        }
        return out;
    }

    @PostMapping("/{requestid}/requirements")
    public ResponseEntity<EntityMessage<RequirementDTO>> createRequirement(
            HttpServletRequest req,
            @PathVariable Long requestid,
            @Valid @RequestBody RequirementDTO data,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }
        Request request = repo.findOne(requestid);
        if (request == null) {
            throw new NotFoundException(requestid.toString());
        }

        Requirement entity = data.toEntity();
        entity.setOwner(checkAuthorization(req, request));
        entity.setRequests(new HashSet(Arrays.asList(request))); // overwrite request id received in body
        JpaUtils.fetchRequirementParticipants(userRepo, entity);
        JpaUtils.fetchOrCreateRequirementTags(tagRepo, entity);
        entity.setCreated(new Date());

        RequirementDTO out = new RequirementDTO(requirementRepo.save(entity));
        return respondCreatedStatus(out, RequirementController.class, "getRequirement", out.getId());
    }

}
