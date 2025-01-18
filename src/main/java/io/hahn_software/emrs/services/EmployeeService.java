package io.hahn_software.emrs.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hahn_software.emrs.annotations.LogUserOperation;
import io.hahn_software.emrs.dao.repositories.DepartmentRepo;
import io.hahn_software.emrs.dao.repositories.EmployeeRepo;
import io.hahn_software.emrs.dtos.EmployeeRequest;
import io.hahn_software.emrs.dtos.EmployeeResponse;
import io.hahn_software.emrs.dtos.OperationResult;
import io.hahn_software.emrs.dtos.PageDTO;
import io.hahn_software.emrs.entities.Employee;
import io.hahn_software.emrs.mappers.EmployeeMapper;
import jakarta.transaction.Transactional;
import lombok.Data;


@Service
@Data
@Transactional
public class EmployeeService {


    private final EmployeeRepo employeeRepo;
    private final DepartmentRepo departmentRepo;
    private final EmployeeMapper employeeMapper;


    @Autowired
    EmployeeService(
        EmployeeRepo employeeRepo ,
        DepartmentRepo departmentRepo ,
        EmployeeMapper employeeMapper
    ) {
        this.departmentRepo = departmentRepo ;
        this.employeeRepo = employeeRepo ;

        this.employeeMapper = employeeMapper ;
    }
    


    @LogUserOperation("Create employees in batch")
    public List<EmployeeResponse> createEmployeesInBatch(List<EmployeeRequest> employeeRequests) {

        // Map EmployeeRequest list to Employee list
        List<Employee> employees = employeeMapper.toEmployeeList(employeeRequests);

        // Save all employees in batch
        List<Employee> savedEmployees = employeeRepo.insertEmployeeInBatch(employees);

        return employeeMapper.toEmployeeResponseList(savedEmployees);
    }



    /**
     * Delete employees by their IDs.
     *
     * @param employeeIds List of employee IDs to delete.
     * @return Number of employees deleted.
     */
    @LogUserOperation("Delete employees in batch")
    public OperationResult deleteEmployees(List<Long> employeeIds) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return OperationResult.of(0);
        }

        int count = employeeRepo.deleteEmployees(employeeIds); 
        // Delete employees by IDs
        return OperationResult.of(count) ;
    }



    /**
     * Find employees by their IDs.
     *
     * @param ids List of employee IDs.
     * @return List of EmployeeResponse DTOs.
     */
    @LogUserOperation
    public List<EmployeeResponse> findEmployees(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        // Find employees by IDs
        List<Employee> employees = employeeRepo.findEmployees(ids);

        // Map employees to EmployeeResponse DTOs
        return employeeMapper.toEmployeeResponseList(employees);
    }



    /**
     * Get employees with pagination.
     *
     * @param page     Page number (starting from 0).
     * @param pageSize Number of employees per page.
     * @return List of EmployeeResponse DTOs.
     */
    public PageDTO<EmployeeResponse> getEmployeesWithPagination(int page, int pageSize) {
        if (page < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Page and pageSize must be positive values");
        }

        // Fetch employees with pagination
        List<Employee> employees = employeeRepo.employeesWithPagination(page, pageSize);

        List<EmployeeResponse> content = employeeMapper.toEmployeeResponseList(employees) ;


        long totalElements = employeeRepo.count();

        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        return new PageDTO<>(content, page, pageSize, totalElements, totalPages);
    }




    /**
     * Update employees in batch using the repository method.
     *
     * @param employeeIds List of employee IDs to update.
     * @param employee    Employee object containing the fields to update.
     * @return Total number of employees updated.
     */
    @LogUserOperation("Update employees in batch")
    public OperationResult updateEmployeesInBatch(List<Long> employeeIds, Employee employee) {
        
        if (employeeIds == null || employeeIds.isEmpty()) {
            throw new IllegalArgumentException("Employee IDs list cannot be null or empty");
        }

        if (employee == null) {
            throw new IllegalArgumentException("Employee object cannot be null");
        }

        int count = employeeRepo.updateClientsInBatch(employeeIds, employee) ;

        return OperationResult.of(count);
    }

}
