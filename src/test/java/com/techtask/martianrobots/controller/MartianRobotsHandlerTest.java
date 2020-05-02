package com.techtask.martianrobots.controller;

import com.techtask.martianrobots.MartianRobotsApplication;
import com.techtask.martianrobots.model.Coordinate;
import com.techtask.martianrobots.model.Grid;
import com.techtask.martianrobots.model.Orientation;
import com.techtask.martianrobots.model.Position;
import com.techtask.martianrobots.service.InstructionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MartianRobotsApplication.class})
class MartianRobotsHandlerTest {

    private MartianRobotsHandler testObj;

    @Mock
    private ContextRefreshedEvent mockStartupEvent;
    @Mock
    private ApplicationContext mockApplicationContext;
    @Mock
    private PrintStream out;
    @Mock
    private PrintStream err;

    @Mock
    private InstructionServiceImpl instructionService;

    @BeforeEach
    private void setUp(){
        System.setOut(out);
        System.setErr(err);
        testObj = new MartianRobotsHandler(instructionService);
        when(mockStartupEvent.getApplicationContext()).thenReturn(mockApplicationContext);
    }

    @Test
    @DisplayName("The grid and robot instructions can be read from console input and expected output is written to console")
    void testInputReadCorrectly() {
        // given
        ArgumentCaptor<String> resultCaptor = ArgumentCaptor.forClass(String.class);
        final String simulatedUserInput = "2 3" + System.lineSeparator() + "1 1 E" + System.lineSeparator() + "F"
                + System.lineSeparator() + " " + System.lineSeparator() + "2 1 E" + System.lineSeparator() + "L";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
        when(instructionService.process(any(Grid.class), any(Position.class), eq("F".toCharArray())))
                .thenReturn(Position.builder().coordinates(
                        Coordinate.builder()
                                .x(2)
                                .y(1)
                                .build())
                        .orientation(Orientation.E)
                        .build());

        when(instructionService.process(any(Grid.class), any(Position.class), eq("L".toCharArray())))
                .thenReturn(Position.builder().coordinates(
                        Coordinate.builder()
                                .x(2)
                                .y(1)
                                .build())
                        .orientation(Orientation.N)
                        .build());

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(out).println(resultCaptor.capture());
        assertThat(resultCaptor.getValue()).contains("2 1 E", System.lineSeparator(), "2 1 N");
        verify(instructionService).process(any(Grid.class), any(Position.class), eq("F".toCharArray()));
        verify(instructionService).process(any(Grid.class), any(Position.class), eq("L".toCharArray()));
    }

    @Test
    @DisplayName("Empty grid details are not accepted")
    void testBlankGridDetailsAreNotAccepted() {
        // given
        final String simulatedUserInput = "" + System.lineSeparator();
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println("For input string: \"\" TRY INPUT AGAIN");
    }

    @ParameterizedTest
    @DisplayName("Invalid instructions are not accepted")
    @ValueSource(strings = {"T", "O"})
    void testInvalidInstructionsNotAccepted(String instructionType) {
        // given
        final String simulatedUserInput = "2 3" + System.lineSeparator() + "1 1 E" + System.lineSeparator() + instructionType;
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
        when(instructionService.process(any(Grid.class), any(Position.class), eq(instructionType.toCharArray())))
                .thenThrow(new IllegalArgumentException(String.format("No such instruction %s", instructionType)));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println(String.format("No such instruction %s %s", instructionType, "TRY INPUT AGAIN"));
    }

    @Test
    @DisplayName("Input with no instructions is not accepted")
    void testInputWithNoInstructionsIsNotAccepted() {
        // given
        ArgumentCaptor<String> messageLoggedCaptor = ArgumentCaptor.forClass(String.class);
        final String simulatedUserInput = "2 3" + System.lineSeparator() + "1 1 O";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println(messageLoggedCaptor.capture());
        assertThat(messageLoggedCaptor.getValue()).contains("No instructions found TRY INPUT AGAIN");
    }

