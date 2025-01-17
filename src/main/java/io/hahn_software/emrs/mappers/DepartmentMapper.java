package io.hahn_software.emrs.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.hahn_software.emrs.dtos.DepartmentRequest;
import io.hahn_software.emrs.dtos.DepartmentResponse;
import io.hahn_software.emrs.entities.Department;

@Component
public class DepartmentMapper {


    public DepartmentResponse toDepartmentResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }
    

    public Department toDepartment(DepartmentRequest departmentRequest) {
        return Department.builder()
                .name(departmentRequest.getName())
                .build();
    }

    public List<DepartmentResponse> toDepartmentResponseList(List<Department> departments) {
        return departments.stream()
                .map(this::toDepartmentResponse)
                .toList();
    }


    public List<Department> toDepartmentList(List<DepartmentRequest> departmentRequests) {
        return departmentRequests.stream()
                .map(this::toDepartment)
                .toList();
    }
}
