package com.feiliks.blog.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "blog_tag")
@NamedQuery(
        name = "TagEntity.findOrphand",
        query = "select t from TagEntity t where size(t.blogs) = 0"
)
class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false, unique = true)
    lateinit var name: String

    @Column(length = 16)
    var color: String? = null

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    lateinit var blogs: Set<BlogEntity>

    override fun hashCode(): Int {
        var hash = 0
        hash += id?.hashCode() ?: 0
        if (hash == 0)
            hash += name.hashCode()
        if (hash != 0)
            return hash;
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TagEntity)
            return false
        if (id != null)
            return id == other.id
        return name == other.name
    }

}
