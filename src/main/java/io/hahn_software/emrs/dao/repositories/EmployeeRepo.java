package io.hahn_software.emrs.dao.repositories;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import io.hahn_software.emrs.dao.interfaces.EmployeeDaoInterface;
import io.hahn_software.emrs.entities.Employee;
import io.hahn_software.emrs.utils.DBUtiles;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
@Repository
public class EmployeeRepo implements EmployeeDaoInterface{


    @Value("${hibernate.jdbc.batch_size}")
    private int batchSize = 10;

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Employee> insertEmployeeInBatch(List<Employee> employees) {
        
        log.debug("batch size is : {} 🔖\n", batchSize);

        if (employees == null || employees.isEmpty()) {
            return employees;
        }

        for (int i = 0; i < employees.size(); i++) {
            em.persist(employees.get(i));

            if (i > 0 && i % batchSize == 0) {
                em.flush();
                em.clear();
            }
        }

        return employees;
    }

    @Override
    public int deleteEmployees(List<Long> employeeIds) {

        if (employeeIds == null || employeeIds.isEmpty()) {
            return 0;
        }

        String jpql = "DELETE FROM Employee e WHERE e.id IN :ids";

        return em.createQuery(jpql)
                .setParameter("ids", employeeIds)
                .executeUpdate();

    }

    @Override
    public List<Employee> findEmployees(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        String jpql = "SELECT e FROM Employee e WHERE e.id IN :ids";

        return em.createQuery(jpql, Employee.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    public List<Employee> employeesWithPagination(int page, int pageSize) {

        String jpql = "SELECT e FROM Employee e ORDER BY e.id";

        TypedQuery<Employee> query = em.createQuery(jpql, Employee.class);

        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    @Override
    public Long count() {

        String jpql = "SELECT COUNT(e) FROM Employee e";

        TypedQuery<Long> query = em.createQuery(jpql, Long.class);

        return query.getSingleResult();
    }

    @Override
    public int updateClientsInBatch(List<Long> employeesIds, Employee employee) {
        
        int totalUpdatedRecords = 0 ;

        Query query = DBUtiles.buildJPQLQueryDynamicallyForUpdate(employee, em) ;


        for (int i = 0; i < employeesIds.size(); i += batchSize) {
            List<Long> batch = employeesIds.subList(i, Math.min(i + batchSize, employeesIds.size()));
    
            
            query.setParameter("Ids", batch);

            // Execute the update
            int updatedRecords = query.executeUpdate();
            totalUpdatedRecords += updatedRecords;
    
            em.flush();
            em.clear();

        }

        return totalUpdatedRecords ;

    }
    
}
