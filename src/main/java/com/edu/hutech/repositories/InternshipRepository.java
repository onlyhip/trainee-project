package com.edu.hutech.repositories;

import com.edu.hutech.entities.Internship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Integer> {

}
