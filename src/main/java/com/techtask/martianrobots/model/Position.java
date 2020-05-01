package com.techtask.martianrobots.model;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public final class Position {
    private Coordinate coordinates;
    private Orientation orientation;
}
