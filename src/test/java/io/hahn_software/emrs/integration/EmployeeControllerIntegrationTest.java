package io.hahn_software.emrs.integration;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hahn_software.emrs.dtos.DepartmentResponse;
import io.hahn_software.emrs.dtos.EmployeeRequest;
import io.hahn_software.emrs.dtos.EmployeeResponse;
import io.hahn_software.emrs.dtos.OperationResult;
import io.hahn_software.emrs.dtos.PageDTO;
import io.hahn_software.emrs.entities.Employee;
import io.hahn_software.emrs.enums.EmploymentStatus;
import io.hahn_software.emrs.services.EmployeeService;
import lombok.extern.slf4j.Slf4j;

@AutoConfigureMockMvc
@Slf4j
class EmployeeControllerIntegrationTest extends BaseControllerTest {
    


    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    EmployeeControllerIntegrationTest(
        MockMvc mockMvc,
        ObjectMapper objectMapper
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }


    private EmployeeRequest createEmployeeRequest() {
        EmployeeRequest request = new EmployeeRequest();
        request.setFullName("John Doe");
        request.setEmployeeID(12345L);
        request.setJobTitle("Software Engineer");
        request.setEmploymentStatus(EmploymentStatus.FULL_TIME);
        request.setAddress("123 Main St, City, Country");
        request.setPhone("+1234567890");
        request.setEmail("john.doe@example.com");
        request.setDepartmentId(1L);
        return request;
    }


    private EmployeeResponse createEmployeeResponse() {
        return EmployeeResponse.builder()
                .id(1L)
                .fullName("John Doe")
                .employeeID(12345L)
                .jobTitle("Software Engineer")
                .employmentStatus(EmploymentStatus.FULL_TIME)
                .address("123 Main St, City, Country")
                .phone("+1234567890")
                .email("john.doe@example.com")
                .department(1L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }


    /***
     * 
     * 
     * Create Tests
     * @throws Exception
     */
    @Test
    void testAdministratorCanCreateEmployees() throws Exception {
        EmployeeRequest request = createEmployeeRequest();
        EmployeeResponse response = createEmployeeResponse();

        when(employeeService.createEmployeesInBatch(anyList()))
                .thenReturn(List.of(response));

        MockHttpServletRequestBuilder requestBuilder = prepareRequestWithOAuthToken(
                HttpMethod.POST, "/employees", "Administrator"
        ).content(objectMapper.writeValueAsString(List.of(request)));

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fullName").value("John Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].employeeID").value(12345));
    }



    @Test
    void testUnauthorizedUserCannotCreateEmployees() throws Exception {

        /**
         * with do not use mockBean of Employee service because test will not passe throw the Controller , 
         * 
         * "will get permission denind from the controller"
         */

         EmployeeRequest request = new EmployeeRequest();
                        request.setFullName("John Doe");
                        request.setEmployeeID(12345L);
                        request.setJobTitle("Software Engineer");
                        request.setEmploymentStatus(EmploymentStatus.FULL_TIME);
                        request.setAddress("123 Main St, City, Country");
                        request.setPhone("+1234567890");
                        request.setEmail("john.doe@example.com");
                        request.setDepartmentId(1L);

        MockHttpServletRequestBuilder requestBuilder = prepareRequestWithOAuthToken(
                HttpMethod.POST, "/employees", "Manager"
        ).content(objectMapper.writeValueAsString(List.of(request)));

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }



    /***
     * 
     * Delete Tests
     * @throws Exception
     */

