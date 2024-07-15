package org.example.model;

public class AInstruction implements Instruction {
    private String symbolName;
    private Integer constant;

    public AInstruction(String symbolName) {
        this.symbolName = symbolName;
    }

    public AInstruction(Integer constant) {
        this.constant = constant;
    }

    public String getSymbolName() {
        return symbolName;
    }

    public Integer getConstant() {
        return constant;
    }
}
