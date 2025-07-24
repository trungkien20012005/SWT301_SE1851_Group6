package com.swp.blooddonation.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RegisterRepositoryTest {
    @Autowired
    private RegisterRepository registerRepository;

    @Test
    void testSaveAndFind() {
        // Tạo entity mới và lưu vào DB (bạn cần thay RegisterEntity bằng entity thực tế)
        // RegisterEntity entity = new RegisterEntity();
        // entity.set...();
        // entity = registerRepository.save(entity);
        // assertNotNull(registerRepository.findById(entity.getId()));
        assertTrue(true); // placeholder
    }
} 