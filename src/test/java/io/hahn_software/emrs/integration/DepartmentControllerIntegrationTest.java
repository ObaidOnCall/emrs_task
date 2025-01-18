package io.hahn_software.emrs.integration;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hahn_software.emrs.dtos.DepartmentRequest;
import io.hahn_software.emrs.dtos.DepartmentResponse;
import io.hahn_software.emrs.dtos.OperationResult;
import io.hahn_software.emrs.dtos.PageDTO;
import io.hahn_software.emrs.services.DepartmentService;
import lombok.extern.slf4j.Slf4j;

@AutoConfigureMockMvc
@Slf4j
class DepartmentControllerIntegrationTest extends BaseControllerTest{
    


    private MockMvc mockMvc;

    private ObjectMapper objectMapper;


    @MockBean
    private DepartmentService departmentService;

    @Autowired
    DepartmentControllerIntegrationTest(
        MockMvc mockMvc ,
        ObjectMapper objectMapper 
    ) {

        this.mockMvc = mockMvc ;
        this.objectMapper = objectMapper ;

    }


    private DepartmentRequest createDepartmentRequest() {
        DepartmentRequest request = new DepartmentRequest();
        request.setName("Eudcation Department");
        return request;
    }

    private DepartmentResponse createDepartmentResponse() {
        DepartmentResponse response = new DepartmentResponse();
        response.setId(1L);
        response.setName("Eudcation Department");
        response.setCreatedAt(Instant.now());
        response.setUpdatedAt(Instant.now());
        return response;
    }


    @Test
    void testAdministratorCanCreateDepartments() throws Exception {

        DepartmentRequest request = createDepartmentRequest();
        DepartmentResponse response = createDepartmentResponse();

        when(departmentService.createDepartments(anyList()))
                .thenReturn(List.of(response));

        // Prepare request with Administrator role
        MockHttpServletRequestBuilder requestBuilder = prepareRequestWithOAuthToken(
                HttpMethod.POST, "/departments", "Administrator"
        ).content(objectMapper.writeValueAsString(List.of(request)));

        log.debug("Mocked JWT token: {}", requestBuilder.buildRequest(null).getHeader("Authorization"));

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Eudcation Department"));
    }



    @Test
    void testUnauthorizedUserCannotCreateDepartments() throws Exception {
        DepartmentRequest request = createDepartmentRequest();

        // Prepare request with an unauthorized role (e.g., "Manager")
        MockHttpServletRequestBuilder requestBuilder = prepareRequestWithOAuthToken(
                HttpMethod.POST, "/departments", "Manager" // Unauthorized role
        ).content(objectMapper.writeValueAsString(List.of(request)));

        // Perform request and expect a 403 Forbidden response
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }


    @Test
    void testAdministratorCanDeleteDepartments() throws Exception {

        // Mock data
        List<Long> departmentIds = List.of(19887L, 18995L);
        OperationResult result = OperationResult.of(2);


        when(departmentService.deleteDepartments(departmentIds))
            .thenReturn(result);

        MockHttpServletRequestBuilder requestBuilder = prepareRequestWithOAuthToken(
                HttpMethod.DELETE, "/departments/19887,18995", "HR_Personnel"
        );
        

        mockMvc.perform(requestBuilder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.affectedRecords").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Operation completed successfully."));

    }



    @Test
    void testAdministratorCanFindDepartments() throws Exception {

        // Mock data
        List<Long> departmentIds = List.of(1L, 2L, 3L);
        List<DepartmentResponse> departments = List.of(

            DepartmentResponse.builder()
                            .id(1L)
                            .name("HR")
                            .build(),
            DepartmentResponse.builder()
                            .id(2L)
                            .name("Finance")
                            .build(),
            DepartmentResponse.builder()
                            .id(3L)
                            .name("IT")
                            .build()
        );

        when(departmentService.findDepartments(departmentIds))
                .thenReturn(departments);

        MockHttpServletRequestBuilder requestBuilder = prepareRequestWithOAuthToken(
                HttpMethod.GET, "/departments/1,2,3", "Administrator"
        );

        // Perform request
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("HR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Finance"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].name").value("IT"));
    }


    @Test
    void testUnauthorizedUserCannotFindDepartments() throws Exception {
        // Prepare request with an unauthorized role (e.g., "Guest")
        MockHttpServletRequestBuilder requestBuilder = prepareRequestWithOAuthToken(
                HttpMethod.GET, "/departments/1,2,3", "Guest"
        );

        // Perform request and expect a 403 Forbidden response
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }


    @Test
    void testAdministratorCanGetDepartmentsWithPagination() throws Exception {

        int page = 0;
        int pageSize = 10;
        PageDTO<DepartmentResponse> pageDTO = new PageDTO<>();
        pageDTO.setContent(List.of(

            DepartmentResponse.builder()
                            .id(1L)
                            .name("HR")
                            .build(),
            DepartmentResponse.builder()
                            .id(2L)
                            .name("Finance")
                            .build()
        ));
        pageDTO.setTotalPages(1);
        pageDTO.setTotalElements(2);

        when(departmentService.getDepartmentsWithPagination(page, pageSize))
                .thenReturn(pageDTO);

        MockHttpServletRequestBuilder requestBuilder = prepareRequestWithOAuthToken(
                HttpMethod.GET, "/departments", "Administrator"
        ).param("page", String.valueOf(page))
         .param("pageSize", String.valueOf(pageSize));

        // Perform request
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("HR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].name").value("Finance"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(2));
    }
}
