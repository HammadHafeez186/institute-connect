package com.instituteconnect.service;

import com.instituteconnect.dto.DepartmentDto;
import com.instituteconnect.entity.Department;
import com.instituteconnect.exception.*;
import com.instituteconnect.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public Page<DepartmentDto> getAllDepartments(Pageable pageable) {
        return departmentRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public DepartmentDto getDepartmentById(Long id) {
        return toDto(findById(id));
    }

    @Transactional
    public DepartmentDto createDepartment(DepartmentDto dto) {
        if (departmentRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("Department code already exists: " + dto.getCode());
        }
        Department dept = Department.builder()
                .name(dto.getName())
                .code(dto.getCode().toUpperCase())
                .description(dto.getDescription())
                .build();
        dept = departmentRepository.save(dept);
        log.info("Created department: {} ({})", dept.getName(), dept.getCode());
        return toDto(dept);
    }

    @Transactional
    public DepartmentDto updateDepartment(Long id, DepartmentDto dto) {
        Department dept = findById(id);
        if (dto.getName() != null) dept.setName(dto.getName());
        if (dto.getDescription() != null) dept.setDescription(dto.getDescription());
        dept = departmentRepository.save(dept);
        log.info("Updated department: {}", dept.getCode());
        return toDto(dept);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department dept = findById(id);
        if (employeeRepository.countByDepartmentId(id) > 0) {
            throw new IllegalArgumentException("Cannot delete department with active employees. Reassign employees first.");
        }
        departmentRepository.delete(dept);
        log.info("Deleted department: {}", dept.getCode());
    }

    private Department findById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    private DepartmentDto toDto(Department d) {
        return DepartmentDto.builder()
                .id(d.getId())
                .name(d.getName())
                .code(d.getCode())
                .description(d.getDescription())
                .employeeCount(employeeRepository.countByDepartmentId(d.getId()))
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
