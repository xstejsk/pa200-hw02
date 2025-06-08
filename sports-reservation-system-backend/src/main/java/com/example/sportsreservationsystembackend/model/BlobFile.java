package com.example.sportsreservationsystembackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotNull;
import lombok.ToString;

/**
 * This class represents reservation entity
 * @author Radim Stejskal
 */

@Entity
@Table(name = "blob_file")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BlobFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name="owner_id")
    @NotNull
    private AppUser owner;

    @NotNull
    @Column(unique = true)
    private String fileName;

    @NotNull
    @Column(unique = true)
    private String blobName;

    @NotNull
    private String contentType;

    @NotNull
    private LocalDateTime createdAt;
}
