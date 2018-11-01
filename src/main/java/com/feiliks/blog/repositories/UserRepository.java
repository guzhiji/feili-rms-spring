package com.feiliks.blog.repositories;

import com.feiliks.blog.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);

    boolean existsByUsername(String username);
}
