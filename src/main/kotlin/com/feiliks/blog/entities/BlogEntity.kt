package com.feiliks.blog.entities

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "blog_blog")
@NamedQueries(
        NamedQuery(
                name = "BlogEntity.findPublished",
                query = "select b from BlogEntity b where b.published=true order by b.modified desc"),
        NamedQuery(
                name = "BlogEntity.findRecentActive",
                query = "select b from BlogEntity b order by b.modified desc"))
class BlogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = true, unique = true, length = 32)
    var slug: String? = null

    @Column(nullable = false, length = 127)
    lateinit var title: String

    @Column(nullable = false)
    var published: Boolean = false

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    lateinit var created: Date

    @Temporal(TemporalType.TIMESTAMP)
    var modified: Date? = null

    @ManyToOne(optional = true)
    var owner: UserEntity? = null

    @Lob
    var content: String? = null

    @ManyToMany
    @JoinTable(
            name = "blog_blog_tag",
            joinColumns = [JoinColumn(name = "blog_id")],
            inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    lateinit var tags: Set<TagEntity>

    override fun hashCode(): Int {
        var hash = 0
        hash += id?.hashCode() ?: 0
        if (hash == 0)
            hash += slug?.hashCode() ?: 0
        if (hash == 0)
            return super.hashCode()
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BlogEntity)
            return false
        if (id != null)
            return id == other.id
        if (slug != null)
            return slug == other.slug
        return super.equals(other)
    }

}
