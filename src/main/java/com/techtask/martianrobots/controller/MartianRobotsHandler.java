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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@Component
public class MartianRobotsHandler {

    private InstructionService instructionService;

    private int counter = 0;

    public MartianRobotsHandler(InstructionService instructionService){
        this.instructionService = instructionService;
    }

    private static final Validator VALIDATOR =
			Validation.byDefaultProvider()
					.configure()
					.messageInterpolator(new ParameterMessageInterpolator())
					.buildValidatorFactory()
					.getValidator();

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        while (counter < 2) {
            try{
            Scanner scanner = new Scanner(System.in);

            if (!scanner.hasNextLine()) {
                counter++;
                break;
            }
            String[] grid = scanner.nextLine().split("\\s");
            validateCoordinates(grid);
            while (scanner.hasNextLine()) {
                String[] startPosition = scanner.nextLine().split("\\s");
                validateCoordinates(startPosition);
                validateStartPoint(grid, startPosition);
                char[] instructions = scanner.nextLine().toCharArray();
                validateInstructionList(instructions);
                Position position = instructionService.process(Grid.builder().coordinates(
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
                System.out.println("Grid " + Arrays.toString(grid));
                System.out.println("Start position " + Arrays.toString(startPosition));
                System.out.println("instructions " + Arrays.toString(instructions));
                System.out.println("End position [" + position.getCoordinates().getX() + ", " + position.getCoordinates().getY() + ", " + position.getOrientation() + "]");
            }
            scanner.close();
        } catch(Exception ex){
            System.err.println(ex.getMessage() + " TRY AGAIN");
            contextRefreshedEvent();
        }
    }
    }

    private void validateCoordinates(String[] coordinates) {
        Set<ConstraintViolation<Object>> constraintViolations = VALIDATOR.validate(
                Coordinate.builder().x(Integer.parseInt(coordinates[0])).y(Integer.parseInt(coordinates[1])).build());

        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(constraintViolations));
        }
    }

    private void validateStartPoint(String[] grid, String[] startCoordinates) {
        if (Integer.parseInt(startCoordinates[0]) > Integer.parseInt(grid[0]) || Integer.parseInt(startCoordinates[1]) > Integer.parseInt(grid[1])) {
            throw new ConstraintViolationException("Start position can't be outside of grid", new HashSet<>());
        }
    }

    private void validateInstructionList(char[] instructions) {
        if (instructions.length > 100) {
            throw new ConstraintViolationException("Too long list of instructions", new HashSet<>());
        }
    }

}
