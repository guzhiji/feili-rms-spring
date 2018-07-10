package com.feiliks.rms;

import com.feiliks.common.ValidationException;
import com.feiliks.rms.entities.Request;
import com.feiliks.rms.entities.RequestType;
import com.feiliks.rms.entities.Requirement;
import com.feiliks.rms.entities.Tag;
import com.feiliks.common.entities.User;
import com.feiliks.rms.repositories.RequestRepository;
import com.feiliks.rms.repositories.RequestTypeRepository;
import com.feiliks.rms.repositories.TagRepository;
import com.feiliks.common.repositories.UserRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class JpaUtils {

    public static void fetchManager(UserRepository repo, RequestType data) {
        User manager = data.getManager(), fetchedManager = null;
        if (manager != null) {
            if (manager.getId() != null) {
                fetchedManager = repo.findOne(manager.getId());
                if (fetchedManager == null) {
                    throw new ValidationException("manager not found: " + manager.getId());
                }
            } else if (manager.getUsername() != null) {
                fetchedManager = repo.findByUsername(manager.getUsername());
                if (fetchedManager == null) {
                    throw new ValidationException("manager not found: " + manager.getUsername());
                }
            }
        }
        if (manager == null) {
            throw new ValidationException("missing manager");
        }
        data.setManager(fetchedManager);
    }

    public static void fetchRequirementParticipants(UserRepository repo, Requirement data) {
        Set<User> fetched = new HashSet<>();
        Collection<User> participants = data.getParticipants();
        if (participants != null) {
            for (User u : participants) {
                if (u != null) {
                    if (u.getId() != null) {
                        User participant = repo.findOne(u.getId());
                        if (participant == null) {
                            throw new ValidationException("participant not found:" + u.getId());
                        }
                        fetched.add(participant);
                    } else if (u.getUsername() != null) {
                        User participant = repo.findByUsername(u.getUsername());
                        if (participant == null) {
                            throw new ValidationException("participant not found:" + u.getUsername());
                        }
                        fetched.add(participant);
                    } // else ignore it
                }
            }
        }
        data.setParticipants(fetched);
    }

    public static void fetchRequestType(RequestTypeRepository repo, Request data) {
        RequestType type = data.getType(), fetchedType = null;
        if (type == null || type.getId() == null) {
            throw new ValidationException("missing request type");
        }
        fetchedType = repo.findOne(type.getId());
        if (fetchedType == null) {
            throw new ValidationException("request type not found:" + type.getId());
        }
        data.setType(fetchedType);
    }

    public static void fetchRequirementRequests(RequestRepository repo, Requirement data) {
        Set<Request> fetched = new HashSet<>();
        Collection<Request> requests = data.getRequests();
        if (requests != null) {
            for (Request r : requests) {
                if (r != null && r.getId() != null) {
                    Request request = repo.findOne(r.getId());
                    if (request == null) {
                        throw new ValidationException("referred request not found:" + r.getId());
                    }
                    fetched.add(request);
                }
            }
        }
        data.setRequests(fetched);
    }

    public static void fetchOrCreateRequirementTags(TagRepository repo, Requirement data) {
        Set<Tag> fetched = new HashSet<>();
        Collection<Tag> tags = data.getTags();
        if (tags != null) {
            for (Tag t : tags) {
                if (t != null) {
                    if (t.getId() != null) {
                        t = repo.findOne(t.getId());
                        if (t != null) {
                            fetched.add(t);
                        }
                    } else if (t.getName() != null) {
                        Tag tag = repo.findByName(t.getName());
                        if (tag == null) {
                            fetched.add(repo.save(new Tag(t.getName())));
                        } else {
                            fetched.add(tag);
                        }
                    }
                }
            }
        }
        data.setTags(fetched);
    }

}
