package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.UserRole;
import com.enigma.wmb_api.dto.request.AuthRequest;
import com.enigma.wmb_api.dto.response.LoginResponse;
import com.enigma.wmb_api.dto.response.RegisterResponse;
import com.enigma.wmb_api.entity.Customer;
import com.enigma.wmb_api.entity.Role;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.repository.UserAccountRepository;
import com.enigma.wmb_api.service.AuthService;
import com.enigma.wmb_api.service.CustomerService;
import com.enigma.wmb_api.service.JwtService;
import com.enigma.wmb_api.service.RoleService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserAccountRepository userAccountRepository;
    private final RoleService roleService;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${wmb.username.superadmin}")
    private String superAdminUsername;
    @Value("${wmb.password.superadmin}")
    private String superAdminPassword;

    @Transactional(rollbackFor = Exception.class)
    @PostConstruct
    public void initSuperAdmin() {
        Optional<UserAccount> currentUser = userAccountRepository.findByUsername(superAdminUsername);
        if (currentUser.isPresent()) return;

        Role superAdmin = roleService.getOrSave(UserRole.ROLE_SUPER_ADMIN);
        Role admin = roleService.getOrSave(UserRole.ROLE_ADMIN);
        Role customer = roleService.getOrSave(UserRole.ROLE_CUSTOMER);

        UserAccount account = UserAccount.builder()
                .username(superAdminUsername)
                .password(passwordEncoder.encode(superAdminPassword))
                .role(List.of(superAdmin, admin, customer))
                .isEnable(true)
                .build();

        userAccountRepository.save(account);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterResponse register(AuthRequest request) {
        Role role = roleService.getOrSave(UserRole.ROLE_CUSTOMER);
        String hashPassword = passwordEncoder.encode(request.getPassword());

        UserAccount account = UserAccount.builder()
                .username(request.getUsername())
                .password(hashPassword)
                .role(List.of(role))
                .isEnable(true)
                .build();

        return saveAccountAndGetRegisterResponse(account);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterResponse registerAdmin(AuthRequest request) {
        Role roleAdmin = roleService.getOrSave(UserRole.ROLE_ADMIN);
        Role roleCustomer = roleService.getOrSave(UserRole.ROLE_CUSTOMER);

        String hashPassword = passwordEncoder.encode(request.getPassword());

        UserAccount account = UserAccount.builder()
                .username(request.getUsername())
                .password(hashPassword)
                .role(List.of(roleAdmin, roleCustomer))
                .isEnable(true)
                .build();

        return saveAccountAndGetRegisterResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(AuthRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        );
        Authentication authenticate = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        UserAccount userAccount = (UserAccount) authenticate.getPrincipal();
        String token = jwtService.generateToken(userAccount);
        return LoginResponse.builder()
                .username(userAccount.getUsername())
                .roles(userAccount.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .token(token)
                .build();
    }

    private RegisterResponse saveAccountAndGetRegisterResponse(UserAccount account) {
        userAccountRepository.saveAndFlush(account);

        Customer customer = Customer.builder().isMember(true).userAccount(account).build();
        customerService.create(customer);

        List<String> roles = account.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return RegisterResponse.builder().username(account.getUsername()).roles(roles).build();
    }
}
