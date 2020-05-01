package com.techtask.martianrobots.action;

import com.techtask.martianrobots.model.Coordinate;
import com.techtask.martianrobots.model.Grid;
import com.techtask.martianrobots.model.Orientation;
import com.techtask.martianrobots.model.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ForwardTest {

    private Forward forwardAction = new Forward();

    @ParameterizedTest
    @DisplayName("Move forward within the grid as expected")
    @CsvSource({"1, 1, E, 2, 1", "1, 1, S, 1, 0", "1, 1, W, 0, 1", "1, 1, N, 1, 2"})
    void execute_moveForwardAsExpected(String startX, String startY, String orientation, String finalX, String finalY){
        // given
        Position startPosition = Position.builder().orientation(Orientation.valueOf(orientation))
                .coordinates(Coordinate.builder().x(Integer.parseInt(startX)).y(Integer.parseInt(startY)).build()).build();
        Position finalPosition = Position.builder().orientation(Orientation.valueOf(orientation))
                .coordinates(Coordinate.builder().x(Integer.parseInt(finalX)).y(Integer.parseInt(finalY)).build()).build();
        Grid grid = Grid.builder().coordinates(Coordinate.builder().x(2).y(2).build()).build();
        // when then
        assertThat(forwardAction.execute(startPosition, grid)).isEqualToComparingFieldByField(finalPosition);
    }

    @ParameterizedTest
    @DisplayName("Move forward off the edge results in lost robot")
    @CsvSource({"2, 2, E, 2, 2", "2, 2, N, 2, 2", "0, 0, W, 0, 0", "0, 0, S, 0, 0"})
    void execute_moveForwardOffTheEdge(String startX, String startY, String orientation, String finalX, String finalY){
        // given
        Position startPosition = Position.builder().orientation(Orientation.valueOf(orientation))
                .coordinates(Coordinate.builder().x(Integer.parseInt(startX)).y(Integer.parseInt(startY)).build()).build();
        Position finalPosition = Position.builder().isLost(Boolean.TRUE).orientation(Orientation.valueOf(orientation))
                .coordinates(Coordinate.builder().x(Integer.parseInt(finalX)).y(Integer.parseInt(finalY)).build()).build();
        Grid grid = Grid.builder().coordinates(Coordinate.builder().x(2).y(2).build()).build();
        // when then
        assertThat(forwardAction.execute(startPosition, grid)).isEqualToComparingFieldByField(finalPosition);
    }

}