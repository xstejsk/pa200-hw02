// src/main/java/com/example/sportsreservationsystembackend/controller/PhotoController.java
package com.example.sportsreservationsystembackend.rest.api;

import com.example.sportsreservationsystembackend.model.AppUser;
import com.example.sportsreservationsystembackend.model.BlobFile;
import com.example.sportsreservationsystembackend.rest.api.model.PhotoFileDto;
import com.example.sportsreservationsystembackend.service.impl.BlobFileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class BlobFileController {

    private final BlobFileService bloblFileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file,
                                                 @AuthenticationPrincipal AppUser currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            BlobFile uploadedPhoto = bloblFileService.uploadPhoto(file, currentUser);
            return ResponseEntity.status(HttpStatus.OK).body("ok");
        } catch (FileExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PhotoFileDto>> getUserPhotos(@AuthenticationPrincipal AppUser currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<BlobFile> photos = bloblFileService.getUserPhotos(currentUser);
        return ResponseEntity.ok(photos.stream().map(x -> new PhotoFileDto(x.getCreatedAt(), x.getFileName(),
            x.getBlobName(), x.getContentType())).toList());
    }

    @GetMapping("/{photoId}/sas-url")
    public ResponseEntity<String> getPhotoSasUrl(@PathVariable String photoId,
        @AuthenticationPrincipal AppUser currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String sasUrl = bloblFileService.generateSasUrlForPhoto(photoId, currentUser);
        return ResponseEntity.ok(sasUrl);
    }
}