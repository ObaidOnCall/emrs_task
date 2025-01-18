package io.hahn_software.emrs.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.hahn_software.emrs.dao.repositories.EmployeeRepo;
import io.hahn_software.emrs.entities.Department;
import io.hahn_software.emrs.entities.Employee;
import io.hahn_software.emrs.enums.EmploymentStatus;
import io.hahn_software.emrs.utils.DBUtiles;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

@ExtendWith(MockitoExtension.class)
class EmployeeRepoUnitTest {


    @Mock
    private EntityManager em;

    @InjectMocks
    private EmployeeRepo employeeRepo;

    private Employee employee1;
    private Employee employee2;
    private List<Long> employeeIds;
    private List<Employee> employees;



    @BeforeEach
    void setUp() {
        employee1 = Employee.builder()
                .id(1L)
                .fullName("John Doe")
                .employeeID(101L)
                .jobTitle("Developer")
                .employmentStatus(EmploymentStatus.FULL_TIME)
                .address("123 Main St")
                .phone("123-456-7890")
                .email("john.doe@example.com")
                .department(
                    Department.builder()
                                .id(1L)
                                .build()
                )
                .build();

        employee2 = Employee.builder()
                .id(2L)
                .fullName("Jane Smith")
                .employeeID(102L)
                .jobTitle("Manager")
                .employmentStatus(EmploymentStatus.PART_TIME)
                .address("456 Elm St")
                .phone("987-654-3210")
                .email("jane.smith@example.com")
                .department(
                    Department.builder()
                                .id(2L)
                                .build()
                )
                .build();

        employeeIds = Arrays.asList(1L, 2L);
        employees = Arrays.asList(employee1, employee2);
    }



    @Test
    void testInsertEmployeeInBatch() {

        employeeRepo.setBatchSize(1);

        List<Employee> result = employeeRepo.insertEmployeeInBatch(employees);

        assertEquals(employees, result);
        verify(em, times(2)).persist(any(Employee.class));
        verify(em, times(1)).flush();
        verify(em, times(1)).clear();
    }


    @Test
    void testInsertEmployeeInBatch_EmptyList() {
        // Act
        List<Employee> result = employeeRepo.insertEmployeeInBatch(Collections.emptyList());

        // Assert
        assertTrue(result.isEmpty());
        verify(em, never()).persist(any(Employee.class));
        verify(em, never()).flush();
        verify(em, never()).clear();
    }


    @Test
    void testDeleteEmployees() {
        // Arrange
        Query query = mock(Query.class);
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(2);

        // wax ghdi tb9a tmetel 3lina ?
        int result = employeeRepo.deleteEmployees(employeeIds);

        // Assert
        assertEquals(2, result);
        verify(em, times(1)).createQuery("DELETE FROM Employee e WHERE e.id IN :ids");
        verify(query, times(1)).setParameter("ids", employeeIds);
        verify(query, times(1)).executeUpdate();
    }


    @Test
    void testDeleteEmployees_EmptyList() {
        // Act
        int result = employeeRepo.deleteEmployees(Collections.emptyList());

        // Assert
        assertEquals(0, result);
        verify(em, never()).createQuery(anyString());
    }


    @Test
    void testFindEmployees() {
        // Arrange
        @SuppressWarnings("unchecked")
        TypedQuery<Employee> query = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Employee.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(employees);

        // Act
        List<Employee> result = employeeRepo.findEmployees(employeeIds);

        // Assert
        assertEquals(employees, result);
        verify(em, times(1)).createQuery("SELECT e FROM Employee e WHERE e.id IN :ids", Employee.class);
        verify(query, times(1)).setParameter("ids", employeeIds);
        verify(query, times(1)).getResultList();
    }


    @Test
    void testFindEmployees_EmptyList() {
        // Act
        List<Employee> result = employeeRepo.findEmployees(Collections.emptyList());

        // Assert
        assertTrue(result.isEmpty());
        verify(em, never()).createQuery(anyString(), eq(Employee.class));
    }


    @Test
    void testEmployeesWithPagination() {
        // Arrange
        @SuppressWarnings("unchecked")
        TypedQuery<Employee> query = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Employee.class))).thenReturn(query);
        when(query.setFirstResult(anyInt())).thenReturn(query);
        when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(employees);

        // Act
        List<Employee> result = employeeRepo.employeesWithPagination(0, 10);

        // Assert
        assertEquals(employees, result);
        verify(em, times(1)).createQuery("SELECT e FROM Employee e ORDER BY e.id", Employee.class);
        verify(query, times(1)).setFirstResult(0);
        verify(query, times(1)).setMaxResults(10);
        verify(query, times(1)).getResultList();
    }



    @Test
    void testUpdateClientsInBatch() {
        // Arrange
        Query query = mock(Query.class);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(2);

        // Mock the static method
        try (MockedStatic<DBUtiles> mockedStatic = Mockito.mockStatic(DBUtiles.class)) {
            mockedStatic.when(() -> DBUtiles.buildJPQLQueryDynamicallyForUpdate(any(Employee.class), eq(em)))
                        .thenReturn(query);

            // Act
            int result = employeeRepo.updateClientsInBatch(employeeIds, employee1);

            // Assert
            assertEquals(2, result);
            verify(query, times(1)).setParameter("Ids", employeeIds);
            verify(query, times(1)).executeUpdate();
            verify(em, times(1)).flush();
            verify(em, times(1)).clear();
        }
    }


    // @Test
    // void testUpdateClientsInBatch_EmptyList() {

    //     Query query = mock(Query.class);
    //     when(query.setParameter(anyString(), any())).thenReturn(query);
    //     when(query.executeUpdate()).thenReturn(2);


    //     try (MockedStatic<DBUtiles> mockedStatic = Mockito.mockStatic(DBUtiles.class)) {
    //         mockedStatic.when(() -> DBUtiles.buildJPQLQueryDynamicallyForUpdate(any(Employee.class), eq(em)))
    //                     .thenReturn(query);

    //         // Act
    //         int result = employeeRepo.updateClientsInBatch(Collections.emptyList(), employee1);

    //         assertEquals(0, result);
    //         verify(em, never()).createQuery(anyString());
    //     }

        
    // }

    
}
