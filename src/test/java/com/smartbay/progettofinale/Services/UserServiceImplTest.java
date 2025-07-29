package com.smartbay.progettofinale.Services;

import com.smartbay.progettofinale.DTO.PersonalInfoDTO;
import com.smartbay.progettofinale.DTO.UserDTO;
import com.smartbay.progettofinale.DTO.UserInfoDTO;
import com.smartbay.progettofinale.Models.Role;
import com.smartbay.progettofinale.Models.User;
import com.smartbay.progettofinale.Repositories.RoleRepository;
import com.smartbay.progettofinale.Repositories.UserRepository;
import com.smartbay.progettofinale.Security.SecurityService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private SecurityService securityService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RedirectAttributes redirectAttributes;
    @Mock
    private HttpSession httpSession;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void dashboard_shouldReturnPersonalInfoDTO() {
        User mockUser = new User();
        mockUser.setEmail("user@example.com");

        PersonalInfoDTO dto = new PersonalInfoDTO();
        dto.setEmail("user@example.com");

        when(securityService.getActiveUser()).thenReturn(mockUser);
        when(modelMapper.map(mockUser, PersonalInfoDTO.class)).thenReturn(dto);

        PersonalInfoDTO result = userService.dashboard();

        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());
    }

    @Test
    void getUserInfo_shouldReturnDTOIfUserExists() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("Test User");

        UserInfoDTO dto = new UserInfoDTO();
        dto.setUsername("Test User");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(modelMapper.map(mockUser, UserInfoDTO.class)).thenReturn(dto);

        UserInfoDTO result = userService.getUserInfo(1L);

        assertNotNull(result);
        assertEquals("Test User", result.getUsername());
    }

    @Test
    void getUserInfo_shouldThrowIfUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.getUserInfo(99L);
        });

        assertEquals("Utente non trovato.", ex.getMessage());
    }

    @Test
    void saveUser_shouldSaveUserAndAuthenticate() {
        UserDTO userDto = new UserDTO();
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPassword("secret");

        Role userRole = new Role();
        userRole.setName("ROLE_USER");

        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(userRole);
        when(request.getSession(true)).thenReturn(httpSession);
        when(customUserDetailsService.loadUserByUsername("john.doe@example.com"))
                .thenReturn(new CustomUserDetails());

        userService.saveUser(userDto, redirectAttributes, request, response);

        verify(userRepository)
                .save(argThat(savedUser -> savedUser.getEmail().equals("john.doe@example.com")
                        && savedUser.getUsername().equals("John Doe")
                        && savedUser.getPassword().equals("encoded-secret")
                        && savedUser.getRoles().contains(userRole)));

        verify(authenticationManager).authenticate(any());
        verify(httpSession).setAttribute(eq("SPRING_SECURITY_CONTEXT"), any());
    }

    @Test
    void find_shouldReturnUserIfFound() {
        User user = new User();
        user.setId(42L);

        when(userRepository.findById(42L)).thenReturn(Optional.of(user));

        User found = userService.find(42L);
        assertEquals(42L, found.getId());
    }
}
