package com.swp.blooddonation.service;

import com.swp.blooddonation.entity.Slot;
import com.swp.blooddonation.repository.SlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SlotServiceTest {
    @Mock
    SlotRepository slotRepository;
    @InjectMocks
    SlotService slotService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void testGet_ReturnsList() {
        when(slotRepository.findAll()).thenReturn(List.of(new Slot()));
        var result = slotService.get();
        assertEquals(1, result.size());
    }
} 