    @Test
    void testAdministratorCanDeleteEmployees() throws Exception {
        List<Long> employeeIds = List.of(1L, 2L, 3L);
        OperationResult result = OperationResult.of(3);

        when(employeeService.deleteEmployees(employeeIds))
                .thenReturn(result);

        MockHttpServletRequestBuilder requestBuilder = prepareRequestWithOAuthToken(
                HttpMethod.DELETE, "/employees/1,2,3", "HR_Personnel"
        );

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.affectedRecords").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Operation completed successfully."));
    }




    /***
     * 
     * find Tests
     * @throws Exception
     */


    @Test
    void testAdministratorCanFindEmployees() throws Exception {
        List<Long> employeeIds = List.of(1L, 2L, 3L);
        List<EmployeeResponse> employees = List.of(
                createEmployeeResponse(),
                EmployeeResponse.builder()
                        .id(2L)
                        .fullName("Jane Smith")
                        .employeeID(67890L)
                        .jobTitle("Product Manager")
                        .employmentStatus(EmploymentStatus.FULL_TIME)
                        .address("456 Elm St, City, Country")
                        .phone("+0987654321")
                        .email("jane.smith@example.com")
                        .department(1L)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()
        );

        when(employeeService.findEmployees(employeeIds))
                .thenReturn(employees);

        MockHttpServletRequestBuilder requestBuilder = prepareRequestWithOAuthToken(
                HttpMethod.GET, "/employees/1,2,3", "Administrator"
        );

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fullName").value("John Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].fullName").value("Jane Smith")) ;


    }



    @Test
    void testUnauthorizedUserCannotFindEmployees() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = prepareRequestWithOAuthToken(
                HttpMethod.GET, "/employees/1,2,3", "Guest"
        );

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }



    /***
     * 
     * Pageination Tests
     * @throws Exception
     */

    @Test
    void testAdministratorCanGetEmployeesWithPagination() throws Exception {
        
        int page = 0;
        int pageSize = 10;

        int totalPages = (int) Math.ceil((double) 1 / pageSize);

        PageDTO<EmployeeResponse> pageDTO = new PageDTO<>(
                List.of(createEmployeeResponse()),
                page,
                pageSize,
                1,
                totalPages
        );

        when(employeeService.getEmployeesWithPagination(page, pageSize))
                .thenReturn(pageDTO);

        MockHttpServletRequestBuilder requestBuilder = prepareRequestWithOAuthToken(
                HttpMethod.GET, "/employees", "Administrator"
        ).param("page", String.valueOf(page))
         .param("pageSize", String.valueOf(pageSize));

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].fullName").value("John Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1));
    }


    /***
     * 
     * Update Tests
     * @throws Exception
     */

    @Test
    void testUpdateEmployeesInBatch_Success() throws Exception {

        List<Long> employeeIds = List.of(1L, 2L, 3L);
        Employee employee = Employee.builder()
                .email("new.email@example.com")
                .jobTitle("Senior Developer")
                .build();

        OperationResult result = OperationResult.of(3); // Simulate 3 employees updated

        when(employeeService.updateEmployeesInBatch(employeeIds, employee))
                .thenReturn(result);

        MockHttpServletRequestBuilder requestBuilder = prepareRequestWithOAuthToken(
                HttpMethod.PUT, "/employees?employeeIds=1,2,3", "HR_Personnel"
        )
                .content(objectMapper.writeValueAsString(employee));

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.affectedRecords").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Operation completed successfully."));

        verify(employeeService, times(1)).updateEmployeesInBatch(employeeIds, employee);
    }


    @Test
    void testUpdateEmployeesInBatch_InvalidInput_EmployeeIdsNull() throws Exception {

        Employee employee = Employee.builder()
                .email("new.email@example.com")
                .jobTitle("Senior Developer")
                .build();

        MockHttpServletRequestBuilder requestBuilder = prepareRequestWithOAuthToken(
                HttpMethod.PUT, "/employees", "HR_Personnel"
        )
                .content(objectMapper.writeValueAsString(employee));

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    void testUpdateEmployeesInBatch_Unauthorized() throws Exception {

        Employee employee = Employee.builder()
                .email("new.email@example.com")
                .jobTitle("Senior Developer")
                .build();

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/employees?employeeIds=1,2,3")
                .content(objectMapper.writeValueAsString(employee));

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
