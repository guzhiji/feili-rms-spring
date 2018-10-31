package com.feiliks.blog.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@Entity
@Table(name = "blog_user")
class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false, unique = true)
    @NotNull(message = "Username must not be null.")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]{3,64}$", message = "Username is invalid.")
    lateinit var username: String

    @Column(nullable = false)
    @NotNull(message = "Password must not be null.")
    @Size(min = 6, max = 128, message = "Length of password should be between 6 to 128.")
    lateinit var password: String

    @Pattern(regexp = "^[ \\-+0-9]{3,16}$", message = "Phone number is invalid.")
    var phone: String? = null

    @Pattern(regexp = "^.+@.+$", message = "Email is invalid.")
    @Size(max = 128, message = "E-mail address must not be longer than 128.")
    var email: String? = null

    @JsonIgnore
    var permissions: String? = null

    var permissionsAsSet: Set<UserPermission>
        @JsonIgnore
        get() {
            val o = HashSet<UserPermission>()
            if (permissions != null) {
                for (p in permissions!!.split(",")) {
                    val perm = p.trim()
                    if (!perm.isEmpty()) {
                        try {
                            o.add(UserPermission.valueOf(perm))
                        } catch (_: IllegalArgumentException) {
                        }
                    }
                }
            }
            return o
        }
        set(value) {
            val sb = StringBuilder()
            for (p in value) {
                if (sb.isNotEmpty())
                    sb.append(',')
                sb.append(p.name)
            }
            permissions = sb.toString()
        }

    @JsonIgnore
    fun hasPermissions(vararg perms: UserPermission): Boolean {
        val owned = permissionsAsSet
        if (UserPermission.ALL in owned)
            return true
        val required = mutableListOf(*perms)
        required.remove(UserPermission.ALL)
        return owned.containsAll(required)
    }

    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    lateinit var blogs: Set<BlogEntity>

    override fun hashCode(): Int {
        var hash = 0
        hash += id?.hashCode() ?: 0
        if (hash == 0)
            hash += username.hashCode()
        if (hash != 0)
            return hash
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is UserEntity)
            return false
        if (id != null)
            return id == other.id
        return username == other.username
    }

}
