package com.swp.blooddonation.service;

import com.swp.blooddonation.dto.request.BlogRequest;
import com.swp.blooddonation.dto.response.BlogResponse;
import com.swp.blooddonation.entity.Blog;
import com.swp.blooddonation.entity.User;
import com.swp.blooddonation.exception.exceptions.UserNotFoundException;
import com.swp.blooddonation.repository.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import java.util.Optional;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlogServiceTest {
    @Mock
    private BlogRepository blogRepository;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserService userService;
    @InjectMocks
    private BlogService blogService;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void testCreateBlog_UserNull_ThrowsException() {
        when(userService.getCurrentUser()).thenReturn(null);
        BlogRequest req = new BlogRequest();
        assertThrows(UserNotFoundException.class, () -> blogService.createBlog(req));
    }

    @Test
    void testGetAllBlogs_ReturnsList() {
        Blog blog = new Blog();
        when(blogRepository.findAllByOrderByCreatedDateDesc()).thenReturn(List.of(blog));
        when(modelMapper.map(any(), eq(BlogResponse.class))).thenReturn(new BlogResponse());
        var result = blogService.getAllBlogs();
        assertEquals(1, result.size());
    }

    @Test
    void testGetBlogById_NotFound_ThrowsException() {
        when(blogRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(com.swp.blooddonation.exception.exceptions.BadRequestException.class, () -> blogService.getBlogById(1L));
    }
} 