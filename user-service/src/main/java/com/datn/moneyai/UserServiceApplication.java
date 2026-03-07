package com.datn.moneyai;

import com.datn.moneyai.models.entities.bases.Role;
import com.datn.moneyai.models.entities.enums.RoleName;
import com.datn.moneyai.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedRoles(RoleRepository roleRepository) {
        return args -> {
            for (RoleName rn : RoleName.values()) {
                if (roleRepository.findByName(rn).isEmpty()) {
                    Role role = new Role();
                    role.setName(rn);
                    roleRepository.save(role);
                }
            }
        };
    }
}
