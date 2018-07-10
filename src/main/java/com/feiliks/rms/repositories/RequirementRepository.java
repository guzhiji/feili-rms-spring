package com.feiliks.rms.repositories;

import com.feiliks.rms.entities.Requirement;
import com.feiliks.common.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface RequirementRepository extends PagingAndSortingRepository<Requirement, Long> {

    List<Requirement> findByOwner(User owner);

    Iterable<Requirement> findByOwner(User owner, Sort sortable);

    Page<Requirement> findByOwner(User owner, Pageable pageable);

    List<Requirement> findParticipated(Long userid);
    List<Requirement> findParticipated2(Long userid, int offset, int size);
}
