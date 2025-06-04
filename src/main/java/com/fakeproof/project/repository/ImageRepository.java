package com.fakeproof.project.repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fakeproof.project.model.ImageRecord;

public interface ImageRepository extends JpaRepository<ImageRecord, UUID> {
    Optional<ImageRecord> findByHash(byte[] hash);
    
    @Query("SELECT i FROM ImageRecord i WHERE i.visualHash LIKE ?1%")
    List<ImageRecord> findVisuallySimilarCandidates(String partialHash);

}