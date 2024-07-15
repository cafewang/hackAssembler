package org.example.model;

public class LocationDef implements Instruction {
    private final String symbolName;

    public LocationDef(String symbolName) {
        this.symbolName = symbolName;
    }

    public String getSymbolName() {
        return symbolName;
    }
}
