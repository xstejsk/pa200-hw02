package com.example.sportsreservationsystembackend.service.impl;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.UserDelegationKey;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.BlobFile;
import com.example.sportsreservationsystembackend.repository.PhotoFileRepository;
import com.example.sportsreservationsystembackend.service.UserService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileExistsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlobFileService {

    private final PhotoFileRepository photoFileRepository;
    private final UserService userService;

    @Value("${azure.storage.url}")
    private String azureStorageUrl;

    @Value("${azure.storage.container-name}")
    private String azureStorageContainerName;

    private BlobContainerClient getBlobContainerClient() {
        DefaultAzureCredential credential = new DefaultAzureCredentialBuilder().build();

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .endpoint(azureStorageUrl)
            .credential(credential)
            .buildClient();

        return blobServiceClient.getBlobContainerClient(azureStorageContainerName);
    }

    @Transactional
    public BlobFile uploadPhoto(MultipartFile file, AppUser currentUser) throws IOException {

        String originalFileName = file.getOriginalFilename();
        String blobName = currentUser.getId() + "_" + originalFileName;
        Optional<BlobFile> fileByName = photoFileRepository.findByBlobName(blobName);
        if (fileByName.isPresent()) {
            throw new FileExistsException("File with the same name already exists for this user.");
        }
        String contentType = file.getContentType();

        BlobContainerClient containerClient = getBlobContainerClient();
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        blobClient.upload(file.getInputStream(), file.getSize(), true);

        BlobFile photoFile = new BlobFile();
        photoFile.setOwner(currentUser);
        photoFile.setFileName(originalFileName);
        photoFile.setBlobName(blobName);
        photoFile.setContentType(contentType);
        photoFile.setCreatedAt(LocalDateTime.now());

        return photoFileRepository.save(photoFile);
    }

    public List<BlobFile> getUserPhotos(AppUser currentUser) {
        return photoFileRepository.findByOwner(currentUser);
    }

    public String generateSasUrlForPhoto(String photoId, AppUser currentUser) {
        Optional<BlobFile> photoFileOptional = photoFileRepository.findByBlobNameAndOwnerId(photoId, currentUser.getId());
        if (photoFileOptional.isEmpty()) {
            throw new IllegalArgumentException("Photo not found or not owned by the current user.");
        }
        BlobFile photoFile = photoFileOptional.get();

        BlobContainerClient containerClient = getBlobContainerClient();
        BlobClient blobClient = containerClient.getBlobClient(photoFile.getBlobName());

        // Define SAS permissions (Read access for a limited time)
        BlobSasPermission sasPermission = new BlobSasPermission().setReadPermission(true);

        // Set SAS expiry time (e.g., 1 hour from now)
        OffsetDateTime expiryTime = OffsetDateTime.now(ZoneOffset.UTC).plusHours(1);

        // Fetch User Delegation Key (Azure AD-based authentication)
        BlobServiceClient blobServiceClient = containerClient.getServiceClient();
        UserDelegationKey userDelegationKey = blobServiceClient.getUserDelegationKey(OffsetDateTime.now(ZoneOffset.UTC),
            expiryTime);

        // Generate SAS token using the User Delegation Key
        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiryTime, sasPermission)
            .setStartTime(OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(5)); // Start a bit earlier to avoid clock skew

        String sasToken = blobClient.generateUserDelegationSas(sasValues, userDelegationKey);

        // Construct and return the full URL
        return blobClient.getBlobUrl() + "?" + sasToken;
    }


}
