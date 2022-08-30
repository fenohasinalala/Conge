package com.gestion.conge.repository;

import com.gestion.conge.model.Leave;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRepository extends JpaRepository<Leave,Long> {

    List<Leave> findByTypeContainingIgnoreCaseAndDescriptionContainingIgnoreCase(String type, String description, Pageable pageable);


    Optional<Leave>  findLeaveByType(String type);
}
