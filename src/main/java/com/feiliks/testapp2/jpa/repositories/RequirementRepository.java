package com.feiliks.testapp2.jpa.repositories;

import com.feiliks.testapp2.jpa.entities.Requirement;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RequirementRepository extends PagingAndSortingRepository<Requirement, Long> {

}
