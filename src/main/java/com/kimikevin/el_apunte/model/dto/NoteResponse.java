package com.kimikevin.el_apunte.model.dto;

import java.time.Instant;
import java.util.UUID;

// Response sent to the Android client
public record NoteResponse(
        UUID id,
        String title,
        String content,
        Instant createdAt,
        Instant lastUpdated
) {}
