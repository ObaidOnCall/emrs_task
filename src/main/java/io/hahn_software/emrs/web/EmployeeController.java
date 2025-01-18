package io.hahn_software.emrs.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.hahn_software.emrs.dtos.EmployeeRequest;
import io.hahn_software.emrs.dtos.EmployeeResponse;
import io.hahn_software.emrs.dtos.OperationResult;
import io.hahn_software.emrs.dtos.PageDTO;
import io.hahn_software.emrs.dtos.interfaces.CreateValidationGroup;
import io.hahn_software.emrs.entities.Employee;
import io.hahn_software.emrs.services.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/employees")
@Validated
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController {
    

    private final EmployeeService employeeService;

    @Autowired
    EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }



    @PostMapping
    @PreAuthorize("hasRole('ROLE_HR_Personnel') or hasRole('ROLE_Administrator')")
    @Validated(CreateValidationGroup.class)
    @Operation(
        summary = "Create employees in batch",
        description = "Creates multiple employees in a single batch operation."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Employees created successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = EmployeeResponse.class, type = "array")
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
    public ResponseEntity<List<EmployeeResponse>> createEmployees(
        @RequestBody @Valid List<EmployeeRequest> employeeRequests
    ) {
        List<EmployeeResponse> responses = employeeService.createEmployeesInBatch(employeeRequests);
        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }



    @DeleteMapping("/{employeeIds}")
    @PreAuthorize("hasRole('ROLE_HR_Personnel') or hasRole('ROLE_Administrator')")
    @Operation(
        summary = "Delete employees by IDs",
        description = "Deletes employees with the provided IDs."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Employees deleted successfully",
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
    public ResponseEntity<OperationResult> deleteEmployees(
        @Parameter(
            description = "Comma-separated list of employee IDs to be deleted",
            required = true,
            example = "1,2,3",
            schema = @Schema(type = "string")
        )
        @PathVariable List<Long> employeeIds
    ) {
        OperationResult result = employeeService.deleteEmployees(employeeIds);
        return ResponseEntity.ok(result);
    }





    @GetMapping("/{employeeIds}")
    @PreAuthorize("hasRole('ROLE_HR_Personnel') or hasRole('ROLE_Administrator') or (hasRole('ROLE_Manager') and @employeeSecurityService.isManagerOfEmployee(#employeeId))")
    @Operation(
        summary = "Find employees by IDs",
        description = "Finds employees with the provided IDs."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Employees found successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = EmployeeResponse.class, type = "array")
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
    public ResponseEntity<List<EmployeeResponse>> findEmployees(
        @Parameter(
            description = "Comma-separated list of employee IDs to be fetched",
            required = true,
            example = "1,2,3",
            schema = @Schema(type = "string")
        )
        @PathVariable List<Long> employeeIds
    ) {
        List<EmployeeResponse> employees = employeeService.findEmployees(employeeIds);
        return ResponseEntity.ok(employees);
    }


    @GetMapping
    @PreAuthorize("hasRole('ROLE_HR_Personnel') or hasRole('ROLE_Administrator') or (hasRole('ROLE_Manager') and @employeeSecurityService.isManagerOfEmployee(#employeeId))")
    @Operation(
        summary = "Get employees with pagination",
        description = "Retrieves employees with pagination."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Employees retrieved successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = EmployeeResponse.class, type = "array")
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
    public ResponseEntity<PageDTO<EmployeeResponse>> getEmployeesWithPagination(
        @RequestParam int page,
        @RequestParam int pageSize
    ) {
        PageDTO<EmployeeResponse> employees = employeeService.getEmployeesWithPagination(page, pageSize);

        return ResponseEntity.ok(employees);
    }



    @PutMapping
    @PreAuthorize("hasRole('ROLE_HR_Personnel') or hasRole('ROLE_Administrator')")
    @Operation(
        summary = "Update employees in batch",
        description = "Updates multiple employees with the provided IDs using the fields from the request body."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Employees updated successfully",
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
    public ResponseEntity<OperationResult> updateEmployeesInBatch(
        @Parameter(
            description = "Comma-separated list of employee IDs to be updated",
            required = true,
            example = "1,2,3",
            schema = @Schema(type = "string")
        )
        @RequestParam List<Long> employeeIds,
        @Parameter(
            description = "Employee object containing the fields to update",
            required = true
        )
        @Valid @RequestBody Employee employee
    ) {
        OperationResult result = employeeService.updateEmployeesInBatch(employeeIds, employee);
        return ResponseEntity.ok(result);
    }
}
