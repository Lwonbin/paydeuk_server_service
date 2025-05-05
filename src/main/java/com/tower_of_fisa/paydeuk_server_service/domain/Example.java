package com.tower_of_fisa.paydeuk_server_service.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "EXAMPLE")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Example {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EXAM_ID")
    private Long examId;

    @Column(name = "NAME", nullable = false)
    private String name;
}
