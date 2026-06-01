package com.instituteconnect.controller;

import com.instituteconnect.dto.RoleDto;
import com.instituteconnect.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Roles", description = "Role management endpoints (Admin only)")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "List all roles")
    public ResponseEntity<List<RoleDto>> getAll() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
