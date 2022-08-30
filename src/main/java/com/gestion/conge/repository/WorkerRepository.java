package com.gestion.conge.repository;

import com.gestion.conge.model.Worker;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<Worker,Long> {
    List<Worker> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName, Pageable pageable);

    Optional<Worker> findWorkerByEmail(String email);
    Optional<Worker> findWorkerByPhone(String phone);
    Optional<Worker> findWorkerByCin(String cin);
}
