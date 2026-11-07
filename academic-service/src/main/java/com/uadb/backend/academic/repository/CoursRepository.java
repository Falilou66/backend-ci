package com.uadb.backend.academic.repository;

import com.uadb.backend.academic.entity.CoursEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursRepository extends JpaRepository<CoursEntity, Long> {
}
