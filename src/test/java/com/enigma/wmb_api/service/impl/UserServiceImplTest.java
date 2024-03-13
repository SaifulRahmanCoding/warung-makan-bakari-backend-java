package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.repository.UserAccountRepository;
import com.enigma.wmb_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserAccountRepository userAccountRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userAccountRepository);
    }

    @Test
    void shouldReturnUserDetailsWhenLoadUserByUsername() {
        String username = "sr.cs";
        UserAccount userAccount = UserAccount.builder().username(username).build();
        Mockito.when(userAccountRepository.findByUsername(username)).thenReturn(Optional.of(userAccount));
        UserDetails userDetails = userService.loadUserByUsername(username);
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
    }

    @Test
    void shouldReturnUserAccountWhenGetByUserId() {
        String id = "user-id";
        UserAccount userAccount = UserAccount.builder().id(id).build();
        Mockito.when(userAccountRepository.findById(id)).thenReturn(Optional.of(userAccount));
        UserAccount account = userService.getByUserId(id);
        assertNotNull(account);
        assertEquals(id, account.getId());
    }
}