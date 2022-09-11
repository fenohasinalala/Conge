package com.gestion.conge.repository;

import com.gestion.conge.model.LeaveTaken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveTakenRepository extends JpaRepository<LeaveTaken,Long> {
    List<LeaveTaken> findByWorkerId(Long id);
}
