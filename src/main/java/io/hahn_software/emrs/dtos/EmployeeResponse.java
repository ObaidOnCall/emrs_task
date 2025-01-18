package io.hahn_software.emrs.dtos;

import java.time.Instant;

import io.hahn_software.emrs.enums.EmploymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {

    private Long id;

    private String fullName;

    private Long employeeID;

    private String jobTitle;

    private EmploymentStatus employmentStatus;

    private String address;

    private String phone;

    private String email;

    private Long department;

    private Instant createdAt;

    private Instant updatedAt;
}
