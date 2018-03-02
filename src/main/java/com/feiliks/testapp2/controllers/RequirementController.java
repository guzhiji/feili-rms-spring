package com.feiliks.testapp2.controllers;

import com.feiliks.testapp2.AuthTokenUtil;
import com.feiliks.testapp2.AuthorizationException;
import com.feiliks.testapp2.JpaUtils;
import com.feiliks.testapp2.NotFoundException;
import com.feiliks.testapp2.ValidationException;
import com.feiliks.testapp2.dto.CheckPointStatusDTO;
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

    private User getUserIfOneOfTheManagers(HttpServletRequest req, Requirement data) {
        User curUser = AuthTokenUtil.getUser(req); // assume protected by TokenAuthInterceptor/Filter
        Set<Request> requests = data.getRequests(); // assume requests been fetched
        for (Request request : requests) {
            RequestType rtype = request.getType();
            User manager = rtype.getManager();
            if (manager != null && Objects.equals(manager.getId(), curUser.getId())) {
                return curUser;
            }
        }
        throw new AuthorizationException();
    }

    private Requirement getRequirementOrRaiseEx(Long requirementid) {
        Requirement entity = repo.findOne(requirementid);
        if (entity == null) {
            throw new NotFoundException(Requirement.class, requirementid.toString());
        }
        return entity;
    }

    @GetMapping
    @Transactional(readOnly = true)
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
        entity.setOwner(getUserIfOneOfTheManagers(req, entity));
        JpaUtils.fetchRequirementParticipants(userRepo, entity);
        JpaUtils.fetchOrCreateRequirementTags(tagRepo, entity);
        // No checkpoints should belong to multiple requirements.
        for (CheckPoint cp : entity.getCheckPoints()) {
            cp.setId(null);
        }
        entity.setCreated(new Date());
        entity.setModified(entity.getCreated());

        RequirementDTO out = new RequirementDTO(repo.save(entity));
        return respondCreatedStatus(out, getClass(), "getRequirement", out.getId());
    }

    @PutMapping("/{requirementid}")
    @Transactional
    public ResponseEntity<EntityMessage<RequirementDTO>> updateRequirement(
            HttpServletRequest req,
            @PathVariable Long requirementid,
            @Valid @RequestBody RequirementDTO data,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }
        Requirement original = getRequirementOrRaiseEx(requirementid);

        Requirement entity = data.toEntity();
        entity.setId(requirementid);
        // JpaUtils.fetchRequirementRequests(requestRepo, entity);
        entity.setRequests(original.getRequests());
        getUserIfOneOfTheManagers(req, entity); // requires requests fetched

        // disallow checkpoint status from changed
        for (CheckPoint ocp : original.getCheckPoints()) {
            for (CheckPoint ncp : entity.getCheckPoints()) {
                if (Objects.equals(ocp.getId(), ncp.getId())) {
                    ncp.setStatus(ocp.getStatus());
                    ncp.setDaysLeft(ocp.getDaysLeft());
                }
            }
        }

        CheckPoint[] orderedCps = entity.getCheckPoints().toArray(new CheckPoint[0]);
        for (int i = 0; i < orderedCps.length; ++i) {
            CheckPoint cp = orderedCps[i];
            // save manual ordering
            cp.setOrdinal(i);
            if (cp.getId() != null) {
                // force to create a new check point
                CheckPoint rcp = checkpointRepo.findOne(cp.getId());
                if (rcp == null) {
                    // no such existing check point
                    cp.setId(null);
                } else if (!Objects.equals(rcp.getRequirement(), original)) {
                    // belongs to another requirement
                    cp.setId(null);
                }
            }
        }

        JpaUtils.fetchRequirementParticipants(userRepo, entity);
        JpaUtils.fetchOrCreateRequirementTags(tagRepo, entity);

        entity.setOwner(original.getOwner());
        entity.setCreated(original.getCreated());
        entity.setModified(new Date());

        RequirementDTO out = new RequirementDTO(repo.save(entity));
        EntityMessage<RequirementDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

    @GetMapping("/{requirementid}")
    @Transactional(readOnly = true)
    public RequirementDTO getRequirement(@PathVariable Long requirementid) {
        return new RequirementDTO(getRequirementOrRaiseEx(requirementid));
    }

    @DeleteMapping("/{requirementid}")
    @Transactional
    public ResponseEntity<?> deleteRequirement(
            HttpServletRequest req,
            @PathVariable Long requirementid) {
        Requirement entity = getRequirementOrRaiseEx(requirementid);
        getUserIfOneOfTheManagers(req, entity);
        repo.delete(entity);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{requirementid}/requests/{requestid}")
    @Transactional
    public ResponseEntity<EntityMessage<RequirementDTO>> detachRequest(
            HttpServletRequest req,
            @PathVariable Long requirementid,
            @PathVariable Long requestid) {
        Requirement entity = getRequirementOrRaiseEx(requirementid);
        // manager
        User curUser = AuthTokenUtil.getUser(req);
        boolean found = false;
        for (Request r : entity.getRequests()) {
            if (Objects.equals(r.getId(), requestid)) {
                found = true;
                if (!r.getType().getManager().equals(curUser)) {
                    throw new AuthorizationException();
                }
            }
        }
        if (!found) {
            throw new NotFoundException(Request.class, requestid.toString());
        }

        Set<Request> requests = entity.getRequests();
        Request d = new Request();
        d.setId(requestid);
        requests.remove(d);

        RequirementDTO out = new RequirementDTO(repo.save(entity));
        EntityMessage<RequirementDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

    @PutMapping("/{requirementid}/checkpoints/{checkpointid}")
    @Transactional
    public ResponseEntity<EntityMessage<CheckPointStatusDTO>> updateCheckPointStatus(
            HttpServletRequest req,
            @PathVariable Long requirementid,
            @PathVariable Long checkpointid,
            @Valid @RequestBody CheckPointStatusDTO data,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult);
        }

        // get the requested requirement->checkpoint
        CheckPoint entity = checkpointRepo.findOne(checkpointid);
        if (entity == null || !Objects.equals(entity.getRequirement().getId(), requirementid)) {
            throw new NotFoundException(CheckPoint.class, checkpointid.toString());
        }

        // one of the participants
        Requirement parent = entity.getRequirement();
        if (!parent.getParticipants().contains(AuthTokenUtil.getUser(req))) {
            throw new AuthorizationException();
        }

        entity.setStatus(data.getStatusAsObject());
        entity.setDaysLeft(data.getDaysLeft());
        checkpointRepo.save(entity);

        EntityMessage<CheckPointStatusDTO> msg = new EntityMessage<>("success", data);
        return ResponseEntity.accepted().body(msg);
    }

    @DeleteMapping("/{requirementid}/checkpoints/{checkpointid}")
    @Transactional
    public ResponseEntity<EntityMessage<RequirementDTO>> deleteCheckPoint(
            HttpServletRequest req,
            @PathVariable Long requirementid,
            @PathVariable Long checkpointid) {
        Requirement entity = getRequirementOrRaiseEx(requirementid);
        getUserIfOneOfTheManagers(req, entity);

        Collection<CheckPoint> checkpoints = entity.getCheckPoints();
        CheckPoint d = new CheckPoint();
        d.setId(checkpointid);
        checkpoints.remove(d);

        RequirementDTO out = new RequirementDTO(repo.save(entity));
        EntityMessage<RequirementDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

    @DeleteMapping("/{requirementid}/participants/{userid}")
    @Transactional
    public ResponseEntity<EntityMessage<RequirementDTO>> removeParticipant(
            HttpServletRequest req,
            @PathVariable Long requirementid,
            @PathVariable Long userid) {
        Requirement entity = getRequirementOrRaiseEx(requirementid);
        getUserIfOneOfTheManagers(req, entity);

        Collection<User> participants = entity.getParticipants();
        User d = new User();
        d.setId(userid);
        participants.remove(d);

        RequirementDTO out = new RequirementDTO(repo.save(entity));
        EntityMessage<RequirementDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

    @DeleteMapping("/{requirementid}/tags/{tag}")
    @Transactional
    public ResponseEntity<EntityMessage<RequirementDTO>> detachTag(
            HttpServletRequest req,
            @PathVariable Long requirementid,
            @PathVariable String tag) {
        Requirement entity = getRequirementOrRaiseEx(requirementid);
        getUserIfOneOfTheManagers(req, entity);

        RequirementDTO out = null;
        Collection<Tag> tags = entity.getTags();
        for (Tag t : tags) {
            if (t.getName().equals(tag)) {
                tags.remove(t);
                out = new RequirementDTO(repo.save(entity));
                if (t.getRequirements().size() <= 1) {
                    tagRepo.delete(t);
                }
                break;
            }
        }
        if (out == null) {
            throw new NotFoundException(Tag.class, tag);
        }
        EntityMessage<RequirementDTO> msg = new EntityMessage<>("success", out);
        return ResponseEntity.accepted().body(msg);
    }

}
