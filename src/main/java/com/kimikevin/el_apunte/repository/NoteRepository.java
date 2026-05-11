package com.kimikevin.el_apunte.repository;

import com.kimikevin.el_apunte.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, String> {}
