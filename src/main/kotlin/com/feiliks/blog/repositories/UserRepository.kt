package com.feiliks.blog.repositories

import com.feiliks.blog.entities.UserEntity
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntity, Long> {

    fun findByUsername(username: String?): UserEntity?

    fun existsByUsername(username: String?): Boolean

}