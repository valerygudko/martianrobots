package com.techtask.martianrobots;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class MartianRobotsApplicationTests {

	private final static String LINE_SEPARATOR = System.getProperty("line.separator");

	@Test
	@DisplayName("The grid and robot instructions can be read from console input and expected output is written to console")
	void testInputReadCorrectly() {

		// given
		final String simulatedUserInput = "2 3" + LINE_SEPARATOR + "1 1 E" + LINE_SEPARATOR + "LRLR" + LINE_SEPARATOR + "2 3 W" + LINE_SEPARATOR + "LFFLR";
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
				eq("instructions [L, R, L, R]"));
		verify(out).println(
				eq("Start position [2, 3, W]"));
		verify(out).println(
				eq("instructions [L, F, F, L, R]"));
	}

}
