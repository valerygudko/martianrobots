package com.techtask.martianrobots;

import com.techtask.martianrobots.action.InstructionFactory;
import com.techtask.martianrobots.model.Coordinate;
import com.techtask.martianrobots.model.Orientation;
import com.techtask.martianrobots.model.Position;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Supplier;

@SpringBootApplication
public class MartianRobotsApplication {

	private static final Validator VALIDATOR =
			Validation.byDefaultProvider()
					.configure()
					.messageInterpolator(new ParameterMessageInterpolator())
					.buildValidatorFactory()
					.getValidator();

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		String[] grid = scanner.nextLine().split("\\s");

		Set<ConstraintViolation<Object>> constraintViolations = VALIDATOR.validate(new Coordinate(Integer.parseInt(grid[0]), Integer.parseInt(grid[1])));

		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraintViolations));
		}

		while (scanner.hasNextLine()) {
			String[] startPosition = scanner.nextLine().split("\\s");
			char[] instructions = scanner.nextLine().toCharArray();

			Supplier<InstructionFactory> instructionFactory =  InstructionFactory::new;
			Coordinate coordinates = new Coordinate(Integer.parseInt(startPosition[0]), Integer.parseInt(startPosition[1]));
			constraintViolations = VALIDATOR.validate(coordinates);

			if (!constraintViolations.isEmpty()) {
				throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraintViolations));
			}

			Position position = new Position(coordinates, Orientation.valueOf(startPosition[2]));;
			for (char instruction: instructions){
				position = instructionFactory.get().getInstruction(String.valueOf(instruction)).execute(position);
			}

			System.out.println("Grid " + Arrays.toString(grid));
			System.out.println("Start position " + Arrays.toString(startPosition));
			System.out.println("instructions " + Arrays.toString(instructions));
			System.out.println("End position [" + position.getCoordinates().getX() + ", " + position.getCoordinates().getY() + ", " + position.getOrientation() + "]");
		}

		scanner.close();
	}

}
