package com.feiliks.rms.repositories;

import com.feiliks.rms.entities.Tag;
import java.util.Collection;
import org.springframework.data.repository.CrudRepository;

public interface TagRepository extends CrudRepository<Tag, Long> {

    Tag findByName(String tag);

    boolean existsByName(String tag);

    Collection<Tag> findOrphaned();
}
