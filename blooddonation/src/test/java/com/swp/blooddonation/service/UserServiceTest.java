package com.swp.blooddonation.service;

import com.swp.blooddonation.dto.CustomerDTO;
import com.swp.blooddonation.entity.User;
import com.swp.blooddonation.repository.UserRepository;
import com.swp.blooddonation.repository.DonationHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.swp.blooddonation.dto.MedicalStaffDTO;
import com.swp.blooddonation.entity.Account;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private DonationHistoryRepository donationHistoryRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProfile_ReturnsCustomerDTO() {
        // Giả lập User
        User user = new User();
        user.setId(1L);
        user.setFullName("Nguyen Van A");
        // ... có thể set thêm các thuộc tính khác nếu cần

        // Giả lập getCurrentUser trả về user
        UserService spyService = spy(userService);
        doReturn(user).when(spyService).getCurrentUser();

        CustomerDTO dto = spyService.getProfile();
        assertNotNull(dto);
        assertEquals("Nguyen Van A", dto.getFullName());
        assertEquals(1L, dto.getId());
    }

    @Test
    void testGetDonationHistory_ReturnsList() {
        User user = new User();
        user.setId(2L);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(user));
        UserService spyService = spy(userService);
        doReturn(user).when(spyService).getCurrentUser();
        when(donationHistoryRepository.findByCustomerOrderByDonationDateDesc(user)).thenReturn(java.util.List.of());
        var result = spyService.getDonationHistory();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetUserById_NotFound_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> userService.getUserById(99L));
    }

    @Test
    void testGetAllCustomers_ReturnsList() {
        when(userRepository.findAllCustomers()).thenReturn(java.util.List.of(new User()));
        var result = userService.getAllCustomers();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllMedicalStaff_ReturnsList() {
        when(userRepository.findAllMedicalStaff()).thenReturn(java.util.List.of(new User()));
        var result = userService.getAllMedicalStaff();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllManagers_ReturnsList() {
        when(userRepository.findAllManagers()).thenReturn(java.util.List.of(new User()));
        var result = userService.getAllManagers();
        assertEquals(1, result.size());
    }

    @Test
    void testGetUsersByRole_ReturnsList() {
        when(userRepository.findByRole("CUSTOMER")).thenReturn(java.util.List.of(new User()));
        var result = userService.getUsersByRole("CUSTOMER");
        assertEquals(1, result.size());
    }

    @Test
    void testGetDonationRecommendation_Customer_Success() {
        Account acc = new Account();
        acc.setRole(com.swp.blooddonation.enums.Role.CUSTOMER);
        User user = new User();
        user.setLastDonationDate(java.time.LocalDate.now().minusDays(100));
        when(userRepository.findByAccount(acc)).thenReturn(java.util.Optional.of(user));
        String result = userService.getDonationRecommendation(acc);
        assertTrue(result.contains("Bạn đã có thể hiến máu trở lại"));
    }

    @Test
    void testGetDonationRecommendation_NotCustomer_ThrowsException() {
        Account acc = new Account();
        acc.setRole(com.swp.blooddonation.enums.Role.ADMIN);
        User user = new User();
        when(userRepository.findByAccount(acc)).thenReturn(java.util.Optional.of(user));
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> userService.getDonationRecommendation(acc));
    }

    @Test
    void testGetReadyDate_Customer_Success() {
        Account acc = new Account();
        acc.setRole(com.swp.blooddonation.enums.Role.CUSTOMER);
        User user = new User();
        user.setLastDonationDate(java.time.LocalDate.of(2024, 1, 1));
        when(userRepository.findByAccount(acc)).thenReturn(java.util.Optional.of(user));
        var resp = userService.getReadyDate(acc);
        assertNotNull(resp);
        assertTrue(resp.getReadyDate().contains("2024"));
    }

    @Test
    void testGetReadyDate_NotCustomer_ThrowsException() {
        Account acc = new Account();
        acc.setRole(com.swp.blooddonation.enums.Role.ADMIN);
        User user = new User();
        when(userRepository.findByAccount(acc)).thenReturn(java.util.Optional.of(user));
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> userService.getReadyDate(acc));
    }

    @Test
    void testGetMedicalStaffProfile_Success() {
        Account acc = new Account();
        acc.setRole(com.swp.blooddonation.enums.Role.MEDICALSTAFF);
        acc.setEmail("ms@example.com");
        User user = new User();
        user.setFullName("Bac Si A");
        user.setPhone("0123456789");
        when(userRepository.findByAccount(acc)).thenReturn(java.util.Optional.of(user));
        org.modelmapper.ModelMapper realMapper = new org.modelmapper.ModelMapper();
        UserService realService = new UserService(userRepository, donationHistoryRepository, realMapper);
        MedicalStaffDTO dto = realService.getMedicalStaffProfile(acc);
        assertEquals("Bac Si A", dto.getFullName());
        assertEquals("ms@example.com", dto.getEmail());
        assertEquals("0123456789", dto.getPhone());
    }

    @Test
    void testGetMedicalStaffProfile_NotStaff_ThrowsException() {
        Account acc = new Account();
        acc.setRole(com.swp.blooddonation.enums.Role.CUSTOMER);
        User user = new User();
        when(userRepository.findByAccount(acc)).thenReturn(java.util.Optional.of(user));
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> userService.getMedicalStaffProfile(acc));
    }

    @Test
    void testGetUser_Success() {
        Account acc = new Account();
        User user = new User();
        when(userRepository.findByAccount(acc)).thenReturn(java.util.Optional.of(user));
        User result = userService.getUser(acc);
        assertNotNull(result);
    }

    @Test
    void testGetUser_NotFound_ThrowsException() {
        Account acc = new Account();
        when(userRepository.findByAccount(acc)).thenReturn(java.util.Optional.empty());
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> userService.getUser(acc));
    }
} 