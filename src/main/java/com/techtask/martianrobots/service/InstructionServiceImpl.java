package com.techtask.martianrobots.service;

import com.techtask.martianrobots.action.InstructionFactory;
import com.techtask.martianrobots.domain.Scent;
import com.techtask.martianrobots.model.Grid;
import com.techtask.martianrobots.model.Position;
import com.techtask.martianrobots.repository.ScentRepository;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class InstructionServiceImpl implements InstructionService {

    private final ScentRepository scentRepository;
    private final String FORWARD = "F";

    public InstructionServiceImpl(ScentRepository scentRepository){
        this.scentRepository = scentRepository;
    }

    @Override
    public Position process(Grid grid, Position startPosition, char[] instructions) {
        Supplier<InstructionFactory> instructionFactory =  InstructionFactory::new;
        Position position = startPosition;
        for (char instruction: instructions){
            if(safeToProceedWithInstruction(position, instruction)) {
                position = instructionFactory.get().getInstruction(String.valueOf(instruction)).execute(position, grid);
                if (position.isLost()) {
                    scentRepository.save(Scent
                            .builder()
                            .coordinateX(position
                                    .getCoordinates().getX())
                            .coordinateY(position
                                    .getCoordinates().getY())
                            .unsafeOrientation(position.getOrientation().name())
                            .build());
                    break;
                }
            }
        }
        return position;
    }

    private boolean safeToProceedWithInstruction(Position position, char instruction) {
        return !scentRepository.findFirstByCoordinateXAndCoordinateYAndUnsafeOrientation(position.getCoordinates().getX(),
                position.getCoordinates().getY(), position.getOrientation().name()).isPresent() || !String.valueOf(instruction).equals(FORWARD);
    }
}
