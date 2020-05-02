package com.techtask.martianrobots.controller;

import com.techtask.martianrobots.MartianRobotsApplication;
import com.techtask.martianrobots.service.InstructionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MartianRobotsApplication.class})
class MartianRobotsHandlerFunctionalTest {

    private MartianRobotsHandler testObj;

    @Mock
    private ContextRefreshedEvent mockStartupEvent;
    @Mock
    private ApplicationContext mockApplicationContext;
    @Mock
    private PrintStream out;
    @Mock
    private PrintStream err;

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
        ArgumentCaptor<String> resultCaptor = ArgumentCaptor.forClass(String.class);
        final String simulatedUserInput = "2 3" + System.lineSeparator() + "1 1 E" + System.lineSeparator() + "F"
                + System.lineSeparator() + " " + System.lineSeparator() + "2 1 E" + System.lineSeparator() + "L";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(out).println(resultCaptor.capture());
        assertThat(resultCaptor.getValue()).contains("2 1 E", System.lineSeparator(), "2 1 N");
    }

    @ParameterizedTest
    @DisplayName("Invalid instructions are not accepted")
    @ValueSource(strings = {"T", "O"})
    void testInvalidInstructionsNotAccepted(String instructionType) {
        // given
        final String simulatedUserInput = "2 3" + System.lineSeparator() + "1 1 E" + System.lineSeparator() + instructionType;
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        // when
        testObj.contextRefreshedEvent();

        // then
        verify(err).println(String.format("No such instruction %s %s", instructionType, "TRY INPUT AGAIN"));
    }

}