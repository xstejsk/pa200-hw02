package com.example.sportsreservationsystembackend.repository;

import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.BlobFile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PhotoFileRepository extends JpaRepository<BlobFile, String> {
  List<BlobFile> findByOwner(AppUser owner);
  Optional<BlobFile> findByBlobNameAndOwnerId(String blobName, String ownerId);
  Optional<BlobFile> findByBlobName(String blobName);
}