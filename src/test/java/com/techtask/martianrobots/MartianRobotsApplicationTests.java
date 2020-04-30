package com.techtask.martianrobots;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;
import java.io.ByteArrayInputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class MartianRobotsApplicationTests {

	private final static String LINE_SEPARATOR = System.getProperty("line.separator");


	@Test
	@DisplayName("The grid and robot instructions can be read from console input and expected output is written to console")
	void testInputReadCorrectly() {
		// given
		final String simulatedUserInput = "2 3" + LINE_SEPARATOR + "1 1 E" + LINE_SEPARATOR + "LLL" + LINE_SEPARATOR + "2 3 W" + LINE_SEPARATOR + "LL";
		System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
		PrintStream out = mock(PrintStream.class);
		System.setOut(out);

		// when
		MartianRobotsApplication.main(new String[]{});

		// then
		verify(out, times(2)).println(
				eq("Grid [2, 3]"));
		verify(out).println(
				eq("Start position [1, 1, E]"));
		verify(out).println(
				eq("instructions [L, L, L]"));
		verify(out).println(
				eq("Start position [2, 3, W]"));
		verify(out).println(
				eq("instructions [L, L]"));
	}

	@ParameterizedTest
	@DisplayName("Invalid instructions are not accepted")
	@ValueSource(strings = {"R", "O"})
	void testInvalidInstructionsNotAccepted(String instructionType) {
		// given
		final String simulatedUserInput = "2 3" + LINE_SEPARATOR + "1 1 E" + LINE_SEPARATOR + instructionType;
		System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
		PrintStream out = mock(PrintStream.class);
		System.setOut(out);

		// when then
		assertThrows(IllegalArgumentException.class, () -> MartianRobotsApplication.main(new String[]{}), String.format("No such instruction %s", instructionType));

	}

	@Test
	@DisplayName("Invalid orientation is not accepted")
	void testInvalidOrientationNotAccepted() {

		// given
		final String simulatedUserInput = "2 3" + LINE_SEPARATOR + "1 1 O" + LINE_SEPARATOR + "L";
		System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
		PrintStream out = mock(PrintStream.class);
		System.setOut(out);

		// when then
		assertThrows(IllegalArgumentException.class, () -> MartianRobotsApplication.main(new String[]{}), "No enum constant");

	}

	@ParameterizedTest
	@DisplayName("Invalid grid coordinates are not accepted")
	@CsvSource({"-52, 7", "9, 58"})
	void testInvalidCoordinatesOfGridNotAccepted(String x, String y) {

		// given
		final String simulatedUserInput = x + " " + y + LINE_SEPARATOR + "1 1 E" + LINE_SEPARATOR + "LLLL";
		System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
		PrintStream out = mock(PrintStream.class);
		System.setOut(out);

		// when then
		assertThrows(ConstraintViolationException.class, () -> MartianRobotsApplication.main(new String[]{}), "Constraint exception");

	}

	@ParameterizedTest
	@DisplayName("Invalid start position coordinates are not accepted")
	@CsvSource({"-51, 0", "9, 53"})
	void testInvalidCoordinatesNotAccepted(String x, String y) {

		// given
		final String simulatedUserInput = "2 3" + LINE_SEPARATOR + x + " " + y + " E" + LINE_SEPARATOR + "LLLL";
		System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
		PrintStream out = mock(PrintStream.class);
		System.setOut(out);

		// when then
		assertThrows(ConstraintViolationException.class, () -> MartianRobotsApplication.main(new String[]{}), "Constraint exception");

	}

}
