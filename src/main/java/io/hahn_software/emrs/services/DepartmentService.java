package io.hahn_software.emrs.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.hahn_software.emrs.dao.repositories.DepartmentRepo;
import io.hahn_software.emrs.dtos.DepartmentRequest;
import io.hahn_software.emrs.dtos.DepartmentResponse;
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





    public List<DepartmentResponse> createDepartments(List<DepartmentRequest> departmentRequests) {
        log.info("Creating departments in batch...");

        List<Department> departments = departmentMapper.toDepartmentList(departmentRequests);

        List<Department> savedDepartments = departmentRepo.insertDepartmentInBatch(departments);

        return departmentMapper.toDepartmentResponseList(savedDepartments);
    }
}
