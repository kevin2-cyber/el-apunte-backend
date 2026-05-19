package com.kimikevin.el_apunte.model.dto;

import jakarta.validation.constraints.NotBlank;

// Request received from the Android client to update a note
public record UpdateNoteRequest(
        @NotBlank(message = "Title is required") String title,
        String content
) {}
