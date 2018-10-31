package com.feiliks.blog.repositories

import com.feiliks.blog.entities.TagEntity
import org.springframework.data.repository.CrudRepository

interface TagRepository : CrudRepository<TagEntity, Long> {

    fun findByName(tag: String): TagEntity?

    fun existsByName(tag: String): Boolean

    fun findOrphand(): Set<TagEntity>

}
