package com.techtask.martianrobots.action;

import com.techtask.martianrobots.model.Position;

import static com.techtask.martianrobots.model.Orientation.*;

public class Right implements Instruction {
    @Override
    public Position execute(Position position) {
        switch (position.getOrientation()){
            case N:
                System.out.println("position now E");
                return new Position(position.getCoordinates(), E);
            case S:
                System.out.println("position now W");
                return new Position(position.getCoordinates(), W);
            case E:
                System.out.println("position now S");
                return new Position(position.getCoordinates(), S);
            case W:
                System.out.println("position now N");
                return new Position(position.getCoordinates(), N);
            default:
                return position;
        }
    }
}
