package com.kimikevin.elapunte.service;

import com.kimikevin.elapunte.model.Note;
import com.kimikevin.elapunte.proto.*;
import com.kimikevin.elapunte.repository.NoteRepository;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NoteGrpcServiceTest {

    private NoteGrpcService noteGrpcService;

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private StreamObserver<NoteResponse> noteResponseObserver;

    @Mock
    private StreamObserver<NoteListResponse> noteListResponseObserver;

    @Mock
    private StreamObserver<Empty> emptyResponseObserver;

    String testId = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        noteGrpcService = new NoteGrpcService(noteRepository);
    }

    @Test
    void testCreateNote() {
        // Arrange
        CreateNoteRequest request = CreateNoteRequest.newBuilder()
                .setId(testId)
                .setTitle("Test Note")
                .setContent("Test Content")
                .build();

        Note savedNote = Note.builder()
                .id(testId)
                .title("Test Note")
                .content("Test Content")
                .formattedDate("2024-01-01T00:00:00Z")
                .build();

        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);

        // Act
        noteGrpcService.createNote(request, noteResponseObserver);

        // Assert
        ArgumentCaptor<NoteResponse> responseCaptor = ArgumentCaptor.forClass(NoteResponse.class);
        verify(noteResponseObserver).onNext(responseCaptor.capture());
        verify(noteResponseObserver).onCompleted();

        NoteResponse response = responseCaptor.getValue();
        assertEquals(testId, response.getId());
        assertEquals("Test Note", response.getTitle());
        assertEquals("Test Content", response.getContent());
        assertNotNull(response.getFormattedDate());
    }

    @Test
    void testGetNote() {
        String testGetId = UUID.randomUUID().toString();
        // Arrange
        Note note = Note.builder()
                .id(testGetId)
                .title("Test Note")
                .content("Test Content")
                .formattedDate("2024-01-01T00:00:00Z")
                .build();

        when(noteRepository.findById(testGetId)).thenReturn(Optional.of(note));

        NoteIdRequest request = NoteIdRequest.newBuilder()
                .setId(note.getId())
                .build();

        // Act
        noteGrpcService.getNote(request, noteResponseObserver);

        // Assert
        ArgumentCaptor<NoteResponse> responseCaptor = ArgumentCaptor.forClass(NoteResponse.class);
        verify(noteResponseObserver).onNext(responseCaptor.capture());
        verify(noteResponseObserver).onCompleted();

        NoteResponse response = responseCaptor.getValue();
        assertEquals(testGetId, response.getId());
        assertEquals("Test Note", response.getTitle());
        assertEquals("Test Content", response.getContent());
    }

    @Test
    void testGetAllNotes() {
        // Arrange
        List<Note> notes = Arrays.asList(
                Note.builder().id(UUID.randomUUID().toString()).title("Note 1").content("Content 1").formattedDate("2024-01-01T00:00:00Z").build(),
                Note.builder().id(UUID.randomUUID().toString()).title("Note 2").content("Content 2").formattedDate("2024-01-02T00:00:00Z").build()
        );

        when(noteRepository.findAll()).thenReturn(notes);

        Empty request = Empty.newBuilder().build();

        // Act
        noteGrpcService.getAllNotes(request, noteListResponseObserver);

        // Assert
        ArgumentCaptor<NoteListResponse> responseCaptor = ArgumentCaptor.forClass(NoteListResponse.class);
        verify(noteListResponseObserver).onNext(responseCaptor.capture());
        verify(noteListResponseObserver).onCompleted();

        NoteListResponse response = responseCaptor.getValue();
        assertEquals(2, response.getNotesCount());
        assertEquals("Note 1", response.getNotes(0).getTitle());
        assertEquals("Note 2", response.getNotes(1).getTitle());
    }

    @Test
    void testUpdateNote() {
        String testUpdateId = UUID.randomUUID().toString();
        // Arrange
        Note existingNote = Note.builder()
                .id(testUpdateId)
                .title("Old Title")
                .content("Old Content")
                .formattedDate("2024-01-01T00:00:00Z")
                .build();

        Note updatedNote = Note.builder()
                .id(testUpdateId)
                .title("Updated Title")
                .content("Updated Content")
                .formattedDate("2024-01-02T00:00:00Z")
                .build();

        when(noteRepository.findById(testUpdateId)).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(any(Note.class))).thenReturn(updatedNote);

        NoteResponse request = NoteResponse.newBuilder()
                .setId(updatedNote.getId())
                .setTitle("Updated Title")
                .setContent("Updated Content")
                .build();

        // Act
        noteGrpcService.updateNote(request, noteResponseObserver);

        // Assert
        ArgumentCaptor<NoteResponse> responseCaptor = ArgumentCaptor.forClass(NoteResponse.class);
        verify(noteResponseObserver).onNext(responseCaptor.capture());
        verify(noteResponseObserver).onCompleted();

        NoteResponse response = responseCaptor.getValue();
        assertEquals(updatedNote.getId(), response.getId());
        assertEquals("Updated Title", response.getTitle());
        assertEquals("Updated Content", response.getContent());
    }

    @Test
    void testDeleteNote() {
        String testDeleteId = UUID.randomUUID().toString();
        // Arrange
        NoteIdRequest request = NoteIdRequest.newBuilder()
                .setId(testDeleteId)
                .build();

        doNothing().when(noteRepository).deleteById(request.getId());

        // Act
        noteGrpcService.deleteNote(request, emptyResponseObserver);

        // Assert
        verify(noteRepository).deleteById(request.getId());
        verify(emptyResponseObserver).onNext(any(Empty.class));
        verify(emptyResponseObserver).onCompleted();
    }

    @Test
    void testGetNoteNotFound() {
        // Arrange
        String randomId = UUID.randomUUID().toString();
        when(noteRepository.findById(randomId)).thenReturn(Optional.empty());

        NoteIdRequest request = NoteIdRequest.newBuilder()
                .setId(randomId)
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            noteGrpcService.getNote(request, noteResponseObserver);
        });
    }

    @Test
    void testUpdateNoteNotFound() {
        // Arrange
        String randomId = UUID.randomUUID().toString();
        when(noteRepository.findById(randomId)).thenReturn(Optional.empty());

        NoteResponse request = NoteResponse.newBuilder()
                .setId(randomId)
                .setTitle("Title")
                .setContent("Content")
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            noteGrpcService.updateNote(request, noteResponseObserver);
        });
    }
}
