package com.feiliks.testapp2.jpa.repositories;

import com.feiliks.testapp2.jpa.entities.Tag;
import org.springframework.data.repository.CrudRepository;

public interface TagRepository extends CrudRepository<Tag, Long> {

    public Tag findByName(String tag);

    boolean existsByName(String tag);
}
