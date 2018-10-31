package com.feiliks.blog.dtos

import com.fasterxml.jackson.annotation.JsonIgnore
import com.feiliks.blog.entities.BlogEntity
import com.feiliks.blog.entities.TagEntity
import java.lang.StringBuilder
import java.util.*

class BlogDto() {

    var id: Long? = null
    var slug: String? = null
    lateinit var title: String
    var published: Boolean = false
    var created: Date? = null
    var modified: Date? = null
    var owner: UserDto? = null
    var tags: String = ""
    val tagsAsSet: Set<TagEntity>
        @JsonIgnore
        get() {
            val tagSet = HashSet<TagEntity>()
            for (tag in tags.split("\\s+".toRegex())) {
                val t = TagEntity()
                t.name = tag
                tagSet += t
            }
            return tagSet
        }
    var content: String? = null

    constructor(entity: BlogEntity) : this() {
        id = entity.id
        slug = entity.slug
        title = entity.title
        published = entity.published
        created = entity.created
        modified = entity.modified
        content = entity.content
        owner = if (entity.owner == null) null
            else UserDto(entity.owner!!)
        val sb = StringBuilder()
        for (tag in entity.tags)
            sb.append(tag.name).append(' ')
        tags = sb.toString()
    }

    fun toEntity() : BlogEntity {
        val e = BlogEntity()
        e.id = id
        e.slug = slug
        e.title = title
        e.published = published
        e.created = created!!
        e.modified = modified
        e.content = content
        e.owner = owner?.toEntity()
        e.tags = tagsAsSet
        return e
    }

}

