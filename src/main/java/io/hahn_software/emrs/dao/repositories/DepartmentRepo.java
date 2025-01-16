package io.hahn_software.emrs.dao.repositories;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import io.hahn_software.emrs.dao.interfaces.DepartmentDaoInterface;
import io.hahn_software.emrs.entities.Department;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
@Repository
public class DepartmentRepo implements DepartmentDaoInterface{
    

    @Value("${hibernate.jdbc.batch_size}")
    private  int batchSize = 10;

    
    @PersistenceContext
    private EntityManager em ;


    @Override
    public List<Department> insertDepartmentInBatch(List<Department> departments) {
        
        log.debug("batch size is : {} 🔖\n" , batchSize);

        if (departments == null || departments.isEmpty()) {
            return departments ;
        }

        for (int i = 0; i < departments.size(); i++) {
            
            em.persist(departments.get(i));

            if (i > 0 && i % batchSize == 0) {
                em.flush();
                em.clear();
            }
        }

        return departments ;
    }

    @Override
    public int deleteDepartments(List<Long> departmentsIds) {

        if (departmentsIds == null || departmentsIds.isEmpty()) {
            return 0;
        }

        String jpql = "DELETE FROM Department d WHERE d.id IN :ids" ;

        return em.createQuery(jpql)
                    .setParameter("ids", departmentsIds)
                    .executeUpdate() ;

    }

    @Override
    public List<Department> findDepartments(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            
            return Collections.emptyList();
        }
    
        String jpql = "SELECT d FROM Department d WHERE d.id IN :ids";
        
        return em.createQuery(jpql, Department.class)
                            .setParameter("ids", ids)
                            .getResultList();
    }

    @Override
    public List<Department> departmentsWithPagination(int page, int pageSize) {

        String jpql = "SELECT d FROM Department d ORDER BY d.id";

        TypedQuery<Department> query = em.createQuery(jpql, Department.class);

        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList() ;
    }

    @Override
    public Long count() {

        String jpql = "SELECT COUNT(d) FROM Department d";

        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        
        return query.getSingleResult() ;
    }
}
