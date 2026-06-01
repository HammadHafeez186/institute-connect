package com.instituteconnect.service;

import com.instituteconnect.dto.RoleDto;
import com.instituteconnect.entity.Role;
import com.instituteconnect.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private RoleDto toDto(Role r) {
        return RoleDto.builder()
                .id(r.getId())
                .name(r.getName().name())
                .description(r.getDescription())
                .build();
    }
}
