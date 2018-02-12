package com.feiliks.testapp2.jpa.repositories;

import com.feiliks.testapp2.jpa.entities.CheckPoint;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CheckPointRepository extends PagingAndSortingRepository<CheckPoint, Long> {

}
