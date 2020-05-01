package com.techtask.martianrobots.service;

import com.techtask.martianrobots.action.InstructionFactory;
import com.techtask.martianrobots.model.Grid;
import com.techtask.martianrobots.model.Position;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class InstructionServiceImpl implements InstructionService {
    @Override
    public Position process(Grid grid, Position startPosition, char[] instructions) {
        Supplier<InstructionFactory> instructionFactory =  InstructionFactory::new;
        Position position = startPosition;
        for (char instruction: instructions){
            position = instructionFactory.get().getInstruction(String.valueOf(instruction)).execute(position, grid);
        }
        return position;
    }
}
