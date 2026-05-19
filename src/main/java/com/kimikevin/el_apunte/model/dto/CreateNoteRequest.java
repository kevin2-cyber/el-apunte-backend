package com.kimikevin.el_apunte.model.dto;

import jakarta.validation.constraints.NotBlank;

// Request received from the Android client to create a note
public record CreateNoteRequest(
        @NotBlank(message = "Title is required") String title,
        String content
) {}
