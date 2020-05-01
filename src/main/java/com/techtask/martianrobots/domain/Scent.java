package com.techtask.martianrobots.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "scent")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Scent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int coordinateX;

    private int coordinateY;

    private String unsafeOrientation;
}
