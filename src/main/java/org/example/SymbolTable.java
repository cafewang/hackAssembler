package org.example;

import com.google.common.collect.ImmutableMap;
import org.example.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    private static final Map<String, Integer> PREDEFINED = new ImmutableMap.Builder<String, Integer>()
            .put("SP", 0)
            .put("LCL", 1)
            .put("ARG", 2)
            .put("THIS", 3)
            .put("THAT", 4)
            .put("SCREEN", 16384)
            .put("KBD", 24576)
            .put("R0", 0)
            .put("R1", 1)
            .put("R2", 2)
            .put("R3", 3)
            .put("R4", 4)
            .put("R5", 5)
            .put("R6", 6)
            .put("R7", 7)
            .put("R8", 8)
            .put("R9", 9)
            .put("R10",10)
            .put("R11",11)
            .put("R12",12)
            .put("R13",13)
            .put("R14",14)
            .put("R15",15)
            .build();

    private static boolean isPredefined(String name) {
        return PREDEFINED.containsKey(name);
    }

    private final Map<String, Symbol> table = new HashMap<>();

    public SymbolTable(List<Instruction> instructions) {
        for (Instruction instruction : instructions) {
            if (instruction instanceof AInstruction) {
                AInstruction aInstruction = (AInstruction) instruction;
                String symbolName = aInstruction.getSymbolName();
                if (isPredefined(symbolName)) {
                    continue;
                }
                if (symbolName != null) {
                    if (!table.containsKey(symbolName)) {
                        table.put(symbolName, new Variable(symbolName, 0));
                    }
                }
            } else if (instruction instanceof LocationDef) {
                LocationDef locationDef = (LocationDef) instruction;
                String symbolName = locationDef.getSymbolName();
                if (isPredefined(symbolName)) {
                    throw new IllegalArgumentException("标签定义错误，使用了预定义的变量");
                }
                Symbol symbol = table.get(symbolName);
                if (symbol != null) {
                    if (symbol instanceof Label) {
                        throw new IllegalArgumentException(String.format("标签定义错误，标签%s重复定义", symbolName));
                    }
                }
                // replace variable definition
                table.put(symbolName, new Label(symbolName, 0));
            }
        }
        // assign address
        int codeAddress = 0, variableAddress = 16;
        for (Instruction instruction : instructions) {
            if (instruction instanceof LocationDef) {
                continue;
            }
            codeAddress++;
            if (instruction instanceof AInstruction) {
                AInstruction aInstruction = (AInstruction) instruction;
                String symbolName = aInstruction.getSymbolName();
                if (symbolName != null) {
                    if (!isPredefined(symbolName)) {
                        Symbol symbol = table.get(symbolName);
                        if (symbol instanceof Variable) {
                            Variable variable = (Variable) symbol;
                            if (variable.getAddress().equals(0)) {
                                table.put(symbolName, new Variable(symbolName, variableAddress++));
                            }
                        }
                    }
                }
            }
        }

        for (int i = instructions.size() - 1; i >= 0; i--) {
            Instruction instruction = instructions.get(i);
            if (instruction instanceof LocationDef) {
                LocationDef locationDef = (LocationDef) instruction;
                String symbolName = locationDef.getSymbolName();
                table.put(symbolName, new Label(symbolName, codeAddress));
            } else {
                codeAddress--;
            }
        }
    }

    public int getAddress(String name) {
        if (isPredefined(name)) {
            return PREDEFINED.get(name);
        }
        Symbol symbol = table.get(name);
        if (symbol instanceof Label) {
            return ((Label)symbol).getAddress();
        } else if (symbol instanceof Variable) {
            return ((Variable) symbol).getAddress();
        }
        return -1;
    }
}
