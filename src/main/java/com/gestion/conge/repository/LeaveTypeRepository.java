package com.gestion.conge.repository;

import com.gestion.conge.model.LeaveType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType,Long> {

    List<LeaveType> findByTypeContainingIgnoreCaseAndDescriptionContainingIgnoreCase(String type, String description, Pageable pageable);


    Optional<LeaveType>  findLeaveByType(String type);
}
