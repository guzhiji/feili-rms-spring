package com.feiliks.blog;

import com.feiliks.common.PasswordUtil;
import com.feiliks.common.entities.User;
import com.feiliks.common.entities.UserPermission;
import com.feiliks.common.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CreateSuperUserCommand implements CommandLineRunner {

    @Autowired
    private UserRepository userRepo;

    @Override
    public void run(String... args) throws Exception {
        if (args.length < 1 || !args[0].equals("superuser")) {
            return;
        }
        System.out.println("--------------------------------");
        System.out.println(getClass().getSimpleName());
        if (args.length < 2) {
            System.err.println("Please type username.");
            return;
        }
        if (args.length < 3) {
            System.err.println("Please type password.");
            return;
        }
        String name = args[1];
        String password = args[2];

        if (name == null || name.isEmpty()) {
            System.err.println("Please type username.");
            return;
        }
        if (password == null || password.isEmpty()) {
            System.err.println("Please type password.");
            return;
        }
        if (userRepo.existsByUsername(name)) {
            System.err.println("Username already exists.");
            return;
        }

        System.out.print("Creating super user: ");
        System.out.println(name);

        User entity = new User();
        entity.setUsername(name);
        entity.setPassword(PasswordUtil.hash(name, password));
        entity.setPermissions(Arrays.asList(UserPermission.ALL));

        System.out.print("\nUser created: ");
        System.out.println(userRepo.save(entity));
        System.out.println();

    }

}
