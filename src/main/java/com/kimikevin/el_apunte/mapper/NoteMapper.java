package com.kimikevin.el_apunte.mapper;

import com.kimikevin.el_apunte.model.Note;
import com.kimikevin.el_apunte.model.dto.CreateNoteRequest;
import com.kimikevin.el_apunte.model.dto.NoteResponse;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    public NoteResponse toResponse(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }

    public Note toEntity(CreateNoteRequest request) {
        return new Note(
                request.title(),
                request.content()
        );
    }
}
