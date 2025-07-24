package com.swp.blooddonation.service;

import com.swp.blooddonation.dto.request.RegisterRequest;
import com.swp.blooddonation.entity.*;
import com.swp.blooddonation.enums.RegisterStatus;
import com.swp.blooddonation.exception.exceptions.BadRequestException;
import com.swp.blooddonation.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterServiceTest {
    @Mock
    UserService userService;
    @Mock
    SlotRepository slotRepository;
    @Mock
    RegisterRepository registerRepository;
    @Mock
    AccountSlotRepository accountSlotRepository;
    @Mock
    ScheduleRepository scheduleRepository;
    @Mock
    BloodTestRepository bloodTestRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    RegisterService registerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRegister_Success() {
        RegisterRequest req = new RegisterRequest();
        req.setDate(LocalDate.now());
        req.setSlotId(1L);
        req.setNote("Đăng ký hiến máu");

        User user = new User();
        Account account = new Account();
        user.setAccount(account);
        user.setId(1L);
        when(userService.getCurrentUser()).thenReturn(user);
        when(accountSlotRepository.existsByUserAndDate(user, req.getDate())).thenReturn(false);
        Slot slot = new Slot();
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        Schedule schedule = new Schedule();
        schedule.setStatus(com.swp.blooddonation.enums.ScheduleStatus.OPEN);
        when(scheduleRepository.findByScheduleDate(req.getDate())).thenReturn(Optional.of(schedule));
        when(registerRepository.existsByUserAndSlotAndRegisterDate(user, slot, req.getDate())).thenReturn(false);
        when(registerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Register reg = registerService.createRegister(req);
        assertNotNull(reg);
        assertEquals(user, reg.getUser());
    }

    @Test
    void testCreateRegister_UserNull_ThrowsException() {
        RegisterRequest req = new RegisterRequest();
        when(userService.getCurrentUser()).thenReturn(null);
        BadRequestException ex = assertThrows(BadRequestException.class, () -> registerService.createRegister(req));
        assertEquals("Thông tin người dùng không tồn tại.", ex.getMessage());
    }

    @Test
    void testCreateRegister_SlotNotFound_ThrowsException() {
        RegisterRequest req = new RegisterRequest();
        req.setDate(LocalDate.now());
        req.setSlotId(1L);
        User user = new User();
        Account account = new Account();
        user.setAccount(account);
        when(userService.getCurrentUser()).thenReturn(user);
        when(accountSlotRepository.existsByUserAndDate(user, req.getDate())).thenReturn(false);
        when(slotRepository.findById(1L)).thenReturn(Optional.empty());
        BadRequestException ex = assertThrows(BadRequestException.class, () -> registerService.createRegister(req));
        assertEquals("Không tìm thấy slot.", ex.getMessage());
    }

    @Test
    void testCreateRegister_AlreadyRegistered_ThrowsException() {
        RegisterRequest req = new RegisterRequest();
        req.setDate(LocalDate.now());
        req.setSlotId(1L);
        User user = new User();
        Account account = new Account();
        user.setAccount(account);
        when(userService.getCurrentUser()).thenReturn(user);
        when(accountSlotRepository.existsByUserAndDate(user, req.getDate())).thenReturn(false);
        Slot slot = new Slot();
        when(slotRepository.findById(1L)).thenReturn(Optional.of(slot));
        Schedule schedule = new Schedule();
        schedule.setStatus(com.swp.blooddonation.enums.ScheduleStatus.OPEN);
        when(scheduleRepository.findByScheduleDate(req.getDate())).thenReturn(Optional.of(schedule));
        when(registerRepository.existsByUserAndSlotAndRegisterDate(user, slot, req.getDate())).thenReturn(true);
        BadRequestException ex = assertThrows(BadRequestException.class, () -> registerService.createRegister(req));
        assertEquals("Bạn đã đăng ký slot này vào ngày này.", ex.getMessage());
    }

    @Test
    void testApproveRegister_NotFound_ThrowsException() {
        when(registerRepository.findById(anyLong())).thenReturn(Optional.empty());
        BadRequestException ex = assertThrows(BadRequestException.class, () -> registerService.approveRegister(1L));
        assertEquals("Không tìm thấy đơn đăng ký.", ex.getMessage());
    }

    @Test
    void testApproveRegister_StatusNotPending_ThrowsException() {
        Register reg = new Register();
        reg.setStatus(com.swp.blooddonation.enums.RegisterStatus.APPROVED);
        when(registerRepository.findById(anyLong())).thenReturn(Optional.of(reg));
        BadRequestException ex = assertThrows(BadRequestException.class, () -> registerService.approveRegister(1L));
        assertEquals("Chỉ được duyệt đơn đang ở trạng thái PENDING.", ex.getMessage());
    }

    @Test
    void testRejectRegister_NotMedicalStaff_ThrowsException() {
        User user = new User();
        Account account = new Account();
        account.setRole(com.swp.blooddonation.enums.Role.CUSTOMER);
        user.setAccount(account);
        when(userService.getCurrentUser()).thenReturn(user);
        BadRequestException ex = assertThrows(BadRequestException.class, () -> registerService.rejectRegister(1L, "Lý do"));
        assertEquals("Chỉ nhân viên y tế mới có quyền từ chối đơn đăng ký.", ex.getMessage());
    }

    @Test
    void testRejectRegister_NotFound_ThrowsException() {
        User user = new User();
        Account account = new Account();
        account.setRole(com.swp.blooddonation.enums.Role.MEDICALSTAFF);
        user.setAccount(account);
        when(userService.getCurrentUser()).thenReturn(user);
        when(registerRepository.findById(anyLong())).thenReturn(Optional.empty());
        BadRequestException ex = assertThrows(BadRequestException.class, () -> registerService.rejectRegister(1L, "Lý do"));
        assertEquals("Không tìm thấy đơn đăng ký.", ex.getMessage());
    }

    @Test
    void testRejectRegister_StatusNotPending_ThrowsException() {
        User user = new User();
        Account account = new Account();
        account.setRole(com.swp.blooddonation.enums.Role.MEDICALSTAFF);
        user.setAccount(account);
        when(userService.getCurrentUser()).thenReturn(user);
        Register reg = new Register();
        reg.setStatus(com.swp.blooddonation.enums.RegisterStatus.APPROVED);
        when(registerRepository.findById(anyLong())).thenReturn(Optional.of(reg));
        BadRequestException ex = assertThrows(BadRequestException.class, () -> registerService.rejectRegister(1L, "Lý do"));
        assertEquals("Chỉ có thể từ chối đơn đăng ký đang chờ duyệt.", ex.getMessage());
    }

    @Test
    void testCancelRegisterByCustomer_NotFound_ThrowsException() {
        when(registerRepository.findById(anyLong())).thenReturn(Optional.empty());
        BadRequestException ex = assertThrows(BadRequestException.class, () -> registerService.cancelRegisterByCustomer(1L, "Lý do"));
        assertEquals("Không tìm thấy đơn đăng ký.", ex.getMessage());
    }

    @Test
    void testCancelRegisterByCustomer_NotOwner_ThrowsException() {
        User user = new User(); user.setId(2L);
        when(userService.getCurrentUser()).thenReturn(user);
        Register reg = new Register();
        User owner = new User(); owner.setId(1L);
        reg.setUser(owner);
        when(registerRepository.findById(anyLong())).thenReturn(Optional.of(reg));
        BadRequestException ex = assertThrows(BadRequestException.class, () -> registerService.cancelRegisterByCustomer(1L, "Lý do"));
        assertEquals("Bạn không có quyền hủy đơn đăng ký này.", ex.getMessage());
    }

    @Test
    void testCancelRegisterByCustomer_AlreadyCanceled_ThrowsException() {
        User user = new User(); user.setId(1L);
        when(userService.getCurrentUser()).thenReturn(user);
        Register reg = new Register();
        reg.setUser(user);
        reg.setStatus(com.swp.blooddonation.enums.RegisterStatus.CANCELED);
        when(registerRepository.findById(anyLong())).thenReturn(Optional.of(reg));
        BadRequestException ex = assertThrows(BadRequestException.class, () -> registerService.cancelRegisterByCustomer(1L, "Lý do"));
        assertEquals("Đơn đăng ký đã bị hủy trước đó.", ex.getMessage());
    }

    @Test
    void testCancelRegisterByCustomer_StatusNotPending_ThrowsException() {
        User user = new User(); user.setId(1L);
        when(userService.getCurrentUser()).thenReturn(user);
        Register reg = new Register();
        reg.setUser(user);
        reg.setStatus(com.swp.blooddonation.enums.RegisterStatus.APPROVED);
        when(registerRepository.findById(anyLong())).thenReturn(Optional.of(reg));
        BadRequestException ex = assertThrows(BadRequestException.class, () -> registerService.cancelRegisterByCustomer(1L, "Lý do"));
        assertEquals("Chỉ có thể hủy đơn đăng ký đang chờ duyệt (PENDING).", ex.getMessage());
    }

    @Test
    void testGetAllRegisters_ReturnsList() {
        Register reg = new Register();
        reg.setId(1L);
        reg.setStatus(com.swp.blooddonation.enums.RegisterStatus.PENDING);
        reg.setNote("Test");
        reg.setUser(new User());
        when(registerRepository.findAll()).thenReturn(java.util.List.of(reg));
        var result = registerService.getAllRegisters();
        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getNote());
    }

    @Test
    void testGetApprovedRegisters_ReturnsList() {
        Register reg = new Register();
        reg.setId(2L);
        reg.setStatus(com.swp.blooddonation.enums.RegisterStatus.APPROVED);
        reg.setNote("Approved");
        reg.setUser(new User());
        when(registerRepository.findByStatus(com.swp.blooddonation.enums.RegisterStatus.APPROVED)).thenReturn(java.util.List.of(reg));
        var result = registerService.getApprovedRegisters();
        assertEquals(1, result.size());
        assertEquals("Approved", result.get(0).getNote());
    }

    @Test
    void testGetRegistersByAccount_ReturnsList() {
        Account acc = new Account();
        User user = new User();
        user.setId(3L);
        user.setAccount(acc);
        Register reg = new Register();
        reg.setUser(user);
        reg.setId(3L);
        reg.setStatus(com.swp.blooddonation.enums.RegisterStatus.PENDING);
        when(userRepository.findByAccount(acc)).thenReturn(java.util.Optional.of(user));
        when(registerRepository.findByUser(user)).thenReturn(java.util.List.of(reg));
        var result = registerService.getRegistersByAccount(acc);
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
    }

    @Test
    void testGetPendingRegistersByAccount_ReturnsList() {
        Account acc = new Account();
        User user = new User();
        user.setId(4L);
        user.setAccount(acc);
        Register reg = new Register();
        reg.setUser(user);
        reg.setId(4L);
        reg.setStatus(com.swp.blooddonation.enums.RegisterStatus.PENDING);
        when(userRepository.findByAccount(acc)).thenReturn(java.util.Optional.of(user));
        when(registerRepository.findByUserAndStatus(user, com.swp.blooddonation.enums.RegisterStatus.PENDING)).thenReturn(java.util.List.of(reg));
        var result = registerService.getPendingRegistersByAccount(acc);
        assertEquals(1, result.size());
        assertEquals(4L, result.get(0).getId());
    }

    @Test
    void testToRegisterResponse_ReturnsCorrectResponse() {
        Register reg = new Register();
        reg.setId(5L);
        reg.setStatus(com.swp.blooddonation.enums.RegisterStatus.PENDING);
        reg.setNote("Note5");
        User user = new User();
        user.setId(5L);
        user.setFullName("User5");
        reg.setUser(user);
        when(bloodTestRepository.findByRegister(reg)).thenReturn(java.util.Optional.empty());
        var resp = registerService.toRegisterResponse(reg);
        assertEquals(5L, resp.getId());
        assertEquals("Note5", resp.getNote());
        assertEquals("User5", resp.getFullName());
    }
} 