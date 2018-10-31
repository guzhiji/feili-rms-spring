package com.feiliks.blog

import com.feiliks.blog.entities.UserEntity
import com.feiliks.blog.entities.UserPermission
import com.feiliks.blog.repositories.UserRepository
import com.feiliks.common.PasswordUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component


@Component
class CreateSuperUserCommand : CommandLineRunner {

    @Autowired
    private lateinit var userRepo: UserRepository

    @Throws(Exception::class)
    override fun run(vararg args: String) {
        if (args.isEmpty() || args[0] != "superuser") {
            return
        }
        System.out.println("--------------------------------")
        System.out.println(this::class.simpleName)
        if (args.size < 2) {
            System.err.println("Please type username.")
            return
        }
        if (args.size < 3) {
            System.err.println("Please type password.")
            return
        }
        val name = args[1]
        val password = args[2]

        if (name.isEmpty()) {
            System.err.println("Please type username.")
            return
        }
        if (password.isEmpty()) {
            System.err.println("Please type password.")
            return
        }
        if (userRepo.existsByUsername(name)) {
            System.err.println("Username already exists.")
            return
        }

        System.out.print("Creating super user: ")
        System.out.println(name)

        val entity = UserEntity()
        entity.username = name
        entity.password = PasswordUtil.hash(name, password)
        entity.permissionsAsSet = setOf(UserPermission.ALL)

        System.out.print("\nUserEntity created: ")
        System.out.println(userRepo.save(entity))
        System.out.println()

    }

}
