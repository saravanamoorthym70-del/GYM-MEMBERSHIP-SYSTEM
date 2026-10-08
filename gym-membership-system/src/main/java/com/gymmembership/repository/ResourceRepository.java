package com.gymmembership.repository;

import com.gymmembership.entity.Resource;
import com.gymmembership.entity.enums.ResourceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, String> {
    List<Resource> findByStatus(ResourceStatus status);
}
