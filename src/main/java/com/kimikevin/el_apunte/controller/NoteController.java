package com.kimikevin.el_apunte.controller;

import com.kimikevin.el_apunte.model.dto.CreateNoteRequest;
import com.kimikevin.el_apunte.model.dto.NoteResponse;
import com.kimikevin.el_apunte.model.dto.UpdateNoteRequest;
import com.kimikevin.el_apunte.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Returns 201 Created
    public NoteResponse createNote(
            @Valid @RequestBody CreateNoteRequest request,
            Authentication authentication
    ) {
        return noteService.createNote(request, authentication.getName());
    }

    @GetMapping
    public List<NoteResponse> getAllNotes(Authentication authentication) {
        // Defaults to returning 200 OK
        return noteService.getAllNotes(authentication.getName());
    }

    @GetMapping("/{id}")
    public NoteResponse getNoteById(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return noteService.getNoteById(id, authentication.getName());
    }

    @PutMapping("/{id}")
    public NoteResponse updateNote(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateNoteRequest request,
            Authentication authentication
    ) {
        return noteService.updateNote(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Returns 204 No Content
    public void deleteNote(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        noteService.deleteNote(id, authentication.getName());
    }
}
