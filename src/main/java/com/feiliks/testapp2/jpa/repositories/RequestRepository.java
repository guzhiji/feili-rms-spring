package com.feiliks.testapp2.jpa.repositories;

import com.feiliks.testapp2.jpa.entities.Request;
import com.feiliks.testapp2.jpa.entities.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RequestRepository extends PagingAndSortingRepository<Request, Long> {

    List<Request> findByOwner(User owner);

    Iterable<Request> findByOwner(User owner, Sort sort);

    Page<Request> findByOwner(User owner, Pageable pageable);
}
