package com.feiliks.testapp2.controllers;

import com.feiliks.testapp2.AuthTokenUtil;
import com.feiliks.testapp2.AuthorizationException;
import com.feiliks.testapp2.JpaUtils;
import com.feiliks.testapp2.NotFoundException;
import com.feiliks.testapp2.ValidationException;
import com.feiliks.testapp2.dto.EntityMessage;
import com.feiliks.testapp2.dto.RequirementDTO;
import com.feiliks.testapp2.jpa.entities.CheckPoint;
import com.feiliks.testapp2.jpa.entities.Request;
import com.feiliks.testapp2.jpa.entities.RequestType;
import com.feiliks.testapp2.jpa.entities.Requirement;
import com.feiliks.testapp2.jpa.entities.Tag;
import com.feiliks.testapp2.jpa.entities.User;
import com.feiliks.testapp2.jpa.repositories.CheckPointRepository;
import com.feiliks.testapp2.jpa.repositories.RequestRepository;
import com.feiliks.testapp2.jpa.repositories.RequirementRepository;
import com.feiliks.testapp2.jpa.repositories.TagRepository;
import com.feiliks.testapp2.jpa.repositories.UserRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/requirements")
public class RequirementController extends AbstractController {

    @Autowired
    private RequirementRepository repo;

    @Autowired
    private RequestRepository requestRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CheckPointRepository checkpointRepo;

    @Autowired
    private TagRepository tagRepo;

    private User checkAuthorization(HttpServletRequest req, Requirement data) {
        User owner = AuthTokenUtil.getUser(req); // assume protected by TokenAuthInterceptor/Filter
        Set<Request> requests = data.getRequests(); // assume requests been fetched
        for (Request request : requests) {
            RequestType rtype = request.getRequestType();
            User manager = rtype.getManager();
            if (manager != null && Objects.equals(manager.getId(), owner.getId())) {
                return owner;
            }
        }
        throw new AuthorizationException();
    }

    @GetMapping
    public List<RequirementDTO> getOwnRequirements(HttpServletRequest req) {
        User owner = AuthTokenUtil.getUser(req);
        List<RequirementDTO> out = new ArrayList<>();
        for (Requirement r : owner.getRequirementsOwned()) {
            out.add(new RequirementDTO(r));
        }
        return out;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<EntityMessage<RequirementDTO>> createRequirement(
            HttpServletRequest req,
            @Valid @RequestBody RequirementDTO data,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }

        Requirement entity = data.toEntity();
        JpaUtils.fetchRequirementRequests(requestRepo, entity);
        entity.setOwner(checkAuthorization(req, entity));
        JpaUtils.fetchRequirementParticipants(userRepo, entity);
        JpaUtils.fetchOrCreateRequirementTags(tagRepo, entity);
        entity.setCreated(new Date());
        entity.setModified(entity.getCreated());

        RequirementDTO out = new RequirementDTO(repo.save(entity));
        return respondCreatedStatus(out, getClass(), "getRequirement", out.getId());
    }

    @PutMapping("/{requirementid}")
    public ResponseEntity<EntityMessage<RequirementDTO>> updateRequirement(
            HttpServletRequest req,
            @PathVariable Long requirementid,
            @Valid @RequestBody RequirementDTO data,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }
        Requirement original = repo.findOne(requirementid);
        if (original == null) {
            throw new NotFoundException(requirementid.toString());
        }

        Requirement entity = data.toEntity();
        entity.setId(requirementid);
        JpaUtils.fetchRequirementRequests(requestRepo, entity);
        entity.setOwner(checkAuthorization(req, entity));
        JpaUtils.fetchRequirementParticipants(userRepo, entity);
        JpaUtils.fetchOrCreateRequirementTags(tagRepo, entity);
        entity.setCreated(original.getCreated());
        entity.setModified(new Date());

        RequirementDTO out = new RequirementDTO(repo.save(entity));
        EntityMessage<RequirementDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

    @GetMapping("/{requirementid}")
    public RequirementDTO getRequirement(@PathVariable Long requirementid) {
        Requirement entity = repo.findOne(requirementid);
        if (entity == null) {
            throw new NotFoundException(requirementid.toString());
        }
        return new RequirementDTO(entity);
    }

    @DeleteMapping("/{requirementid}")
    public ResponseEntity<?> deleteRequirement(
            HttpServletRequest req,
            @PathVariable Long requirementid) {
        Requirement entity = repo.findOne(requirementid);
        if (entity == null) {
            throw new NotFoundException(requirementid.toString());
        }
        checkAuthorization(req, entity);
        repo.delete(entity);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{requirementid}/requests/{requestid}")
    public ResponseEntity<EntityMessage<RequirementDTO>> deleteRequest(
            HttpServletRequest req,
            @PathVariable Long requirementid,
            @PathVariable Long requestid) {
        Requirement entity = repo.findOne(requirementid);
        if (entity == null) {
            throw new NotFoundException(requirementid.toString());
        }
        checkAuthorization(req, entity);

        Set<Request> requests = entity.getRequests();
        Request d = new Request();
        d.setId(requestid);
        requests.remove(d);

        RequirementDTO out = new RequirementDTO(repo.save(entity));
        EntityMessage<RequirementDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

    @DeleteMapping("/{requirementid}/checkpoints/{checkpointid}")
    public ResponseEntity<EntityMessage<RequirementDTO>> deleteCheckPoint(
            HttpServletRequest req,
            @PathVariable Long requirementid,
            @PathVariable Long checkpointid) {
        Requirement entity = repo.findOne(requirementid);
        if (entity == null) {
            throw new NotFoundException(requirementid.toString());
        }
        checkAuthorization(req, entity);

        CheckPoint found = null;
        for (CheckPoint cp : entity.getCheckPoints()) {
            if (Objects.equals(cp.getId(), checkpointid)) {
                found = cp;
            }
        }
        if (found == null) {
            throw new NotFoundException(checkpointid.toString());
        }
        entity.getCheckPoints().remove(found);
        checkpointRepo.delete(checkpointid); // TODO

        RequirementDTO out = new RequirementDTO(entity);
        EntityMessage<RequirementDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

    @DeleteMapping("/{requirementid}/participants/{userid}")
    public ResponseEntity<EntityMessage<RequirementDTO>> deleteParticipant(
            HttpServletRequest req,
            @PathVariable Long requirementid,
            @PathVariable Long userid) {
        Requirement entity = repo.findOne(requirementid);
        if (entity == null) {
            throw new NotFoundException(requirementid.toString());
        }
        checkAuthorization(req, entity);

        Collection<User> participants = entity.getParticipants();
        User d = new User();
        d.setId(userid);
        participants.remove(d);

        RequirementDTO out = new RequirementDTO(repo.save(entity));
        EntityMessage<RequirementDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

    @DeleteMapping("/{requirementid}/tags/{tagid}")
    public ResponseEntity<EntityMessage<RequirementDTO>> deleteTag(
            HttpServletRequest req,
            @PathVariable Long requirementid,
            @PathVariable Long tagid) {
        Requirement entity = repo.findOne(requirementid);
        if (entity == null) {
            throw new NotFoundException(requirementid.toString());
        }
        checkAuthorization(req, entity);

        Tag found = null;
        for (Tag t : entity.getTags()) {
            if (Objects.equals(t.getId(), tagid)) {
                found = t;
            }
        }
        if (found == null) {
            throw new NotFoundException(tagid.toString());
        }
        entity.getTags().remove(found); // TODO

        RequirementDTO out = new RequirementDTO(repo.save(entity));
        EntityMessage<RequirementDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

}
