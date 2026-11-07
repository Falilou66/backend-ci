package com.uadb.backend.resource.repository;

import com.uadb.backend.resource.entity.RessourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RessourceRepository extends JpaRepository<RessourceEntity, Long> {
    List<RessourceEntity> findByCoursIdOrderByDateCreationDesc(Long coursId);
}
