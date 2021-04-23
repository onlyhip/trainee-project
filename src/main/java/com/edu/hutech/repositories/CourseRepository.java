package com.edu.hutech.repositories;

import com.edu.hutech.entities.Course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    /**
     * find Course (class) by id of class
     * @param id
     * @return
     */
    public Course findById(int id);

    /**
     * find the course by name of course
     * @param name is the name of course
     * @return Course object or null
     */
    Course findCourseByName(String name);
}
