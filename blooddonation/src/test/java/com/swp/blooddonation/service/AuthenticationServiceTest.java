package com.swp.blooddonation.service;

import com.swp.blooddonation.dto.AddressDTO;
import com.swp.blooddonation.dto.request.RegisRequest;
import com.swp.blooddonation.dto.response.RegisterAccountResponse;
import com.swp.blooddonation.entity.Account;
import com.swp.blooddonation.entity.Province;
import com.swp.blooddonation.entity.District;
import com.swp.blooddonation.entity.Ward;
import com.swp.blooddonation.enums.EnableStatus;
import com.swp.blooddonation.enums.Gender;
import com.swp.blooddonation.enums.Role;
import com.swp.blooddonation.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {
    @Mock
    AuthenticationReponsitory authenticationReponsitory;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    ModelMapper modelMapper;
    @Mock
    TokenService tokenService;
    @Mock
    EmailService emailService;
    @Mock
    UserRepository userRepository;
    @Mock
    ProvinceRepository provinceRepository;
    @Mock
    DistrictRepository districtRepository;
    @Mock
    WardRepository wardRepository;

    @InjectMocks
    AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        RegisRequest req = new RegisRequest();
        req.setEmail("test@example.com");
        req.setPassword("password123");
        req.setPhone("0123456789");
        req.setFullName("Nguyen Van Test");
        req.setGender(Gender.MALE);
        req.setBirthDate(new Date());
        AddressDTO address = new AddressDTO();
        address.setStreet("123 Đường ABC");
        address.setWardId(1L);
        address.setDistrictId(2L);
        address.setProvinceId(3L);
        req.setAddress(address);

        when(authenticationReponsitory.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPass");
        when(provinceRepository.findById(3L)).thenReturn(Optional.of(new Province()));
        when(districtRepository.findById(2L)).thenReturn(Optional.of(new District()));
        when(wardRepository.findById(1L)).thenReturn(Optional.of(new Ward()));
        // Giả lập lưu user
        when(userRepository.save(any())).thenAnswer(invocation -> {
            Account acc = ((com.swp.blooddonation.entity.User) invocation.getArgument(0)).getAccount();
            acc.setId(100L);
            return invocation.getArgument(0);
        });

        RegisterAccountResponse res = authenticationService.register(req);
        assertNotNull(res);
        assertEquals("test@example.com", res.getEmail());
        assertEquals(100L, res.getId());
    }

    @Test
    void testRegister_EmailExists_ThrowsException() {
        RegisRequest req = new RegisRequest();
        req.setEmail("test@example.com");
        when(authenticationReponsitory.existsByEmail("test@example.com")).thenReturn(true);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authenticationService.register(req));
        assertEquals("Email đã được sử dụng!", ex.getMessage());
    }
} 