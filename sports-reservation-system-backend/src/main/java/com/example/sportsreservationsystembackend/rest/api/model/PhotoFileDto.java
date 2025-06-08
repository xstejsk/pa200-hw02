package com.example.sportsreservationsystembackend.rest.api.model;

import java.time.LocalDateTime;

public record PhotoFileDto(LocalDateTime createdAt, String fileName, String blobName, String contentType) {

}

