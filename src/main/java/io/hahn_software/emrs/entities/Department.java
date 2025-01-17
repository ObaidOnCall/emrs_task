package io.hahn_software.emrs.entities;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
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
    name = "departments" ,
    indexes = {
        @Index(columnList = "name" , name = "department_name_idx")
    }
)
@DynamicUpdate
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "department_seq")
    @SequenceGenerator(name = "department_seq" , sequenceName = "department_id_seq"  , allocationSize = 1)
    private Long id ;


    @Column(unique = true)
    private String name ;


    @OneToMany(mappedBy = "department")
    private List<Employee> employees ;

    @CreationTimestamp
    private Instant createdAt ;

    @UpdateTimestamp
    private Instant updatedAt ;

}
