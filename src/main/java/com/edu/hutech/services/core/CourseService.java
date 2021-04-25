package com.edu.hutech.services.core;

import com.edu.hutech.entities.Course;

import org.springframework.data.domain.Page;

import java.util.List;


public interface CourseService extends IService<Course> {
    Page<Course> findPaginated(int pageNo, int pageSize, String sortField);
    Page<Course> findPaginatedDesc(int pageNo, int pageSize, String sortField);
    Page<Course> findPaginated(int pageNo, int pageSize);

    List<Course> searchByKeyword(String keyword);

}
