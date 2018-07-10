package com.feiliks.common.repositories;

import com.feiliks.common.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    public User findByUsername(String username);

    boolean existsByUsername(String username);
}
