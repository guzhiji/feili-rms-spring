package com.feiliks.rms.controllers;

import com.feiliks.common.AuthTokenUtil;
import com.feiliks.common.AuthorizationException;
import com.feiliks.common.NotFoundException;
import com.feiliks.common.ValidationException;
import com.feiliks.common.controllers.AbstractRestController;
import com.feiliks.common.repositories.UserRepository;
import com.feiliks.rms.JpaUtils;
import com.feiliks.common.dto.EntityMessage;
import com.feiliks.rms.dto.RequestDTO;
import com.feiliks.rms.dto.RequestStatusDTO;
import com.feiliks.rms.dto.RequirementDTO;
import com.feiliks.rms.entities.Request;
import com.feiliks.rms.entities.Requirement;
import com.feiliks.common.entities.User;
import com.feiliks.rms.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/requests")
public class RequestController extends AbstractRestController {

    @Autowired
    private RequestRepository repo;

    @Autowired
    private RequestTypeRepository requestTypeRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TagRepository tagRepo;

    @Autowired
    private RequirementRepository requirementRepo;

    @GetMapping("/own")
    @Transactional(readOnly = true)
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

    @GetMapping
    @Transactional(readOnly = true)
    public EntityMessage<List<RequestDTO>> getRequests(HttpServletRequest req) {
        return getRequests(req, 1);
    }

    @GetMapping("/pages/{n}")
    @Transactional(readOnly = true)
    public EntityMessage<List<RequestDTO>> getRequests(
            HttpServletRequest req,
            @PathVariable int n) {
        Page<Request> result = repo.findAll(new PageRequest(n - 1, 10));
        return respondListWithType(result.getContent(), Request.class, RequestDTO.class);
    }

    @PostMapping
    @Transactional
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
        entity.setModified(entity.getCreated());

        RequestDTO out = new RequestDTO(repo.save(entity));
        return respondCreatedStatus(out, getClass(), "getRequest", out.getId());
    }

    @PutMapping("/{requestid}/status")
    @Transactional
    public ResponseEntity<EntityMessage<RequestStatusDTO>> updateRequestStatus(
            HttpServletRequest req,
            @PathVariable Long requestid,
            @Valid @RequestBody RequestStatusDTO data,
            BindingResult validationResult) {
        // 1.passes data validation
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }
        // 2.exists
        Request entity = repo.findOne(requestid);
        if (entity == null) {
            throw new NotFoundException(Request.class, requestid.toString());
        }
        // 3.is owner
        User owner = AuthTokenUtil.getUser(req);
        if (!owner.equals(entity.getOwner())) {
            throw new AuthorizationException();
        }

        entity.setStatus(data.getStatusAsObject());
        entity.setModified(new Date());
        repo.save(entity);

        EntityMessage<RequestStatusDTO> msg = new EntityMessage<>("success", data);
        return ResponseEntity.accepted().body(msg);
    }

    @PutMapping("/{requestid}")
    @Transactional
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
            throw new NotFoundException(Request.class, requestid.toString());
        }
        // 3.is owner
        User owner = AuthTokenUtil.getUser(req);
        if (!owner.equals(original.getOwner())) {
            throw new AuthorizationException();
        }

        Request entity = data.toEntity();
        entity.setId(requestid);
        entity.setType(original.getType()); // cannot change type
        entity.setOwner(owner);
        entity.setCreated(original.getCreated());
        entity.setModified(new Date());

        RequestDTO out = new RequestDTO(repo.save(entity));
        EntityMessage<RequestDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

    @GetMapping("/{requestid}")
    @Transactional(readOnly = true)
    public EntityMessage<RequestDTO> getRequest(@PathVariable Long requestid) {
        Request r = repo.findOne(requestid);
        if (r == null) {
            throw new NotFoundException(Request.class, requestid.toString());
        }
        return new EntityMessage<>("success", new RequestDTO(r));
    }

    @DeleteMapping("/{requestid}")
    @Transactional
    public ResponseEntity<?> deleteRequest(
            HttpServletRequest req,
            @PathVariable Long requestid) {
        Request r = repo.findOne(requestid);
        if (r == null) {
            throw new NotFoundException(Request.class, requestid.toString());
        }
        User owner = AuthTokenUtil.getUser(req);
        if (!owner.equals(r.getOwner())) {
            throw new AuthorizationException();
        }
        repo.delete(r);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{requestid}/requirements")
    @Transactional(readOnly = true)
    public List<RequirementDTO> getRequirements(
            HttpServletRequest req,
            @PathVariable Long requestid) {
        Request request = repo.findOne(requestid);
        if (request == null) {
            throw new NotFoundException(Request.class, requestid.toString());
        }
        return convertListItemType(
                request.getRequirements(),
                Requirement.class, RequirementDTO.class);
    }

    @PostMapping("/{requestid}/requirements")
    @Transactional
    public ResponseEntity<EntityMessage<RequirementDTO>> createRequirement(
            HttpServletRequest req,
            @PathVariable Long requestid,
            @Valid @RequestBody RequirementDTO data,
            BindingResult validationResult) {
        // 1. passes validation
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }
        // 2. parent request exists
        Request request = repo.findOne(requestid);
        if (request == null) {
            throw new NotFoundException(Request.class, requestid.toString());
        }
        if (request.getStatus().equals(Request.Status.CLOSED)) {
            throw new ValidationException(String.format("Request %d is closed.", requestid));
        }
        // 3. is manager
        User owner = AuthTokenUtil.getUser(req);
        User manager = request.getType().getManager();
        if (manager == null || !Objects.equals(manager.getId(), owner.getId())) {
            throw new AuthorizationException();
        }

        Requirement entity = data.toEntity();
        entity.setOwner(owner);
        entity.setRequests(new HashSet<>(Arrays.asList(request))); // overwrite request id received in body
        JpaUtils.fetchRequirementParticipants(userRepo, entity);
        JpaUtils.fetchOrCreateRequirementTags(tagRepo, entity);
        entity.setCreated(new Date());
        entity.setModified(entity.getCreated());

        RequirementDTO out = new RequirementDTO(requirementRepo.save(entity));
        return respondCreatedStatus(out, RequirementController.class, "getRequirement", out.getId());
    }

}
