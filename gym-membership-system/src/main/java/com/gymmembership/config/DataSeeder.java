package com.gymmembership.config;

import com.gymmembership.entity.Resource;
import com.gymmembership.entity.User;
import com.gymmembership.entity.enums.ResourceStatus;
import com.gymmembership.entity.enums.Role;
import com.gymmembership.entity.enums.UserStatus;
import com.gymmembership.repository.ResourceRepository;
import com.gymmembership.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;

    public DataSeeder(UserRepository userRepository, ResourceRepository resourceRepository) {
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User("U001", "Arun Kumar", Role.USER, UserStatus.ACTIVE));
            userRepository.save(new User("U002", "Bala Kumar", Role.USER, UserStatus.ACTIVE));
            userRepository.save(new User("A001", "Admin", Role.ADMINISTRATOR, UserStatus.ACTIVE));
            System.out.println("Users seeded successfully.");
        }
        if (resourceRepository.count() == 0) {
            resourceRepository.save(new Resource("S101", "S101", "Membership A", 10, 10, ResourceStatus.ACTIVE));
            resourceRepository.save(new Resource("S102", "S102", "Membership B", 5, 5, ResourceStatus.ACTIVE));
            resourceRepository.save(new Resource("S103", "S103", "Membership C", 0, 0, ResourceStatus.INACTIVE));
            System.out.println("Resources seeded successfully.");
        }
    }
}
