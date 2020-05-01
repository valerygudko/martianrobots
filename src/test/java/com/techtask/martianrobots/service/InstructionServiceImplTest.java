package com.techtask.martianrobots.service;

import com.techtask.martianrobots.domain.Scent;
import com.techtask.martianrobots.model.Coordinate;
import com.techtask.martianrobots.model.Grid;
import com.techtask.martianrobots.model.Orientation;
import com.techtask.martianrobots.model.Position;
import com.techtask.martianrobots.repository.ScentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructionServiceImplTest {

    @Mock
    private Scent scent;

    @Mock
    private ScentRepository scentRepository;

    private InstructionServiceImpl testObj;

    @BeforeEach
    private void setUp(){
        testObj = new InstructionServiceImpl(scentRepository);
        lenient().when(scentRepository.save(scent)).thenReturn(scent);
    }

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
                                .x(2)
                                .y(1)
                                .build()
                )
                .orientation(Orientation.E).build();

        char[] instructions = "LRF".toCharArray();
        when(scentRepository.findFirstByCoordinateXAndCoordinateYAndUnsafeOrientation(1, 1, "E")).thenReturn(Optional.empty());
        when(scentRepository.findFirstByCoordinateXAndCoordinateYAndUnsafeOrientation(1, 1, "N")).thenReturn(Optional.empty());

        //when
        Position result = testObj.process(grid, startPosition, instructions);

        //then
        verify(scentRepository, times(0)).save(any(Scent.class));
        verify(scentRepository, times(1)).findFirstByCoordinateXAndCoordinateYAndUnsafeOrientation(1, 1, "N");
        verify(scentRepository, times(2)).findFirstByCoordinateXAndCoordinateYAndUnsafeOrientation(1, 1, "E");
        assertThat(result).isEqualToComparingFieldByField(expectedPosition);
    }

    @Test
    @DisplayName("Scent instructions from previous robots are ignored")
    void process_scentInstructionGiven_instructionIgnored(){
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
                                .x(2)
                                .y(2)
                                .build()
                )
                .orientation(Orientation.E).build();

        Position expectedPosition = Position.builder()
                .coordinates(
                        Coordinate
                                .builder()
                                .x(2)
                                .y(2)
                                .build()
                )
                .orientation(Orientation.E).build();

        char[] instructions = "F".toCharArray();
        when(scentRepository.findFirstByCoordinateXAndCoordinateYAndUnsafeOrientation(2, 2, "E"))
                .thenReturn(Optional.of(Scent.builder().build()));

        //when
        Position result = testObj.process(grid, startPosition, instructions);

        //then
        verify(scentRepository, times(0)).save(any(Scent.class));
        verify(scentRepository, times(1)).findFirstByCoordinateXAndCoordinateYAndUnsafeOrientation(2, 2, "E");
        assertThat(result).isEqualToComparingFieldByField(expectedPosition);
    }

    @Test
    @DisplayName("Scent instructions present but safe instruction is given and processed")
    void process_scentInstructionPresentAndSafeInstructionIsGiven_instructionGetsProcessed(){
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
                                .x(2)
                                .y(2)
                                .build()
                )
                .orientation(Orientation.E).build();

        Position expectedPosition = Position.builder()
                .coordinates(
                        Coordinate
                                .builder()
                                .x(2)
                                .y(2)
                                .build()
                )
                .orientation(Orientation.W).build();

        char[] instructions = "LL".toCharArray();
        when(scentRepository.findFirstByCoordinateXAndCoordinateYAndUnsafeOrientation(2, 2, "E"))
                .thenReturn(Optional.of(Scent.builder().build()));
        when(scentRepository.findFirstByCoordinateXAndCoordinateYAndUnsafeOrientation(2, 2, "N"))
                .thenReturn(Optional.of(Scent.builder().build()));

        //when
        Position result = testObj.process(grid, startPosition, instructions);

        //then
        verify(scentRepository, times(0)).save(any(Scent.class));
        verify(scentRepository, times(1)).findFirstByCoordinateXAndCoordinateYAndUnsafeOrientation(2, 2, "E");
        verify(scentRepository, times(1)).findFirstByCoordinateXAndCoordinateYAndUnsafeOrientation(2, 2, "N");
        assertThat(result).isEqualToComparingFieldByField(expectedPosition);
    }

    @Test
    @DisplayName("If robot is lost scent instruction is saved and no more instructions processed")
    void process_robotIsLost_scentInstructionSaved_instructionsProcessingStopped(){
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
                                .x(2)
                                .y(2)
                                .build()
                )
                .orientation(Orientation.E).build();

        Position expectedPosition = Position.builder()
                .coordinates(
                        Coordinate
                                .builder()
                                .x(2)
                                .y(2)
                                .build()
                )
                .orientation(Orientation.E)
                .isLost(Boolean.TRUE)
                .build();

        Scent expectedScent = Scent
                .builder()
                .coordinateX(2)
                .coordinateY(2)
                .unsafeOrientation(Orientation.E.name())
                .build();

        char[] instructions = "FRF".toCharArray();
        when(scentRepository.findFirstByCoordinateXAndCoordinateYAndUnsafeOrientation(2, 2, "E"))
                .thenReturn(Optional.empty());

        //when
        Position result = testObj.process(grid, startPosition, instructions);

        //then
        ArgumentCaptor<Scent> scentArgumentCaptor = ArgumentCaptor.forClass(Scent.class);
        verify(scentRepository, times(1)).save(scentArgumentCaptor.capture());
        assertThat(scentArgumentCaptor.getValue()).isEqualToComparingFieldByField(expectedScent);
        verify(scentRepository, times(1)).findFirstByCoordinateXAndCoordinateYAndUnsafeOrientation(2, 2, "E");
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

        char[] instructions = "T".toCharArray();

        //when then
        assertThrows(IllegalArgumentException.class, () -> testObj.process(grid, startPosition, instructions), "No such instruction T");
    }

}