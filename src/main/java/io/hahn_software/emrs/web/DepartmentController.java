package io.hahn_software.emrs.web;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.hahn_software.emrs.dtos.DepartmentRequest;
import io.hahn_software.emrs.dtos.DepartmentResponse;
import io.hahn_software.emrs.dtos.OperationResult;
import io.hahn_software.emrs.dtos.PageDTO;
import io.hahn_software.emrs.services.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/departments")
@Tag(name = "Department Management", description = "APIs for managing departments")
public class DepartmentController {
    
    private DepartmentService departmentService;


    @Autowired
    DepartmentController(
        DepartmentService departmentService
    ) {
        this.departmentService = departmentService ;
    }



    @PostMapping
    @PreAuthorize("hasRole('ROLE_HR_Personnel') or hasRole('ROLE_Administrator')")
    @Operation(
        summary = "Create departments in batch",
        description = "Creates multiple departments in a single batch operation."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Departments created successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = DepartmentResponse.class, type = "array")
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input provided",
        content = @Content(schema = @Schema(hidden = true))
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(hidden = true))
    )
    public ResponseEntity<List<DepartmentResponse>> createDepartments(@RequestBody List<DepartmentRequest> departmentRequests) {

        List<DepartmentResponse> responses = departmentService.createDepartments(departmentRequests);

        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }






    @DeleteMapping("{departmentIds}")
    @PreAuthorize("hasRole('ROLE_HR_Personnel') or hasRole('ROLE_Administrator')")
    @Operation(
        summary = "Delete departments by IDs",
        description = "Deletes departments with the provided IDs."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Departments deleted successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = OperationResult.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input provided",
        content = @Content(schema = @Schema(hidden = true))
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(hidden = true))
    )
    public ResponseEntity<OperationResult> deleteDepartments(

        @Parameter(
            description = "Comma-separated list of department Ids to be deleted",
            required = true,
            example = "19887,18995",
            schema = @Schema(type = "string")
        )
        @PathVariable List<Long> departmentIds
    ) {
        OperationResult result = departmentService.deleteDepartments(departmentIds);
        return ResponseEntity.ok(result);
    }



    @GetMapping("/{departmentIds}")
    @PreAuthorize("hasRole('ROLE_HR_Personnel') or hasRole('ROLE_Administrator') or (hasRole('ROLE_Manager') and @departmentSecurityService.isManagerOfDepartment(#departmentId))")
    @Operation(
        summary = "Find departments by IDs",
        description = "Finds departments with the provided IDs."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Departments found successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = DepartmentResponse.class, type = "array")
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input provided",
        content = @Content(schema = @Schema(hidden = true))
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(hidden = true))
    )
    public ResponseEntity<List<DepartmentResponse>> findDepartments(

        @Parameter(
            description = "Comma-separated list of department IDs to be fetech",
            required = true,
            example = "1,2,3",
            schema = @Schema(type = "string")
        )
        @PathVariable List<Long> departmentIds

    ) {
        List<DepartmentResponse> departments = departmentService.findDepartments(departmentIds);
        return ResponseEntity.ok(departments);
    }




    @GetMapping
    @PreAuthorize("hasRole('ROLE_HR_Personnel') or hasRole('ROLE_Administrator') or (hasRole('ROLE_Manager') and @departmentSecurityService.isManagerOfDepartment(#departmentId))")
    @Operation(
        summary = "Get departments with pagination",
        description = "Retrieves departments with pagination."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Departments retrieved successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = DepartmentResponse.class, type = "array")
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input provided",
        content = @Content(schema = @Schema(hidden = true))
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(hidden = true))
    )
    public ResponseEntity<PageDTO<DepartmentResponse>> getDepartmentsWithPagination(
        @RequestParam int page,
        @RequestParam int pageSize
    ) {
        return ResponseEntity.ok(
            departmentService.getDepartmentsWithPagination(page, pageSize)
        );
    }
}
