package com.techtask.martianrobots.action;

import com.techtask.martianrobots.model.Grid;
import com.techtask.martianrobots.model.Position;

public interface Instruction {
    Position execute(Position position, Grid grid);
}
