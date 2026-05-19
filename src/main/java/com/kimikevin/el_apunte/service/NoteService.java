package com.kimikevin.el_apunte.service;

import com.kimikevin.el_apunte.mapper.NoteMapper;
import com.kimikevin.el_apunte.model.Note;
import com.kimikevin.el_apunte.model.dto.CreateNoteRequest;
import com.kimikevin.el_apunte.model.dto.NoteResponse;
import com.kimikevin.el_apunte.model.dto.UpdateNoteRequest;
import com.kimikevin.el_apunte.repository.NoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    public NoteService(NoteRepository noteRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
    }

    @Transactional
    public NoteResponse createNote(CreateNoteRequest request){
        Note note = noteMapper.toEntity(request);
        Note savedNote = noteRepository.save(note);
        return noteMapper.toResponse(savedNote);
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getAllNotes() {
        return noteRepository.findAll().stream()
                .map(noteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NoteResponse getNoteById(UUID id) {
        Note note = findNoteOrThrow(id);
        return noteMapper.toResponse(note);
    }

    @Transactional
    public NoteResponse updateNote(UUID id, UpdateNoteRequest request) {
        Note note = findNoteOrThrow(id);

        // Update the existing entity
        note.setTitle(request.title());
        note.setContent(request.content());

        // Save and return
        Note updatedNote = noteRepository.save(note);
        return noteMapper.toResponse(updatedNote);
    }

    @Transactional
    public void deleteNote(UUID id) {
        Note note = findNoteOrThrow(id);
        noteRepository.delete(note);
    }

    // Helper method to keep code DRY
    private Note findNoteOrThrow(UUID id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Note not found with id: " + id
                ));
    }

}
