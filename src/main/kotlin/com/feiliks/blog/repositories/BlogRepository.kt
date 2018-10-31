package com.feiliks.blog.repositories

import com.feiliks.blog.entities.BlogEntity
import com.feiliks.blog.entities.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository


interface BlogRepository : PagingAndSortingRepository<BlogEntity, Long> {

    fun findBySlug(slug: String?): BlogEntity?

    fun findBySlugAndOwner(slug: String?, user: UserEntity?): BlogEntity?

    fun existsBySlug(slug: String?): Boolean

    fun findPublished(): Set<BlogEntity>

    fun findRecentActive(): Set<BlogEntity>

    fun findByOwner(user: UserEntity, pageable: Pageable): Page<BlogEntity>

}
