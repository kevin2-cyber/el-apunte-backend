package com.kimikevin.elapunte.model;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Note {
    @Id
    private String id;
    private String title;
    private String content;
    private String formattedDate;
}
