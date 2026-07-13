package com.example.courseservice.service;

import com.example.courseservice.model.Course;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CourseService {

    // Danh sách tĩnh lưu trữ courses
    private final List<Course> courses = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public CourseService() {
        // Khởi tạo dữ liệu mẫu
        courses.add(new Course(idGenerator.getAndIncrement(),
                "Java Programming",
                "Learn Java from basics to advanced",
                "Dr. Smith",
                3));
        courses.add(new Course(idGenerator.getAndIncrement(),
                "Spring Boot Microservices",
                "Build microservices with Spring Boot",
                "Prof. Johnson",
                4));
        courses.add(new Course(idGenerator.getAndIncrement(),
                "Database Design",
                "Relational database design and SQL",
                "Dr. Williams",
                3));
    }

    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }

    public Course getCourseById(Long id) {
        return courses.stream()
                .filter(course -> course.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Course createCourse(Course course) {
        course.setId(idGenerator.getAndIncrement());
        courses.add(course);
        return course;
    }

    public Course updateCourse(Long id, Course courseDetails) {
        Course existingCourse = getCourseById(id);
        if (existingCourse != null) {
            existingCourse.setName(courseDetails.getName());
            existingCourse.setDescription(courseDetails.getDescription());
            existingCourse.setInstructor(courseDetails.getInstructor());
            existingCourse.setCredits(courseDetails.getCredits());
            return existingCourse;
        }
        return null;
    }

    public boolean deleteCourse(Long id) {
        return courses.removeIf(course -> course.getId().equals(id));
    }
}