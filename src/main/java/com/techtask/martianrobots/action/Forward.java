package com.techtask.martianrobots.action;

import com.techtask.martianrobots.model.Grid;
import com.techtask.martianrobots.model.Position;

public class Forward implements Instruction {
    @Override
    public Position execute(Position position, Grid grid) {
        //TODO: add scent functionality, base logic and grid border check
                return position;
    }
}
