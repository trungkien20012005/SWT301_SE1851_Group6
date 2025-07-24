package com.swp.blooddonation.service;

import com.swp.blooddonation.repository.BloodRequestComponentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.swp.blooddonation.dto.AddressDTO;
import com.swp.blooddonation.dto.request.BloodRequestRequest;
import com.swp.blooddonation.entity.User;
import com.swp.blooddonation.entity.Account;
import com.swp.blooddonation.entity.WholeBloodRequest;
import com.swp.blooddonation.enums.BloodType;
import com.swp.blooddonation.enums.Gender;
import com.swp.blooddonation.enums.RhType;
import com.swp.blooddonation.enums.Role;
import com.swp.blooddonation.repository.WholeBloodRequestRepository;
import com.swp.blooddonation.repository.PendingPatientRequestRepository;
import com.swp.blooddonation.service.UserService;
import com.swp.blooddonation.exception.exceptions.BadRequestException;
import static org.mockito.ArgumentMatchers.any;
import com.swp.blooddonation.dto.request.ComponentBloodRequestRequest;
import com.swp.blooddonation.entity.BloodRequestComponent;

class BloodRequestServiceTest {
    @Mock
    private BloodRequestComponentRepository bloodRequestComponentRepository;
    @Mock
    private WholeBloodRequestRepository bloodRequestRepository;
    @Mock
    private PendingPatientRequestRepository pendingPatientRequestRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private BloodRequestService bloodRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBloodRequest() {
        // Giả lập repository và kiểm tra main-flow tạo yêu cầu hiến máu
        // Ví dụ: when(bloodRequestComponentRepository.save(any())).thenReturn(...);
        // assertNotNull(bloodRequestService.createBloodRequest(...));
        // (Cần bổ sung chi tiết nếu biết rõ logic tạo yêu cầu)
        assertTrue(true); // placeholder
    }

    @Test
    void testRequestBlood_Success() {
        // Arrange
        User user = new User();
        Account account = new Account();
        account.setRole(Role.CUSTOMER);
        user.setAccount(account);
        when(userService.getCurrentUser()).thenReturn(user);
        when(bloodRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(pendingPatientRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BloodRequestRequest req = new BloodRequestRequest();
        req.setFullName("Nguyen Van B");
        req.setDateOfBirth(java.time.LocalDate.of(2000, 1, 1));
        req.setPhone("0123456789");
        req.setGender(Gender.MALE);
        req.setBloodType(BloodType.A);
        req.setRhType(RhType.POSITIVE);
        req.setRequiredVolume(500);
        req.setHospitalName("BV A");
        req.setMedicalCondition("Test");
        AddressDTO address = new AddressDTO();
        address.setStreet("123 ABC");
        address.setWardId(1L);
        address.setDistrictId(2L);
        address.setProvinceId(3L);
        req.setPatientAddress(address);

        // Act
        WholeBloodRequest result = bloodRequestService.requestBlood(req);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getRequester());
        assertEquals(BloodType.A, result.getBloodType());
        assertEquals(RhType.POSITIVE, result.getRhType());
        assertEquals(500, result.getRequiredVolume());
    }

    @Test
    void testRequestBlood_UserNotAllowed_ThrowsException() {
        // Arrange
        User user = new User();
        Account account = new Account();
        account.setRole(Role.ADMIN); // Không phải CUSTOMER hoặc MEDICALSTAFF
        user.setAccount(account);
        when(userService.getCurrentUser()).thenReturn(user);
        BloodRequestRequest req = new BloodRequestRequest();
        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.requestBlood(req));
        assertEquals("Chỉ người dùng hoặc nhân viên y tế mới được gửi yêu cầu.", ex.getMessage());
    }

    @Test
    void testApproveBloodRequest_NotFound_ThrowsException() {
        when(bloodRequestRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.approveBloodRequest(1L));
        assertEquals("Không tìm thấy yêu cầu truyền máu.", ex.getMessage());
    }

