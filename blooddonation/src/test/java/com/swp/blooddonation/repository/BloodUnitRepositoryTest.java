package com.swp.blooddonation.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BloodUnitRepositoryTest {
    @Autowired
    private BloodUnitRepository bloodUnitRepository;

    @Test
    void testSaveAndFind() {
//         Tạo entity mới và lưu vào DB (bạn cần thay BloodUnitEntity bằng entity thực tế)
//         BloodUnitEntity entity = new BloodUnitEntity();
//         entity.set...();
//         entity = bloodUnitRepository.save(entity);
//         assertNotNull(bloodUnitRepository.findById(entity.getId()));
        assertTrue(true); // placeholder
    }
} 