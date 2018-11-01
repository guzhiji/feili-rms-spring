package com.feiliks.blog.repositories;

import java.util.Collection;

import com.feiliks.blog.entities.Tag;
import org.springframework.data.repository.CrudRepository;

public interface TagRepository extends CrudRepository<Tag, Long> {

    Tag findByName(String tag);

    boolean existsByName(String tag);

    Collection<Tag> findOrphaned();
}