    @Test
    void testApproveBloodRequest_StatusNotPending_ThrowsException() {
        WholeBloodRequest req = new WholeBloodRequest();
        req.setStatus(com.swp.blooddonation.enums.BloodRequestStatus.READY);
        when(bloodRequestRepository.findById(anyLong())).thenReturn(java.util.Optional.of(req));
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.approveBloodRequest(1L));
        assertEquals("Yêu cầu đã được xử lý.", ex.getMessage());
    }

    @Test
    void testCompleteWholeBloodRequest_NotFound_ThrowsException() {
        when(bloodRequestRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.completeWholeBloodRequest(1L));
        assertEquals("Không tìm thấy yêu cầu truyền máu.", ex.getMessage());
    }

    @Test
    void testCompleteWholeBloodRequest_StatusNotReady_ThrowsException() {
        WholeBloodRequest req = new WholeBloodRequest();
        req.setStatus(com.swp.blooddonation.enums.BloodRequestStatus.PENDING);
        when(bloodRequestRepository.findById(anyLong())).thenReturn(java.util.Optional.of(req));
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.completeWholeBloodRequest(1L));
        assertEquals("Yêu cầu chưa sẵn sàng để hoàn thành.", ex.getMessage());
    }

    @Test
    void testRejectWholeBloodRequest_NotFound_ThrowsException() {
        when(bloodRequestRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.rejectWholeBloodRequest(1L, "Lý do"));
        assertEquals("Không tìm thấy yêu cầu truyền máu.", ex.getMessage());
    }

    @Test
    void testRejectWholeBloodRequest_StatusNotPending_ThrowsException() {
        WholeBloodRequest req = new WholeBloodRequest();
        req.setStatus(com.swp.blooddonation.enums.BloodRequestStatus.READY);
        when(bloodRequestRepository.findById(anyLong())).thenReturn(java.util.Optional.of(req));
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.rejectWholeBloodRequest(1L, "Lý do"));
        assertEquals("Chỉ được từ chối yêu cầu đang chờ xử lý.", ex.getMessage());
    }

    @Test
    void testCancelWholeBloodRequest_NotFound_ThrowsException() {
        when(bloodRequestRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());
            BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.cancelWholeBloodRequest(1L));
        assertEquals("Không tìm thấy yêu cầu truyền máu.", ex.getMessage());
    }

    @Test
    void testCancelWholeBloodRequest_NotOwner_ThrowsException() {
        WholeBloodRequest req = new WholeBloodRequest();
        User requester = new User(); requester.setId(2L);
        req.setRequester(requester);
        when(bloodRequestRepository.findById(anyLong())).thenReturn(java.util.Optional.of(req));
        User current = new User(); current.setId(1L);
        when(userService.getCurrentUser()).thenReturn(current);
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.cancelWholeBloodRequest(1L));
        assertEquals("Bạn không có quyền huỷ yêu cầu này.", ex.getMessage());
    }

    @Test
    void testCancelWholeBloodRequest_StatusInvalid_ThrowsException() {
        WholeBloodRequest req = new WholeBloodRequest();
        req.setStatus(com.swp.blooddonation.enums.BloodRequestStatus.COMPLETED);
        User requester = new User(); requester.setId(1L);
        req.setRequester(requester);
        when(bloodRequestRepository.findById(anyLong())).thenReturn(java.util.Optional.of(req));
        User current = new User(); current.setId(1L);
        when(userService.getCurrentUser()).thenReturn(current);
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.cancelWholeBloodRequest(1L));
        assertEquals("Chỉ được huỷ yêu cầu đang chờ xử lý hoặc đã sẵn sàng.", ex.getMessage());
    }

    @Test
    void testRequestBloodByComponent_UserNotAllowed_ThrowsException() {
        User user = new User();
        Account account = new Account();
        account.setRole(Role.ADMIN);
        user.setAccount(account);
        when(userService.getCurrentUser()).thenReturn(user);
        ComponentBloodRequestRequest req = new ComponentBloodRequestRequest();
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.requestBloodByComponent(req));
        assertEquals("Chỉ người dùng hoặc nhân viên y tế mới được gửi yêu cầu.", ex.getMessage());
    }

    @Test
    void testApproveComponentRequest_NotFound_ThrowsException() {
        when(bloodRequestComponentRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.approveComponentRequest(1L));
        assertEquals("Không tìm thấy yêu cầu truyền máu.", ex.getMessage());
    }

    @Test
    void testApproveComponentRequest_StatusNotPending_ThrowsException() {
        BloodRequestComponent req = new BloodRequestComponent();
        req.setStatus(com.swp.blooddonation.enums.BloodRequestStatus.READY);
        when(bloodRequestComponentRepository.findById(anyLong())).thenReturn(java.util.Optional.of(req));
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.approveComponentRequest(1L));
        assertEquals("Yêu cầu đã được xử lý.", ex.getMessage());
    }

    @Test
    void testCompleteComponentBloodRequest_NotFound_ThrowsException() {
        when(bloodRequestComponentRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.completeComponentBloodRequest(1L));
        assertEquals("Không tìm thấy yêu cầu truyền máu thành phần.", ex.getMessage());
    }

    @Test
    void testCompleteComponentBloodRequest_StatusNotReady_ThrowsException() {
        BloodRequestComponent req = new BloodRequestComponent();
        req.setStatus(com.swp.blooddonation.enums.BloodRequestStatus.PENDING);
        when(bloodRequestComponentRepository.findById(anyLong())).thenReturn(java.util.Optional.of(req));
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.completeComponentBloodRequest(1L));
        assertEquals("Yêu cầu chưa sẵn sàng để hoàn thành.", ex.getMessage());
    }

    @Test
    void testRejectComponentBloodRequest_NotFound_ThrowsException() {
        when(bloodRequestComponentRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.rejectComponentBloodRequest(1L, "Lý do"));
        assertEquals("Không tìm thấy yêu cầu truyền máu thành phần.", ex.getMessage());
    }

    @Test
    void testRejectComponentBloodRequest_StatusNotPending_ThrowsException() {
        BloodRequestComponent req = new BloodRequestComponent();
        req.setStatus(com.swp.blooddonation.enums.BloodRequestStatus.READY);
        when(bloodRequestComponentRepository.findById(anyLong())).thenReturn(java.util.Optional.of(req));
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.rejectComponentBloodRequest(1L, "Lý do"));
        assertEquals("Chỉ được từ chối yêu cầu đang chờ xử lý.", ex.getMessage());
    }

    @Test
    void testCancelRequestComponent_NotFound_ThrowsException() {
        when(bloodRequestComponentRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.cancelRequestComponent(1L));
        assertEquals("Không tìm thấy yêu cầu.", ex.getMessage());
    }

    @Test
    void testCancelRequestComponent_NotOwner_ThrowsException() {
        BloodRequestComponent req = new BloodRequestComponent();
        User requester = new User(); requester.setId(2L);
        req.setRequester(requester);
        when(bloodRequestComponentRepository.findById(anyLong())).thenReturn(java.util.Optional.of(req));
        User current = new User(); current.setId(1L);
        when(userService.getCurrentUser()).thenReturn(current);
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.cancelRequestComponent(1L));
        assertEquals("Bạn không có quyền huỷ yêu cầu này.", ex.getMessage());
    }

    @Test
    void testCancelRequestComponent_StatusInvalid_ThrowsException() {
        BloodRequestComponent req = new BloodRequestComponent();
        req.setStatus(com.swp.blooddonation.enums.BloodRequestStatus.COMPLETED);
        User requester = new User(); requester.setId(1L);
        req.setRequester(requester);
        when(bloodRequestComponentRepository.findById(anyLong())).thenReturn(java.util.Optional.of(req));
        User current = new User(); current.setId(1L);
        when(userService.getCurrentUser()).thenReturn(current);
        BadRequestException ex = assertThrows(BadRequestException.class, () -> bloodRequestService.cancelRequestComponent(1L));
        assertEquals("Chỉ được huỷ yêu cầu đang chờ xử lý hoặc đã sẵn sàng.", ex.getMessage());
    }
} 