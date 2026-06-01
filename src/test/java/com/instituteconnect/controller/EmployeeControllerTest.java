package com.instituteconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.instituteconnect.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String loginAsAdmin() throws Exception {
        LoginRequest req = LoginRequest.builder().email("admin@institute.com").password("admin123").build();
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }

    private String loginAsEmployee() throws Exception {
        LoginRequest req = LoginRequest.builder().email("jane@institute.com").password("user123").build();
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }

    @Test
    void shouldListEmployeesWhenAuthenticated() throws Exception {
        String token = loginAsEmployee();
        mockMvc.perform(get("/api/employees").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldSearchEmployees() throws Exception {
        String token = loginAsAdmin();
        mockMvc.perform(get("/api/employees?search=Jane").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("Jane"));
    }

    @Test
    void shouldDenyCreateForNonAdmin() throws Exception {
        String token = loginAsEmployee();
        mockMvc.perform(post("/api/employees")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Test\",\"lastName\":\"User\",\"email\":\"new@test.com\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToAssignRole() throws Exception {
        String token = loginAsAdmin();
        mockMvc.perform(post("/api/employees/3/roles")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"role\":\"MANAGER\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").isArray());
    }
}
