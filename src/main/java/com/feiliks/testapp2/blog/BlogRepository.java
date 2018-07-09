package com.feiliks.testapp2.blog;

import com.feiliks.testapp2.jpa.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BlogRepository extends PagingAndSortingRepository<Blog, Long> {

    Blog findBySlug(String slug);

    Blog findBySlugAndOwner(String slug, User user);

    boolean existsBySlug(String slug);

    Iterable<Blog> findPublished();

    Iterable<Blog> findRecentActive();

    Page<Blog> findByOwner(User user, Pageable pageable);

}
