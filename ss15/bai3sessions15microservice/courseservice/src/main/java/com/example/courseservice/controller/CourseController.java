package com.example.courseservice.controller;

import com.example.courseservice.model.Course;
import com.example.courseservice.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // GET /api/courses - Cho phép STUDENT và INSTRUCTOR
    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // GET /api/courses/{id} - Cho phép STUDENT và INSTRUCTOR
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STUDENT') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        if (course != null) {
            return ResponseEntity.ok(course);
        }
        return ResponseEntity.notFound().build();
    }

    // POST /api/courses - CHỈ CHO PHÉP INSTRUCTOR
    @PostMapping
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Course createdCourse = courseService.createCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
    }

    // PUT /api/courses/{id} - CHỈ CHO PHÉP INSTRUCTOR
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        Course updatedCourse = courseService.updateCourse(id, course);
        if (updatedCourse != null) {
            return ResponseEntity.ok(updatedCourse);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /api/courses/{id} - CHỈ CHO PHÉP INSTRUCTOR
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        if (courseService.deleteCourse(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}