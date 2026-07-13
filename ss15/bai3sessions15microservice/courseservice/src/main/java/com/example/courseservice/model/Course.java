package com.example.courseservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Long id;
    private String name;
    private String description;
    private String instructor;
    private Integer credits;
}