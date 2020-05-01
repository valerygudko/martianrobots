package com.techtask.martianrobots.service;

import com.techtask.martianrobots.model.Coordinate;
import com.techtask.martianrobots.model.Grid;
import com.techtask.martianrobots.model.Orientation;
import com.techtask.martianrobots.model.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class InstructionServiceImplTest {

    private InstructionServiceImpl testObj = new InstructionServiceImpl();

    @Test
    @DisplayName("List of instructions get processed correctly")
    void process_validInputGridStartPositionInstructions_validOutputPosition(){
        //given
        Grid grid = Grid.builder().coordinates(
                Coordinate
                        .builder()
                        .x(2)
                        .y(2)
                        .build())
                .build();

        Position startPosition = Position.builder()
                .coordinates(
                        Coordinate
                                .builder()
                                .x(1)
                                .y(1)
                                .build()
                )
                .orientation(Orientation.E).build();

        Position expectedPosition = Position.builder()
                .coordinates(
                        Coordinate
                                .builder()
                                .x(1)
                                .y(1)
                                .build()
                )
                .orientation(Orientation.E).build();

        char[] instructions = "LLLLRRRR".toCharArray();

        //when
        Position result = testObj.process(grid, startPosition, instructions);

        //then
        assertThat(result).isEqualToComparingFieldByField(expectedPosition);
    }

    @Test
    @DisplayName("Invalid instructionType causes IllegalArgumentException thrown")
    void process_invalidInstructionType_IllegalArgumentExceptionThrown(){
        //given
        Grid grid = Grid.builder().coordinates(
                Coordinate
                        .builder()
                        .x(2)
                        .y(2)
                        .build())
                .build();

        Position startPosition = Position.builder()
                .coordinates(
                        Coordinate
                                .builder()
                                .x(1)
                                .y(1)
                                .build()
                )
                .orientation(Orientation.E).build();

        char[] instructions = "F".toCharArray();

        //when then
        assertThrows(IllegalArgumentException.class, () -> testObj.process(grid, startPosition, instructions), "No such instruction F");
    }

}