package io.hahn_software.emrs.services;

import java.util.Collections;
import java.util.List;

import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hahn_software.emrs.annotations.LogUserOperation;
import io.hahn_software.emrs.dao.repositories.DepartmentRepo;
import io.hahn_software.emrs.dtos.DepartmentRequest;
import io.hahn_software.emrs.dtos.DepartmentResponse;
import io.hahn_software.emrs.dtos.OperationResult;
import io.hahn_software.emrs.dtos.PageDTO;
import io.hahn_software.emrs.entities.Department;
import io.hahn_software.emrs.mappers.DepartmentMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class DepartmentService {
    

    private DepartmentRepo departmentRepo;

    private DepartmentMapper departmentMapper;


    DepartmentService(
        DepartmentRepo departmentRepo ,
        DepartmentMapper departmentMapper
    ) {
        this.departmentRepo = departmentRepo ;

        this.departmentMapper = departmentMapper ;
    }


    /**
     * Creates multiple departments in batch.
     *
     * @param departmentRequests List of DepartmentRequest objects.
     * @return List of DepartmentResponse objects for the created departments.
     */


    @LogUserOperation
    public List<DepartmentResponse> createDepartments(List<DepartmentRequest> departmentRequests) {
        log.info("Creating departments in batch...");

        List<Department> departments = departmentMapper.toDepartmentList(departmentRequests);

        List<Department> savedDepartments = departmentRepo.insertDepartmentInBatch(departments);

        return departmentMapper.toDepartmentResponseList(savedDepartments);
    }


    /**
     * Deletes departments by their IDs.
     *
     * @param departmentIds List of department IDs to delete.
     * @return Number of departments deleted.
     */
    @LogUserOperation
    public OperationResult deleteDepartments(List<Long> departmentIds) {
        log.info("Deleting departments with IDs: {}", departmentIds);

        if (departmentIds == null || departmentIds.isEmpty()) {
            log.warn("No department IDs provided for deletion");
            return OperationResult.of(0, "No department IDs provided.");
        }

        int deletedCount = departmentRepo.deleteDepartments(departmentIds);

        return OperationResult.of(deletedCount , "Delete operation completed successfully.");
    }



    /**
     * Finds departments by their IDs.
     *
     * @param departmentIds List of department IDs to find.
     * @return List of DepartmentResponse objects for the found departments.
     */
    @LogUserOperation
    public List<DepartmentResponse> findDepartments(List<Long> departmentIds) {
        log.info("Finding departments with IDs: {}", departmentIds);

        if (departmentIds == null || departmentIds.isEmpty()) {
            log.warn("No department IDs provided for search");
            return Collections.emptyList();
        }

        List<Department> departments = departmentRepo.findDepartments(departmentIds);
        return departmentMapper.toDepartmentResponseList(departments);
    }



    /**
     * Retrieves departments with pagination.
     *
     * @param page     Page number (starting from 0).
     * @param pageSize Number of departments per page.
     * @return List of DepartmentResponse objects for the retrieved departments.
     */
    public PageDTO<DepartmentResponse> getDepartmentsWithPagination(int page, int pageSize) {
        

        log.info("Retrieving departments with pagination - Page: {}, PageSize: {}", page, pageSize);

        List<Department> departments = departmentRepo.departmentsWithPagination(page, pageSize);

        List<DepartmentResponse> content = departmentMapper.toDepartmentResponseList(departments);

        long totalElements = departmentRepo.count();

        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        return new PageDTO<>(content, page, pageSize, totalElements, totalPages);

    }
}
