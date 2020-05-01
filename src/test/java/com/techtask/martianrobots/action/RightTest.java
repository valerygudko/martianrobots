package com.techtask.martianrobots.action;

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
class RightTest {

    private Right rightAction = new Right();

    @ParameterizedTest
    @DisplayName("Turn right as expected")
    @CsvSource({"N, E", "S, W", "E, S", "W, N"})
    void execute_turnRightAsExpected(String initialOrientation, String finalOrientation){
        // when then
        assertThat(rightAction.execute(Position.builder().orientation(Orientation.valueOf(initialOrientation)).build(),
                Grid.builder().build()).getOrientation().name().contentEquals(finalOrientation));
    }

}