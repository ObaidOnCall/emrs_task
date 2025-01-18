package io.hahn_software.emrs.dao.interfaces;

import java.util.List;

import io.hahn_software.emrs.entities.Employee;

public interface EmployeeDaoInterface {

    
    List<Employee> insertEmployeeInBatch(List<Employee> employees) ;
    
    int deleteEmployees(List<Long> employeesIds ) ;

    List<Employee> findEmployees(List<Long> ids)  ;

    List<Employee> employeesWithPagination(int page, int pageSize) ;

    int updateClientsInBatch(List<Long> employeesIds, Employee Employee) ;

    Long count() ;
}
