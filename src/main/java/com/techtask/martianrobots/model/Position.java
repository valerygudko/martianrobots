package com.techtask.martianrobots.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class Position {
    private Coordinate coordinates;
    private Orientation orientation;
}
