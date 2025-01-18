package io.hahn_software.emrs.dtos;

import io.hahn_software.emrs.dtos.interfaces.CreateValidationGroup;
import io.hahn_software.emrs.dtos.interfaces.UpdateValidationGroup;
import io.hahn_software.emrs.enums.EmploymentStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {


    @NotNull(groups = CreateValidationGroup.class, message = "Full name is required for create operation")
    @NotBlank(groups = {CreateValidationGroup.class, UpdateValidationGroup.class}, message = "Full name is required")
    private String fullName;

    @NotNull(groups = CreateValidationGroup.class, message = "Employee ID is required for create operation")
    @Null(groups = UpdateValidationGroup.class, message = "Employee ID cannot be updated")
    private Long employeeID;

    @NotNull(groups = CreateValidationGroup.class, message = "Job title is required for create operation")
    @NotBlank(groups = {CreateValidationGroup.class, UpdateValidationGroup.class}, message = "Job title is required")
    private String jobTitle;

    @NotNull(groups = {CreateValidationGroup.class}, message = "Employment status is required")
    private EmploymentStatus employmentStatus;

    @NotBlank(groups = {CreateValidationGroup.class, UpdateValidationGroup.class}, message = "Address is required")
    private String address;

    @NotBlank(groups = {CreateValidationGroup.class, UpdateValidationGroup.class}, message = "Phone number is required")
    private String phone;

    @NotBlank(groups = {CreateValidationGroup.class, UpdateValidationGroup.class}, message = "Email is required")
    @Email(groups = {CreateValidationGroup.class, UpdateValidationGroup.class}, message = "Email should be valid")
    private String email;


    @NotNull(groups = {CreateValidationGroup.class}, message = "Department ID is required for create operation")
    @NotBlank(groups = {CreateValidationGroup.class , UpdateValidationGroup.class}, message = "Department ID is required")
    private Long departmentId;
    
}
