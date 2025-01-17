package io.hahn_software.emrs.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hahn_software.emrs.dtos.DepartmentRequest;
import io.hahn_software.emrs.dtos.DepartmentResponse;
import io.hahn_software.emrs.services.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
}
