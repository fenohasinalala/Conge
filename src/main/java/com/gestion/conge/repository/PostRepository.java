package com.gestion.conge.repository;

import com.gestion.conge.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("select s from Post s where s.name = ?1")
    Optional<Post> findPostByName(String name);
}
