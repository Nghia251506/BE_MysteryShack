package org.example.be_eproject_sem4.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "topics")
@Data
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

      @Column(length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}