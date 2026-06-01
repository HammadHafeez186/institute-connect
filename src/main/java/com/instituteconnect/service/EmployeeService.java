package com.instituteconnect.service;

import com.instituteconnect.dto.EmployeeDto;
import com.instituteconnect.entity.*;
import com.instituteconnect.exception.*;
import com.instituteconnect.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Page<EmployeeDto> getAllEmployees(String search, Long departmentId, Pageable pageable) {
        Page<Employee> page;
        if (departmentId != null && search != null && !search.isBlank()) {
            page = employeeRepository.searchByDepartment(departmentId, search, pageable);
        } else if (departmentId != null) {
            page = employeeRepository.findByDepartmentId(departmentId, pageable);
        } else if (search != null && !search.isBlank()) {
            page = employeeRepository.searchEmployees(search, pageable);
        } else {
            page = employeeRepository.findAll(pageable);
        }
        return page.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeById(Long id) {
        return toDto(findById(id));
    }

    @Transactional
    public EmployeeDto createEmployee(EmployeeDto dto) {
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + dto.getEmail());
        }

        Employee employee = Employee.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password("$2a$12$defaultHashedPassword") // Admin-created employees get temporary password
                .phone(dto.getPhone())
                .designation(dto.getDesignation())
                .build();

        if (dto.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + dto.getDepartmentId()));
            employee.setDepartment(dept);
        }

        // Default role
        roleRepository.findByName(Role.RoleName.EMPLOYEE)
                .ifPresent(role -> employee.getRoles().add(role));

        employee = employeeRepository.save(employee);
        log.info("Created employee: {}", employee.getEmail());
        return toDto(employee);
    }

    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeDto dto) {
        Employee employee = findById(id);

        if (dto.getFirstName() != null) employee.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) employee.setLastName(dto.getLastName());
        if (dto.getPhone() != null) employee.setPhone(dto.getPhone());
        if (dto.getDesignation() != null) employee.setDesignation(dto.getDesignation());

        if (dto.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + dto.getDepartmentId()));
            employee.setDepartment(dept);
        }

        employee = employeeRepository.save(employee);
        log.info("Updated employee: {}", employee.getEmail());
        return toDto(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = findById(id);
        employeeRepository.delete(employee);
        log.info("Deleted employee: {}", employee.getEmail());
    }

    @Transactional
    public EmployeeDto assignRole(Long employeeId, String roleName) {
        Employee employee = findById(employeeId);
        Role.RoleName rn = Role.RoleName.valueOf(roleName.toUpperCase());
        Role role = roleRepository.findByName(rn)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
        employee.getRoles().add(role);
        employee = employeeRepository.save(employee);
        log.info("Assigned role {} to employee {}", roleName, employee.getEmail());
        return toDto(employee);
    }

    private Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    private EmployeeDto toDto(Employee e) {
        return EmployeeDto.builder()
                .id(e.getId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .email(e.getEmail())
                .phone(e.getPhone())
                .designation(e.getDesignation())
                .departmentName(e.getDepartment() != null ? e.getDepartment().getName() : null)
                .departmentId(e.getDepartment() != null ? e.getDepartment().getId() : null)
                .roles(e.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .active(e.isActive())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
