package io.hahn_software.emrs.dao.interfaces;

import java.util.List;

import io.hahn_software.emrs.entities.Department;

public interface DepartmentDaoInterface {

    List<Department> insertDepartmentInBatch(List<Department> departments) ;
    
    int deleteDepartments(List<Long> departmentsIds ) ;

    List<Department> findDepartments(List<Long> ids)  ;

    List<Department> departmentsWithPagination(int page, int pageSize) ;

    Long count() ;
}
