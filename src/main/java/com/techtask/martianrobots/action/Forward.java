package com.techtask.martianrobots.action;

import com.techtask.martianrobots.model.Grid;
import com.techtask.martianrobots.model.Orientation;
import com.techtask.martianrobots.model.Position;

public class Forward implements Instruction {

    @Override
    public Position execute(Position position, Grid grid) {
        if(position.getOrientation() == Orientation.E && position.getCoordinates().getX() + 1 <= grid.getCoordinates().getX()){
                return position.toBuilder()
                        .coordinates(position
                                .getCoordinates()
                                .toBuilder()
                                .x(position.getCoordinates().getX() + 1)
                                .build())
                        .build();

        } else if (position.getOrientation() == Orientation.S && position.getCoordinates().getY() - 1 >= 0) {
                return position.toBuilder()
                        .coordinates(position
                                .getCoordinates()
                                .toBuilder()
                                .y(position.getCoordinates().getY() - 1)
                                .build())
                        .build();
        } else if (position.getOrientation() == Orientation.W && position.getCoordinates().getX() - 1 >= 0){
            return position.toBuilder()
                    .coordinates(position
                            .getCoordinates()
                            .toBuilder()
                            .x(position.getCoordinates().getX() - 1)
                            .build())
                    .build();
        } else if (position.getOrientation() == Orientation.N && position.getCoordinates().getY() + 1 <= grid.getCoordinates().getY()){
            return position.toBuilder()
                    .coordinates(position
                            .getCoordinates()
                            .toBuilder()
                            .y(position.getCoordinates().getY() + 1)
                            .build())
                    .build();
        } else {
            return position.toBuilder().isLost(Boolean.TRUE).build();
        }
    }

}
