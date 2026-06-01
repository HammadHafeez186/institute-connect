-- Database indexes for optimized queries (<100ms)
-- These run after Hibernate DDL auto-creation

CREATE INDEX IF NOT EXISTS idx_employee_email ON employees(email);
CREATE INDEX IF NOT EXISTS idx_employee_department ON employees(department_id);
CREATE INDEX IF NOT EXISTS idx_employee_dept_lastname ON employees(department_id, last_name);
CREATE INDEX IF NOT EXISTS idx_employee_first_last ON employees(first_name, last_name);
CREATE INDEX IF NOT EXISTS idx_department_code ON departments(code);
