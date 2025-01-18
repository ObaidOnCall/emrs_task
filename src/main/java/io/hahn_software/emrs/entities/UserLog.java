package io.hahn_software.emrs.entities;

import java.time.Instant;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
    name = "user_logs" ,
    indexes = {
        @Index(columnList = "userId" , name = "user_logs_userId_idx")
    }
)
public class UserLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;




    private String userId ;

    private String username;

    private String operation;

    private String method;

    private String ipAddress;

    private String status; 


    @CreationTimestamp
    private Instant createdAt ;

    @UpdateTimestamp
    private Instant updatedAt ;
    
}