    @Test
    @DisplayName("Too long list of instructions is not accepted")
    void testTooLongListOfInstructionsIsNotAccepted() {
        // given
        ArgumentCaptor<String> messageLoggedCaptor = ArgumentCaptor.forClass(String.class);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 102; i++){
            sb.append("L");
        }
        final String simulatedUserInput = "2 3" + System.lineSeparator() + "1 1 O" + System.lineSeparator() + sb.toString();
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println(messageLoggedCaptor.capture());
        assertThat(messageLoggedCaptor.getValue()).contains("Too long list of instructions", "TRY INPUT AGAIN");
    }

    @Test
    @DisplayName("Invalid orientation is not accepted")
    void testInvalidOrientationNotAccepted() {
        // given
        ArgumentCaptor<String> messageLoggedCaptor = ArgumentCaptor.forClass(String.class);
        final String simulatedUserInput = "2 3" + System.lineSeparator() + "1 1 O" + System.lineSeparator() + "L";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println(messageLoggedCaptor.capture());
        assertThat(messageLoggedCaptor.getValue()).contains("No enum constant", "O", "TRY INPUT AGAIN");
    }

    @ParameterizedTest
    @DisplayName("Invalid grid coordinates are not accepted")
    @CsvSource({"-52, 7", "9, 58"})
    void testInvalidCoordinatesOfGridNotAccepted(String x, String y) {
        // given
        ArgumentCaptor<String> messageLoggedCaptor = ArgumentCaptor.forClass(String.class);
        final String simulatedUserInput = x + " " + y + System.lineSeparator() + "1 1 E" + System.lineSeparator() + "LLLL";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println(messageLoggedCaptor.capture());
        assertThat(messageLoggedCaptor.getValue()).contains("Parameter is out of range:[0,50] TRY INPUT AGAIN");
    }

    @ParameterizedTest
    @DisplayName("Start point outside of grid boundaries is not accepted")
    @CsvSource({"2, 3, 3, 3", "2, 2, 3, 2"})
    void testInvalidCoordinatesOfStartPointOnTheGridNotAccepted(String gridX, String gridY, String startX, String startY) {
        // given
        ArgumentCaptor<String> messageLoggedCaptor = ArgumentCaptor.forClass(String.class);
        final String simulatedUserInput = gridX + " " + gridY + System.lineSeparator() + startX + " " + startY + " E" + System.lineSeparator() + "LLLL";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println(messageLoggedCaptor.capture());
        assertThat(messageLoggedCaptor.getValue()).contains("Start position can't be outside of the grid TRY INPUT AGAIN");
    }

    @Test
    @DisplayName("Non numeric coordinates are not accepted")
    void testNonNumericCoordinatesNotAccepted() {
        // given
        ArgumentCaptor<String> messageLoggedCaptor = ArgumentCaptor.forClass(String.class);
        final String simulatedUserInput = "U, 1" + System.lineSeparator() + "1 1 E" + System.lineSeparator() + "LLLL";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println(messageLoggedCaptor.capture());
        assertThat(messageLoggedCaptor.getValue()).contains("For input string", "U", "TRY INPUT AGAIN");
    }

    @ParameterizedTest
    @DisplayName("Invalid start position coordinates are not accepted")
    @CsvSource({"-51, 0", "9, 53"})
    void testInvalidCoordinatesNotAccepted(String x, String y) {
        // given
        ArgumentCaptor<String> messageLoggedCaptor = ArgumentCaptor.forClass(String.class);
        final String simulatedUserInput = "2 3" + System.lineSeparator() + x + " " + y + " E" + System.lineSeparator() + "LLLL";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println(messageLoggedCaptor.capture());
        assertThat(messageLoggedCaptor.getValue()).contains("Parameter is out of range:[0,50] TRY INPUT AGAIN");
    }

}