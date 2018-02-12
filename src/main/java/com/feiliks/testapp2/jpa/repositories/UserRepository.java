package com.feiliks.testapp2.jpa.repositories;

import com.feiliks.testapp2.jpa.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    public User findByUsername(String username);

    boolean existsByUsername(String username);
}
