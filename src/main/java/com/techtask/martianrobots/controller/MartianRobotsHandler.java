package com.techtask.martianrobots.controller;

import com.techtask.martianrobots.model.Coordinate;
import com.techtask.martianrobots.model.Grid;
import com.techtask.martianrobots.model.Orientation;
import com.techtask.martianrobots.model.Position;
import com.techtask.martianrobots.service.InstructionService;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@Component
public class MartianRobotsHandler {

    private InstructionService instructionService;

    public MartianRobotsHandler(InstructionService instructionService){
        this.instructionService = instructionService;
    }

    private final static String WHITESPACE = " ";
    private final static String LOST_STATE = "LOST";
    private final static String LINE_SEPARATOR = System.lineSeparator();
    private int counter = 0;

    private static final Validator VALIDATOR =
			Validation.byDefaultProvider()
					.configure()
					.messageInterpolator(new ParameterMessageInterpolator())
					.buildValidatorFactory()
					.getValidator();

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        while (counter < 2) {
            Scanner scanner = new Scanner(System.in);
            if (!scanner.hasNextLine()) {
                counter++;
                break;
            }
            try {
                StringBuilder sb = new StringBuilder();
                String line;
                String[] grid = buildGrid(scanner);
                while (scanner.hasNextLine() && !(line = scanner.nextLine()).isEmpty()) {
                    String[] startPosition = buildRobotStartPosition(line, grid);
                    char[] instructions = buildInstructions(scanner);
                    Position position = getNewPosition(grid, startPosition, instructions);
                    recordFinalPosition(sb, position);
                    skipLineBetweenRobotInputs(scanner);
                }
                System.out.println(sb.toString());
            } catch (Exception ex) {
                System.err.println(ex.getMessage() + " TRY INPUT AGAIN");
                contextRefreshedEvent();
            }
        }
    }

    private void recordFinalPosition(StringBuilder sb, Position position) {
        String lostState = position.isLost() ? LOST_STATE : "";
        sb.append(position.getCoordinates().getX()).append(WHITESPACE)
            .append(position.getCoordinates().getY()).append(WHITESPACE)
            .append(position.getOrientation()).append(WHITESPACE)
            .append(lostState).append(LINE_SEPARATOR);
    }

    private Position getNewPosition(String[] grid, String[] startPosition, char[] instructions){
        return instructionService.process(Grid.builder().coordinates(
                Coordinate
                        .builder()
                        .x(Integer.parseInt(grid[0]))
                        .y(Integer.parseInt(grid[1]))
                        .build())
                        .build(),
                Position.builder()
                        .coordinates(
                                Coordinate
                                        .builder()
                                        .x(Integer.parseInt(startPosition[0]))
                                        .y(Integer.parseInt(startPosition[1]))
                                        .build()
                        )
                        .orientation(Orientation.valueOf(startPosition[2])).build(), instructions);
    }

    private void skipLineBetweenRobotInputs(Scanner scanner) {
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    private char[] buildInstructions(Scanner scanner) {
        if (!scanner.hasNextLine()){
            throw new ConstraintViolationException("No instructions found", new HashSet<>());
        }
        char[] instructions = scanner.nextLine().toCharArray();
        validateInstructionList(instructions);
        return instructions;
    }

    private String[] buildRobotStartPosition(String line, String[] grid) {
        String[] startPosition = line.split("\\s");
        validateCoordinates(startPosition);
        ensureStartPointInsideGrid(grid, startPosition);
        return startPosition;
    }

    private String[] buildGrid(Scanner scanner) {
        String[] grid = scanner.nextLine().split("\\s");
        validateCoordinates(grid);
        return grid;
    }

    private void validateCoordinates(String[] coordinates) {
        Set<ConstraintViolation<Object>> constraintViolations = VALIDATOR.validate(
                Coordinate.builder().x(Integer.parseInt(coordinates[0])).y(Integer.parseInt(coordinates[1])).build());

        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraintViolations));
        }
    }

    private void ensureStartPointInsideGrid(String[] grid, String[] startCoordinates) {
        if (Integer.parseInt(startCoordinates[0]) < 0 || Integer.parseInt(startCoordinates[0]) > Integer.parseInt(grid[0])
                || Integer.parseInt(startCoordinates[1]) < 0 || Integer.parseInt(startCoordinates[1]) > Integer.parseInt(grid[1])) {
            throw new ConstraintViolationException("Start position can't be outside of the grid", new HashSet<>());
        }
    }

    private void validateInstructionList(char[] instructions) {
        if (instructions.length > 100) {
            throw new ConstraintViolationException("Too long list of instructions", new HashSet<>());
        }
    }

}
