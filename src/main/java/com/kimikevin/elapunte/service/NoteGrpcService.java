package com.kimikevin.elapunte.service;

import com.kimikevin.elapunte.model.Note;
import com.kimikevin.elapunte.proto.*;
import com.kimikevin.elapunte.repository.NoteRepository;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

import java.time.Instant;
import java.util.List;

@GrpcService
public class NoteGrpcService extends NoteServiceGrpc.NoteServiceImplBase {

    private final NoteRepository noteRepository;

    public NoteGrpcService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public void createNote(CreateNoteRequest request, StreamObserver<NoteResponse> responseObserver) {
        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .formattedDate(Instant.now().toString())
                .build();

        Note savedNote = noteRepository.save(note);

        NoteResponse noteResponse = NoteResponse.newBuilder()
                .setId(savedNote.getId())
                .setTitle(savedNote.getTitle())
                .setContent(savedNote.getContent())
                .setFormattedDate(savedNote.getFormattedDate())
                .build();

        responseObserver.onNext(noteResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getNote(NoteIdRequest request, StreamObserver<NoteResponse> responseObserver) {
        Note note = noteRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Note not found"));

        NoteResponse noteResponse = NoteResponse.newBuilder()
                .setId(note.getId())
                .setTitle(note.getTitle())
                .setContent(note.getContent())
                .setFormattedDate(note.getFormattedDate())
                .build();

        responseObserver.onNext(noteResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllNotes(Empty request, StreamObserver<NoteListResponse> responseObserver) {
        List<Note> noteList = noteRepository.findAll();

        NoteListResponse.Builder noteListBuilder = NoteListResponse.newBuilder();
        noteList.forEach(note -> noteListBuilder.addNotes(
                NoteResponse.newBuilder()
                        .setId(note.getId())
                        .setTitle(note.getTitle())
                        .setContent(note.getContent())
                        .setFormattedDate(note.getFormattedDate())
                        .build())
        );

        responseObserver.onNext(noteListBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateNote(NoteResponse request, StreamObserver<NoteResponse> responseObserver) {
        Note existingNote = noteRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Note not found"));

        existingNote.setTitle(request.getTitle());
        existingNote.setContent(request.getContent());
        existingNote.setFormattedDate(Instant.now().toString());

        Note updatedNote = noteRepository.save(existingNote);

        NoteResponse noteResponse = NoteResponse.newBuilder()
                .setId(updatedNote.getId())
                .setTitle(updatedNote.getTitle())
                .setContent(updatedNote.getContent())
                .setFormattedDate(updatedNote.getFormattedDate())
                .build();

        responseObserver.onNext(noteResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteNote(NoteIdRequest request, StreamObserver<Empty> responseObserver) {
        noteRepository.deleteById(request.getId());

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
