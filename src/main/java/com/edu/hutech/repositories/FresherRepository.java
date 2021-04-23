package com.edu.hutech.repositories;

import com.edu.hutech.entities.Fresher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FresherRepository extends JpaRepository<Fresher, Integer>{

}
