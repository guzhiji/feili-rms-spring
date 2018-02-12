package com.feiliks.testapp2.jpa.repositories;

import com.feiliks.testapp2.jpa.entities.Request;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RequestRepository extends PagingAndSortingRepository<Request, Long> {

}
