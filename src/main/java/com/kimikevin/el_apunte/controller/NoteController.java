package com.kimikevin.el_apunte.controller;

import com.kimikevin.el_apunte.model.dto.CreateNoteRequest;
import com.kimikevin.el_apunte.model.dto.NoteResponse;
import com.kimikevin.el_apunte.model.dto.UpdateNoteRequest;
import com.kimikevin.el_apunte.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public NoteResponse createNote(@Valid @RequestBody CreateNoteRequest request) {
        return noteService.createNote(request);
    }

    @GetMapping
    public List<NoteResponse> getAllNotes() {
        // Defaults to returning 200 OK
        return noteService.getAllNotes();
    }

    @GetMapping("/{id}")
    public NoteResponse getNoteById(@PathVariable UUID id) {
        return noteService.getNoteById(id);
    }

    @PutMapping("/{id}")
    public NoteResponse updateNote(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateNoteRequest request) {
        return noteService.updateNote(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Returns 204 No Content
    public void deleteNote(@PathVariable UUID id) {
        noteService.deleteNote(id);
    }
}
