package io.hahn_software.emrs.entities;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;

import io.hahn_software.emrs.enums.EmploymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data 
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "employees" ,
    indexes = {
        @Index(columnList = "employeeID" , name = "employee_employeeID_idx")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"fullName" , "employeeID"})
    }
)
@DynamicUpdate
public class Employee implements AbstractBaseEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq" , sequenceName = "employee_id_seq"  , allocationSize = 1)
    private Long id ;

    @Column(nullable = false)
    private String fullName ;


    @Column(unique = true , nullable = false)
    private Long employeeID ;

    @Column(nullable = false)
    private String jobTitle ;
    
    @Column(nullable = false)
    private EmploymentStatus employmentStatus ;


    @Column(nullable = false)
    private String address ;


    @Column(nullable = false)
    private String phone ;

    @Column(nullable = false)
    private String email ;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Department department ;


    @CreationTimestamp
    private Instant createdAt ;

    @UpdateTimestamp
    private Instant updatedAt ;

}
