package com.swp.blooddonation.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BloodRequestComponentRepositoryTest {
    @Autowired
    private BloodRequestComponentRepository bloodRequestComponentRepository;

    @Test
    void testSaveAndFind() {
        // Tạo entity mới và lưu vào DB (bạn cần thay ComponentEntity bằng entity thực tế)
        // ComponentEntity entity = new ComponentEntity();
        // entity.set...();
        // entity = bloodRequestComponentRepository.save(entity);
        // assertNotNull(bloodRequestComponentRepository.findById(entity.getId()));
        assertTrue(true); // placeholder
    }
} 