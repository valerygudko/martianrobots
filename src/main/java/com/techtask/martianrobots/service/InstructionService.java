package com.techtask.martianrobots.service;

import com.techtask.martianrobots.model.Grid;
import com.techtask.martianrobots.model.Position;

public interface InstructionService {
    Position process(Grid grid, Position startPosition, char[] instructions);
}
