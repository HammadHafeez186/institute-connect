package com.instituteconnect.init;

import com.instituteconnect.entity.*;
import com.instituteconnect.entity.Role.RoleName;
import com.instituteconnect.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (roleRepository.count() > 0) return;

        // Create roles
        Role adminRole = roleRepository.save(Role.builder().name(RoleName.ADMIN).description("Full system access").build());
        Role managerRole = roleRepository.save(Role.builder().name(RoleName.MANAGER).description("Department management access").build());
        Role employeeRole = roleRepository.save(Role.builder().name(RoleName.EMPLOYEE).description("Basic employee access").build());

        // Create departments
        Department engineering = departmentRepository.save(Department.builder().name("Engineering").code("ENG").description("Software Engineering").build());
        Department hr = departmentRepository.save(Department.builder().name("Human Resources").code("HR").description("People operations").build());
        Department finance = departmentRepository.save(Department.builder().name("Finance").code("FIN").description("Financial operations").build());
        Department marketing = departmentRepository.save(Department.builder().name("Marketing").code("MKT").description("Marketing and communications").build());

        // Create employees
        employeeRepository.save(Employee.builder()
                .firstName("Admin").lastName("User").email("admin@institute.com")
                .password(passwordEncoder.encode("admin123"))
                .designation("System Administrator").department(engineering)
                .roles(Set.of(adminRole, employeeRole)).build());

        employeeRepository.save(Employee.builder()
                .firstName("John").lastName("Manager").email("manager@institute.com")
                .password(passwordEncoder.encode("manager123"))
                .designation("Engineering Manager").department(engineering)
                .roles(Set.of(managerRole, employeeRole)).build());

        employeeRepository.save(Employee.builder()
                .firstName("Jane").lastName("Smith").email("jane@institute.com")
                .password(passwordEncoder.encode("user123"))
                .designation("Software Engineer").department(engineering)
                .roles(Set.of(employeeRole)).build());

        employeeRepository.save(Employee.builder()
                .firstName("Bob").lastName("Wilson").email("bob@institute.com")
                .password(passwordEncoder.encode("user123"))
                .designation("HR Specialist").department(hr)
                .roles(Set.of(employeeRole)).build());

        employeeRepository.save(Employee.builder()
                .firstName("Alice").lastName("Johnson").email("alice@institute.com")
                .password(passwordEncoder.encode("user123"))
                .designation("Financial Analyst").department(finance)
                .roles(Set.of(employeeRole)).build());

        log.info("=== InstituteConnect sample data created ===");
        log.info("Admin   -> email: admin@institute.com,   password: admin123");
        log.info("Manager -> email: manager@institute.com, password: manager123");
        log.info("Employee-> email: jane@institute.com,    password: user123");
    }
}
