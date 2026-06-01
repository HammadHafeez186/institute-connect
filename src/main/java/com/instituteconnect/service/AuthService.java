package com.instituteconnect.service;

import com.instituteconnect.config.JwtTokenProvider;
import com.instituteconnect.dto.*;
import com.instituteconnect.entity.Employee;
import com.instituteconnect.entity.Role;
import com.instituteconnect.exception.DuplicateResourceException;
import com.instituteconnect.repository.EmployeeRepository;
import com.instituteconnect.repository.DepartmentRepository;
import com.instituteconnect.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .designation(request.getDesignation())
                .roles(Set.of(roleRepository.findByName(Role.RoleName.EMPLOYEE)
                        .orElseThrow(() -> new RuntimeException("Default role not found"))))
                .build();

        if (request.getDepartmentId() != null) {
            employee.setDepartment(departmentRepository.findById(request.getDepartmentId()).orElse(null));
        }

        employeeRepository.save(employee);
        log.info("Registered employee: {}", employee.getEmail());

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String token = tokenProvider.generateToken(auth);

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(900)
                .email(employee.getEmail())
                .name(employee.getFirstName() + " " + employee.getLastName())
                .roles(employee.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String token = tokenProvider.generateToken(auth);

        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow();

        log.info("Employee logged in: {}", request.getEmail());

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(900)
                .email(employee.getEmail())
                .name(employee.getFirstName() + " " + employee.getLastName())
                .roles(employee.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .build();
    }
}
