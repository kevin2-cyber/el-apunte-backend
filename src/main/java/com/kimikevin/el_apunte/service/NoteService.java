package com.kimikevin.el_apunte.service;

import com.kimikevin.el_apunte.auth.AppUser;
import com.kimikevin.el_apunte.auth.AppUserRepository;
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
    private final AppUserRepository userRepository;

    public NoteService(
            NoteRepository noteRepository,
            NoteMapper noteMapper,
            AppUserRepository userRepository
            ) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public NoteResponse createNote(CreateNoteRequest request, String username){
        AppUser user = findUserOrThrow(username);

        Note note = noteMapper.toEntity(request);
        note.setUser(user);

        Note savedNote = noteRepository.save(note);
        return noteMapper.toResponse(savedNote);
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getAllNotes(String username) {
        return noteRepository.findAllByUserUsername(username).stream()
                .map(noteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NoteResponse getNoteById(UUID id, String username) {
        Note note = findNoteOrThrow(id, username);
        return noteMapper.toResponse(note);
    }

    @Transactional
    public NoteResponse updateNote(UUID id, UpdateNoteRequest request, String username) {
        Note note = findNoteOrThrow(id, username);

        // Update the existing entity
        note.setTitle(request.title());
        note.setContent(request.content());

        // Save and return
        Note updatedNote = noteRepository.save(note);
        return noteMapper.toResponse(updatedNote);
    }

    @Transactional
    public void deleteNote(UUID id, String username) {
        Note note = findNoteOrThrow(id, username);
        noteRepository.delete(note);
    }

    // Helper method to keep code DRY
    private Note findNoteOrThrow(UUID id, String username) {
        return noteRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Note not found with id: " + id
                ));
    }

    private AppUser findUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Authenticated user not found"
                ));
    }

}
