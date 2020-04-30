package com.techtask.martianrobots.action;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class InstructionFactory {

    final static Map<String, Supplier<Instruction>> map = new HashMap<>();
    static {
        map.put("L", Left::new);
        map.put("R", Right::new);
       // TODO: add F instruction
    }
    public Instruction getInstruction(String instructionType){
        Supplier<Instruction> instruction = map.get(instructionType.toUpperCase());
        if(instruction != null) {
            return instruction.get();
        }
        throw new IllegalArgumentException("No such instruction " + instructionType.toUpperCase());
    }
}
