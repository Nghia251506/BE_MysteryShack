package org.example.be_eproject_sem4.Entity;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "permission", nullable = false, length = 255)
    private String permission;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate = LocalDateTime.now();
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();
}
