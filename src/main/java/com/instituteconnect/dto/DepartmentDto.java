package com.instituteconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentDto {
    private Long id;
    @NotBlank private String name;
    @NotBlank private String code;
    private String description;
    private long employeeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
