package com.techtask.martianrobots.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Builder(toBuilder = true)
@Data
public final class Coordinate {

    @Range(min = 0, max = 50, message = "Parameter is out of range:[0,50]")
    private int x;

    @Range(min = 0, max = 50, message = "Parameter is out of range:[0,50]")
    private int y;


}
