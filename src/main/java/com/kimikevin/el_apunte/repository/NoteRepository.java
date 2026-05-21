package com.kimikevin.el_apunte.repository;

import com.kimikevin.el_apunte.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {
    List<Note> findAllByUserUsername(String username);

    Optional<Note> findByIdAndUserUsername(UUID id, String username);
}
