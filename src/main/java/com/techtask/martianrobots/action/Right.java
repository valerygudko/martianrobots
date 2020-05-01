package com.techtask.martianrobots.action;

import com.techtask.martianrobots.model.Grid;
import com.techtask.martianrobots.model.Position;

import static com.techtask.martianrobots.model.Orientation.*;

public class Right implements Instruction {
    @Override
    public Position execute(Position position, Grid grid) {
        switch (position.getOrientation()){
            case N:
                position = position.toBuilder().orientation(E).build();
                break;
            case S:
                position = position.toBuilder().orientation(W).build();
                break;
            case E:
                position = position.toBuilder().orientation(S).build();
                break;
            case W:
                position = position.toBuilder().orientation(N).build();
                break;
        }
        return position;
    }
}
