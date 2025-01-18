package io.hahn_software.emrs.mappers ;

import java.util.List;

import org.springframework.stereotype.Component;

import io.hahn_software.emrs.dtos.DepartmentResponse;
import io.hahn_software.emrs.dtos.EmployeeRequest;
import io.hahn_software.emrs.dtos.EmployeeResponse;
import io.hahn_software.emrs.entities.Department;
import io.hahn_software.emrs.entities.Employee;

@Component
public class EmployeeMapper {

    public EmployeeResponse toEmployeeResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .fullName(employee.getFullName())
                .employeeID(employee.getEmployeeID())
                .jobTitle(employee.getJobTitle())
                .employmentStatus(employee.getEmploymentStatus())
                .address(employee.getAddress())
                .phone(employee.getPhone())
                .email(employee.getEmail())
                .department(employee.getDepartment().getId())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }

    public Employee toEmployee(EmployeeRequest employeeRequest) {
        return Employee.builder()
                .fullName(employeeRequest.getFullName())
                .employeeID(employeeRequest.getEmployeeID())
                .jobTitle(employeeRequest.getJobTitle())
                .employmentStatus(employeeRequest.getEmploymentStatus())
                .address(employeeRequest.getAddress())
                .phone(employeeRequest.getPhone())
                .email(employeeRequest.getEmail())
                .department(
                    Department.builder()
                                .id(employeeRequest.getDepartmentId())
                                .build()

                )
                .build();
    }

    public List<EmployeeResponse> toEmployeeResponseList(List<Employee> employees) {
        return employees.stream()
                .map(this::toEmployeeResponse)
                .toList();
    }

    public List<Employee> toEmployeeList(List<EmployeeRequest> employeeRequests) {
        return employeeRequests.stream()
                .map(this::toEmployee)
                .toList();
    }
}