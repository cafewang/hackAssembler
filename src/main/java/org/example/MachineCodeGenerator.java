package org.example;

import org.example.model.AInstruction;
import org.example.model.CInstruction;
import org.example.model.Instruction;
import org.example.model.LocationDef;

import java.util.ArrayList;
import java.util.List;

public class MachineCodeGenerator {
    private final SymbolTable symbolTable;
    private final List<Instruction> instructions;
    public MachineCodeGenerator(List<Instruction> instructions) {
        this.instructions = instructions;
        symbolTable = new SymbolTable(instructions);
    }

    public List<String> generate() {
        List<String> result = new ArrayList<>();
        for (Instruction instruction : instructions) {
            if (instruction instanceof LocationDef) {
                continue;
            }
            if (instruction instanceof AInstruction) {
                AInstruction aInstruction = (AInstruction) instruction;
                String symbolName = aInstruction.getSymbolName();
                Integer constant = aInstruction.getConstant();
                int binaryValue = symbolName != null ? symbolTable.getAddress(symbolName) : constant;
                result.add(to16BitBinary(binaryValue));
            } else if (instruction instanceof CInstruction) {
                CInstruction cInstruction = (CInstruction) instruction;
                result.add(cInstruction.machineCode());
            }
        }
        return result;
    }

    private String to16BitBinary(int binaryValue) {
        StringBuilder builder = new StringBuilder();
        for (int i = 15; i >=0; i--) {
            int bit = binaryValue & (1 << i);
            builder.append(bit != 0 ? '1' : '0');
        }
        return builder.toString();
    }
}
