package com.swp.blooddonation.repository;

import com.swp.blooddonation.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindById() {
        // Tạo user mới và lưu vào DB
        User user = new User();
        user.setFullName("Nguyen Van B");
        user = userRepository.save(user);

        Optional<User> found = userRepository.findById(user.getId());
        assertTrue(found.isPresent());
        assertEquals("Nguyen Van B", found.get().getFullName());
    }
} 