package com.techtask.martianrobots.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@AllArgsConstructor
public final class Coordinate {

    @Min(-50)
    @Max(50)
    private int x;

    @Min(-50)
    @Max(50)
    private int y;

}
