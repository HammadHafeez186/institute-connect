package com.instituteconnect.service;

import com.instituteconnect.entity.*;
import com.instituteconnect.entity.Role.RoleName;
import com.instituteconnect.exception.ResourceNotFoundException;
import com.instituteconnect.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private RoleRepository roleRepository;

    @InjectMocks private EmployeeService employeeService;

    @Test
    void shouldGetEmployeeById() {
        Employee emp = Employee.builder()
                .id(1L).firstName("Jane").lastName("Smith")
                .email("jane@test.com").password("hash")
                .roles(Set.of(Role.builder().id(1L).name(RoleName.EMPLOYEE).build()))
                .build();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));

        var dto = employeeService.getEmployeeById(1L);

        assertEquals("Jane", dto.getFirstName());
        assertEquals("jane@test.com", dto.getEmail());
        assertTrue(dto.getRoles().contains("EMPLOYEE"));
    }

    @Test
    void shouldThrowWhenEmployeeNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(99L));
    }
}
