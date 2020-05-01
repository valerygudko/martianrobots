package com.techtask.martianrobots.controller;

import com.techtask.martianrobots.MartianRobotsApplication;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MartianRobotsApplication.class)
class MartianRobotsHandlerTest {

    private final static String LINE_SEPARATOR = System.getProperty("line.separator");
    private MartianRobotsHandler testObj;

    @Mock
    private PrintStream out;
    @Mock
    private PrintStream err;
    @Mock
    private ContextRefreshedEvent mockStartupEvent;
    @Mock
    private ApplicationContext mockApplicationContext;

    @Autowired
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
        final String simulatedUserInput = "2 3" + LINE_SEPARATOR + "1 1 E" + LINE_SEPARATOR + "LLL" + LINE_SEPARATOR + "2 3 W" + LINE_SEPARATOR + "LRR";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(out, times(2)).println(
                eq("Grid [2, 3]"));
        verify(out).println(
                eq("Start position [1, 1, E]"));
        verify(out).println(
                eq("instructions [L, L, L]"));
        verify(out).println(
                eq("End position [1, 1, S]"));
        verify(out).println(
                eq("Start position [2, 3, W]"));
        verify(out).println(
                eq("instructions [L, R, R]"));
        verify(out).println(
                eq("End position [2, 3, N]"));
    }

    @ParameterizedTest
    @DisplayName("Invalid instructions are not accepted")
    @ValueSource(strings = {"T", "O"})
    void testInvalidInstructionsNotAccepted(String instructionType) {
        // given
        final String simulatedUserInput = "2 3" + LINE_SEPARATOR + "1 1 E" + LINE_SEPARATOR + instructionType;
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println(
                String.format("No such instruction %s %s", instructionType, "TRY AGAIN"));
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
        final String simulatedUserInput = "2 3" + LINE_SEPARATOR + "1 1 O" + LINE_SEPARATOR + sb.toString();
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println(messageLoggedCaptor.capture());
        assertThat(messageLoggedCaptor.getValue()).contains("Too long list of instructions", "TRY AGAIN");
    }

    @Test
    @DisplayName("Invalid orientation is not accepted")
    void testInvalidOrientationNotAccepted() {
        // given
        ArgumentCaptor<String> messageLoggedCaptor = ArgumentCaptor.forClass(String.class);
        final String simulatedUserInput = "2 3" + LINE_SEPARATOR + "1 1 O" + LINE_SEPARATOR + "L";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println(messageLoggedCaptor.capture());
        assertThat(messageLoggedCaptor.getValue()).contains("No enum constant", "O", "TRY AGAIN");
    }

    @ParameterizedTest
    @DisplayName("Invalid grid coordinates are not accepted")
    @CsvSource({"-52, 7", "9, 58"})
    void testInvalidCoordinatesOfGridNotAccepted(String x, String y) {
        // given
        ArgumentCaptor<String> messageLoggedCaptor = ArgumentCaptor.forClass(String.class);
        final String simulatedUserInput = x + " " + y + LINE_SEPARATOR + "1 1 E" + LINE_SEPARATOR + "LLLL";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println(messageLoggedCaptor.capture());
        assertThat(messageLoggedCaptor.getValue()).contains("Parameter is out of range:[-50,50] TRY AGAIN");
    }

    @ParameterizedTest
    @DisplayName("Start point outside of grid boundaries is not accepted")
    @CsvSource({"2, 3, 3, 3", "2, 2, 3, 2"})
    void testInvalidCoordinatesOfStartPointOnTheGridNotAccepted(String gridX, String gridY, String startX, String startY) {
        // given
        ArgumentCaptor<String> messageLoggedCaptor = ArgumentCaptor.forClass(String.class);
        final String simulatedUserInput = gridX + " " + gridY + LINE_SEPARATOR + startX + " " + startY + " E" + LINE_SEPARATOR + "LLLL";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println(messageLoggedCaptor.capture());
        assertThat(messageLoggedCaptor.getValue()).contains("Start position can't be outside of grid TRY AGAIN");
    }

    @Test
    @DisplayName("Non numeric coordinates are not accepted")
    void testNonNumericCoordinatesNotAccepted() {
        // given
        ArgumentCaptor<String> messageLoggedCaptor = ArgumentCaptor.forClass(String.class);
        final String simulatedUserInput = "U, 1" + LINE_SEPARATOR + "1 1 E" + LINE_SEPARATOR + "LLLL";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println(messageLoggedCaptor.capture());
        assertThat(messageLoggedCaptor.getValue()).contains("For input string", "U", "TRY AGAIN");
    }

    @ParameterizedTest
    @DisplayName("Invalid start position coordinates are not accepted")
    @CsvSource({"-51, 0", "9, 53"})
    void testInvalidCoordinatesNotAccepted(String x, String y) {
        // given
        ArgumentCaptor<String> messageLoggedCaptor = ArgumentCaptor.forClass(String.class);
        final String simulatedUserInput = "2 3" + LINE_SEPARATOR + x + " " + y + " E" + LINE_SEPARATOR + "LLLL";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println(messageLoggedCaptor.capture());
        assertThat(messageLoggedCaptor.getValue()).contains("Parameter is out of range:[-50,50] TRY AGAIN");
    }

}